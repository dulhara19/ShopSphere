package com.shopsphere.inventory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopsphere.inventory.dto.request.WebhookRegistrationRequest;
import com.shopsphere.inventory.dto.response.BulkUpdateResponse;
import com.shopsphere.inventory.dto.response.WebhookRegistrationResponse;
import com.shopsphere.inventory.service.InventoryIntegrationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InventoryIntegrationController.class)
class InventoryIntegrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private InventoryIntegrationService inventoryIntegrationService;

    @Test
    void importCsvReturnsOk() throws Exception {
        when(inventoryIntegrationService.importCsv(any())).thenReturn(BulkUpdateResponse.builder().total(1).build());

        MockMultipartFile file = new MockMultipartFile(
                "file", "inventory.csv", "text/csv", "product_id,quantity\n1,2".getBytes()
        );

        mockMvc.perform(multipart("/inventory/import").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1));
    }

    @Test
    void exportCsvReturnsTextCsv() throws Exception {
        when(inventoryIntegrationService.exportCsv()).thenReturn("product_id,quantity\n1,2");

        mockMvc.perform(get("/inventory/export"))
                .andExpect(status().isOk())
                .andExpect(content().string("product_id,quantity\n1,2"));
    }

    @Test
    void registerWebhookReturnsOk() throws Exception {
        WebhookRegistrationRequest request = WebhookRegistrationRequest.builder()
                .url("https://example.com/webhook")
                .eventType("inventory.updated")
                .build();

        when(inventoryIntegrationService.registerWebhook(any())).thenReturn(
                WebhookRegistrationResponse.builder()
                        .id(java.util.UUID.randomUUID())
                        .url("https://example.com/webhook")
                        .eventType("inventory.updated")
                        .active(true)
                        .build()
        );

        mockMvc.perform(post("/inventory/webhooks")
                        .contentType(APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.event_type").value("inventory.updated"));
    }
}
