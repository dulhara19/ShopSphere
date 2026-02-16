package com.shopsphere.inventory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopsphere.inventory.dto.InventoryDTO;
import com.shopsphere.inventory.dto.request.BulkUpdateInventoryRequest;
import com.shopsphere.inventory.dto.request.InventoryAdjustmentRequest;
import com.shopsphere.inventory.dto.response.BulkUpdateItemResult;
import com.shopsphere.inventory.dto.response.BulkUpdateResponse;
import com.shopsphere.inventory.dto.response.StockHistoryResponse;
import com.shopsphere.inventory.model.StockMovementLog;
import com.shopsphere.inventory.service.InventoryService;
import com.shopsphere.inventory.service.LowStockService;
import com.shopsphere.inventory.service.ReservationService;
import com.shopsphere.inventory.service.StockHistoryService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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
                        .contentType(MediaType.APPLICATION_JSON)
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
                        .contentType(MediaType.APPLICATION_JSON)
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
                        .contentType(MediaType.APPLICATION_JSON)
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
                        .contentType(MediaType.APPLICATION_JSON)
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
                        .contentType(MediaType.APPLICATION_JSON)
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
}
