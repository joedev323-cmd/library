
-- V3__populate_library_demo_data.sql
-- Synthetic data for the H2 testing database only.
-- Existing V1 and V2 migrations remain unchanged.

-- =========================================================
-- 1. ADDITIONAL CATEGORIES
-- =========================================================

INSERT INTO categories (name, loan_period_days, max_renewals)
VALUES
    ('Computing', 21, 2),
    ('Mathematics', 21, 2),
    ('Literature', 14, 2),
    ('Geography', 14, 2),
    ('Business', 14, 2);

-- =========================================================
-- 2. DEMO STAFF
-- =========================================================

INSERT INTO users (name, username, password, role, active)
VALUES
    ('Demo Administrator', 'demo_admin',
     '{REPLACE_WITH_VALID_PASSWORD_HASH}', 'SUPER_ADMIN', 1),
    ('Grace Wanjiku', 'grace.librarian',
     '{REPLACE_WITH_VALID_PASSWORD_HASH}', 'LIBRARIAN', 1),
    ('Peter Otieno', 'peter.librarian',
     '{REPLACE_WITH_VALID_PASSWORD_HASH}', 'LIBRARIAN', 1);

-- =========================================================
-- 3. BOOK COLLECTION
-- =========================================================

INSERT INTO books
(isbn, title, author, publisher, edition, description, category_id)
VALUES
    ('9781000000001', 'Introduction to Computing',
     'James Mwangi', 'Academic Press', '2nd Edition',
     'Fundamentals of computing and information systems',
     (SELECT category_id FROM categories WHERE name = 'Computing')),

    ('9781000000002', 'Practical Database Design',
     'Mary Njeri', 'Tech Publishers', '1st Edition',
     'Relational databases, normalization, and SQL',
     (SELECT category_id FROM categories WHERE name = 'Computing')),

    ('9781000000003', 'Applied Mathematics',
     'David Kamau', 'Learning House', '3rd Edition',
     'Mathematics for science and technology',
     (SELECT category_id FROM categories WHERE name = 'Mathematics')),

    ('9781000000004', 'The River Between',
     'Ngugi wa Thiong''o', 'East African Educational Publishers',
     'Reprint', 'A novel exploring tradition and change',
     (SELECT category_id FROM categories WHERE name = 'Literature')),

    ('9781000000005', 'African Short Stories',
     'Various Authors', 'Regional Books', '1st Edition',
     'A collection of fictional short stories',
     (SELECT category_id FROM categories WHERE name = 'Literature')),

    ('9781000000006', 'Principles of Business',
     'Anne Kariuki', 'Business Learning Press', '2nd Edition',
     'An introduction to business management',
     (SELECT category_id FROM categories WHERE name = 'Business')),

    ('9781000000007', 'Physical Geography',
     'John Maina', 'Academic Press', '1st Edition',
     'Landforms, climate, and human environments',
     (SELECT category_id FROM categories WHERE name = 'Geography')),

    ('9781000000008', 'Modern Physics',
     'Samuel Ochieng', 'Science Publishers', '2nd Edition',
     'An introduction to modern physics',
     (SELECT category_id FROM categories WHERE name = 'Science'));

-- =========================================================
-- 4. PHYSICAL COPIES
-- Active loans below will reference copies marked BORROWED.
-- =========================================================

INSERT INTO accessions
(accession_id, book_id, copy_number, barcode, shelf_location,
 condition_status, availability_status, purchase_date,
 replacement_cost, notes)
VALUES
    ('ACC-DEMO-001',
     (SELECT book_id FROM books WHERE isbn = '9781000000001'),
     1, 'BAR-DEMO-001', 'C-01', 'GOOD', 'BORROWED',
      '2025-02-10', 1800.00, 'Demo circulation copy'),

    ('ACC-DEMO-002',
     (SELECT book_id FROM books WHERE isbn = '9781000000002'),
     1, 'BAR-DEMO-002', 'C-02', 'GOOD', 'BORROWED',
      '2025-03-12', 2200.00, 'Demo circulation copy'),

    ('ACC-DEMO-003',
     (SELECT book_id FROM books WHERE isbn = '9781000000003'),
     1, 'BAR-DEMO-003', 'M-01', 'GOOD', 'AVAILABLE',
     '2025-01-15', 1500.00, 'Returned copy'),

    ('ACC-DEMO-004',
     (SELECT book_id FROM books WHERE isbn = '9781000000004'),
     1, 'BAR-DEMO-004', 'L-01', 'GOOD', 'BORROWED',
     '2024-08-20', 900.00, 'Overdue demo copy'),

    ('ACC-DEMO-005',
     (SELECT book_id FROM books WHERE isbn = '9781000000005'),
     1, 'BAR-DEMO-005', 'L-02', 'GOOD', 'AVAILABLE',
      '2025-06-01', 1200.00, 'Returned copy'),

    ('ACC-DEMO-006',
     (SELECT book_id FROM books WHERE isbn = '9781000000006'),
     1, 'BAR-DEMO-006', 'B-01', 'GOOD', 'BORROWED',
      '2025-04-05', 1600.00, 'Current loan'),

    ('ACC-DEMO-007',
     (SELECT book_id FROM books WHERE isbn = '9781000000007'),
     1, 'BAR-DEMO-007', 'G-01', 'GOOD', 'AVAILABLE',
     '2025-05-18', 1400.00, 'Returned copy'),

    ('ACC-DEMO-008',
     (SELECT book_id FROM books WHERE isbn = '9781000000008'),
     1, 'BAR-DEMO-008', 'S-01', 'GOOD', 'BORROWED',
      '2025-07-10', 2500.00, 'Overdue demo copy'),

    ('ACC-DEMO-009',
     (SELECT book_id FROM books WHERE isbn = '9781000000001'),
     2, 'BAR-DEMO-009', 'C-01', 'NEW', 'AVAILABLE',
      '2026-02-01', 1800.00, 'Additional available copy'),

    ('ACC-DEMO-010',
     (SELECT book_id FROM books WHERE isbn = '9781000000002'),
     2, 'BAR-DEMO-010', 'C-02', 'DAMAGED', 'DAMAGED',
      '2025-03-12', 2200.00, 'Damaged; awaiting inspection');

-- =========================================================
-- 5. BORROWERS
-- =========================================================

INSERT INTO members
(member_id, name, email, phone_number, member_type,
 active, registration_date)
VALUES
    ('MEM-DEMO-001', 'Brian Kiptoo',
     'brian.kiptoo@example.test', '0700001001',
     'STUDENT', 1,  '2025-01-10'),

    ('MEM-DEMO-002', 'Faith Achieng',
     'faith.achieng@example.test', '0700001002',
     'STUDENT', 1,  '2025-02-14'),

    ('MEM-DEMO-003', 'Kevin Mwende',
     'kevin.mwende@example.test', '0700001003',
     'STUDENT', 1, '2025-03-01'),

    ('MEM-DEMO-004', 'Lydia Chebet',
     'lydia.chebet@example.test', '0700001004',
     'STUDENT', 1,  '2025-04-12'),

    ('MEM-DEMO-005', 'Mark Ouma',
     'mark.ouma@example.test', '0700001005',
     'STUDENT', 1,  '2025-05-20'),

    ('MEM-DEMO-006', 'Nancy Wairimu',
     'nancy.wairimu@example.test', '0700001006',
     'STUDENT', 1,  '2025-06-05'),

    ('MEM-DEMO-007', 'Oscar Mutua',
     'oscar.mutua@example.test', '0700001007',
     'STUDENT', 1,  '2025-07-16'),

    ('MEM-DEMO-008', 'Sheila Atieno',
     'sheila.atieno@example.test', '0700001008',
     'STUDENT', 1,  '2025-08-22');

-- =========================================================
-- 6. CIRCULATION HISTORY
-- Dates are fixed deliberately so reports are reproducible.
-- =========================================================

-- Active overdue loan: 8 days overdue as of 2026-10-18.
INSERT INTO loans
(accession_id, member_id, issued_by, checkout_date, due_date,
 returned_date, renewal_count, fine_accrued, fine_paid, status)
VALUES
    ('ACC-DEMO-001', 'MEM-DEMO-001',
     (SELECT user_id FROM users WHERE username = 'testlibrarian'),
      '2026-10-01 09:00:00',
      '2026-10-10 17:00:00',
     NULL, 0, 160.00, 50.00, 'ACTIVE');

-- Due soon.
INSERT INTO loans
(accession_id, member_id, issued_by, checkout_date, due_date,
 returned_date, renewal_count, fine_accrued, fine_paid, status)
VALUES
    ('ACC-DEMO-002', 'MEM-DEMO-002',
     (SELECT user_id FROM users WHERE username = 'grace.librarian'),
      '2026-10-10 10:30:00',
      '2026-10-25 17:00:00',
     NULL, 1, 0.00, 0.00, 'ACTIVE');

-- Returned loan.
INSERT INTO loans
(accession_id, member_id, issued_by, checkout_date, due_date,
 returned_date, renewal_count, fine_accrued, fine_paid, status)
VALUES
    ('ACC-DEMO-003', 'MEM-DEMO-003',
     (SELECT user_id FROM users WHERE username = 'testlibrarian'),
      '2026-09-01 08:30:00',
      '2026-09-15 17:00:00',
      '2026-09-14 14:20:00',
     0, 0.00, 0.00, 'RETURNED');

-- Older overdue loan with an outstanding balance.
INSERT INTO loans
(accession_id, member_id, issued_by, checkout_date, due_date,
 returned_date, renewal_count, fine_accrued, fine_paid, status)
VALUES
    ('ACC-DEMO-004', 'MEM-DEMO-004',
     (SELECT user_id FROM users WHERE username = 'grace.librarian'),
      '2026-09-01 11:00:00',
      '2026-09-30 17:00:00',
     NULL, 1, 360.00, 100.00, 'ACTIVE');

-- Returned loan with a fully paid fine.
INSERT INTO loans
(accession_id, member_id, issued_by, checkout_date, due_date,
 returned_date, renewal_count, fine_accrued, fine_paid, status)
VALUES
    ('ACC-DEMO-005', 'MEM-DEMO-005',
     (SELECT user_id FROM users WHERE username = 'peter.librarian'),
      '2026-08-01 09:15:00',
      '2026-08-15 17:00:00',
      '2026-08-20 10:00:00',
     0, 100.00, 100.00, 'RETURNED');

-- Active loan due in the near future.
INSERT INTO loans
(accession_id, member_id, issued_by, checkout_date, due_date,
 returned_date, renewal_count, fine_accrued, fine_paid, status)
VALUES
    ('ACC-DEMO-006', 'MEM-DEMO-006',
     (SELECT user_id FROM users WHERE username = 'testlibrarian'),
      '2026-10-05 12:00:00',
      '2026-10-20 17:00:00',
     NULL, 0, 0.00, 0.00, 'ACTIVE');

-- Returned on time.
INSERT INTO loans
(accession_id, member_id, issued_by, checkout_date, due_date,
 returned_date, renewal_count, fine_accrued, fine_paid, status)
VALUES
    ('ACC-DEMO-007', 'MEM-DEMO-007',
     (SELECT user_id FROM users WHERE username = 'grace.librarian'),
      '2026-09-10 13:00:00',
      '2026-09-24 17:00:00',
      '2026-09-22 15:30:00',
     0, 0.00, 0.00, 'RETURNED');

-- Overdue with no payment yet.
INSERT INTO loans
(accession_id, member_id, issued_by, checkout_date, due_date,
 returned_date, renewal_count, fine_accrued, fine_paid, status)
VALUES
    ('ACC-DEMO-008', 'MEM-DEMO-008',
     (SELECT user_id FROM users WHERE username = 'peter.librarian'),
      '2026-09-20 10:00:00',
      '2026-10-15 17:00:00',
     NULL, 0, 90.00, 0.00, 'ACTIVE');

-- =========================================================
-- 7. PAYMENTS
-- Each payment references a real loan and existing staff user.
-- =========================================================

INSERT INTO payments
(loan_id, amount, payment_date, payment_method,
 received_by, receipt_number, remarks)
VALUES
    (
        (SELECT loan_id FROM loans
         WHERE accession_id = 'ACC-DEMO-001'),
        50.00,  '2026-10-15 11:20:00', 'CASH',
        (SELECT user_id FROM users WHERE username = 'grace.librarian'),
        'RCP-DEMO-001', 'Partial fine payment'
    ),
    (
        (SELECT loan_id FROM loans
         WHERE accession_id = 'ACC-DEMO-004'),
        100.00,  '2026-10-12 14:10:00', 'CASH',
        (SELECT user_id FROM users WHERE username = 'peter.librarian'),
        'RCP-DEMO-002', 'Partial fine payment'
    ),
    (
        (SELECT loan_id FROM loans
         WHERE accession_id = 'ACC-DEMO-005'),
        100.00,  '2026-08-20 10:05:00', 'CASH',
        (SELECT user_id FROM users WHERE username = 'grace.librarian'),
        'RCP-DEMO-003', 'Fine settled'
    );

-- =========================================================
-- 8. AUDIT LOGS
-- =========================================================

INSERT INTO audit_logs
(actor_id, action, entity_type, entity_id, details,created_at )
VALUES
    (
        (SELECT user_id FROM users WHERE username = 'testlibrarian'),
        'CHECKOUT', 'LOAN',
        CAST((SELECT loan_id FROM loans
              WHERE accession_id = 'ACC-DEMO-001') AS VARCHAR),
        'Demo checkout recorded',
         '2026-10-01 09:00:00'
    ),
    (
        (SELECT user_id FROM users WHERE username = 'grace.librarian'),
        'PAYMENT', 'PAYMENT', 'RCP-DEMO-001',
        'Partial fine payment received',
         '2026-10-15 11:20:00'
    ),
    (
        (SELECT user_id FROM users WHERE username = 'grace.librarian'),
        'RETURN', 'LOAN',
        CAST((SELECT loan_id FROM loans
              WHERE accession_id = 'ACC-DEMO-003') AS VARCHAR),
        'Book returned',
         '2026-09-14 14:20:00'
    );
