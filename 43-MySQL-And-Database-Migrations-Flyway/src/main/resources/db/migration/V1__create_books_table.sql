-- V1: the very first version of our schema.
-- File name rule:  V<version>__<description>.sql   (TWO underscores!)
-- NEVER edit this file after it has run anywhere. Add a new V-file instead.

CREATE TABLE books (
    id             BIGINT        NOT NULL AUTO_INCREMENT,
    title          VARCHAR(200)  NOT NULL,
    author         VARCHAR(100)  NOT NULL,
    price          DECIMAL(10,2) NOT NULL,
    published_year INT,
    created_at     TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);
