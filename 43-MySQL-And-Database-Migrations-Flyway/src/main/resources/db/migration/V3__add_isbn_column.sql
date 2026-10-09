-- V3: the business asked for an ISBN number. We do NOT touch V1.
-- We add a NEW migration that changes the existing table.

ALTER TABLE books ADD COLUMN isbn VARCHAR(20);

-- Fill the new column for the rows that already exist
UPDATE books SET isbn = '978-0132350884' WHERE title = 'Clean Code';
UPDATE books SET isbn = '978-0134685991' WHERE title = 'Effective Java';

-- An index makes "search by author" fast when the table grows
CREATE INDEX idx_books_author ON books (author);
