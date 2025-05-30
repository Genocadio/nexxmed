-- V6__update_insurance_product_variants.sql

     -- First ensure the product variant exists (only if it might have been missed)
     INSERT INTO product_variants (id, family_id, name, display_name, sku, upc, brand, unit_of_measure, status, is_limited_edition, created_at, updated_at, created_by, updated_by, version)
     SELECT '6f43c9b1-a7d6-5e14-c0f5-8f9b73d6e4c5', '4d21a7f9-e5b4-4c12-a8e3-6d7f51b3c4a2', 'Galaxy S23', 'Samsung Galaxy S23', 'SM-S911B', '8806094749489', 'Samsung', 'PIECE', 'ACTIVE', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 0
     WHERE NOT EXISTS (SELECT 1 FROM product_variants WHERE id = '6f43c9b1-a7d6-5e14-c0f5-8f9b73d6e4c5');

     -- Then proceed with your insurance coverage insert
     DELETE FROM product_insurance_coverages WHERE id = 'c9d0e1f2-a3b4-4fc6-d7e8-f9a0b1c2d3e4';

     INSERT INTO product_insurance_coverages (
         id, insurance_id, product_variant_id, product_family_id, status, insurance_price,
         client_contribution_percentage, insurance_coverage_percentage, requires_pre_approval, approval_type,
         max_coverage_amount, min_client_contribution, max_client_contribution,
         effective_from, conditions, approval_notes,
         created_at, updated_at, created_by, updated_by, version
     )
     VALUES
         ('c9d0e1f2-a3b4-4fc6-d7e8-f9a0b1c2d3e4', 'a1b2c3d4-e5f6-47a8-b9c0-d1e2f3a4b5c6',
          '6f43c9b1-a7d6-5e14-c0f5-8f9b73d6e4c5', NULL, 'ACTIVE', 800.00, 25.00, 75.00,
          true, 'PHONE', 1500.00, 200.00, 500.00, CURRENT_TIMESTAMP,
          'Standard coverage for Samsung Galaxy S23 with basic warranty extension',
          'Requires proof of purchase',
          CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 0);