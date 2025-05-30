-- V3__Update_variant_generic_relationship.sql

-- Drop existing index on the old column
DROP INDEX IF EXISTS idx_variants_generic_id;

-- Create junction table for many-to-many relationship
CREATE TABLE variant_generic (
    variant_id UUID NOT NULL REFERENCES variants(id) ON DELETE CASCADE,
    generic_id UUID NOT NULL REFERENCES generics(id) ON DELETE CASCADE,
    PRIMARY KEY (variant_id, generic_id)
);

-- Create indexes for better performance
CREATE INDEX idx_variant_generic_variant_id ON variant_generic(variant_id);
CREATE INDEX idx_variant_generic_generic_id ON variant_generic(generic_id);

-- Migrate existing data to the junction table
INSERT INTO variant_generic (variant_id, generic_id)
SELECT id, generic_id FROM variants
WHERE generic_id IS NOT NULL;

-- Alter variants table to remove the one-to-many relationship column
-- We're keeping it temporarily to avoid immediate errors until all code is updated
ALTER TABLE variants RENAME COLUMN generic_id TO old_generic_id;

-- Finally, after migration and code update is complete, run:
-- ALTER TABLE variants DROP COLUMN old_generic_id;