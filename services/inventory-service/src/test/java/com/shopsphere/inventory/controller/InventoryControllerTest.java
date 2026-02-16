package com.shopsphere.inventory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopsphere.inventory.dto.InventoryDTO;
import com.shopsphere.inventory.dto.request.BulkUpdateInventoryRequest;
import com.shopsphere.inventory.service.InventoryService;
import com.shopsphere.inventory.service.LowStockService;
import com.shopsphere.inventory.service.ReservationService;
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

        List<InventoryDTO> response = List.of(
                InventoryDTO.builder()
                        .productId(productId)
                        .quantity(50L)
                        .reservedQuantity(10L)
                        .availableQuantity(40L)
                        .lowStockThreshold(5L)
                        .status("IN_STOCK")
                        .build()
        );

        when(inventoryService.bulkUpdateStock(ArgumentMatchers.anyList())).thenReturn(response);

        mockMvc.perform(post("/inventory/bulk-update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].product_id").value(productId.toString()))
                .andExpect(jsonPath("$[0].quantity").value(50))
                .andExpect(jsonPath("$[0].available_quantity").value(40));
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
}
