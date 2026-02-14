package com.shopsphere.order.service;

import com.shopsphere.order.dto.OrderDto;
import com.shopsphere.order.dto.OrderStatusResponseDto;
import com.shopsphere.order.dto.ShippingDetailsDto;
import com.shopsphere.order.dto.request.CancelOrderRequest;
import com.shopsphere.order.dto.request.UpdateStatusRequest;
import com.shopsphere.order.dto.response.PaginationMeta;
import com.shopsphere.order.exception.InvalidStatusTransitionException;
import com.shopsphere.order.exception.OrderCancellationException;
import com.shopsphere.order.exception.OrderNotFoundException;
import com.shopsphere.order.model.Order;
import com.shopsphere.order.model.OrderStatus;
import com.shopsphere.order.model.OrderStatusHistory;
import com.shopsphere.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher eventPublisher;
    private final InventoryService inventoryService;

    @Transactional(readOnly = true)
    public OrderDto getOrder(UUID orderId, UUID userId) {
        Order order = orderRepository.findByIdAndUserIdWithDetails(orderId, userId)
            .orElseThrow(() -> OrderNotFoundException.forId(orderId));
        return OrderDto.from(order);
    }

    @Transactional(readOnly = true)
    public OrderDto getOrderById(UUID orderId) {
        Order order = orderRepository.findByIdWithDetails(orderId)
            .orElseThrow(() -> OrderNotFoundException.forId(orderId));
        return OrderDto.from(order);
    }

    @Transactional(readOnly = true)
    public List<OrderDto> listOrders(UUID userId, OrderStatus status, String sortBy, String sortOrder, int page, int limit) {
        Sort sort = Sort.by(sortOrder.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(page - 1, limit, sort);

        Page<Order> orders;
        if (status != null) {
            orders = orderRepository.findByUserIdAndStatus(userId, status, pageable);
        } else {
            orders = orderRepository.findByUserId(userId, pageable);
        }

        return orders.getContent().stream()
            .map(OrderDto::from)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PaginationMeta getOrdersPaginationMeta(UUID userId, OrderStatus status, int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<Order> orders;
        if (status != null) {
            orders = orderRepository.findByUserIdAndStatus(userId, status, pageable);
        } else {
            orders = orderRepository.findByUserId(userId, pageable);
        }
        return PaginationMeta.from(orders);
    }

    @Transactional(readOnly = true)
    public OrderStatusResponseDto getOrderStatus(UUID orderId, UUID userId) {
        Order order = orderRepository.findByIdAndUserIdWithDetails(orderId, userId)
            .orElseThrow(() -> OrderNotFoundException.forId(orderId));
        return OrderStatusResponseDto.from(order);
    }

    @Transactional
    public OrderDto cancelOrder(UUID orderId, UUID userId, CancelOrderRequest request) {
        Order order = orderRepository.findByIdAndUserIdWithDetails(orderId, userId)
            .orElseThrow(() -> OrderNotFoundException.forId(orderId));

        if (!order.canBeCancelled()) {
            throw OrderCancellationException.cannotCancel(order.getStatus());
        }

        OrderStatusHistory history = OrderStatusHistory.builder()
            .status(OrderStatus.CANCELLED)
            .note(request.getReason())
            .updatedBy("USER:" + userId)
            .build();
        order.addStatusHistory(history);
        order.setStatus(OrderStatus.CANCELLED);

        order = orderRepository.save(order);

        // Release reserved inventory
        Map<UUID, Integer> productQuantities = order.getItems().stream()
            .collect(Collectors.toMap(
                item -> item.getProductId(),
                item -> item.getQuantity()
            ));
        inventoryService.releaseInventory(order.getId(), productQuantities);

        // Publish cancellation event
        eventPublisher.publishOrderCancelled(order);

        log.info("Order {} cancelled by user {}", orderId, userId);
        return OrderDto.from(order);
    }

    @Transactional
    public OrderDto updateOrderStatus(UUID orderId, UpdateStatusRequest request, String updatedBy) {
        Order order = orderRepository.findByIdWithDetails(orderId)
            .orElseThrow(() -> OrderNotFoundException.forId(orderId));

        if (!order.canTransitionTo(request.getStatus())) {
            throw InvalidStatusTransitionException.of(order.getStatus(), request.getStatus());
        }

        OrderStatusHistory history = OrderStatusHistory.builder()
            .status(request.getStatus())
            .note(request.getNote())
            .updatedBy(updatedBy)
            .build();
        order.addStatusHistory(history);
        order.setStatus(request.getStatus());

        if (request.getTrackingNumber() != null) {
            order.setTrackingNumber(request.getTrackingNumber());
        }

        order = orderRepository.save(order);

        // Publish appropriate event
        publishStatusChangeEvent(order);

        log.info("Order {} status updated to {} by {}", orderId, request.getStatus(), updatedBy);
        return OrderDto.from(order);
    }

    @Transactional(readOnly = true)
    public List<OrderDto> listAllOrders(
        OrderStatus status,
        UUID userId,
        String orderNumber,
        LocalDate fromDate,
        LocalDate toDate,
        int page,
        int limit
    ) {
        Specification<Order> spec = Specification.where(null);

        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }
        if (userId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("userId"), userId));
        }
        if (orderNumber != null && !orderNumber.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.like(root.get("orderNumber"), "%" + orderNumber + "%"));
        }
        if (fromDate != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), fromDate.atStartOfDay()));
        }
        if (toDate != null) {
            spec = spec.and((root, query, cb) -> cb.lessThan(root.get("createdAt"), toDate.plusDays(1).atStartOfDay()));
        }

        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Order> orders = orderRepository.findAll(spec, pageable);

        return orders.getContent().stream()
            .map(OrderDto::from)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PaginationMeta getAdminOrdersPaginationMeta(
        OrderStatus status,
        UUID userId,
        String orderNumber,
        LocalDate fromDate,
        LocalDate toDate,
        int page,
        int limit
    ) {
        Specification<Order> spec = Specification.where(null);

        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }
        if (userId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("userId"), userId));
        }
        if (orderNumber != null && !orderNumber.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.like(root.get("orderNumber"), "%" + orderNumber + "%"));
        }
        if (fromDate != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), fromDate.atStartOfDay()));
        }
        if (toDate != null) {
            spec = spec.and((root, query, cb) -> cb.lessThan(root.get("createdAt"), toDate.plusDays(1).atStartOfDay()));
        }

        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<Order> orders = orderRepository.findAll(spec, pageable);

        return PaginationMeta.from(orders);
    }

    @Transactional(readOnly = true)
    public ShippingDetailsDto getShippingDetails(UUID orderId) {
        Order order = orderRepository.findByIdWithDetails(orderId)
            .orElseThrow(() -> OrderNotFoundException.forId(orderId));
        return ShippingDetailsDto.from(order);
    }

    @Transactional
    public OrderDto addInternalNote(UUID orderId, String note, String addedBy) {
        Order order = orderRepository.findByIdWithDetails(orderId)
            .orElseThrow(() -> OrderNotFoundException.forId(orderId));

        String timestamp = LocalDateTime.now().toString();
        String newNote = String.format("[%s by %s] %s", timestamp, addedBy, note);

        String existingNotes = order.getInternalNotes();
        if (existingNotes != null && !existingNotes.isBlank()) {
            order.setInternalNotes(existingNotes + "\n" + newNote);
        } else {
            order.setInternalNotes(newNote);
        }

        order = orderRepository.save(order);
        log.info("Internal note added to order {} by {}", orderId, addedBy);
        return OrderDto.from(order);
    }

    @Transactional(readOnly = true)
    public List<OrderStatusHistory> getOrderAuditTrail(UUID orderId) {
        Order order = orderRepository.findByIdWithDetails(orderId)
            .orElseThrow(() -> OrderNotFoundException.forId(orderId));
        return order.getStatusHistory();
    }

    private void publishStatusChangeEvent(Order order) {
        switch (order.getStatus()) {
            case CONFIRMED -> eventPublisher.publishOrderConfirmed(order);
            case SHIPPED -> eventPublisher.publishOrderShipped(order);
            case DELIVERED -> eventPublisher.publishOrderDelivered(order);
            default -> {}
        }
    }
}
