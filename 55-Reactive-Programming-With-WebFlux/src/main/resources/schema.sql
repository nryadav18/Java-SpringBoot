-- Table for our reactive Product repository (created at startup).
CREATE TABLE IF NOT EXISTS products (
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    name     VARCHAR(100)   NOT NULL,
    category VARCHAR(50)    NOT NULL,
    price    DECIMAL(10, 2) NOT NULL
);
