package com.example.shipping_service.repository;

import com.example.shipping_service.model.Shipping;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface ShippingRepository extends MongoRepository<Shipping, String> {
    java.util.Optional<Shipping> findByTrackingNumber(String trackingNumber);
}
