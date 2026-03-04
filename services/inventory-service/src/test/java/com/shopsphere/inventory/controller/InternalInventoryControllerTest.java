package com.shopsphere.inventory.controller;

import com.shopsphere.inventory.dto.InventoryDTO;
import com.shopsphere.inventory.service.InventoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InternalInventoryController.class)
class InternalInventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InventoryService inventoryService;

    @Test
    void getStockForProductReturnsInventory() throws Exception {
        UUID productId = UUID.randomUUID();
        when(inventoryService.getStockLevel(productId))
                .thenReturn(InventoryDTO.builder().productId(productId).quantity(10L).build());

        mockMvc.perform(get("/internal/inventory/{productId}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.product_id").value(productId.toString()))
                .andExpect(jsonPath("$.quantity").value(10));
    }

    @Test
    void batchStockReturnsList() throws Exception {
        UUID p1 = UUID.randomUUID();
        UUID p2 = UUID.randomUUID();

        when(inventoryService.getInventoryForProducts(anyList()))
                .thenReturn(List.of(
                        InventoryDTO.builder().productId(p1).quantity(10L).build(),
                        InventoryDTO.builder().productId(p2).quantity(5L).build()
                ));

        mockMvc.perform(post("/internal/inventory/batch")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("[\"" + p1 + "\",\"" + p2 + "\"]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].product_id").value(p1.toString()))
                .andExpect(jsonPath("$[1].product_id").value(p2.toString()));
    }

    @Test
    void availableReturnsTrue() throws Exception {
        UUID productId = UUID.randomUUID();
        when(inventoryService.hasStock(eq(productId), eq(2L))).thenReturn(true);

        mockMvc.perform(get("/internal/inventory/{productId}/available/{quantity}", productId, 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }
}
