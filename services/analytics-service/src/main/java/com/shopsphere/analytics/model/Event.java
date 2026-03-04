package com.shopsphere.analytics.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String eventType;

    @Column(nullable = false)
    private LocalDateTime eventTimestamp;

    @Column(nullable = false)
    private String userId;

    @Column
    private String productId;

    @Column
    private String orderId;

    @Column
    private String categoryId;

    @Column
    private String sessionId;

    @Column(columnDefinition = "TEXT")
    private String properties;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column
    private String source;

    @Column
    private String version = "1.0";
}
