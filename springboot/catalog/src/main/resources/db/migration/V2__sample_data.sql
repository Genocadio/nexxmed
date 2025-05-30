-- Insert sample attribute definitions
INSERT INTO attribute_definitions (id, key, label, data_type, unit, is_required, is_searchable, is_filterable, display_order)
VALUES
    ('791290c0-7b93-4261-ba51-d3b9a033e8c3', 'color', 'Color', 'STRING', NULL, true, true, true, 1),
    ('8f1cbfaf-7072-4cd2-95e2-8a67c315a8c7', 'size', 'Size', 'STRING', NULL, true, true, true, 2),
    ('c8a2a70b-4c79-4fb9-94a0-b106e7551ce2', 'weight', 'Weight', 'NUMBER', 'kg', true, false, true, 3),
    ('fd156474-7b1a-4428-9aab-36d2271ad02f', 'material', 'Material', 'STRING', NULL, false, true, true, 4);

-- Insert sample allowed values for attributes
INSERT INTO attribute_allowed_values (attribute_definition_id, allowed_values)
VALUES
    ('791290c0-7b93-4261-ba51-d3b9a033e8c3', 'Red'),
    ('791290c0-7b93-4261-ba51-d3b9a033e8c3', 'Blue'),
    ('791290c0-7b93-4261-ba51-d3b9a033e8c3', 'Green'),
    ('791290c0-7b93-4261-ba51-d3b9a033e8c3', 'Black'),
    ('791290c0-7b93-4261-ba51-d3b9a033e8c3', 'White'),
    ('8f1cbfaf-7072-4cd2-95e2-8a67c315a8c7', 'S'),
    ('8f1cbfaf-7072-4cd2-95e2-8a67c315a8c7', 'M'),
    ('8f1cbfaf-7072-4cd2-95e2-8a67c315a8c7', 'L'),
    ('8f1cbfaf-7072-4cd2-95e2-8a67c315a8c7', 'XL'),
    ('fd156474-7b1a-4428-9aab-36d2271ad02f', 'Cotton'),
    ('fd156474-7b1a-4428-9aab-36d2271ad02f', 'Polyester'),
    ('fd156474-7b1a-4428-9aab-36d2271ad02f', 'Leather'),
    ('fd156474-7b1a-4428-9aab-36d2271ad02f', 'Wool');

-- Insert sample localized labels for attributes
INSERT INTO attribute_localized_labels (attribute_definition_id, locale, localized_label)
VALUES
    ('791290c0-7b93-4261-ba51-d3b9a033e8c3', 'es', 'Color'),
    ('791290c0-7b93-4261-ba51-d3b9a033e8c3', 'fr', 'Couleur'),
    ('8f1cbfaf-7072-4cd2-95e2-8a67c315a8c7', 'es', 'Tamaño'),
    ('8f1cbfaf-7072-4cd2-95e2-8a67c315a8c7', 'fr', 'Taille'),
    ('c8a2a70b-4c79-4fb9-94a0-b106e7551ce2', 'es', 'Peso'),
    ('c8a2a70b-4c79-4fb9-94a0-b106e7551ce2', 'fr', 'Poids'),
    ('fd156474-7b1a-4428-9aab-36d2271ad02f', 'es', 'Material'),
    ('fd156474-7b1a-4428-9aab-36d2271ad02f', 'fr', 'Matériel');