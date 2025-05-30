-- Add the missing trade_name column to variants table
ALTER TABLE variants ADD COLUMN trade_name VARCHAR(150);