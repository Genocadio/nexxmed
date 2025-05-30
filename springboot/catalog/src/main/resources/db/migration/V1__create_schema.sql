-- Create base tables
CREATE TABLE attribute_definitions (
                                       id UUID PRIMARY KEY,
                                       key VARCHAR(255) NOT NULL UNIQUE,
                                       label VARCHAR(255) NOT NULL,
                                       data_type VARCHAR(50) NOT NULL,
                                       unit VARCHAR(255),
                                       is_required BOOLEAN NOT NULL DEFAULT FALSE,
                                       is_searchable BOOLEAN NOT NULL DEFAULT FALSE,
                                       is_filterable BOOLEAN NOT NULL DEFAULT FALSE,
                                       display_order INTEGER NOT NULL
);

CREATE TABLE attribute_allowed_values (
                                          attribute_definition_id UUID REFERENCES attribute_definitions,
                                          allowed_values VARCHAR(255),
                                          PRIMARY KEY (attribute_definition_id, allowed_values)
);

CREATE TABLE attribute_localized_labels (
                                            attribute_definition_id UUID REFERENCES attribute_definitions,
                                            locale VARCHAR(10),
                                            localized_label VARCHAR(255),
                                            PRIMARY KEY (attribute_definition_id, locale)
);

CREATE TABLE categories (
                            id UUID PRIMARY KEY,
                            name VARCHAR(255) NOT NULL,
                            code VARCHAR(255) NOT NULL UNIQUE,
                            parent_id UUID REFERENCES categories,
                            level INTEGER NOT NULL,
                            display_order INTEGER NOT NULL,
                            is_active BOOLEAN NOT NULL DEFAULT TRUE,
                            icon VARCHAR(255),
                            description TEXT,
                            tax_category VARCHAR(255),
                            regulatory_category VARCHAR(255)
);

CREATE TABLE category_localized_names (
                                          category_id UUID REFERENCES categories,
                                          locale VARCHAR(10),
                                          localized_name VARCHAR(255),
                                          PRIMARY KEY (category_id, locale)
);

CREATE TABLE category_metadata (
                                   category_id UUID REFERENCES categories,
                                   metadata_key VARCHAR(255),
                                   metadata_value VARCHAR(255),
                                   PRIMARY KEY (category_id, metadata_key)
);

CREATE TABLE packaging_info (
                                id UUID PRIMARY KEY,
                                type VARCHAR(255) NOT NULL,
                                quantity INTEGER NOT NULL,
                                unit_type VARCHAR(255) NOT NULL,
                                description TEXT,
                                is_recyclable BOOLEAN,
                                is_biodegradable BOOLEAN,
                                carbon_footprint VARCHAR(255),
                                certifications JSONB
);

CREATE TABLE packaging_materials (
                                     packaging_info_id UUID REFERENCES packaging_info,
                                     packaging_materials VARCHAR(255),
                                     PRIMARY KEY (packaging_info_id, packaging_materials)
);

CREATE TABLE product_families (
                                  id UUID PRIMARY KEY,
                                  name VARCHAR(255) NOT NULL,
                                  description VARCHAR(1000),
                                  short_description VARCHAR(500),
                                  category_id UUID NOT NULL REFERENCES categories,
                                  status VARCHAR(50) NOT NULL,
                                  launch_date TIMESTAMP,
                                  discontinue_date TIMESTAMP,
                                  lifecycle_stage VARCHAR(50) NOT NULL,
                                  brand VARCHAR(255),
                                  search_keywords VARCHAR(1000),
                                  age_restricted BOOLEAN NOT NULL DEFAULT FALSE,
                                  hazard_class VARCHAR(50),
                                  created_at TIMESTAMP NOT NULL,
                                  updated_at TIMESTAMP NOT NULL,
                                  created_by VARCHAR(255) NOT NULL,
                                  updated_by VARCHAR(255) NOT NULL,
                                  version INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE product_family_subcategories (
                                              product_family_id UUID REFERENCES product_families,
                                              subcategories_id UUID REFERENCES categories,
                                              PRIMARY KEY (product_family_id, subcategories_id)
);

CREATE TABLE product_family_tags (
                                     product_family_id UUID REFERENCES product_families,
                                     tags VARCHAR(255),
                                     PRIMARY KEY (product_family_id, tags)
);

CREATE TABLE product_family_certifications (
                                               product_family_id UUID REFERENCES product_families,
                                               certifications VARCHAR(255),
                                               PRIMARY KEY (product_family_id, certifications)
);

CREATE TABLE product_family_attributes (
                                           product_family_id UUID REFERENCES product_families,
                                           attributes_schema_id UUID REFERENCES attribute_definitions,
                                           PRIMARY KEY (product_family_id, attributes_schema_id)
);

CREATE TABLE product_family_localized_content (
                                                  product_family_id UUID REFERENCES product_families,
                                                  locale VARCHAR(10),
                                                  name VARCHAR(255),
                                                  description VARCHAR(2000),
                                                  short_description VARCHAR(500),
                                                  search_keywords VARCHAR(1000),
                                                  meta_description VARCHAR(500),
                                                  specifications JSONB,
                                                  warnings JSONB,
                                                  instructions JSONB,
                                                  PRIMARY KEY (product_family_id, locale)
);

CREATE TABLE product_images (
                                id UUID PRIMARY KEY,
                                url VARCHAR(255) NOT NULL,
                                label VARCHAR(255),
                                alt_text VARCHAR(255) NOT NULL,
                                position INTEGER NOT NULL,
                                is_primary BOOLEAN NOT NULL DEFAULT FALSE,
                                image_type VARCHAR(50),
                                width INTEGER,
                                height INTEGER,
                                file_size BIGINT,
                                format VARCHAR(50),
                                copyright_info VARCHAR(255),
                                product_family_id UUID REFERENCES product_families,
                                product_variant_id UUID
);

CREATE TABLE image_usage_rights (
                                    product_image_id UUID REFERENCES product_images,
                                    usage_rights VARCHAR(255),
                                    PRIMARY KEY (product_image_id, usage_rights)
);

CREATE TABLE product_documents (
                                   id UUID PRIMARY KEY,
                                   url VARCHAR(255) NOT NULL,
                                   title VARCHAR(255) NOT NULL,
                                   document_type VARCHAR(50) NOT NULL,
                                   file_format VARCHAR(50) NOT NULL,
                                   file_size BIGINT,
                                   language VARCHAR(50),
                                   version VARCHAR(50),
                                   upload_date TIMESTAMP NOT NULL,
                                   is_public BOOLEAN NOT NULL DEFAULT TRUE,
                                   access_level VARCHAR(50),
                                   product_family_id UUID REFERENCES product_families,
                                   product_variant_id UUID
);

CREATE TABLE product_videos (
                                id UUID PRIMARY KEY,
                                url VARCHAR(255) NOT NULL,
                                title VARCHAR(255),
                                description TEXT,
                                duration_seconds INTEGER,
                                thumbnail_url VARCHAR(255),
                                format VARCHAR(50),
                                file_size BIGINT,
                                product_family_id UUID REFERENCES product_families,
                                product_variant_id UUID
);

CREATE TABLE product_variants (
                                  id UUID PRIMARY KEY,
                                  family_id UUID NOT NULL REFERENCES product_families,
                                  name VARCHAR(255) NOT NULL,
                                  display_name VARCHAR(255),
                                  sku VARCHAR(255) NOT NULL UNIQUE,
                                  upc VARCHAR(255),
                                  gtin VARCHAR(255),
                                  brand VARCHAR(255) NOT NULL,
                                  manufacturer VARCHAR(255),
                                  manufacturer_part_number VARCHAR(255),
                                  country_of_origin VARCHAR(100),
                                  color VARCHAR(100),
                                  dimension_unit VARCHAR(10),
                                  length DECIMAL,
                                  width DECIMAL,
                                  height DECIMAL,
                                  weight_unit VARCHAR(10),
                                  value DECIMAL,
                                  unit_of_measure VARCHAR(50) NOT NULL,
                                  units_per_package INTEGER,
                                  status VARCHAR(50) NOT NULL,
                                  is_limited_edition BOOLEAN NOT NULL DEFAULT FALSE,
                                  search_keywords VARCHAR(1000),
                                  seo_url VARCHAR(255),
                                  meta_description VARCHAR(500),
                                  average_rating DECIMAL,
                                  review_count INTEGER,
                                  packaging_id UUID REFERENCES packaging_info,
                                  created_at TIMESTAMP NOT NULL,
                                  updated_at TIMESTAMP NOT NULL,
                                  created_by VARCHAR(255) NOT NULL,
                                  updated_by VARCHAR(255) NOT NULL,
                                  version INTEGER NOT NULL DEFAULT 0
);

-- Add the foreign key constraint now that product_variants exists
ALTER TABLE product_images ADD CONSTRAINT fk_product_image_variant FOREIGN KEY (product_variant_id) REFERENCES product_variants;
ALTER TABLE product_documents ADD CONSTRAINT fk_product_document_variant FOREIGN KEY (product_variant_id) REFERENCES product_variants;
ALTER TABLE product_videos ADD CONSTRAINT fk_product_video_variant FOREIGN KEY (product_variant_id) REFERENCES product_variants;

CREATE TABLE product_variant_barcodes (
                                          product_variant_id UUID REFERENCES product_variants,
                                          barcodes VARCHAR(255),
                                          PRIMARY KEY (product_variant_id, barcodes)
);

CREATE TABLE product_variant_attributes (
                                            product_variant_id UUID REFERENCES product_variants,
                                            attribute_key VARCHAR(255),
                                            attribute_value VARCHAR(255),
                                            PRIMARY KEY (product_variant_id, attribute_key)
);

CREATE TABLE product_variant_materials (
                                           product_variant_id UUID REFERENCES product_variants,
                                           material VARCHAR(255),
                                           PRIMARY KEY (product_variant_id, material)
);

CREATE TABLE product_variant_allergens (
                                           product_variant_id UUID REFERENCES product_variants,
                                           allergens VARCHAR(255),
                                           PRIMARY KEY (product_variant_id, allergens)
);

CREATE TABLE product_variant_ingredients (
                                             product_variant_id UUID REFERENCES product_variants,
                                             ingredients VARCHAR(255),
                                             PRIMARY KEY (product_variant_id, ingredients)
);

CREATE TABLE product_variant_warnings (
                                          product_variant_id UUID REFERENCES product_variants,
                                          warnings VARCHAR(255),
                                          PRIMARY KEY (product_variant_id, warnings)
);

CREATE TABLE product_variant_instructions (
                                              product_variant_id UUID REFERENCES product_variants,
                                              instructions VARCHAR(255),
                                              PRIMARY KEY (product_variant_id, instructions)
);

CREATE TABLE product_variant_specifications (
                                                product_variant_id UUID REFERENCES product_variants,
                                                spec_key VARCHAR(255),
                                                spec_value VARCHAR(255),
                                                PRIMARY KEY (product_variant_id, spec_key)
);

CREATE TABLE product_variant_compatibility (
                                               product_variant_id UUID REFERENCES product_variants,
                                               compatibility VARCHAR(255),
                                               PRIMARY KEY (product_variant_id, compatibility)
);

CREATE TABLE product_variant_quality_certifications (
                                                        product_variant_id UUID REFERENCES product_variants,
                                                        quality_certifications VARCHAR(255),
                                                        PRIMARY KEY (product_variant_id, quality_certifications)
);

CREATE TABLE product_variant_localized_content (
                                                   product_variant_id UUID REFERENCES product_variants,
                                                   locale VARCHAR(10),
                                                   name VARCHAR(255),
                                                   description VARCHAR(2000),
                                                   short_description VARCHAR(500),
                                                   search_keywords VARCHAR(1000),
                                                   meta_description VARCHAR(500),
                                                   specifications JSONB,
                                                   warnings JSONB,
                                                   instructions JSONB,
                                                   PRIMARY KEY (product_variant_id, locale)
);

-- Add nutritional info columns to product_variants
ALTER TABLE product_variants ADD COLUMN calories DECIMAL;
ALTER TABLE product_variants ADD COLUMN protein DECIMAL;
ALTER TABLE product_variants ADD COLUMN carbohydrates DECIMAL;
ALTER TABLE product_variants ADD COLUMN fat DECIMAL;
ALTER TABLE product_variants ADD COLUMN fiber DECIMAL;
ALTER TABLE product_variants ADD COLUMN sugar DECIMAL;
ALTER TABLE product_variants ADD COLUMN sodium DECIMAL;
ALTER TABLE product_variants ADD COLUMN serving_size VARCHAR(100);

-- Add warranty info columns to product_variants
ALTER TABLE product_variants ADD COLUMN duration_months INTEGER;
ALTER TABLE product_variants ADD COLUMN terms VARCHAR(255);
ALTER TABLE product_variants ADD COLUMN coverage VARCHAR(255);
ALTER TABLE product_variants ADD COLUMN provider VARCHAR(255);

-- Add seasonality info columns to product_variants
ALTER TABLE product_variants ADD COLUMN available_months JSONB;
ALTER TABLE product_variants ADD COLUMN seasonality_description VARCHAR(255);