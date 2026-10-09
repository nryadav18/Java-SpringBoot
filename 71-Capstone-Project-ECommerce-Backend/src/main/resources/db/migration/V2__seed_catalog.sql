-- =====================================================================
-- V2__seed_catalog.sql  -  demo catalog data (categories + products)
--
-- Reference data like this is a good fit for a migration: it is the same
-- in every environment and it is versioned together with the schema.
-- Users are NOT seeded here, because their passwords must be BCrypt-hashed
-- by the application - see config/DataSeeder.java.
-- We do not insert explicit ids: the database generates them (1, 2, 3 ...
-- on a fresh database), and products find their category with a sub-query.
-- =====================================================================

INSERT INTO categories (name, description, created_at, updated_at) VALUES
  ('Electronics', 'Phones, laptops and gadgets',        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('Books',       'Programming and self-help books',    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('Home',        'Kitchen and home essentials',        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('Fashion',     'Clothes, shoes and accessories',     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO products (sku, name, description, price, stock_quantity, image_url, active, category_id, version, created_at, updated_at) VALUES
  ('ELEC-001', 'Smartphone X12',          '6.5 inch display, 128 GB storage',          24999.00, 50,  NULL, TRUE, (SELECT c.id FROM categories c WHERE c.name = 'Electronics'), 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('ELEC-002', 'Wireless Earbuds Pro',    'Noise cancelling, 30h battery',              3999.00, 120, NULL, TRUE, (SELECT c.id FROM categories c WHERE c.name = 'Electronics'), 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('ELEC-003', 'Ultrabook 14',            '14 inch laptop, 16 GB RAM, 512 GB SSD',     64999.00, 15,  NULL, TRUE, (SELECT c.id FROM categories c WHERE c.name = 'Electronics'), 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('ELEC-004', 'Smart Watch Fit',         'Heart-rate and sleep tracking',              4999.00, 3,   NULL, TRUE, (SELECT c.id FROM categories c WHERE c.name = 'Electronics'), 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('BOOK-001', 'Java for Beginners',      'Learn Java from zero',                        499.00, 200, NULL, TRUE, (SELECT c.id FROM categories c WHERE c.name = 'Books'), 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('BOOK-002', 'Spring Boot in Practice', 'Build production-ready apps',                 899.00, 80,  NULL, TRUE, (SELECT c.id FROM categories c WHERE c.name = 'Books'), 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('BOOK-003', 'Clean Code Habits',       'Write code that humans can read',             649.00, 60,  NULL, TRUE, (SELECT c.id FROM categories c WHERE c.name = 'Books'), 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('HOME-001', 'Steel Water Bottle',      '1 litre, keeps water cold for 24h',           599.00, 300, NULL, TRUE, (SELECT c.id FROM categories c WHERE c.name = 'Home'), 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('HOME-002', 'Non-stick Frying Pan',    '28 cm, induction friendly',                  1299.00, 40,  NULL, TRUE, (SELECT c.id FROM categories c WHERE c.name = 'Home'), 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('HOME-003', 'Coffee Maker',            'Drip coffee maker, 12 cups',                 3499.00, 25,  NULL, TRUE, (SELECT c.id FROM categories c WHERE c.name = 'Home'), 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('FASH-001', 'Running Shoes',           'Lightweight shoes for daily runs',           2999.00, 70,  NULL, TRUE, (SELECT c.id FROM categories c WHERE c.name = 'Fashion'), 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('FASH-002', 'Cotton T-Shirt',          '100% cotton, regular fit',                    399.00, 500, NULL, TRUE, (SELECT c.id FROM categories c WHERE c.name = 'Fashion'), 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
