-- =====================================================================
-- V1__create_schema.sql  -  ShopEasy database schema (Flyway migration)
--
-- Flyway runs this file ONCE, in version order, and records it in the
-- table "flyway_schema_history". Never edit a migration that already ran
-- in a shared environment - add a new V3__..., V4__... file instead.
--
-- The SQL is written so it runs on BOTH:
--   * H2 (default profile; the JDBC URL uses MODE=MySQL)
--   * MySQL 8.4 (profile "mysql")
-- Hibernate only VALIDATES this schema (spring.jpa.hibernate.ddl-auto=validate).
-- =====================================================================

-- ---------- users ----------
CREATE TABLE users (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    full_name   VARCHAR(100) NOT NULL,
    email       VARCHAR(150) NOT NULL,
    password    VARCHAR(100) NOT NULL,          -- BCrypt hash, never plain text
    role        VARCHAR(20)  NOT NULL,          -- ADMIN or CUSTOMER
    enabled     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  DATETIME(6)  NOT NULL,
    updated_at  DATETIME(6)  NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uk_users_email UNIQUE (email)
);

-- ---------- categories ----------
CREATE TABLE categories (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    name        VARCHAR(80)  NOT NULL,
    description VARCHAR(255),
    created_at  DATETIME(6)  NOT NULL,
    updated_at  DATETIME(6)  NOT NULL,
    CONSTRAINT pk_categories PRIMARY KEY (id),
    CONSTRAINT uk_categories_name UNIQUE (name)
);

-- ---------- products ----------
CREATE TABLE products (
    id             BIGINT        NOT NULL AUTO_INCREMENT,
    sku            VARCHAR(40)   NOT NULL,
    name           VARCHAR(150)  NOT NULL,
    description    VARCHAR(1000),
    price          DECIMAL(12,2) NOT NULL,
    stock_quantity INT           NOT NULL,
    image_url      VARCHAR(500),
    active         BOOLEAN       NOT NULL DEFAULT TRUE,   -- soft delete flag
    category_id    BIGINT        NOT NULL,
    version        BIGINT        NOT NULL DEFAULT 0,      -- optimistic locking (@Version)
    created_at     DATETIME(6)   NOT NULL,
    updated_at     DATETIME(6)   NOT NULL,
    CONSTRAINT pk_products PRIMARY KEY (id),
    CONSTRAINT uk_products_sku UNIQUE (sku),
    CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES categories (id),
    CONSTRAINT ck_products_price CHECK (price >= 0),
    CONSTRAINT ck_products_stock CHECK (stock_quantity >= 0)
);
-- Indexes for the columns we search / filter on (topic 70: performance tips)
CREATE INDEX idx_products_name ON products (name);
CREATE INDEX idx_products_category ON products (category_id);

-- ---------- carts (one cart per user) ----------
CREATE TABLE carts (
    id          BIGINT      NOT NULL AUTO_INCREMENT,
    user_id     BIGINT      NOT NULL,
    created_at  DATETIME(6) NOT NULL,
    updated_at  DATETIME(6) NOT NULL,
    CONSTRAINT pk_carts PRIMARY KEY (id),
    CONSTRAINT uk_carts_user UNIQUE (user_id),
    CONSTRAINT fk_carts_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE cart_items (
    id          BIGINT NOT NULL AUTO_INCREMENT,
    cart_id     BIGINT NOT NULL,
    product_id  BIGINT NOT NULL,
    quantity    INT    NOT NULL,
    CONSTRAINT pk_cart_items PRIMARY KEY (id),
    CONSTRAINT uk_cart_items_cart_product UNIQUE (cart_id, product_id),
    CONSTRAINT fk_cart_items_cart FOREIGN KEY (cart_id) REFERENCES carts (id) ON DELETE CASCADE,
    CONSTRAINT fk_cart_items_product FOREIGN KEY (product_id) REFERENCES products (id),
    CONSTRAINT ck_cart_items_qty CHECK (quantity > 0)
);

-- ---------- orders ----------
CREATE TABLE orders (
    id               BIGINT        NOT NULL AUTO_INCREMENT,
    user_id          BIGINT        NOT NULL,
    status           VARCHAR(20)   NOT NULL,
    total_amount     DECIMAL(12,2) NOT NULL,
    shipping_address VARCHAR(500)  NOT NULL,
    created_at       DATETIME(6)   NOT NULL,
    updated_at       DATETIME(6)   NOT NULL,
    CONSTRAINT pk_orders PRIMARY KEY (id),
    CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users (id)
);
CREATE INDEX idx_orders_user ON orders (user_id);

-- Order lines keep a SNAPSHOT of the product name and price at the time of
-- purchase. If the admin later changes the price, old orders stay correct.
CREATE TABLE order_items (
    id           BIGINT        NOT NULL AUTO_INCREMENT,
    order_id     BIGINT        NOT NULL,
    product_id   BIGINT        NOT NULL,
    product_name VARCHAR(150)  NOT NULL,
    unit_price   DECIMAL(12,2) NOT NULL,
    quantity     INT           NOT NULL,
    line_total   DECIMAL(12,2) NOT NULL,
    CONSTRAINT pk_order_items PRIMARY KEY (id),
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE,
    CONSTRAINT fk_order_items_product FOREIGN KEY (product_id) REFERENCES products (id),
    CONSTRAINT ck_order_items_qty CHECK (quantity > 0)
);
