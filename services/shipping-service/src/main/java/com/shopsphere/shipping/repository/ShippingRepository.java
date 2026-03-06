package com.shopsphere.shipping.repository;

import com.shopsphere.shipping.model.Shipping;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ShippingRepository extends MongoRepository<Shipping, String> {
    Optional<Shipping> findByTrackingNumber(String trackingNumber);
    List<Shipping> findByOrderId(String orderId);
}
