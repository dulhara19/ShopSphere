-- Phase 3 - Epic 2.4: Address Management
-- Creates the addresses table with one-to-many relationship to users table

-- =====================================================
-- Create ADDRESSES table
-- =====================================================
CREATE TABLE IF NOT EXISTS addresses (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    street VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    zip_code VARCHAR(20) NOT NULL,
    country VARCHAR(100) NOT NULL,
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_addresses_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create indexes for query optimization
CREATE INDEX idx_user_id ON addresses(user_id);
CREATE INDEX idx_user_id_is_default ON addresses(user_id, is_default);

-- =====================================================
-- Ensure only one default address per user
-- =====================================================
-- Note: This constraint should be enforced at the application level
-- in the AddressService when setting default addresses

