-- V7__fix_approval_type_values.sql

-- Update the incorrect approval_type value in product_insurance_coverages
UPDATE product_insurance_coverages
SET approval_type = 'MANUAL_APPROVAL'
WHERE id = 'c9d0e1f2-a3b4-4fc6-d7e8-f9a0b1c2d3e4' AND approval_type = 'PHONE';

-- Make sure all approval_type values are valid
UPDATE product_insurance_coverages
SET approval_type = 'MANUAL_APPROVAL'
WHERE approval_type NOT IN ('AUTOMATIC', 'MANUAL_APPROVAL', 'CONDITIONAL', 'PRE_AUTHORIZATION');