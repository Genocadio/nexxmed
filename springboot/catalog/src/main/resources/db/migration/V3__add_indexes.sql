-- Add indexes for better performance
CREATE INDEX idx_product_family_category ON product_families(category_id);
CREATE INDEX idx_product_variant_family ON product_variants(family_id);
CREATE INDEX idx_product_variant_sku ON product_variants(sku);
CREATE INDEX idx_category_parent ON categories(parent_id);
CREATE INDEX idx_product_family_status ON product_families(status);
CREATE INDEX idx_product_variant_status ON product_variants(status);
CREATE INDEX idx_product_image_family ON product_images(product_family_id);
CREATE INDEX idx_product_image_variant ON product_images(product_variant_id);
CREATE INDEX idx_product_document_family ON product_documents(product_family_id);
CREATE INDEX idx_product_document_variant ON product_documents(product_variant_id);
CREATE INDEX idx_product_video_family ON product_videos(product_family_id);
CREATE INDEX idx_product_video_variant ON product_videos(product_variant_id);