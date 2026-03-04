package com.shopsphere.inventory.controller;

import com.shopsphere.inventory.dto.request.WebhookRegistrationRequest;
import com.shopsphere.inventory.dto.response.BulkUpdateResponse;
import com.shopsphere.inventory.dto.response.WebhookRegistrationResponse;
import com.shopsphere.inventory.service.InventoryIntegrationService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryIntegrationController {

    private final InventoryIntegrationService inventoryIntegrationService;

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BulkUpdateResponse> importCsv(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(inventoryIntegrationService.importCsv(file));
    }

    @GetMapping(value = "/export", produces = "text/csv")
    public ResponseEntity<String> exportCsv() {
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("text/csv"))
                .body(inventoryIntegrationService.exportCsv());
    }

    @PostMapping("/webhooks")
    public ResponseEntity<WebhookRegistrationResponse> registerWebhook(
            @Valid @RequestBody WebhookRegistrationRequest request) {
        return ResponseEntity.ok(inventoryIntegrationService.registerWebhook(request));
    }
}
