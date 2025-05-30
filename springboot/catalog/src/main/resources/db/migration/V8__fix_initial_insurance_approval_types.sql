-- V8__fix_initial_insurance_approval_types.sql

-- Update approval_type values in insurances table
UPDATE insurances
SET default_approval_type = 'MANUAL_APPROVAL'
WHERE default_approval_type = 'EMAIL';

UPDATE insurances
SET default_approval_type = 'MANUAL_APPROVAL'
WHERE default_approval_type = 'PHONE';

UPDATE insurances
SET default_approval_type = 'AUTOMATIC'
WHERE default_approval_type = 'ONLINE';

-- Update approval_type values in product_insurance_coverages table
UPDATE product_insurance_coverages
SET approval_type = 'MANUAL_APPROVAL'
WHERE approval_type = 'EMAIL';

UPDATE product_insurance_coverages
SET approval_type = 'MANUAL_APPROVAL'
WHERE approval_type = 'PHONE';

UPDATE product_insurance_coverages
SET approval_type = 'AUTOMATIC'
WHERE approval_type = 'ONLINE';

-- Make sure all approval_type values are valid in both tables
UPDATE insurances
SET default_approval_type = 'MANUAL_APPROVAL'
WHERE default_approval_type NOT IN ('AUTOMATIC', 'MANUAL_APPROVAL', 'CONDITIONAL', 'PRE_AUTHORIZATION');

UPDATE product_insurance_coverages
SET approval_type = 'MANUAL_APPROVAL'
WHERE approval_type NOT IN ('AUTOMATIC', 'MANUAL_APPROVAL', 'CONDITIONAL', 'PRE_AUTHORIZATION');