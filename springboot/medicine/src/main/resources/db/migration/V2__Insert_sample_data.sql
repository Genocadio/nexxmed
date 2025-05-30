-- Insert sample therapeutic classes
INSERT INTO classes (name, description) VALUES
                                            ('Corticosteroids', 'Anti-inflammatory medications that mimic cortisol'),
                                            ('Antibiotics', 'Medications that fight bacterial infections'),
                                            ('Analgesics', 'Pain relieving medications');

-- Insert sample generics
INSERT INTO generics (name, chemical_name, class_id, description, is_parent)
SELECT
    'Dexamethasone',
    'Dexamethasone',
    c.id,
    'A synthetic corticosteroid with anti-inflammatory properties',
    true
FROM classes c WHERE c.name = 'Corticosteroids';

INSERT INTO generics (name, chemical_name, class_id, description, is_parent)
SELECT
    'Amoxicillin',
    'Amoxicillin trihydrate',
    c.id,
    'A penicillin-type antibiotic',
    true
FROM classes c WHERE c.name = 'Antibiotics';

-- Insert sample variants
INSERT INTO variants (generic_id, name, form, route, strength, packaging, notes)
SELECT
    g.id,
    'Dexamethasone Sodium Phosphate',
    'Injection',
    'IV/IM',
    '4mg/ml',
    'Vial 1ml',
    'For intravenous or intramuscular administration'
FROM generics g WHERE g.name = 'Dexamethasone';

INSERT INTO variants (generic_id, name, form, route, strength, packaging, notes)
SELECT
    g.id,
    'Dexamethasone',
    'Tablet',
    'Oral',
    '0.5mg',
    'Blister pack of 30',
    'For oral administration'
FROM generics g WHERE g.name = 'Dexamethasone';

-- Insert sample brands
INSERT INTO brands (variant_id, brand_name, manufacturer, country)
SELECT
    v.id,
    'Decadron',
    'Merck & Co.',
    'USA'
FROM variants v
         JOIN generics g ON v.generic_id = g.id
WHERE g.name = 'Dexamethasone' AND v.form = 'Injection';

INSERT INTO brands (variant_id, brand_name, manufacturer, country)
SELECT
    v.id,
    'Dexona',
    'Zydus Cadila',
    'India'
FROM variants v
         JOIN generics g ON v.generic_id = g.id
WHERE g.name = 'Dexamethasone' AND v.form = 'Tablet';