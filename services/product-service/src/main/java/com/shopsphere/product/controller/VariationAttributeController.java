package com.shopsphere.product.controller;

import com.shopsphere.product.model.VariationAttribute;
import com.shopsphere.product.repository.VariationAttributeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/attributes")
public class VariationAttributeController {

    @Autowired
    private VariationAttributeRepository attributeRepository;

    /**
     * Story 2.2.1: Create a new global attribute (Admin only)
     */
    @PostMapping
    public ResponseEntity<VariationAttribute> createAttribute(@RequestBody VariationAttribute attribute) {
        if (attributeRepository.existsByNameIgnoreCase(attribute.getName())) {
            throw new RuntimeException("Attribute with this name already exists!");
        }
        VariationAttribute savedAttribute = attributeRepository.save(attribute);
        return ResponseEntity.ok(savedAttribute);
    }

    /**
     * Story 2.2.1: Get all predefined attributes (Used by Sellers when creating products)
     */
    @GetMapping
    public ResponseEntity<List<VariationAttribute>> getAllAttributes() {
        return ResponseEntity.ok(attributeRepository.findAll());
    }

    /**
     * Story 2.2.1: Update an attribute (Add new default values)
     */
    @PutMapping("/{id}")
    public ResponseEntity<VariationAttribute> updateAttribute(
            @PathVariable String id, 
            @RequestBody VariationAttribute updatedAttribute) {
        
        VariationAttribute existing = attributeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Attribute not found"));
        
        existing.setName(updatedAttribute.getName());
        existing.setDescription(updatedAttribute.getDescription());
        existing.setDefaultValues(updatedAttribute.getDefaultValues());
        
        return ResponseEntity.ok(attributeRepository.save(existing));
    }
}