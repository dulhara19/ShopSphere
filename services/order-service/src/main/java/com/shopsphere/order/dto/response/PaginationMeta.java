package com.shopsphere.order.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginationMeta {

    private int page;
    private int limit;
    private long total;
    private int totalPages;

    public static PaginationMeta from(Page<?> page) {
        return PaginationMeta.builder()
            .page(page.getNumber() + 1)
            .limit(page.getSize())
            .total(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .build();
    }
}
