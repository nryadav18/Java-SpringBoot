-- =====================================================================
-- db/init.sql - runs ONCE, the very first time the MySQL container starts
-- with an EMPTY data volume (files in /docker-entrypoint-initdb.d/).
-- It runs inside the database named by MYSQL_DATABASE (librarydb).
--
-- To run it again you must delete the volume:  docker compose down -v
-- =====================================================================

CREATE TABLE IF NOT EXISTS books (
    id     BIGINT       NOT NULL AUTO_INCREMENT,
    title  VARCHAR(200) NOT NULL,
    author VARCHAR(100) NOT NULL,
    PRIMARY KEY (id)
);

INSERT INTO books (title, author) VALUES
    ('Clean Code', 'Robert C. Martin'),
    ('Effective Java', 'Joshua Bloch'),
    ('Spring in Action', 'Craig Walls');
