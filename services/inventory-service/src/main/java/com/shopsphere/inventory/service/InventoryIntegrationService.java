package com.shopsphere.inventory.service;

import com.shopsphere.inventory.dto.request.BulkUpdateInventoryRequest;
import com.shopsphere.inventory.dto.request.WebhookRegistrationRequest;
import com.shopsphere.inventory.dto.response.BulkUpdateResponse;
import com.shopsphere.inventory.dto.response.WebhookRegistrationResponse;
import com.shopsphere.inventory.exception.ProductNotFoundException;
import com.shopsphere.inventory.model.Inventory;
import com.shopsphere.inventory.repository.InventoryRepository;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class InventoryIntegrationService {

    private final InventoryService inventoryService;
    private final InventoryRepository inventoryRepository;
    private final WebhookService webhookService;

    @Transactional
    public BulkUpdateResponse importCsv(MultipartFile file) {
        try {
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);
            List<BulkUpdateInventoryRequest> updates = parseCsv(content);
            return inventoryService.bulkUpdateStock(updates);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Failed to import CSV: " + ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public String exportCsv() {
        StringBuilder csv = new StringBuilder();
        csv.append("product_id,quantity,reserved_quantity,available_quantity,low_stock_threshold,status\n");
        for (Inventory inv : inventoryRepository.findAll()) {
            csv.append(inv.getProductId()).append(",")
                    .append(inv.getQuantity()).append(",")
                    .append(inv.getReservedQuantity()).append(",")
                    .append(inv.getAvailableQuantity()).append(",")
                    .append(inv.getLowStockThreshold()).append(",")
                    .append(inv.getStatus()).append("\n");
        }
        return csv.toString();
    }

    @Transactional
    public WebhookRegistrationResponse registerWebhook(WebhookRegistrationRequest request) {
        return webhookService.registerWebhook(request);
    }

    private List<BulkUpdateInventoryRequest> parseCsv(String csvContent) {
        String[] lines = csvContent.split("\\r?\\n");
        if (lines.length < 2) {
            throw new IllegalArgumentException("CSV is empty");
        }

        String[] headers = lines[0].trim().split(",");
        if (headers.length < 2 || !"product_id".equalsIgnoreCase(headers[0].trim())
                || !"quantity".equalsIgnoreCase(headers[1].trim())) {
            throw new IllegalArgumentException("CSV must start with headers: product_id,quantity");
        }

        List<BulkUpdateInventoryRequest> updates = new ArrayList<>();
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) {
                continue;
            }
            String[] cols = line.split(",");
            UUID productId = UUID.fromString(cols[0].trim());
            Long quantity = Long.parseLong(cols[1].trim());
            Long threshold = cols.length > 2 && !cols[2].trim().isEmpty() ? Long.parseLong(cols[2].trim()) : null;

            if (inventoryRepository.findByProductId(productId).isEmpty()) {
                throw new ProductNotFoundException(productId.toString());
            }

            updates.add(BulkUpdateInventoryRequest.builder()
                    .productId(productId)
                    .quantity(quantity)
                    .lowStockThreshold(threshold)
                    .build());
        }
        return updates;
    }
}
