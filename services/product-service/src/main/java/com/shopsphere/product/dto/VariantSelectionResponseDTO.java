package com.shopsphere.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VariantSelectionResponseDTO {
    // Unique attributes for dropdowns: e.g., "Size" -> ["S", "M", "L"], "Color" -> ["Red", "Blue"]
    private Map<String, Set<String>> availableAttributes; 
    
    // The exact list of all combinations and their stock/price details
    private List<VariantCombinationDTO> combinations;
}