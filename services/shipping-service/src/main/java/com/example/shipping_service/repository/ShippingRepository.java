package com.shopsphere.shipping.repository;

import com.shopsphere.shipping.model.Shipping;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface ShippingRepository extends MongoRepository<Shipping, String> {
    java.util.Optional<Shipping> findByTrackingNumber(String trackingNumber);
}
