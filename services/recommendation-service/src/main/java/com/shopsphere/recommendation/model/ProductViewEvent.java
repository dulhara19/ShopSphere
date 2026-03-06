package com.shopsphere.recommendation.model;

public class ProductViewEvent {
    private String productId;
    private String userId;
    private long timestamp; // optional, time in milliseconds

    // Constructor
    public ProductViewEvent(String productId, String userId, long timestamp) {
        this.productId = productId;
        this.userId = userId;
        this.timestamp = timestamp;
    }

    // Getters and setters
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
