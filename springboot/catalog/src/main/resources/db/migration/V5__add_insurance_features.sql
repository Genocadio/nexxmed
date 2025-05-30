-- V5__add_insurance_features.sql

-- Create insurance tables
CREATE TABLE insurances (
                            id UUID PRIMARY KEY,
                            name VARCHAR(255) NOT NULL UNIQUE,
                            code VARCHAR(255) NOT NULL UNIQUE,
                            description VARCHAR(1000),
                            status VARCHAR(50) NOT NULL,
                            contact_email VARCHAR(255) NOT NULL,
                            contact_phone VARCHAR(50),
                            address VARCHAR(500),
                            requires_pre_approval BOOLEAN NOT NULL DEFAULT FALSE,
                            default_approval_type VARCHAR(50),
                            default_client_contribution_percentage DECIMAL(5, 2) NOT NULL DEFAULT 0,
                            max_coverage_amount DECIMAL(15, 2),
                            created_at TIMESTAMP NOT NULL,
                            updated_at TIMESTAMP NOT NULL,
                            created_by VARCHAR(255) NOT NULL,
                            updated_by VARCHAR(255) NOT NULL,
                            version INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE product_insurance_coverages (
                                             id UUID PRIMARY KEY,
                                             insurance_id UUID NOT NULL REFERENCES insurances,
                                             product_family_id UUID REFERENCES product_families,
                                             product_variant_id UUID REFERENCES product_variants,
                                             status VARCHAR(50) NOT NULL,
                                             insurance_price DECIMAL(15, 2),
                                             client_contribution_percentage DECIMAL(5, 2) NOT NULL,
                                             insurance_coverage_percentage DECIMAL(5, 2) NOT NULL,
                                             requires_pre_approval BOOLEAN NOT NULL DEFAULT FALSE,
                                             approval_type VARCHAR(50),
                                             max_coverage_amount DECIMAL(15, 2),
                                             min_client_contribution DECIMAL(15, 2),
                                             max_client_contribution DECIMAL(15, 2),
                                             effective_from TIMESTAMP NOT NULL,
                                             effective_to TIMESTAMP,
                                             conditions VARCHAR(1000),
                                             approval_notes VARCHAR(500),
                                             created_at TIMESTAMP NOT NULL,
                                             updated_at TIMESTAMP NOT NULL,
                                             created_by VARCHAR(255) NOT NULL,
                                             updated_by VARCHAR(255) NOT NULL,
                                             version INTEGER NOT NULL DEFAULT 0,
                                             CONSTRAINT unique_coverage_constraint UNIQUE (insurance_id, product_family_id, product_variant_id),
                                             CONSTRAINT product_coverage_check CHECK (
                                                 (product_family_id IS NOT NULL AND product_variant_id IS NULL) OR
                                                 (product_family_id IS NULL AND product_variant_id IS NOT NULL)
                                                 )
);

-- Create indexes for better query performance
CREATE INDEX idx_insurance_status ON insurances (status);
CREATE INDEX idx_insurance_code ON insurances (code);
CREATE INDEX idx_coverage_insurance_id ON product_insurance_coverages (insurance_id);
CREATE INDEX idx_coverage_product_family_id ON product_insurance_coverages (product_family_id);
CREATE INDEX idx_coverage_product_variant_id ON product_insurance_coverages (product_variant_id);
CREATE INDEX idx_coverage_status ON product_insurance_coverages (status);
CREATE INDEX idx_coverage_effective_dates ON product_insurance_coverages (effective_from, effective_to);

-- Insert sample insurance providers
INSERT INTO insurances (
    id, name, code, description, status, contact_email,
    contact_phone, address, requires_pre_approval, default_approval_type,
    default_client_contribution_percentage, max_coverage_amount,
    created_at, updated_at, created_by, updated_by, version
)
VALUES
    ('a1b2c3d4-e5f6-47a8-b9c0-d1e2f3a4b5c6', 'National Health Insurance Fund', 'NHIF',
     'Government health insurance provider offering comprehensive coverage for citizens',
     'ACTIVE', 'info@nhif.gov', '+254-20-2723255', 'NHIF Building, Ragati Road, Nairobi',
     true, 'EMAIL', 20.00, 1000000.00,
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 0),

    ('b2c3d4e5-f6a7-48b9-c0d1-e2f3a4b5c6d7', 'AAR Insurance', 'AAR',
     'Private health insurance provider offering various coverage plans for individuals and businesses',
     'ACTIVE', 'info@aar.co.ke', '+254-20-2895000', 'AAR House, Kiambere Road, Nairobi',
     true, 'PHONE', 30.00, 500000.00,
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 0),

    ('c3d4e5f6-a7b8-49c0-d1e2-f3a4b5c6d7e8', 'Jubilee Insurance', 'JUBILEE',
     'Leading insurance provider offering health, life, and property insurance products',
     'ACTIVE', 'info@jubileeinsurance.com', '+254-20-3281000', 'Jubilee Insurance House, Wabera Street, Nairobi',
     false, 'ONLINE', 25.00, 750000.00,
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 0),

    ('d4e5f6a7-b8c9-4ad1-e2f3-a4b5c6d7e8f9', 'Britam Insurance', 'BRITAM',
     'Comprehensive insurance solutions for health, life, and property protection',
     'ACTIVE', 'info@britam.com', '+254-20-2833000', 'Britam Centre, Mara/Ragati Road, Nairobi',
     true, 'EMAIL', 35.00, 600000.00,
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 0),

    ('e5f6a7b8-c9d0-4be2-f3a4-b5c6d7e8f9a0', 'Madison Insurance', 'MADISON',
     'Insurance provider specializing in health and medical coverage plans',
     'INACTIVE', 'info@madison.co.ke', '+254-20-2864000', 'Madison House, Upper Hill, Nairobi',
     false, 'ONLINE', 40.00, 400000.00,
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 0);

-- Insert sample product insurance coverages
-- For product families
INSERT INTO product_insurance_coverages (
    id, insurance_id, product_family_id, status, client_contribution_percentage,
    insurance_coverage_percentage, requires_pre_approval, approval_type,
    max_coverage_amount, min_client_contribution, max_client_contribution,
    effective_from, conditions, approval_notes,
    created_at, updated_at, created_by, updated_by, version
)
VALUES
    ('f6a7b8c9-d0e1-4cf3-a4b5-c6d7e8f9a0b1', 'a1b2c3d4-e5f6-47a8-b9c0-d1e2f3a4b5c6',
     '4d21a7f9-e5b4-4c12-a8e3-6d7f51b3c4a2', 'ACTIVE', 15.00, 85.00, true, 'EMAIL',
     50000.00, 5000.00, 15000.00, CURRENT_TIMESTAMP,
     'Coverage applies to all Samsung Galaxy S series smartphones. Pre-approval required for devices over 1000 USD.',
     'Requires proof of purchase and device serial number',
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 0),

    ('a7b8c9d0-e1f2-4da4-b5c6-d7e8f9a0b1c2', 'b2c3d4e5-f6a7-48b9-c0d1-e2f3a4b5c6d7',
     '4d21a7f9-e5b4-4c12-a8e3-6d7f51b3c4a2', 'ACTIVE', 25.00, 75.00, false, 'ONLINE',
     45000.00, 7500.00, 20000.00, CURRENT_TIMESTAMP,
     'Coverage includes accidental damage and theft protection for all Samsung Galaxy S phones',
     null,
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 0),

    ('b8c9d0e1-f2a3-4eb5-c6d7-e8f9a0b1c2d3', 'c3d4e5f6-a7b8-49c0-d1e2-f3a4b5c6d7e8',
     '5e32b8a0-f6c5-4d13-b9f4-7e8a62c5d3b4', 'ACTIVE', 30.00, 70.00, true, 'EMAIL',
     100000.00, 15000.00, 35000.00, CURRENT_TIMESTAMP,
     'Comprehensive coverage for all Apple MacBook Pro laptops including extended warranty',
     'Requires Apple Care verification',
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 0);

