#!/bin/bash
# ShopSphere Seed Data Script
# Run on VM: bash scripts/seed-data.sh

set -e

echo "=== Seeding ShopSphere Data ==="

# ============================================================
# 1. SEED CATEGORIES INTO MONGODB
# ============================================================
echo ""
echo "--- Seeding Categories ---"

docker compose exec -T shopsphere-mongo mongosh productdb --quiet --eval '
db.categories.deleteMany({});
db.categories.insertMany([
  {
    _id: ObjectId("aaa000000000000000000001"),
    name: "Electronics",
    description: "Gadgets, devices, and tech accessories",
    image: "https://images.unsplash.com/photo-1498049794561-7780e7231661?w=400",
    parentId: null,
    createdAt: new Date(),
    updatedAt: new Date()
  },
  {
    _id: ObjectId("aaa000000000000000000002"),
    name: "Clothing",
    description: "Apparel and fashion for all",
    image: "https://images.unsplash.com/photo-1445205170230-053b83016050?w=400",
    parentId: null,
    createdAt: new Date(),
    updatedAt: new Date()
  },
  {
    _id: ObjectId("aaa000000000000000000003"),
    name: "Home & Garden",
    description: "Everything for your home and outdoor spaces",
    image: "https://images.unsplash.com/photo-1484154218962-a197022b5858?w=400",
    parentId: null,
    createdAt: new Date(),
    updatedAt: new Date()
  },
  {
    _id: ObjectId("aaa000000000000000000004"),
    name: "Sports & Outdoors",
    description: "Gear for active lifestyles",
    image: "https://images.unsplash.com/photo-1461896836934-bd45ba8fcf9b?w=400",
    parentId: null,
    createdAt: new Date(),
    updatedAt: new Date()
  },
  {
    _id: ObjectId("aaa000000000000000000005"),
    name: "Books",
    description: "Fiction, non-fiction, and educational materials",
    image: "https://images.unsplash.com/photo-1512820790803-83ca734da794?w=400",
    parentId: null,
    createdAt: new Date(),
    updatedAt: new Date()
  }
]);
print("Inserted " + db.categories.countDocuments() + " categories");
'

# ============================================================
# 2. SEED PRODUCTS INTO MONGODB
# ============================================================
echo ""
echo "--- Seeding Products ---"

docker compose exec -T shopsphere-mongo mongosh productdb --quiet --eval '
db.products.deleteMany({});
db.products.insertMany([
  {
    _id: ObjectId("bbb000000000000000000001"),
    sellerId: "seller-001",
    name: "Premium Wireless Headphones",
    description: "High-fidelity Bluetooth 5.3 headphones with active noise cancellation, 40-hour battery life, and premium memory foam ear cushions. Perfect for music lovers and professionals.",
    price: NumberDecimal("299.99"),
    compareAtPrice: NumberDecimal("399.99"),
    categoryId: "aaa000000000000000000001",
    images: [
      "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600",
      "https://images.unsplash.com/photo-1484704849700-f032a568e944?w=600"
    ],
    primaryImage: "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600",
    status: "ACTIVE",
    sku: "WH-PRO-001",
    brand: "AudioMax",
    deleted: false,
    createdAt: new Date(),
    updatedAt: new Date()
  },
  {
    _id: ObjectId("bbb000000000000000000002"),
    sellerId: "seller-001",
    name: "Minimalist Leather Watch",
    description: "Elegant Swiss-movement watch with genuine Italian leather strap. Water resistant to 50m. Sapphire crystal glass with anti-reflective coating.",
    price: NumberDecimal("149.99"),
    compareAtPrice: NumberDecimal("199.99"),
    categoryId: "aaa000000000000000000001",
    images: [
      "https://images.unsplash.com/photo-1524592094714-0f0654e20314?w=600",
      "https://images.unsplash.com/photo-1522312346375-d1a52e2b99b3?w=600"
    ],
    primaryImage: "https://images.unsplash.com/photo-1524592094714-0f0654e20314?w=600",
    status: "ACTIVE",
    sku: "WCH-MIN-002",
    brand: "TimeKeeper",
    deleted: false,
    createdAt: new Date(),
    updatedAt: new Date()
  },
  {
    _id: ObjectId("bbb000000000000000000003"),
    sellerId: "seller-002",
    name: "Running Shoes Pro",
    description: "Lightweight performance running shoes with responsive cushioning and breathable mesh upper. Ideal for marathon training and daily runs.",
    price: NumberDecimal("189.99"),
    compareAtPrice: NumberDecimal("229.99"),
    categoryId: "aaa000000000000000000004",
    images: [
      "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600",
      "https://images.unsplash.com/photo-1460353581641-37baddab0fa2?w=600"
    ],
    primaryImage: "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600",
    status: "ACTIVE",
    sku: "SH-RUN-003",
    brand: "SpeedStride",
    deleted: false,
    createdAt: new Date(),
    updatedAt: new Date()
  },
  {
    _id: ObjectId("bbb000000000000000000004"),
    sellerId: "seller-002",
    name: "Organic Cotton T-Shirt",
    description: "Ultra-soft 100% organic cotton t-shirt. Pre-shrunk, tagless comfort. Available in multiple colors. Ethically manufactured.",
    price: NumberDecimal("39.99"),
    compareAtPrice: null,
    categoryId: "aaa000000000000000000002",
    images: [
      "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=600",
      "https://images.unsplash.com/photo-1583743814966-8936f5b7be1a?w=600"
    ],
    primaryImage: "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=600",
    status: "ACTIVE",
    sku: "TS-ORG-004",
    brand: "EcoWear",
    deleted: false,
    createdAt: new Date(),
    updatedAt: new Date()
  },
  {
    _id: ObjectId("bbb000000000000000000005"),
    sellerId: "seller-001",
    name: "Smart Home Hub",
    description: "Central smart home controller with voice assistant, touchscreen display, and compatibility with 500+ smart devices. WiFi 6 and Zigbee support.",
    price: NumberDecimal("129.99"),
    compareAtPrice: NumberDecimal("179.99"),
    categoryId: "aaa000000000000000000001",
    images: [
      "https://images.unsplash.com/photo-1558089687-f282ffcbc126?w=600",
      "https://images.unsplash.com/photo-1556228578-0d85b1a4d571?w=600"
    ],
    primaryImage: "https://images.unsplash.com/photo-1558089687-f282ffcbc126?w=600",
    status: "ACTIVE",
    sku: "SH-HUB-005",
    brand: "SmartLive",
    deleted: false,
    createdAt: new Date(),
    updatedAt: new Date()
  },
  {
    _id: ObjectId("bbb000000000000000000006"),
    sellerId: "seller-003",
    name: "Premium Coffee Maker",
    description: "Professional-grade drip coffee maker with built-in grinder, programmable timer, thermal carafe, and 12-cup capacity. Brew restaurant-quality coffee at home.",
    price: NumberDecimal("249.99"),
    compareAtPrice: NumberDecimal("329.99"),
    categoryId: "aaa000000000000000000003",
    images: [
      "https://images.unsplash.com/photo-1517668808822-9ebb02f2a0e6?w=600",
      "https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?w=600"
    ],
    primaryImage: "https://images.unsplash.com/photo-1517668808822-9ebb02f2a0e6?w=600",
    status: "ACTIVE",
    sku: "CF-PRO-006",
    brand: "BrewMaster",
    deleted: false,
    createdAt: new Date(),
    updatedAt: new Date()
  },
  {
    _id: ObjectId("bbb000000000000000000007"),
    sellerId: "seller-003",
    name: "Yoga Mat Premium",
    description: "Extra thick 6mm non-slip yoga mat with alignment lines. Made from eco-friendly TPE material. Includes carrying strap.",
    price: NumberDecimal("59.99"),
    compareAtPrice: NumberDecimal("79.99"),
    categoryId: "aaa000000000000000000004",
    images: [
      "https://images.unsplash.com/photo-1601925260368-ae2f83cf8b7f?w=600",
      "https://images.unsplash.com/photo-1544367567-0f2fcb009e0b?w=600"
    ],
    primaryImage: "https://images.unsplash.com/photo-1601925260368-ae2f83cf8b7f?w=600",
    status: "ACTIVE",
    sku: "YG-MAT-007",
    brand: "ZenFit",
    deleted: false,
    createdAt: new Date(),
    updatedAt: new Date()
  },
  {
    _id: ObjectId("bbb000000000000000000008"),
    sellerId: "seller-001",
    name: "Mechanical Keyboard RGB",
    description: "Hot-swappable mechanical keyboard with per-key RGB lighting, PBT keycaps, and USB-C connection. Cherry MX compatible switches.",
    price: NumberDecimal("119.99"),
    compareAtPrice: NumberDecimal("159.99"),
    categoryId: "aaa000000000000000000001",
    images: [
      "https://images.unsplash.com/photo-1511467687858-23d96c32e4ae?w=600",
      "https://images.unsplash.com/photo-1618384887929-16ec33fab9ef?w=600"
    ],
    primaryImage: "https://images.unsplash.com/photo-1511467687858-23d96c32e4ae?w=600",
    status: "ACTIVE",
    sku: "KB-RGB-008",
    brand: "TypeForce",
    deleted: false,
    createdAt: new Date(),
    updatedAt: new Date()
  },
  {
    _id: ObjectId("bbb000000000000000000009"),
    sellerId: "seller-002",
    name: "Bestselling Novel Collection",
    description: "Curated collection of 5 bestselling novels from award-winning authors. Hardcover edition with premium binding.",
    price: NumberDecimal("69.99"),
    compareAtPrice: NumberDecimal("99.99"),
    categoryId: "aaa000000000000000000005",
    images: [
      "https://images.unsplash.com/photo-1512820790803-83ca734da794?w=600",
      "https://images.unsplash.com/photo-1524578271613-d550eacf6090?w=600"
    ],
    primaryImage: "https://images.unsplash.com/photo-1512820790803-83ca734da794?w=600",
    status: "ACTIVE",
    sku: "BK-NOV-009",
    brand: "ReadMore",
    deleted: false,
    createdAt: new Date(),
    updatedAt: new Date()
  },
  {
    _id: ObjectId("bbb000000000000000000010"),
    sellerId: "seller-003",
    name: "Wireless Charging Pad",
    description: "15W fast wireless charger compatible with all Qi-enabled devices. Slim design with LED indicator and foreign object detection.",
    price: NumberDecimal("34.99"),
    compareAtPrice: NumberDecimal("49.99"),
    categoryId: "aaa000000000000000000001",
    images: [
      "https://images.unsplash.com/photo-1586953208448-b95a79798f07?w=600"
    ],
    primaryImage: "https://images.unsplash.com/photo-1586953208448-b95a79798f07?w=600",
    status: "ACTIVE",
    sku: "CH-WIR-010",
    brand: "ChargePro",
    deleted: false,
    createdAt: new Date(),
    updatedAt: new Date()
  }
]);
print("Inserted " + db.products.countDocuments() + " products");
'

# ============================================================
# 3. SEED REVIEWS INTO MONGODB
# ============================================================
echo ""
echo "--- Seeding Reviews ---"

docker compose exec -T shopsphere-mongo mongosh reviewdb --quiet --eval '
db.reviews.deleteMany({});
db.reviews.insertMany([
  {
    productId: "bbb000000000000000000001",
    userId: "6f8cb88d-cf97-4583-8895-9a3f96e9762d",
    userName: "Admin User",
    rating: 5,
    title: "Best headphones I have ever owned!",
    comment: "The noise cancellation is incredible and the battery lasts forever. Highly recommend for daily use.",
    verified: true,
    createdAt: new Date("2026-02-15"),
    updatedAt: new Date("2026-02-15")
  },
  {
    productId: "bbb000000000000000000001",
    userId: "user-002",
    userName: "Jane Smith",
    rating: 4,
    title: "Great sound, slightly heavy",
    comment: "Sound quality is top notch. Only minor complaint is they feel a bit heavy after 3+ hours.",
    verified: true,
    createdAt: new Date("2026-02-20"),
    updatedAt: new Date("2026-02-20")
  },
  {
    productId: "bbb000000000000000000002",
    userId: "user-003",
    userName: "Mike Johnson",
    rating: 5,
    title: "Elegant and reliable",
    comment: "Beautiful watch that goes with everything. The leather strap is very comfortable.",
    verified: true,
    createdAt: new Date("2026-02-18"),
    updatedAt: new Date("2026-02-18")
  },
  {
    productId: "bbb000000000000000000003",
    userId: "user-002",
    userName: "Jane Smith",
    rating: 5,
    title: "Perfect for marathon training",
    comment: "These shoes are incredibly light and the cushioning is perfect for long runs.",
    verified: true,
    createdAt: new Date("2026-03-01"),
    updatedAt: new Date("2026-03-01")
  },
  {
    productId: "bbb000000000000000000004",
    userId: "6f8cb88d-cf97-4583-8895-9a3f96e9762d",
    userName: "Admin User",
    rating: 4,
    title: "Soft and comfortable",
    comment: "Great quality cotton, very soft. Fits true to size.",
    verified: true,
    createdAt: new Date("2026-02-25"),
    updatedAt: new Date("2026-02-25")
  },
  {
    productId: "bbb000000000000000000005",
    userId: "user-003",
    userName: "Mike Johnson",
    rating: 4,
    title: "Works great with my smart home",
    comment: "Easy setup and works with all my existing devices. Voice control is responsive.",
    verified: true,
    createdAt: new Date("2026-03-02"),
    updatedAt: new Date("2026-03-02")
  },
  {
    productId: "bbb000000000000000000006",
    userId: "user-002",
    userName: "Jane Smith",
    rating: 5,
    title: "Coffee shop quality at home",
    comment: "The built-in grinder makes all the difference. Best coffee maker I have owned.",
    verified: true,
    createdAt: new Date("2026-02-28"),
    updatedAt: new Date("2026-02-28")
  },
  {
    productId: "bbb000000000000000000008",
    userId: "6f8cb88d-cf97-4583-8895-9a3f96e9762d",
    userName: "Admin User",
    rating: 5,
    title: "Excellent typing experience",
    comment: "The RGB is beautiful and the hot-swap feature makes it easy to customize switches.",
    verified: true,
    createdAt: new Date("2026-03-03"),
    updatedAt: new Date("2026-03-03")
  }
]);
print("Inserted " + db.reviews.countDocuments() + " reviews");
'

# ============================================================
# 4. SEED ORDERS INTO POSTGRESQL
# ============================================================
echo ""
echo "--- Seeding Orders ---"

docker compose exec -T shopsphere-postgres psql -U postgres -d shopsphere_orders --quiet <<'EOSQL'

-- Clear existing data
DELETE FROM order_status_history;
DELETE FROM order_items;
DELETE FROM orders;

-- Insert sample orders
INSERT INTO orders (
  id, order_number, user_id, status,
  shipping_full_name, shipping_address_line1, shipping_city, shipping_state, shipping_postal_code, shipping_country, shipping_phone,
  billing_full_name, billing_address_line1, billing_city, billing_state, billing_postal_code, billing_country, billing_phone,
  subtotal, tax_amount, shipping_amount, discount_amount, total_amount,
  payment_status, payment_method, tracking_number, created_at, updated_at
) VALUES
(
  'a0000000-0000-0000-0000-000000000001', 'ORD-20260215-A1B2C3D4',
  '6f8cb88d-cf97-4583-8895-9a3f96e9762d', 'DELIVERED',
  'Admin User', '123 Main Street', 'New York', 'NY', '10001', 'US', '+1-555-0100',
  'Admin User', '123 Main Street', 'New York', 'NY', '10001', 'US', '+1-555-0100',
  449.98, 36.00, 0.00, 0.00, 485.98,
  'COMPLETED', 'CARD', 'FX-9876543210',
  '2026-02-15 10:30:00', '2026-02-20 14:15:00'
),
(
  'a0000000-0000-0000-0000-000000000002', 'ORD-20260301-E5F6G7H8',
  '6f8cb88d-cf97-4583-8895-9a3f96e9762d', 'SHIPPED',
  'Admin User', '123 Main Street', 'New York', 'NY', '10001', 'US', '+1-555-0100',
  'Admin User', '123 Main Street', 'New York', 'NY', '10001', 'US', '+1-555-0100',
  189.99, 15.20, 5.99, 0.00, 211.18,
  'COMPLETED', 'CARD', 'UPS-1234567890',
  '2026-03-01 09:00:00', '2026-03-04 11:30:00'
),
(
  'a0000000-0000-0000-0000-000000000003', 'ORD-20260305-I9J0K1L2',
  '6f8cb88d-cf97-4583-8895-9a3f96e9762d', 'PROCESSING',
  'Admin User', '456 Oak Avenue', 'Los Angeles', 'CA', '90001', 'US', '+1-555-0200',
  'Admin User', '456 Oak Avenue', 'Los Angeles', 'CA', '90001', 'US', '+1-555-0200',
  379.98, 30.40, 0.00, 38.00, 372.38,
  'COMPLETED', 'CARD', NULL,
  '2026-03-05 14:20:00', '2026-03-06 08:00:00'
);

-- Insert order items
INSERT INTO order_items (id, order_id, product_id, product_name, product_image, quantity, unit_price, total_price) VALUES
-- Order 1 items
('b0000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000001',
 'bbb000000000000000000001', 'Premium Wireless Headphones',
 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600',
 1, 299.99, 299.99),
('b0000000-0000-0000-0000-000000000002', 'a0000000-0000-0000-0000-000000000001',
 'bbb000000000000000000002', 'Minimalist Leather Watch',
 'https://images.unsplash.com/photo-1524592094714-0f0654e20314?w=600',
 1, 149.99, 149.99),

-- Order 2 items
('b0000000-0000-0000-0000-000000000003', 'a0000000-0000-0000-0000-000000000002',
 'bbb000000000000000000003', 'Running Shoes Pro',
 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600',
 1, 189.99, 189.99),

-- Order 3 items
('b0000000-0000-0000-0000-000000000004', 'a0000000-0000-0000-0000-000000000003',
 'bbb000000000000000000006', 'Premium Coffee Maker',
 'https://images.unsplash.com/photo-1517668808822-9ebb02f2a0e6?w=600',
 1, 249.99, 249.99),
('b0000000-0000-0000-0000-000000000005', 'a0000000-0000-0000-0000-000000000003',
 'bbb000000000000000000005', 'Smart Home Hub',
 'https://images.unsplash.com/photo-1558089687-f282ffcbc126?w=600',
 1, 129.99, 129.99);

-- Insert order status history
INSERT INTO order_status_history (id, order_id, status, timestamp, note, updated_by) VALUES
-- Order 1 history (DELIVERED)
('c0000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000001', 'PENDING', '2026-02-15 10:30:00', 'Order placed', 'SYSTEM'),
('c0000000-0000-0000-0000-000000000002', 'a0000000-0000-0000-0000-000000000001', 'CONFIRMED', '2026-02-15 10:45:00', 'Payment verified', 'SYSTEM'),
('c0000000-0000-0000-0000-000000000003', 'a0000000-0000-0000-0000-000000000001', 'PROCESSING', '2026-02-16 08:00:00', 'Order being prepared', 'ADMIN'),
('c0000000-0000-0000-0000-000000000004', 'a0000000-0000-0000-0000-000000000001', 'SHIPPED', '2026-02-17 14:00:00', 'Shipped via FedEx', 'ADMIN'),
('c0000000-0000-0000-0000-000000000005', 'a0000000-0000-0000-0000-000000000001', 'DELIVERED', '2026-02-20 14:15:00', 'Delivered to front door', 'SYSTEM'),

-- Order 2 history (SHIPPED)
('c0000000-0000-0000-0000-000000000006', 'a0000000-0000-0000-0000-000000000002', 'PENDING', '2026-03-01 09:00:00', 'Order placed', 'SYSTEM'),
('c0000000-0000-0000-0000-000000000007', 'a0000000-0000-0000-0000-000000000002', 'CONFIRMED', '2026-03-01 09:15:00', 'Payment verified', 'SYSTEM'),
('c0000000-0000-0000-0000-000000000008', 'a0000000-0000-0000-0000-000000000002', 'PROCESSING', '2026-03-02 10:00:00', 'Preparing shipment', 'ADMIN'),
('c0000000-0000-0000-0000-000000000009', 'a0000000-0000-0000-0000-000000000002', 'SHIPPED', '2026-03-04 11:30:00', 'Shipped via UPS', 'ADMIN'),

-- Order 3 history (PROCESSING)
('c0000000-0000-0000-0000-000000000010', 'a0000000-0000-0000-0000-000000000003', 'PENDING', '2026-03-05 14:20:00', 'Order placed', 'SYSTEM'),
('c0000000-0000-0000-0000-000000000011', 'a0000000-0000-0000-0000-000000000003', 'CONFIRMED', '2026-03-05 14:35:00', 'Payment verified', 'SYSTEM'),
('c0000000-0000-0000-0000-000000000012', 'a0000000-0000-0000-0000-000000000003', 'PROCESSING', '2026-03-06 08:00:00', 'Order being prepared', 'ADMIN');

SELECT 'Inserted ' || count(*) || ' orders' FROM orders;
SELECT 'Inserted ' || count(*) || ' order items' FROM order_items;
SELECT 'Inserted ' || count(*) || ' status history entries' FROM order_status_history;

EOSQL

# ============================================================
# 5. SEED INVENTORY INTO POSTGRESQL
# ============================================================
echo ""
echo "--- Seeding Inventory ---"

docker compose exec -T shopsphere-postgres psql -U postgres -d shopsphere_inventory --quiet <<'EOSQL'

-- Check if inventory table exists
DO $$
BEGIN
  IF EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'inventory' OR table_name = 'inventory_items') THEN
    RAISE NOTICE 'Inventory table found, seeding...';
  ELSE
    RAISE NOTICE 'No inventory table found - skipping (inventory service may use different schema)';
  END IF;
END $$;

EOSQL

echo ""
echo "=== Seed Data Complete ==="
echo ""
echo "Summary:"
echo "  - 5 Categories (Electronics, Clothing, Home & Garden, Sports, Books)"
echo "  - 10 Products with images and descriptions"
echo "  - 8 Reviews across multiple products"
echo "  - 3 Orders (DELIVERED, SHIPPED, PROCESSING) with items and status history"
echo ""
echo "You can now browse products, view orders, and test the admin dashboard."
