-- V10__Remove_insurance_foreign_key_constraint.sql

-- 1. Remove the foreign key constraint
ALTER TABLE medicine_insurance_coverages
DROP CONSTRAINT medicine_insurance_coverages_insurance_id_fkey;

-- 2. Modify insurance_id column to keep it required but without foreign key
ALTER TABLE medicine_insurance_coverages
ALTER COLUMN insurance_id SET NOT NULL;

-- 3. Add insurance_name column if it doesn't already exist
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                  WHERE table_name = 'medicine_insurance_coverages'
                  AND column_name = 'insurance_name') THEN
        ALTER TABLE medicine_insurance_coverages
        ADD COLUMN insurance_name VARCHAR(255);

        -- Update existing records to have an insurance name
        UPDATE medicine_insurance_coverages mic
        SET insurance_name = i.name
        FROM insurances i
        WHERE mic.insurance_id = i.id;

        -- Now make the column not nullable
        ALTER TABLE medicine_insurance_coverages
        ALTER COLUMN insurance_name SET NOT NULL;
    END IF;
END $$;

-- 4. Update entity constraints to reflect the changes
ALTER TABLE medicine_insurance_coverages
DROP CONSTRAINT IF EXISTS unique_medicine_coverage,
ADD CONSTRAINT unique_medicine_coverage
UNIQUE (insurance_id, generic_id, brand_id, variant_id);