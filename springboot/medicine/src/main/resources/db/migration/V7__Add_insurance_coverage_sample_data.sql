-- V7__Add_insurance_coverage_sample_data.sql

-- Create insurance table if it doesn't exist
CREATE TABLE IF NOT EXISTS insurances (
                                          id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(150) NOT NULL,
    provider_name VARCHAR(150) NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
    );

-- Create medicine_insurance_coverages table
CREATE TABLE IF NOT EXISTS medicine_insurance_coverages (
                                                            id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    insurance_id UUID NOT NULL REFERENCES insurances(id),
    generic_id UUID REFERENCES generics(id),
    brand_id UUID REFERENCES brands(id),
    variant_id UUID REFERENCES variants(id),
    status VARCHAR(20) NOT NULL,
    insurance_price DECIMAL(15, 2),
    client_contribution_percentage DECIMAL(5, 2) NOT NULL,
    insurance_coverage_percentage DECIMAL(5, 2) NOT NULL,
    requires_pre_approval BOOLEAN NOT NULL DEFAULT FALSE,
    approval_type VARCHAR(20),
    max_coverage_amount DECIMAL(15, 2),
    min_client_contribution DECIMAL(15, 2),
    max_client_contribution DECIMAL(15, 2),
    effective_from TIMESTAMP NOT NULL,
    effective_to TIMESTAMP,
    conditions VARCHAR(1000),
    approval_notes VARCHAR(500),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    updated_by VARCHAR(100) NOT NULL,
    version INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT unique_medicine_coverage UNIQUE (insurance_id, generic_id, brand_id, variant_id),
    CONSTRAINT one_medicine_type_only CHECK (
(CASE WHEN generic_id IS NOT NULL THEN 1 ELSE 0 END) +
(CASE WHEN brand_id IS NOT NULL THEN 1 ELSE 0 END) +
(CASE WHEN variant_id IS NOT NULL THEN 1 ELSE 0 END) = 1
    )
    );

-- Create sample insurance IDs (in a real system these would reference actual insurance providers)
INSERT INTO insurances (id, name, provider_name) VALUES
                                                     (uuid_generate_v4(), 'Basic Health Plan', 'ABC Insurance'),
                                                     (uuid_generate_v4(), 'Premium Health Cover', 'XYZ Healthcare')
    ON CONFLICT DO NOTHING;

-- Variable to hold insurance IDs for reference
DO $$
DECLARE
basic_insurance_id UUID;
    premium_insurance_id UUID;
    admin_username VARCHAR := 'system_admin';
    timestamp_now TIMESTAMP := NOW()::timestamp without time zone;
    one_year_later TIMESTAMP := (NOW() + INTERVAL '1 year')::timestamp without time zone;
BEGIN
    -- Get the insurance IDs
SELECT id INTO basic_insurance_id FROM insurances WHERE name = 'Basic Health Plan' LIMIT 1;
SELECT id INTO premium_insurance_id FROM insurances WHERE name = 'Premium Health Cover' LIMIT 1;

-- If no insurance found in table, generate UUIDs for this migration
IF basic_insurance_id IS NULL THEN
        basic_insurance_id := uuid_generate_v4();
END IF;

    IF premium_insurance_id IS NULL THEN
        premium_insurance_id := uuid_generate_v4();
END IF;

    -- Insert coverage for Generic medicines
INSERT INTO medicine_insurance_coverages (
    id, insurance_id, generic_id, brand_id, variant_id, status, insurance_price,
    client_contribution_percentage, insurance_coverage_percentage,
    requires_pre_approval, approval_type, max_coverage_amount,
    min_client_contribution, max_client_contribution, effective_from, effective_to,
    conditions, approval_notes, created_at, updated_at, created_by, updated_by, version
)
SELECT
    uuid_generate_v4(),
    basic_insurance_id,
    g.id,
    NULL,
    NULL,
    'ACTIVE',
    NULL,
    20.00,
    80.00,
    FALSE,
    'AUTOMATIC',
    1000.00,
    5.00,
    100.00,
    timestamp_now,
    one_year_later,
    'Standard coverage for generic medications',
    NULL,
    timestamp_now,
    timestamp_now,
    admin_username,
    admin_username,
    0
FROM generics g;

-- Insert coverage for Brand medicines
INSERT INTO medicine_insurance_coverages (
    id, insurance_id, generic_id, brand_id, variant_id, status, insurance_price,
    client_contribution_percentage, insurance_coverage_percentage,
    requires_pre_approval, approval_type, max_coverage_amount,
    min_client_contribution, max_client_contribution, effective_from, effective_to,
    conditions, approval_notes, created_at, updated_at, created_by, updated_by, version
)
SELECT
    uuid_generate_v4(),
    premium_insurance_id,
    NULL,
    b.id,
    NULL,
    'ACTIVE',
    NULL,
    30.00,
    70.00,
    TRUE,
    'MANUAL_APPROVAL',
    2000.00,
    10.00,
    200.00,
    timestamp_now,
    one_year_later,
    'Coverage for brand medications requires approval for claims over $500',
    'Submit approval request through online portal',
    timestamp_now,
    timestamp_now,
    admin_username,
    admin_username,
    0
FROM brands b;

-- Insert coverage for Variant medicines
INSERT INTO medicine_insurance_coverages (
    id, insurance_id, generic_id, brand_id, variant_id, status, insurance_price,
    client_contribution_percentage, insurance_coverage_percentage,
    requires_pre_approval, approval_type, max_coverage_amount,
    min_client_contribution, max_client_contribution, effective_from, effective_to,
    conditions, approval_notes, created_at, updated_at, created_by, updated_by, version
)
SELECT
    uuid_generate_v4(),
    premium_insurance_id,
    NULL,
    NULL,
    v.id,
    'ACTIVE',
    NULL,
    15.00,
    85.00,
    FALSE,
    'CONDITIONAL',
    3000.00,
    0.00,
    150.00,
    timestamp_now,
    one_year_later,
    'Enhanced coverage for specific variants subject to medical necessity',
    NULL,
    timestamp_now,
    timestamp_now,
    admin_username,
    admin_username,
    0
FROM variants v;

-- Add some pending approvals for certain variants (injectable forms)
INSERT INTO medicine_insurance_coverages (
    id, insurance_id, generic_id, brand_id, variant_id, status, insurance_price,
    client_contribution_percentage, insurance_coverage_percentage,
    requires_pre_approval, approval_type, max_coverage_amount,
    min_client_contribution, max_client_contribution, effective_from, effective_to,
    conditions, approval_notes, created_at, updated_at, created_by, updated_by, version
)
SELECT
    uuid_generate_v4(),
    basic_insurance_id,
    NULL,
    NULL,
    v.id,
    'PENDING_APPROVAL',
    NULL,
    10.00,
    90.00,
    TRUE,
    'PRE_AUTHORIZATION',
    5000.00,
    0.00,
    200.00,
    timestamp_now,
    one_year_later,
    'Special coverage for injectable medications pending review',
    'Requires medical documentation and pre-authorization code',
    timestamp_now,
    timestamp_now,
    admin_username,
    admin_username,
    0
FROM variants v
WHERE v.form = 'Injection';
END $$;

-- Add indexes to improve query performance
CREATE INDEX IF NOT EXISTS idx_med_ins_cov_insurance_id ON medicine_insurance_coverages(insurance_id);
CREATE INDEX IF NOT EXISTS idx_med_ins_cov_generic_id ON medicine_insurance_coverages(generic_id);
CREATE INDEX IF NOT EXISTS idx_med_ins_cov_brand_id ON medicine_insurance_coverages(brand_id);
CREATE INDEX IF NOT EXISTS idx_med_ins_cov_variant_id ON medicine_insurance_coverages(variant_id);
CREATE INDEX IF NOT EXISTS idx_med_ins_cov_status ON medicine_insurance_coverages(status);