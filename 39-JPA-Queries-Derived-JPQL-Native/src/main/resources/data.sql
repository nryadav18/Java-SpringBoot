-- =====================================================================
-- Topic 39 - sample library data, loaded at startup (and in @DataJpaTest tests).
-- No "id" column: the database generates ids 1..13 (IDENTITY).
-- =====================================================================
INSERT INTO books (title, author, isbn, genre, price, published_year, pages, available) VALUES
('Clean Code',                    'Robert C. Martin',  '978-0132350884', 'PROGRAMMING', 37.99, 2008,  464, TRUE),
('Effective Java',                'Joshua Bloch',      '978-0134685991', 'PROGRAMMING', 45.50, 2018,  412, TRUE),
('Head First Java',               'Kathy Sierra',      '978-1491910771', 'PROGRAMMING', 52.00, 2022,  752, TRUE),
('Java Concurrency in Practice',  'Brian Goetz',       '978-0321349606', 'PROGRAMMING', 49.99, 2006,  432, FALSE),
('Refactoring',                   'Martin Fowler',     '978-0134757599', 'PROGRAMMING', 47.99, 2018,  448, TRUE),
('The Hobbit',                    'J.R.R. Tolkien',    '978-0547928227', 'FANTASY',     12.99, 1937,  310, TRUE),
('The Lord of the Rings',         'J.R.R. Tolkien',    '978-0544003415', 'FANTASY',     29.99, 1954, 1178, TRUE),
('A Brief History of Time',       'Stephen Hawking',   '978-0553380163', 'SCIENCE',     18.00, 1988,  256, TRUE),
('Cosmos',                        'Carl Sagan',        '978-0345539434', 'SCIENCE',     16.50, 1980,  396, FALSE),
('Sapiens',                       'Yuval Noah Harari', '978-0062316097', 'HISTORY',     22.00, 2011,  443, TRUE),
('Guns, Germs, and Steel',        'Jared Diamond',     '978-0393354324', 'HISTORY',     19.99, 1997,  480, TRUE),
('1984',                          'George Orwell',     '978-0451524935', 'FICTION',      9.99, 1949,  328, TRUE),
('To Kill a Mockingbird',         'Harper Lee',        '978-0060935467', 'FICTION',     10.99, 1960,  281, TRUE);
