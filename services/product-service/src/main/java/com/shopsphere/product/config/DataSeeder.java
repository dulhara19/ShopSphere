package com.shopsphere.product.config;

import com.shopsphere.product.model.Category;
import com.shopsphere.product.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    @Override
    public void run(String... args) {
        if (categoryRepository.count() > 0) {
            log.info("Categories already exist, skipping seed");
            return;
        }

        List<Category> categories = List.of(
                Category.builder().name("Electronics").description("Electronic devices and gadgets").build(),
                Category.builder().name("Clothing").description("Apparel and fashion items").build(),
                Category.builder().name("Home & Kitchen").description("Home appliances and kitchen essentials").build(),
                Category.builder().name("Books").description("Books, eBooks and audiobooks").build(),
                Category.builder().name("Sports & Outdoors").description("Sports equipment and outdoor gear").build(),
                Category.builder().name("Beauty & Health").description("Beauty products and health care").build(),
                Category.builder().name("Toys & Games").description("Toys, games and puzzles").build(),
                Category.builder().name("Automotive").description("Vehicle parts and accessories").build(),
                Category.builder().name("Grocery").description("Food, beverages and household supplies").build(),
                Category.builder().name("Furniture").description("Home and office furniture").build()
        );

        categoryRepository.saveAll(categories);
        log.info("Seeded {} default categories", categories.size());
    }
}
