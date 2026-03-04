package com.shopsphere.inventory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopsphere.inventory.dto.InventoryDTO;
import com.shopsphere.inventory.dto.ReservationDTO;
import com.shopsphere.inventory.dto.request.BulkUpdateInventoryRequest;
import com.shopsphere.inventory.dto.request.ConfirmReservationRequest;
import com.shopsphere.inventory.dto.request.CreateInventoryRequest;
import com.shopsphere.inventory.dto.request.InventoryAdjustmentRequest;
import com.shopsphere.inventory.dto.request.ReleaseReservationRequest;
import com.shopsphere.inventory.dto.request.ReserveStockRequest;
import com.shopsphere.inventory.dto.request.UpdateInventoryRequest;
import com.shopsphere.inventory.dto.response.BulkUpdateItemResult;
import com.shopsphere.inventory.dto.response.BulkUpdateResponse;
import com.shopsphere.inventory.dto.response.StockHistoryResponse;
import com.shopsphere.inventory.model.StockMovementLog;
import com.shopsphere.inventory.service.InventoryService;
import com.shopsphere.inventory.service.LowStockService;
import com.shopsphere.inventory.service.ReservationService;
import com.shopsphere.inventory.service.StockHistoryService;
import com.shopsphere.inventory.service.StockStreamService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InventoryController.class)
class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private InventoryService inventoryService;

    @MockBean
    private ReservationService reservationService;

    @MockBean
    private LowStockService lowStockService;

    @MockBean
    private StockHistoryService stockHistoryService;

    @MockBean
    private StockStreamService stockStreamService;

    @Test
    void bulkUpdateStockReturnsUpdatedInventories() throws Exception {
        UUID productId = UUID.randomUUID();

        List<BulkUpdateInventoryRequest> request = List.of(
                BulkUpdateInventoryRequest.builder()
                        .productId(productId)
                        .quantity(50L)
                        .lowStockThreshold(5L)
                        .build()
        );

        BulkUpdateResponse response = BulkUpdateResponse.builder()
                .total(1)
                .successCount(1)
                .failureCount(0)
                .results(List.of(
                        BulkUpdateItemResult.builder()
                                .productId(productId)
                                .success(true)
                                .inventory(InventoryDTO.builder()
                                        .productId(productId)
                                        .quantity(50L)
                                        .reservedQuantity(10L)
                                        .availableQuantity(40L)
                                        .lowStockThreshold(5L)
                                        .status("IN_STOCK")
                                        .build())
                                .build()
                ))
                .build();

        when(inventoryService.bulkUpdateStock(ArgumentMatchers.anyList())).thenReturn(response);

        mockMvc.perform(post("/inventory/bulk-update")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.success_count").value(1))
                .andExpect(jsonPath("$.failure_count").value(0))
                .andExpect(jsonPath("$.results[0].product_id").value(productId.toString()))
                .andExpect(jsonPath("$.results[0].success").value(true))
                .andExpect(jsonPath("$.results[0].inventory.available_quantity").value(40));
    }

    @Test
    void bulkUpdateStockReturnsMultiStatusWhenAnyItemFails() throws Exception {
        UUID productId = UUID.randomUUID();

        List<BulkUpdateInventoryRequest> request = List.of(
                BulkUpdateInventoryRequest.builder()
                        .productId(productId)
                        .quantity(50L)
                        .build()
        );

        BulkUpdateResponse response = BulkUpdateResponse.builder()
                .total(1)
                .successCount(0)
                .failureCount(1)
                .results(List.of(
                        BulkUpdateItemResult.builder()
                                .productId(productId)
                                .success(false)
                                .error("Inventory not found")
                                .build()
                ))
                .build();

        when(inventoryService.bulkUpdateStock(ArgumentMatchers.anyList())).thenReturn(response);

        mockMvc.perform(post("/inventory/bulk-update")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isMultiStatus())
                .andExpect(jsonPath("$.failure_count").value(1))
                .andExpect(jsonPath("$.results[0].error").value("Inventory not found"));
    }

    @Test
    void checkAvailabilitySupportsBatchPayload() throws Exception {
        UUID p1 = UUID.randomUUID();
        UUID p2 = UUID.randomUUID();

        String request = "[" +
                "{\"product_id\":\"" + p1 + "\",\"quantity\":2}," +
                "{\"product_id\":\"" + p2 + "\",\"quantity\":4}" +
                "]";

        when(reservationService.checkAvailability(p1, 2L)).thenReturn(true);
        when(reservationService.checkAvailability(p2, 4L)).thenReturn(false);

        when(inventoryService.getStockLevel(p1)).thenReturn(
                InventoryDTO.builder().productId(p1).availableQuantity(10L).build()
        );
        when(inventoryService.getStockLevel(p2)).thenReturn(
                InventoryDTO.builder().productId(p2).availableQuantity(1L).build()
        );

        mockMvc.perform(post("/inventory/check-availability")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].product_id").value(p1.toString()))
                .andExpect(jsonPath("$[0].available").value(true))
                .andExpect(jsonPath("$[1].product_id").value(p2.toString()))
                .andExpect(jsonPath("$[1].available").value(false));
    }

    @Test
    void checkAvailabilityRejectsInvalidBatchQuantity() throws Exception {
        UUID productId = UUID.randomUUID();
        String request = "[{\"product_id\":\"" + productId + "\",\"quantity\":0}]";

        mockMvc.perform(post("/inventory/check-availability")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Quantity must be at least 1"));
    }

    @Test
    void adjustInventoryReturnsUpdatedStock() throws Exception {
        UUID productId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        InventoryAdjustmentRequest request = InventoryAdjustmentRequest.builder()
                .quantityChange(5L)
                .changeType(StockMovementLog.ChangeType.RESTOCK)
                .reason("Manual restock")
                .userId(userId)
                .build();

        when(stockHistoryService.adjustStock(ArgumentMatchers.eq(productId), ArgumentMatchers.any()))
                .thenReturn(InventoryDTO.builder()
                        .productId(productId)
                        .quantity(25L)
                        .availableQuantity(25L)
                        .build());

        mockMvc.perform(post("/inventory/{productId}/adjustment", productId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.product_id").value(productId.toString()))
                .andExpect(jsonPath("$.quantity").value(25));
    }

    @Test
    void getHistoryReturnsOk() throws Exception {
        UUID productId = UUID.randomUUID();

        when(stockHistoryService.getHistory(ArgumentMatchers.eq(productId), ArgumentMatchers.isNull(),
                ArgumentMatchers.isNull(), ArgumentMatchers.isNull()))
                .thenReturn(List.of(
                        StockHistoryResponse.builder()
                                .productId(productId)
                                .quantityBefore(10L)
                                .quantityAfter(15L)
                                .quantityChange(5L)
                                .changeType(StockMovementLog.ChangeType.RESTOCK)
                                .build()
                ));

        mockMvc.perform(get("/inventory/{productId}/history", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].product_id").value(productId.toString()))
                .andExpect(jsonPath("$[0].quantity_change").value(5))
                .andExpect(jsonPath("$[0].change_type").value("RESTOCK"));
    }

    @Test
    void createInventoryReturnsCreated() throws Exception {
        UUID productId = UUID.randomUUID();
        CreateInventoryRequest request = CreateInventoryRequest.builder()
                .productId(productId)
                .quantity(100L)
                .lowStockThreshold(5L)
                .build();

        when(inventoryService.createInventory(ArgumentMatchers.any())).thenReturn(
                InventoryDTO.builder().productId(productId).quantity(100L).availableQuantity(100L).build()
        );

        mockMvc.perform(post("/inventory")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.product_id").value(productId.toString()));
    }

    @Test
    void getStockLevelReturnsOk() throws Exception {
        UUID productId = UUID.randomUUID();
        when(inventoryService.getStockLevel(productId)).thenReturn(
                InventoryDTO.builder().productId(productId).quantity(20L).build()
        );

        mockMvc.perform(get("/inventory/{productId}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.product_id").value(productId.toString()))
                .andExpect(jsonPath("$.quantity").value(20));
    }

    @Test
    void updateStockQuantityReturnsOk() throws Exception {
        UUID productId = UUID.randomUUID();
        UpdateInventoryRequest request = UpdateInventoryRequest.builder()
                .quantity(120L)
                .lowStockThreshold(10L)
                .build();

        when(inventoryService.updateStockQuantity(ArgumentMatchers.eq(productId), ArgumentMatchers.any())).thenReturn(
                InventoryDTO.builder().productId(productId).quantity(120L).build()
        );

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/inventory/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(120));
    }

    @Test
    void deleteInventoryReturnsNoContent() throws Exception {
        UUID productId = UUID.randomUUID();

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/inventory/{productId}", productId))
                .andExpect(status().isNoContent());
    }

    @Test
    void reserveConfirmReleaseFlowReturnsOk() throws Exception {
        UUID reservationId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        ReserveStockRequest reserveRequest = ReserveStockRequest.builder()
                .productId(productId)
                .quantity(2L)
                .orderId(UUID.randomUUID())
                .build();
        ConfirmReservationRequest confirmRequest = ConfirmReservationRequest.builder().reservationId(reservationId).build();
        ReleaseReservationRequest releaseRequest = ReleaseReservationRequest.builder().reservationId(reservationId).build();

        ReservationDTO dto = ReservationDTO.builder()
                .reservationId(reservationId)
                .productId(productId)
                .quantity(2L)
                .status("PENDING")
                .build();

        when(reservationService.reserveStock(ArgumentMatchers.any())).thenReturn(dto);
        when(reservationService.confirmReservation(reservationId)).thenReturn(dto);
        when(reservationService.releaseReservation(reservationId)).thenReturn(dto);

        mockMvc.perform(post("/inventory/reserve")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(reserveRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reservation_id").value(reservationId.toString()));

        mockMvc.perform(post("/inventory/confirm")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(confirmRequest)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/inventory/release")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(releaseRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void lowStockAndOutOfStockEndpointsReturnOk() throws Exception {
        UUID productId = UUID.randomUUID();
        InventoryDTO dto = InventoryDTO.builder().productId(productId).status("LOW_STOCK").build();

        when(lowStockService.getLowStockProducts()).thenReturn(List.of(dto));
        when(lowStockService.getOutOfStockProducts()).thenReturn(List.of(
                InventoryDTO.builder().productId(productId).status("OUT_OF_STOCK").build()
        ));

        mockMvc.perform(get("/inventory/low-stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].product_id").value(productId.toString()));

        mockMvc.perform(get("/inventory/out-of-stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("OUT_OF_STOCK"));
    }

    @Test
    void setThresholdReturnsOk() throws Exception {
        UUID productId = UUID.randomUUID();
        when(lowStockService.setLowStockThreshold(productId, 8L)).thenReturn(
                InventoryDTO.builder().productId(productId).lowStockThreshold(8L).build()
        );

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/inventory/{productId}/threshold", productId)
                        .param("threshold", "8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.low_stock_threshold").value(8));
    }

    @Test
    void reservationsAndAuditEndpointsReturnOk() throws Exception {
        UUID productId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        when(reservationService.getReservationsByProduct(productId)).thenReturn(List.of(
                ReservationDTO.builder().reservationId(UUID.randomUUID()).productId(productId).build()
        ));
        when(reservationService.getReservationsByOrder(orderId)).thenReturn(List.of(
                ReservationDTO.builder().reservationId(UUID.randomUUID()).orderId(orderId).build()
        ));
        when(stockHistoryService.exportAuditReport(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn("timestamp,product_id\n");

        mockMvc.perform(get("/inventory/{productId}/reservations", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].product_id").value(productId.toString()));

        mockMvc.perform(get("/inventory/order/{orderId}/reservations", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].order_id").value(orderId.toString()));

        mockMvc.perform(get("/inventory/audit-report")
                        .param("from", LocalDateTime.now().minusDays(1).toString())
                        .param("to", LocalDateTime.now().toString()))
                .andExpect(status().isOk());
    }

    @Test
    void streamEndpointReturnsOk() throws Exception {
        when(stockStreamService.subscribe()).thenReturn(new SseEmitter());

        mockMvc.perform(get("/inventory/stream"))
                .andExpect(status().isOk());
    }
}
