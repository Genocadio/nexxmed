-- V8__Add_insurance_name_column.sql

-- Add insurance_name column to medicine_insurance_coverages table
ALTER TABLE medicine_insurance_coverages
ADD COLUMN insurance_name VARCHAR(255) NOT NULL DEFAULT 'Default Coverage';

-- Update existing records with insurance names from the insurances table
UPDATE medicine_insurance_coverages mic
SET insurance_name = i.name
FROM insurances i
WHERE mic.insurance_id = i.id;

-- Remove the default constraint after populating data
ALTER TABLE medicine_insurance_coverages
ALTER COLUMN insurance_name DROP DEFAULT;