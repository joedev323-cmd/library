
-- V2__insert_test_data.sql

-- 1. Categories
INSERT INTO categories (name, loan_period_days, max_renewals)
VALUES
    ('Fiction', 14, 2),
    ('Science', 21, 3),
    ('History', 14, 2);

-- 2. Users
-- Demo password values below are placeholders, not usable
-- Spring Security password hashes.
INSERT INTO users (name, username, password, role, active)
VALUES
    ('Test Librarian', 'testlibrarian',
     '{REPLACE_WITH_VALID_PASSWORD_HASH}', 'LIBRARIAN', TRUE);

-- 3. Books
INSERT INTO books (isbn, title, author, publisher, edition,
                   description, category_id)
VALUES
    ('9780000000001', 'Test Fiction Book', 'Test Author',
     'Test Publisher', '1st Edition', 'Sample book for testing',
     (SELECT category_id FROM categories WHERE name = 'Fiction')),

    ('9780000000002', 'Test Science Book', 'Science Author',
     'Test Publisher', '1st Edition', 'Another sample book',
     (SELECT category_id FROM categories WHERE name = 'Science'));

-- 4. Physical copies
INSERT INTO accessions
(accession_id, book_id, copy_number, barcode, shelf_location,
 condition_status, availability_status, purchase_date,
 replacement_cost, notes)
VALUES
    ('ACC-TEST-001',
     (SELECT book_id FROM books WHERE isbn = '9780000000001'),
     1, 'BARCODE-TEST-001', 'A-01', 'GOOD', 'AVAILABLE',
     DATE '2026-01-01', 500.00, 'Test copy'),

    ('ACC-TEST-002',
     (SELECT book_id FROM books WHERE isbn = '9780000000002'),
     1, 'BARCODE-TEST-002', 'B-01', 'GOOD', 'AVAILABLE',
     DATE '2026-01-01', 750.00, 'Test copy');
