-- Migration: Add indexes on restaurant entities
-- Purpose: Speed up lookups by name in restaurants table

-- Index to optimize searches by restaurant name
CREATE INDEX idx_restaurant_name ON restaurants(name);

-- Index to speed up queries filtering by city
CREATE INDEX idx_restaurant_city ON restaurants(city);

-- Index to speed up queries filtering by restaurant phone
CREATE INDEX idx_restaurant_phone ON restaurants(phone);

-- Index to improve sorting by creation date
CREATE INDEX idx_restaurant_created_at ON restaurants(created_at);