CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE classes (
                         id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                         name VARCHAR(100) NOT NULL UNIQUE,
                         description TEXT,
                         created_at TIMESTAMP DEFAULT NOW(),
                         updated_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE generics (
                          id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                          name VARCHAR(150) NOT NULL,
                          chemical_name VARCHAR(150),
                          class_id UUID REFERENCES classes(id) ON DELETE SET NULL,
                          description TEXT,
                          is_parent BOOLEAN DEFAULT TRUE,
                          created_at TIMESTAMP DEFAULT NOW(),
                          updated_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE variants (
                          id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                          generic_id UUID REFERENCES generics(id) ON DELETE CASCADE,
                          name VARCHAR(150) NOT NULL,
                          form VARCHAR(50),
                          route VARCHAR(50),
                          strength VARCHAR(50),
                          concentration VARCHAR(50),
                          packaging VARCHAR(100),
                          notes TEXT,
                          extra_info JSONB,
                          created_at TIMESTAMP DEFAULT NOW(),
                          updated_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE brands (
                        id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                        variant_id UUID REFERENCES variants(id) ON DELETE CASCADE,
                        brand_name VARCHAR(150) NOT NULL,
                        manufacturer VARCHAR(150),
                        country VARCHAR(100),
                        created_at TIMESTAMP DEFAULT NOW(),
                        updated_at TIMESTAMP DEFAULT NOW()
);

-- Create indexes for better performance
CREATE INDEX idx_generics_class_id ON generics(class_id);
CREATE INDEX idx_generics_name ON generics(name);
CREATE INDEX idx_variants_generic_id ON variants(generic_id);
CREATE INDEX idx_variants_form ON variants(form);
CREATE INDEX idx_variants_route ON variants(route);
CREATE INDEX idx_brands_variant_id ON brands(variant_id);
CREATE INDEX idx_brands_brand_name ON brands(brand_name);
CREATE INDEX idx_brands_manufacturer ON brands(manufacturer);