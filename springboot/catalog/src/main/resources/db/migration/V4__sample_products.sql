-- Insert sample categories
INSERT INTO categories (id, name, code, parent_id, level, display_order, is_active, description)
VALUES
    ('18f2f9c5-e8d1-4a2c-9f0e-3edc2654c3a1', 'Electronics', 'ELECT', NULL, 0, 1, true, 'Electronic devices and accessories'),
    ('29e3d8a7-b7f2-4e11-8c5d-4af7c48d1b5f', 'Smartphones', 'PHONE', '18f2f9c5-e8d1-4a2c-9f0e-3edc2654c3a1', 1, 1, true, 'Mobile phones and smartphones'),
    ('3bc4d9e8-f6c3-4a10-b9e2-5df6e74a2c1b', 'Laptops', 'LAPTOPS', '18f2f9c5-e8d1-4a2c-9f0e-3edc2654c3a1', 1, 2, true, 'Portable computers');

-- Insert sample product families
INSERT INTO product_families (id, name, description, short_description, category_id, status, lifecycle_stage, brand, search_keywords, age_restricted, created_at, updated_at, created_by, updated_by, version)
VALUES
    ('4d21a7f9-e5b4-4c12-a8e3-6d7f51b3c4a2', 'Galaxy S Series', 'Samsung Galaxy S series smartphones', 'Premium Samsung smartphones', '29e3d8a7-b7f2-4e11-8c5d-4af7c48d1b5f', 'ACTIVE', 'GROWING', 'Samsung', 'samsung, galaxy, smartphone, android, mobile', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 0),
    ('5e32b8a0-f6c5-4d13-b9f4-7e8a62c5d3b4', 'MacBook Pro', 'Apple MacBook Pro laptops', 'Premium Apple laptops', '3bc4d9e8-f6c3-4a10-b9e2-5df6e74a2c1b', 'ACTIVE', 'MATURE', 'Apple', 'apple, macbook, laptop, pro, macOS', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 0);

-- Insert sample product variants
INSERT INTO product_variants (id, family_id, name, display_name, sku, upc, brand, unit_of_measure, status, is_limited_edition, created_at, updated_at, created_by, updated_by, version)
VALUES
    ('6f43c9b1-a7d6-5e14-c0f5-8f9b73d6e4c5', '4d21a7f9-e5b4-4c12-a8e3-6d7f51b3c4a2', 'Galaxy S23', 'Samsung Galaxy S23', 'SM-S911B', '8806094749489', 'Samsung', 'PIECE', 'ACTIVE', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 0),
    ('7a54d0c2-b8e7-6f25-d1a6-9a0c84e7f5d6', '4d21a7f9-e5b4-4c12-a8e3-6d7f51b3c4a2', 'Galaxy S23 Ultra', 'Samsung Galaxy S23 Ultra', 'SM-S918B', '8806094749496', 'Samsung', 'PIECE', 'ACTIVE', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 0),
    ('8b65e1d3-c9f8-7a36-e2b7-0b1d95f8a6e7', '5e32b8a0-f6c5-4d13-b9f4-7e8a62c5d3b4', 'MacBook Pro 14-inch', 'Apple MacBook Pro 14-inch M2 Pro', 'MPHE3LL/A', '194253317036', 'Apple', 'PIECE', 'ACTIVE', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 0),
    ('9c76f2e4-d0a9-8b47-f3c8-1c2e06a9b7f8', '5e32b8a0-f6c5-4d13-b9f4-7e8a62c5d3b4', 'MacBook Pro 16-inch', 'Apple MacBook Pro 16-inch M2 Pro', 'MNW93LL/A', '194253317043', 'Apple', 'PIECE', 'ACTIVE', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 0);

-- Add variant attributes
INSERT INTO product_variant_attributes (product_variant_id, attribute_key, attribute_value)
VALUES
    ('6f43c9b1-a7d6-5e14-c0f5-8f9b73d6e4c5', 'color', 'Black'),
    ('6f43c9b1-a7d6-5e14-c0f5-8f9b73d6e4c5', 'storage', '128GB'),
    ('6f43c9b1-a7d6-5e14-c0f5-8f9b73d6e4c5', 'ram', '8GB'),
    ('7a54d0c2-b8e7-6f25-d1a6-9a0c84e7f5d6', 'color', 'Black'),
    ('7a54d0c2-b8e7-6f25-d1a6-9a0c84e7f5d6', 'storage', '256GB'),
    ('7a54d0c2-b8e7-6f25-d1a6-9a0c84e7f5d6', 'ram', '12GB'),
    ('8b65e1d3-c9f8-7a36-e2b7-0b1d95f8a6e7', 'color', 'Space Gray'),
    ('8b65e1d3-c9f8-7a36-e2b7-0b1d95f8a6e7', 'processor', 'M2 Pro'),
    ('8b65e1d3-c9f8-7a36-e2b7-0b1d95f8a6e7', 'storage', '512GB'),
    ('8b65e1d3-c9f8-7a36-e2b7-0b1d95f8a6e7', 'ram', '16GB'),
    ('9c76f2e4-d0a9-8b47-f3c8-1c2e06a9b7f8', 'color', 'Space Gray'),
    ('9c76f2e4-d0a9-8b47-f3c8-1c2e06a9b7f8', 'processor', 'M2 Pro'),
    ('9c76f2e4-d0a9-8b47-f3c8-1c2e06a9b7f8', 'storage', '1TB'),
    ('9c76f2e4-d0a9-8b47-f3c8-1c2e06a9b7f8', 'ram', '32GB');

-- Add product images
INSERT INTO product_images (id, url, alt_text, position, is_primary, product_variant_id)
VALUES
    ('ad87e3f5-e1b0-9c48-a4d9-2e3f1a0b9c8d', 'https://example.com/images/galaxy-s23-black.jpg', 'Samsung Galaxy S23 Black Front View', 1, true, '6f43c9b1-a7d6-5e14-c0f5-8f9b73d6e4c5'),
    ('be98f4a6-f2c1-0d59-b5e0-3f4a2b1c0d9e', 'https://example.com/images/galaxy-s23-ultra-black.jpg', 'Samsung Galaxy S23 Ultra Black Front View', 1, true, '7a54d0c2-b8e7-6f25-d1a6-9a0c84e7f5d6'),
    ('cf09a5b7-a3d2-1e60-c6f1-4a5b3c2d1e0f', 'https://example.com/images/macbook-pro-14.jpg', 'Apple MacBook Pro 14" Front View', 1, true, '8b65e1d3-c9f8-7a36-e2b7-0b1d95f8a6e7'),
    ('da10b6c8-b4e3-2f71-d7a2-5b6c4d3e2f1a', 'https://example.com/images/macbook-pro-16.jpg', 'Apple MacBook Pro 16" Front View', 1, true, '9c76f2e4-d0a9-8b47-f3c8-1c2e06a9b7f8');