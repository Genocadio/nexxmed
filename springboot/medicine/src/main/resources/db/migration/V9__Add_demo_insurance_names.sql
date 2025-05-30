-- V9__Add_demo_insurance_names.sql

-- Update generics insurance coverage names to be more descriptive
UPDATE medicine_insurance_coverages mic
SET insurance_name = CONCAT(i.name, ' - Generic Coverage')
FROM insurances i, generics g
WHERE mic.insurance_id = i.id
  AND mic.generic_id = g.id;

-- Update brand insurance coverage names to be more descriptive
UPDATE medicine_insurance_coverages mic
SET insurance_name = CONCAT(i.name, ' - Brand Coverage')
FROM insurances i, brands b
WHERE mic.insurance_id = i.id
  AND mic.brand_id = b.id;

-- Update variant insurance coverage names to be more descriptive
UPDATE medicine_insurance_coverages mic
SET insurance_name = CONCAT(i.name, ' - Variant Coverage')
FROM insurances i, variants v
WHERE mic.insurance_id = i.id
  AND mic.variant_id = v.id;

-- Add specific note for pending approvals
UPDATE medicine_insurance_coverages
SET insurance_name = CONCAT(insurance_name, ' (Pending Approval)')
WHERE status = 'PENDING_APPROVAL';