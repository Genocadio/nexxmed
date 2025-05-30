-- V4__Fix_variant_generic_table_name.sql

-- Create the correctly named table if it doesn't exist
CREATE TABLE IF NOT EXISTS variant_generics (
    variant_id UUID NOT NULL REFERENCES variants(id) ON DELETE CASCADE,
    generic_id UUID NOT NULL REFERENCES generics(id) ON DELETE CASCADE,
    PRIMARY KEY (variant_id, generic_id)
);

-- Copy data from the old table if it exists
INSERT INTO variant_generics (variant_id, generic_id)
SELECT variant_id, generic_id FROM variant_generic
ON CONFLICT DO NOTHING;

-- Create indexes
CREATE INDEX IF NOT EXISTS idx_variant_generics_variant_id ON variant_generics(variant_id);
CREATE INDEX IF NOT EXISTS idx_variant_generics_generic_id ON variant_generics(generic_id);