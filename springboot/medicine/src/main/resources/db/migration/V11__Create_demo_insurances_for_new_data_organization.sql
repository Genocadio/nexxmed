-- V11__Create_demo_insurances_for_new_data_organization.sql

-- First, update existing insurances table to match entity structure
ALTER TABLE insurances
ADD COLUMN IF NOT EXISTS abbreviation VARCHAR(50),
ADD COLUMN IF NOT EXISTS default_client_contribution_percentage DECIMAL(5, 2),
ADD COLUMN IF NOT EXISTS default_requires_pre_approval BOOLEAN,
ADD COLUMN IF NOT EXISTS active BOOLEAN DEFAULT TRUE,
ADD COLUMN IF NOT EXISTS version INTEGER DEFAULT 0;

-- Rename provider_name to updated_by if it exists and the column doesn't already exist
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns
               WHERE table_name = 'insurances'
               AND column_name = 'provider_name')
       AND NOT EXISTS (SELECT 1 FROM information_schema.columns
                      WHERE table_name = 'insurances'
                      AND column_name = 'updated_by') THEN
        ALTER TABLE insurances RENAME COLUMN provider_name TO updated_by;
    END IF;
END $$;

-- Add missing audit columns if they don't exist
ALTER TABLE insurances
ADD COLUMN IF NOT EXISTS created_by VARCHAR(100) DEFAULT 'system_migration',
ADD COLUMN IF NOT EXISTS updated_by VARCHAR(100) DEFAULT 'system_migration';

-- Create demo insurance data with more detailed information
INSERT INTO insurances (
    id,
    name,
    abbreviation,
    default_client_contribution_percentage,
    default_requires_pre_approval,
    active,
    created_at,
    updated_at,
    created_by,
    updated_by
) VALUES
(uuid_generate_v4(), 'National Health Insurance', 'NHI', 15.00, FALSE, TRUE, NOW(), NOW(), 'system_migration', 'system_migration'),
(uuid_generate_v4(), 'Premium Care Plan', 'PCP', 10.00, TRUE, TRUE, NOW(), NOW(), 'system_migration', 'system_migration'),
(uuid_generate_v4(), 'Medical Coverage Association', 'MCA', 20.00, FALSE, TRUE, NOW(), NOW(), 'system_migration', 'system_migration')
ON CONFLICT (id) DO NOTHING;

-- Update existing medicine_insurance_coverages to reference new insurances where possible
DO $$
DECLARE
    nhi_id UUID;
    pcp_id UUID;
    mca_id UUID;
BEGIN
    -- Get the IDs of the new insurance records
    SELECT id INTO nhi_id FROM insurances WHERE name = 'National Health Insurance' LIMIT 1;
    SELECT id INTO pcp_id FROM insurances WHERE name = 'Premium Care Plan' LIMIT 1;
    SELECT id INTO mca_id FROM insurances WHERE name = 'Medical Coverage Association' LIMIT 1;

    -- Update generic coverages to use NHI
    UPDATE medicine_insurance_coverages
    SET insurance_id = nhi_id,
        insurance_name = 'National Health Insurance - Generic Coverage'
    WHERE generic_id IS NOT NULL
    AND status = 'ACTIVE'
    AND insurance_id IS NOT NULL;

    -- Update brand coverages to use PCP
    UPDATE medicine_insurance_coverages
    SET insurance_id = pcp_id,
        insurance_name = 'Premium Care Plan - Brand Coverage'
    WHERE brand_id IS NOT NULL
    AND status = 'ACTIVE'
    AND insurance_id IS NOT NULL;

    -- Update variant coverages to use MCA
    UPDATE medicine_insurance_coverages
    SET insurance_id = mca_id,
        insurance_name = 'Medical Coverage Association - Variant Coverage'
    WHERE variant_id IS NOT NULL
    AND status = 'ACTIVE'
    AND insurance_id IS NOT NULL;

    -- Update pending approval coverages
    UPDATE medicine_insurance_coverages
    SET insurance_name = CONCAT(insurance_name, ' (Pending Approval)')
    WHERE status = 'PENDING_APPROVAL';
END $$;

-- Add a new constraint to medicine_insurance_coverages to ensure exactly one medicine type is set
-- This is a redundancy as we already have the check in the entity, but it's good to have it at the database level too
ALTER TABLE medicine_insurance_coverages
DROP CONSTRAINT IF EXISTS one_medicine_type_only;

ALTER TABLE medicine_insurance_coverages
ADD CONSTRAINT one_medicine_type_only CHECK (
    (CASE WHEN generic_id IS NOT NULL THEN 1 ELSE 0 END) +
    (CASE WHEN brand_id IS NOT NULL THEN 1 ELSE 0 END) +
    (CASE WHEN variant_id IS NOT NULL THEN 1 ELSE 0 END) = 1
);