package com.shopsphere.analytics.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "search_analytics", indexes = {
    @Index(name = "idx_search_term", columnList = "search_term"),
    @Index(name = "idx_search_date", columnList = "date")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchAnalytic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String searchTerm;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    @Builder.Default
    private Long searchCount = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Long resultCount = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Long clickCount = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Boolean hasResults = true;

    @Column(nullable = false)
    @Builder.Default
    private Long createdAt = System.currentTimeMillis();
}
