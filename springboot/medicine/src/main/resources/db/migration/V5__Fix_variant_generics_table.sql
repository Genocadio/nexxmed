-- Remove the conditional statement and create the table directly
CREATE TABLE variant_generics (
    variant_id UUID NOT NULL REFERENCES variants(id) ON DELETE CASCADE,
    generic_id UUID NOT NULL REFERENCES generics(id) ON DELETE CASCADE,
    PRIMARY KEY (variant_id, generic_id)
);

-- Copy data from the old table
INSERT INTO variant_generics (variant_id, generic_id)
SELECT variant_id, generic_id FROM variant_generic
ON CONFLICT DO NOTHING;

-- Create indexes
CREATE INDEX idx_variant_generics_variant_id ON variant_generics(variant_id);
CREATE INDEX idx_variant_generics_generic_id ON variant_generics(generic_id);

-- Optionally, drop the old table if no longer needed
-- DROP TABLE IF EXISTS variant_generic;