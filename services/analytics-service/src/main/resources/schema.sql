-- Analytics Service Database Schema

-- Events Table
CREATE TABLE IF NOT EXISTS events (
    id BIGSERIAL PRIMARY KEY,
    event_type VARCHAR(50) NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    product_id VARCHAR(255),
    order_id VARCHAR(255),
    category_id VARCHAR(255),
    session_id VARCHAR(255),
    properties JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    source VARCHAR(50),
    version VARCHAR(10) DEFAULT '1.0'
);

CREATE INDEX idx_event_type ON events(event_type);
CREATE INDEX idx_user_id ON events(user_id);
CREATE INDEX idx_timestamp ON events(timestamp);
CREATE INDEX idx_event_type_timestamp ON events(event_type, timestamp);
CREATE INDEX idx_product_id ON events(product_id);
CREATE INDEX idx_order_id ON events(order_id);

-- Sales Metrics Table
CREATE TABLE IF NOT EXISTS sales_metrics (
    id BIGSERIAL PRIMARY KEY,
    metric_date DATE NOT NULL,
    granularity VARCHAR(20) NOT NULL,
    category_id VARCHAR(255),
    product_id VARCHAR(255),
    total_revenue DECIMAL(15, 2) NOT NULL DEFAULT 0,
    total_orders BIGINT NOT NULL DEFAULT 0,
    total_items BIGINT NOT NULL DEFAULT 0,
    average_order_value DECIMAL(15, 2) NOT NULL DEFAULT 0,
    view_count BIGINT NOT NULL DEFAULT 0,
    add_to_cart_count BIGINT NOT NULL DEFAULT 0,
    checkout_count BIGINT NOT NULL DEFAULT 0,
    purchase_count BIGINT NOT NULL DEFAULT 0,
    seller VARCHAR(255),
    processed BOOLEAN DEFAULT FALSE,
    created_at BIGINT NOT NULL,
    updated_at BIGINT
);

CREATE INDEX idx_metric_date ON sales_metrics(metric_date);
CREATE INDEX idx_category_id ON sales_metrics(category_id);
CREATE INDEX idx_product_id ON sales_metrics(product_id);
CREATE INDEX idx_date_range ON sales_metrics(metric_date, category_id);

-- Product Metrics Table
CREATE TABLE IF NOT EXISTS product_metrics (
    id BIGSERIAL PRIMARY KEY,
    product_id VARCHAR(255) NOT NULL,
    category_id VARCHAR(255) NOT NULL,
    metric_date DATE NOT NULL,
    view_count BIGINT NOT NULL DEFAULT 0,
    unique_viewers BIGINT NOT NULL DEFAULT 0,
    add_to_cart_count BIGINT NOT NULL DEFAULT 0,
    purchase_count BIGINT NOT NULL DEFAULT 0,
    revenue DECIMAL(15, 2) NOT NULL DEFAULT 0,
    units_sold BIGINT NOT NULL DEFAULT 0,
    conversion_rate DECIMAL(5, 4) NOT NULL DEFAULT 0,
    avg_rating DECIMAL(3, 2) NOT NULL DEFAULT 0,
    review_count BIGINT NOT NULL DEFAULT 0,
    created_at BIGINT NOT NULL,
    updated_at BIGINT
);

CREATE INDEX idx_pm_product_id ON product_metrics(product_id);
CREATE INDEX idx_pm_metric_date ON product_metrics(metric_date);
CREATE INDEX idx_pm_category_id ON product_metrics(category_id);

-- User Metrics Table
CREATE TABLE IF NOT EXISTS user_metrics (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    metric_date DATE NOT NULL,
    total_purchases BIGINT NOT NULL DEFAULT 0,
    lifetime_value DECIMAL(15, 2) NOT NULL DEFAULT 0,
    last_purchase_day BIGINT NOT NULL DEFAULT 0,
    session_count BIGINT NOT NULL DEFAULT 0,
    page_view_count BIGINT NOT NULL DEFAULT 0,
    active BOOLEAN DEFAULT FALSE,
    segment VARCHAR(50),
    activity_level VARCHAR(50),
    created_at BIGINT NOT NULL,
    updated_at BIGINT,
    registration_date DATE,
    last_activity_date DATE
);

CREATE INDEX idx_um_user_id ON user_metrics(user_id);
CREATE INDEX idx_um_metric_date ON user_metrics(metric_date);
CREATE INDEX idx_um_segment ON user_metrics(segment);

-- User Cohorts Table
CREATE TABLE IF NOT EXISTS user_cohorts (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    cohort_date DATE NOT NULL,
    cohort_age INTEGER NOT NULL,
    active_users BIGINT NOT NULL,
    retained BOOLEAN DEFAULT FALSE,
    created_at BIGINT NOT NULL
);

CREATE INDEX idx_cohort_date ON user_cohorts(cohort_date);
CREATE INDEX idx_user_id_cohort ON user_cohorts(user_id);

-- Search Analytics Table
CREATE TABLE IF NOT EXISTS search_analytics (
    id BIGSERIAL PRIMARY KEY,
    search_term VARCHAR(500) NOT NULL,
    date DATE NOT NULL,
    search_count BIGINT NOT NULL DEFAULT 0,
    result_count BIGINT NOT NULL DEFAULT 0,
    click_count BIGINT NOT NULL DEFAULT 0,
    has_results BOOLEAN DEFAULT TRUE,
    created_at BIGINT NOT NULL
);

CREATE INDEX idx_search_term ON search_analytics(search_term);
CREATE INDEX idx_search_date ON search_analytics(date);

-- Exports Table
CREATE TABLE IF NOT EXISTS exports (
    id BIGSERIAL PRIMARY KEY,
    export_id VARCHAR(255) NOT NULL UNIQUE,
    user_id VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    format VARCHAR(20) NOT NULL,
    status VARCHAR(50) NOT NULL,
    date_from DATE,
    date_to DATE,
    file_path VARCHAR(500),
    error_message TEXT,
    created_at BIGINT NOT NULL,
    completed_at BIGINT,
    filters TEXT
);

CREATE INDEX idx_export_status ON exports(status);
CREATE INDEX idx_export_user ON exports(user_id);
CREATE INDEX idx_export_id ON exports(export_id);
