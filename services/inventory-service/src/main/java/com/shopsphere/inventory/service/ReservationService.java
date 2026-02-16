package com.shopsphere.inventory.service;

import com.shopsphere.inventory.dto.ReservationDTO;
import com.shopsphere.inventory.dto.request.ReserveStockRequest;
import com.shopsphere.inventory.exception.InsufficientStockException;
import com.shopsphere.inventory.exception.ProductNotFoundException;
import com.shopsphere.inventory.exception.ReservationNotFoundException;
import com.shopsphere.inventory.model.Inventory;
import com.shopsphere.inventory.model.StockReservation;
import com.shopsphere.inventory.model.StockMovementLog;
import com.shopsphere.inventory.repository.InventoryRepository;
import com.shopsphere.inventory.repository.StockReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {

    private final StockReservationRepository reservationRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryEventService eventService;
    private final StockHistoryService stockHistoryService;

    @Value("${inventory.reservation.expiry-minutes:15}")
    private int expiryMinutes;

    /**
     * Epic 1.2.1: Reserve stock for checkout
     */
    @Transactional
    public ReservationDTO reserveStock(ReserveStockRequest request) {
        log.info("Reserving stock for product: {}, quantity: {}", request.getProductId(), request.getQuantity());

        Inventory inventory = inventoryRepository.findByProductId(request.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(request.getProductId().toString()));

        // Check if sufficient stock is available
        if (!inventory.hasSufficientStock(request.getQuantity())) {
            throw new InsufficientStockException(
                    inventory.getProductId().toString(),
                    request.getQuantity(),
                    inventory.getAvailableQuantity()
            );
        }

        // Create reservation
        StockReservation reservation = StockReservation.builder()
                .productId(request.getProductId())
                .quantity(request.getQuantity())
                .status(StockReservation.ReservationStatus.PENDING)
                .orderId(request.getOrderId())
                .expiresAt(LocalDateTime.now().plusMinutes(expiryMinutes))
                .build();

        // Update inventory reserved quantity
        long quantityBefore = inventory.getQuantity();
        inventory.setReservedQuantity(inventory.getReservedQuantity() + request.getQuantity());
        inventoryRepository.save(inventory);

        StockReservation saved = reservationRepository.save(reservation);
        stockHistoryService.logMovement(
                request.getProductId(),
                StockMovementLog.ChangeType.RESERVE,
                quantityBefore,
                quantityBefore,
                "Stock reserved for checkout",
                null,
                saved.getId()
        );
        log.info("Stock reserved for product: {}, reservation ID: {}", request.getProductId(), saved.getId());

        return ReservationDTO.fromEntity(saved);
    }

    /**
     * Epic 1.2.2: Confirm reservation (order placed)
     */
    @Transactional
    public ReservationDTO confirmReservation(UUID reservationId) {
        log.info("Confirming reservation: {}", reservationId);

        StockReservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException(reservationId.toString()));

        Inventory inventory = inventoryRepository.findByProductId(reservation.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(reservation.getProductId().toString()));

        // Convert reserved to sold
        long quantityBefore = inventory.getQuantity();
        inventory.setQuantity(inventory.getQuantity() - reservation.getQuantity());
        inventory.setReservedQuantity(inventory.getReservedQuantity() - reservation.getQuantity());
        inventory.updateStatus();

        inventoryRepository.save(inventory);

        // Update reservation
        reservation.setStatus(StockReservation.ReservationStatus.CONFIRMED);
        reservation.setConfirmedAt(LocalDateTime.now());
        StockReservation updated = reservationRepository.save(reservation);

        stockHistoryService.logMovement(
                reservation.getProductId(),
                StockMovementLog.ChangeType.SALE,
                quantityBefore,
                inventory.getQuantity(),
                "Reservation confirmed and converted to sale",
                null,
                reservationId
        );
        log.info("Reservation confirmed: {}", reservationId);
        eventService.publishStockUpdatedEvent(inventory);

        return ReservationDTO.fromEntity(updated);
    }

    /**
     * Epic 1.2.3: Release reservation (timeout/cancel)
     */
    @Transactional
    public ReservationDTO releaseReservation(UUID reservationId) {
        log.info("Releasing reservation: {}", reservationId);

        StockReservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException(reservationId.toString()));

        Inventory inventory = inventoryRepository.findByProductId(reservation.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(reservation.getProductId().toString()));

        // Return reserved to available
        long quantityBefore = inventory.getQuantity();
        inventory.setReservedQuantity(inventory.getReservedQuantity() - reservation.getQuantity());
        inventory.updateStatus();

        inventoryRepository.save(inventory);

        // Update reservation
        reservation.setStatus(StockReservation.ReservationStatus.RELEASED);
        reservation.setReleasedAt(LocalDateTime.now());
        StockReservation updated = reservationRepository.save(reservation);

        stockHistoryService.logMovement(
                reservation.getProductId(),
                StockMovementLog.ChangeType.RELEASE,
                quantityBefore,
                quantityBefore,
                "Reservation released",
                null,
                reservationId
        );
        log.info("Reservation released: {}", reservationId);
        eventService.publishStockUpdatedEvent(inventory);

        return ReservationDTO.fromEntity(updated);
    }

    /**
     * Epic 1.2.4: Check availability (for internal use in controllers)
     */
    @Transactional(readOnly = true)
    public boolean checkAvailability(UUID productId, Long quantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId.toString()));

        return inventory.hasSufficientStock(quantity);
    }

    /**
     * Epic 1.2.5: Reservation expiry job - Auto-release expired reservations
     * Scheduled to run every 5 minutes
     */
    @Scheduled(fixedRateString = "${inventory.reservation.cleanup-interval-minutes:5}m", initialDelay = 60000)
    @Transactional
    public void releaseExpiredReservations() {
        log.info("Running scheduled job to release expired reservations");

        List<StockReservation> expiredReservations = reservationRepository.findExpiredReservations(LocalDateTime.now());

        for (StockReservation reservation : expiredReservations) {
            try {
                releaseReservation(reservation.getId());
            } catch (Exception e) {
                log.error("Error releasing expired reservation: {}", reservation.getId(), e);
            }
        }

        log.info("Expired reservations released: {}", expiredReservations.size());
    }

    /**
     * Get all reservations for a product
     */
    @Transactional(readOnly = true)
    public List<ReservationDTO> getReservationsByProduct(UUID productId) {
        return reservationRepository.findByProductId(productId)
                .stream()
                .map(ReservationDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get all reservations for an order
     */
    @Transactional(readOnly = true)
    public List<ReservationDTO> getReservationsByOrder(UUID orderId) {
        return reservationRepository.findByOrderId(orderId)
                .stream()
                .map(ReservationDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
