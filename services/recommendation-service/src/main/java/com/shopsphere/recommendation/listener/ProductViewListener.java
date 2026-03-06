package com.shopsphere.recommendation.listener;

import com.shopsphere.recommendation.config.RabbitMQConfig;
import com.shopsphere.recommendation.service.RecentlyViewedService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.shopsphere.recommendation.model.ProductViewEvent;

@Component
public class ProductViewListener {

    private final RecentlyViewedService recentlyViewedService;

    public ProductViewListener(RecentlyViewedService recentlyViewedService) {
        this.recentlyViewedService = recentlyViewedService;
    }

    @RabbitListener(queues = RabbitMQConfig.PRODUCT_VIEW_QUEUE)
    public void handleProductViewEvent(ProductViewEvent event) {
        recentlyViewedService.addRecentlyViewed(event.getUserId(), event.getProductId());
        System.out.println("Received PRODUCT_VIEW event: " + event);
    }
}
