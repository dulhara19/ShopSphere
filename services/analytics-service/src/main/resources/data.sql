-- Analytics Service Seed Data
-- Populates sales_metrics, product_metrics, and user_metrics with 30 days of sample data

-- Guard: only insert if tables are empty
DO $$
BEGIN
  IF (SELECT COUNT(*) FROM sales_metrics) > 0 THEN
    RAISE NOTICE 'Seed data already exists, skipping';
    RETURN;
  END IF;

  -- ============================================================
  -- SALES METRICS (daily granularity, last 60 days for comparison)
  -- ============================================================
  INSERT INTO sales_metrics (metric_date, granularity, category_id, product_id, total_revenue, total_orders, total_items, average_order_value, view_count, add_to_cart_count, checkout_count, purchase_count, seller, processed, created_at)
  SELECT
    d::date,
    'day',
    NULL,
    NULL,
    ROUND((800 + random() * 1200)::numeric, 2),                          -- revenue 800-2000
    (15 + floor(random() * 25))::bigint,                                  -- orders 15-40
    (25 + floor(random() * 50))::bigint,                                  -- items 25-75
    ROUND((30 + random() * 40)::numeric, 2),                             -- aov 30-70
    (200 + floor(random() * 600))::bigint,                                -- views 200-800
    (40 + floor(random() * 80))::bigint,                                  -- add-to-cart 40-120
    (20 + floor(random() * 40))::bigint,                                  -- checkout 20-60
    (15 + floor(random() * 25))::bigint,                                  -- purchase 15-40
    NULL,
    TRUE,
    EXTRACT(EPOCH FROM NOW())::bigint * 1000
  FROM generate_series(CURRENT_DATE - INTERVAL '60 days', CURRENT_DATE, '1 day') AS d;

  -- ============================================================
  -- PRODUCT METRICS (10 products, last 30 days)
  -- ============================================================
  INSERT INTO product_metrics (product_id, category_id, metric_date, view_count, unique_viewers, add_to_cart_count, purchase_count, revenue, units_sold, conversion_rate, avg_rating, review_count, created_at)
  SELECT
    'product-' || p.id,
    'category-' || ((p.id % 3) + 1),
    d::date,
    (50 + floor(random() * 200))::bigint,
    (30 + floor(random() * 120))::bigint,
    (10 + floor(random() * 30))::bigint,
    (3 + floor(random() * 15))::bigint,
    ROUND((100 + random() * 500)::numeric, 2),
    (3 + floor(random() * 15))::bigint,
    ROUND((0.02 + random() * 0.08)::numeric, 4),
    ROUND((3.5 + random() * 1.5)::numeric, 2),
    (floor(random() * 5))::bigint,
    EXTRACT(EPOCH FROM NOW())::bigint * 1000
  FROM generate_series(1, 10) AS p(id),
       generate_series(CURRENT_DATE - INTERVAL '30 days', CURRENT_DATE, '1 day') AS d;

  -- Add product names via update (product_metrics doesn't have product_name column,
  -- but ProductMetricDTO.productName is populated from the entity)
  -- The ProductMetric entity needs a product_name column — let's add it inline

  -- ============================================================
  -- USER METRICS (20 users, spread across the last 30 days)
  -- ============================================================
  INSERT INTO user_metrics (user_id, metric_date, total_purchases, lifetime_value, last_purchase_day, session_count, page_view_count, active, segment, activity_level, created_at, registration_date, last_activity_date)
  SELECT
    'user-' || u.id,
    CURRENT_DATE,
    (1 + floor(random() * 20))::bigint,
    ROUND((50 + random() * 500)::numeric, 2),
    EXTRACT(EPOCH FROM (CURRENT_DATE - (floor(random() * 10))::int))::bigint,
    (5 + floor(random() * 30))::bigint,
    (20 + floor(random() * 100))::bigint,
    TRUE,
    CASE (u.id % 4)
      WHEN 0 THEN 'new'
      WHEN 1 THEN 'active'
      WHEN 2 THEN 'at_risk'
      WHEN 3 THEN 'loyal'
    END,
    CASE
      WHEN random() > 0.3 THEN 'high'
      ELSE 'medium'
    END,
    EXTRACT(EPOCH FROM NOW())::bigint * 1000,
    CURRENT_DATE - (floor(random() * 30))::int,
    CURRENT_DATE - (floor(random() * 5))::int
  FROM generate_series(1, 20) AS u(id);

  -- Also add some users with registration_date in the current month (for newUsers count)
  INSERT INTO user_metrics (user_id, metric_date, total_purchases, lifetime_value, last_purchase_day, session_count, page_view_count, active, segment, activity_level, created_at, registration_date, last_activity_date)
  SELECT
    'new-user-' || u.id,
    CURRENT_DATE,
    (0 + floor(random() * 3))::bigint,
    ROUND((10 + random() * 100)::numeric, 2),
    EXTRACT(EPOCH FROM CURRENT_DATE)::bigint,
    (1 + floor(random() * 10))::bigint,
    (5 + floor(random() * 30))::bigint,
    TRUE,
    'new',
    'low',
    EXTRACT(EPOCH FROM NOW())::bigint * 1000,
    CURRENT_DATE - (floor(random() * 7))::int,
    CURRENT_DATE
  FROM generate_series(1, 8) AS u(id);

  RAISE NOTICE 'Analytics seed data inserted successfully';
END $$;
