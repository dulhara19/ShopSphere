package com.shopsphere.inventory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopsphere.inventory.dto.request.AssignWarehouseInventoryRequest;
import com.shopsphere.inventory.dto.request.CreateWarehouseRequest;
import com.shopsphere.inventory.dto.request.TransferStockRequest;
import com.shopsphere.inventory.dto.request.UpdateWarehouseRequest;
import com.shopsphere.inventory.dto.response.ProductWarehouseStockResponse;
import com.shopsphere.inventory.dto.response.WarehouseResponse;
import com.shopsphere.inventory.service.WarehouseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WarehouseController.class)
class WarehouseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private WarehouseService warehouseService;

    @Test
    void createWarehouseReturnsCreated() throws Exception {
        UUID warehouseId = UUID.randomUUID();
        CreateWarehouseRequest request = CreateWarehouseRequest.builder()
                .name("Central Warehouse")
                .code("WH-001")
                .location("Colombo")
                .address("Address")
                .build();

        when(warehouseService.createWarehouse(any())).thenReturn(
                WarehouseResponse.builder().id(warehouseId).code("WH-001").name("Central Warehouse").build()
        );

        mockMvc.perform(post("/warehouses")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(warehouseId.toString()))
                .andExpect(jsonPath("$.code").value("WH-001"));
    }

    @Test
    void getWarehousesReturnsOk() throws Exception {
        UUID warehouseId = UUID.randomUUID();
        when(warehouseService.getWarehouses()).thenReturn(
                List.of(WarehouseResponse.builder().id(warehouseId).code("WH-001").name("W1").build())
        );

        mockMvc.perform(get("/warehouses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(warehouseId.toString()));
    }

    @Test
    void getWarehouseReturnsOk() throws Exception {
        UUID warehouseId = UUID.randomUUID();
        when(warehouseService.getWarehouse(warehouseId)).thenReturn(
                WarehouseResponse.builder().id(warehouseId).code("WH-001").name("W1").build()
        );

        mockMvc.perform(get("/warehouses/{warehouseId}", warehouseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(warehouseId.toString()));
    }

    @Test
    void updateWarehouseReturnsOk() throws Exception {
        UUID warehouseId = UUID.randomUUID();
        UpdateWarehouseRequest request = UpdateWarehouseRequest.builder()
                .name("Updated")
                .location("Kandy")
                .address("Updated Address")
                .build();

        when(warehouseService.updateWarehouse(any(), any())).thenReturn(
                WarehouseResponse.builder().id(warehouseId).code("WH-001").name("Updated").build()
        );

        mockMvc.perform(put("/warehouses/{warehouseId}", warehouseId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    void assignInventoryReturnsOk() throws Exception {
        UUID productId = UUID.randomUUID();
        UUID warehouseId = UUID.randomUUID();
        AssignWarehouseInventoryRequest request = AssignWarehouseInventoryRequest.builder().quantity(20L).build();

        when(warehouseService.assignStock(any(), any(), anyLong())).thenReturn(
                ProductWarehouseStockResponse.builder().productId(productId).totalQuantity(20L).build()
        );

        mockMvc.perform(post("/inventory/{productId}/warehouses/{warehouseId}", productId, warehouseId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.product_id").value(productId.toString()));
    }

    @Test
    void getWarehouseStockReturnsOk() throws Exception {
        UUID productId = UUID.randomUUID();
        when(warehouseService.getWarehouseStockForProduct(productId)).thenReturn(
                ProductWarehouseStockResponse.builder().productId(productId).totalQuantity(10L).build()
        );

        mockMvc.perform(get("/inventory/{productId}/warehouses", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.product_id").value(productId.toString()));
    }

    @Test
    void transferStockReturnsOk() throws Exception {
        UUID productId = UUID.randomUUID();
        UUID from = UUID.randomUUID();
        UUID to = UUID.randomUUID();
        TransferStockRequest request = TransferStockRequest.builder()
                .productId(productId)
                .fromWarehouseId(from)
                .toWarehouseId(to)
                .quantity(5L)
                .build();

        when(warehouseService.transferStock(any(), any(), any(), anyLong())).thenReturn(
                ProductWarehouseStockResponse.builder().productId(productId).totalQuantity(10L).build()
        );

        mockMvc.perform(post("/inventory/transfer")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.product_id").value(productId.toString()));
    }
}
