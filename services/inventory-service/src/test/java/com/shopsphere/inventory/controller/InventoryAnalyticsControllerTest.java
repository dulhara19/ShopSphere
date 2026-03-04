package com.shopsphere.inventory.controller;

import com.shopsphere.inventory.dto.response.AnalyticsTurnoverResponse;
import com.shopsphere.inventory.dto.response.DaysRemainingResponse;
import com.shopsphere.inventory.dto.response.DeadStockResponse;
import com.shopsphere.inventory.dto.response.RestockRecommendationResponse;
import com.shopsphere.inventory.dto.response.ValuationResponse;
import com.shopsphere.inventory.service.InventoryAnalyticsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InventoryAnalyticsController.class)
class InventoryAnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InventoryAnalyticsService inventoryAnalyticsService;

    @Test
    void turnoverReturnsOk() throws Exception {
        UUID productId = UUID.randomUUID();
        when(inventoryAnalyticsService.getTurnover()).thenReturn(List.of(
                AnalyticsTurnoverResponse.builder().productId(productId).turnoverRate(1.2).build()
        ));

        mockMvc.perform(get("/inventory/analytics/turnover"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].product_id").value(productId.toString()));
    }

    @Test
    void daysRemainingReturnsOk() throws Exception {
        UUID productId = UUID.randomUUID();
        when(inventoryAnalyticsService.getDaysRemaining()).thenReturn(List.of(
                DaysRemainingResponse.builder().productId(productId).daysRemaining(10.0).build()
        ));

        mockMvc.perform(get("/inventory/analytics/days-remaining"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].product_id").value(productId.toString()));
    }

    @Test
    void deadStockReturnsOk() throws Exception {
        UUID productId = UUID.randomUUID();
        when(inventoryAnalyticsService.getDeadStock(anyInt())).thenReturn(List.of(
                DeadStockResponse.builder().productId(productId).quantity(3L).build()
        ));

        mockMvc.perform(get("/inventory/analytics/dead-stock").param("thresholdDays", "30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].product_id").value(productId.toString()));
    }

    @Test
    void valuationReturnsOk() throws Exception {
        UUID productId = UUID.randomUUID();
        when(inventoryAnalyticsService.getValuation(anyDouble())).thenReturn(List.of(
                ValuationResponse.builder().productId(productId).value(500.0).build()
        ));

        mockMvc.perform(get("/inventory/analytics/valuation").param("unitCost", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].product_id").value(productId.toString()));
    }

    @Test
    void restockRecommendationReturnsOk() throws Exception {
        UUID productId = UUID.randomUUID();
        when(inventoryAnalyticsService.getRestockRecommendation(productId)).thenReturn(
                RestockRecommendationResponse.builder().productId(productId).recommendedReorderQuantity(20L).build()
        );

        mockMvc.perform(get("/inventory/{productId}/restock-recommendation", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.product_id").value(productId.toString()));
    }

    @Test
    void forecastsReturnsOk() throws Exception {
        UUID productId = UUID.randomUUID();
        when(inventoryAnalyticsService.getForecasts()).thenReturn(List.of(
                RestockRecommendationResponse.builder().productId(productId).recommendedReorderQuantity(10L).build()
        ));

        mockMvc.perform(get("/inventory/forecasts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].product_id").value(productId.toString()));
    }
}
