-- =============================================
-- 1. Categories
-- =============================================
CREATE TABLE categories (
                            category_id BIGINT IDENTITY(1,1) PRIMARY KEY,
                            name VARCHAR(100) NOT NULL UNIQUE,
                            loan_period_days INT NOT NULL,
                            max_renewals INT NOT NULL
);

-- =============================================
-- 2. Users
-- =============================================
CREATE TABLE users (
                       user_id BIGINT IDENTITY(1,1) PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       username VARCHAR(100) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(20) NOT NULL,
                       active BIT NOT NULL
);

-- =============================================
-- 3. Members
-- =============================================
CREATE TABLE members (
                         member_id VARCHAR(50) PRIMARY KEY,
                         name VARCHAR(255) NOT NULL,
                         email VARCHAR(255) NOT NULL UNIQUE,
                         phone_number VARCHAR(20),
                         member_type VARCHAR(20) NOT NULL,
                         active BIT NOT NULL,
                         registration_date DATE NOT NULL
);

-- =============================================
-- 4. Books
-- =============================================
CREATE TABLE books (
                       book_id BIGINT IDENTITY(1,1) PRIMARY KEY,
                       isbn VARCHAR(20) NOT NULL UNIQUE,
                       title VARCHAR(255) NOT NULL,
                       author VARCHAR(255) NOT NULL,
                       publisher VARCHAR(255),
                       edition VARCHAR(100),
                       description VARCHAR(MAX),
    category_id BIGINT NOT NULL,

    CONSTRAINT fk_books_category
        FOREIGN KEY (category_id)
        REFERENCES categories(category_id)
);

-- =============================================
-- 5. Accessions
-- =============================================
CREATE TABLE accessions (
                            accession_id VARCHAR(50) PRIMARY KEY,
                            book_id BIGINT NOT NULL,
                            copy_number INT NOT NULL,
                            barcode VARCHAR(100) NOT NULL UNIQUE,
                            shelf_location VARCHAR(50),
                            condition_status VARCHAR(20) NOT NULL,
                            availability_status VARCHAR(20) NOT NULL,
                            purchase_date DATE NOT NULL,
                            replacement_cost DECIMAL(10,2) NOT NULL,
                            notes VARCHAR(MAX),

    CONSTRAINT uq_accession_book_copy
        UNIQUE (book_id, copy_number),

    CONSTRAINT fk_accessions_book
        FOREIGN KEY (book_id)
        REFERENCES books(book_id)
);

-- =============================================
-- 6. Audit Logs
-- =============================================
CREATE TABLE audit_logs (
                            audit_id BIGINT IDENTITY(1,1) PRIMARY KEY,
                            actor_id BIGINT,
                            action VARCHAR(50) NOT NULL,
                            entity_type VARCHAR(50),
                            entity_id VARCHAR(100),
                            details VARCHAR(MAX),
    created_at DATETIME2 NOT NULL,

    CONSTRAINT fk_audit_actor
        FOREIGN KEY (actor_id)
        REFERENCES users(user_id)
);

-- =============================================
-- 7. Loans
-- =============================================
CREATE TABLE loans (
                       loan_id BIGINT IDENTITY(1,1) PRIMARY KEY,
                       accession_id VARCHAR(50) NOT NULL,
                       member_id VARCHAR(50) NOT NULL,
                       issued_by BIGINT NOT NULL,
                       checkout_date DATETIME2 NOT NULL,
                       due_date DATETIME2 NOT NULL,
                       returned_date DATETIME2,
                       renewal_count INT NOT NULL,
                       fine_accrued DECIMAL(10,2) NOT NULL,
                       fine_paid DECIMAL(10,2) NOT NULL,
                       status VARCHAR(20) NOT NULL,

                       CONSTRAINT fk_loans_accession
                           FOREIGN KEY (accession_id)
                               REFERENCES accessions(accession_id),

                       CONSTRAINT fk_loans_member
                           FOREIGN KEY (member_id)
                               REFERENCES members(member_id),

                       CONSTRAINT fk_loans_issuer
                           FOREIGN KEY (issued_by)
                               REFERENCES users(user_id)
);

-- =============================================
-- 8. Payments
-- =============================================
CREATE TABLE payments (
                          payment_id BIGINT IDENTITY(1,1) PRIMARY KEY,
                          loan_id BIGINT NOT NULL,
                          amount DECIMAL(10,2) NOT NULL,
                          payment_date DATETIME2 NOT NULL,
                          payment_method VARCHAR(20) NOT NULL,
                          received_by BIGINT NOT NULL,
                          receipt_number VARCHAR(50),
                          remarks VARCHAR(255),

                          CONSTRAINT fk_payments_loan
                              FOREIGN KEY (loan_id)
                                  REFERENCES loans(loan_id),

                          CONSTRAINT fk_payments_receiver
                              FOREIGN KEY (received_by)
                                  REFERENCES users(user_id)
);

-- =============================================
-- 9. Indexes
-- =============================================

-- Accessions
CREATE INDEX idx_accession_book
    ON accessions(book_id);

CREATE INDEX idx_accession_status
    ON accessions(availability_status);

-- barcode already has a UNIQUE constraint,
-- so a separate barcode index is unnecessary.

-- Audit logs
CREATE INDEX idx_audit_actor
    ON audit_logs(actor_id);

CREATE INDEX idx_audit_action
    ON audit_logs(action);

CREATE INDEX idx_audit_timestamp
    ON audit_logs(created_at);

-- Loans
CREATE INDEX idx_loan_status
    ON loans(status);

CREATE INDEX idx_loan_due_date
    ON loans(due_date);

CREATE INDEX idx_loan_member
    ON loans(member_id);

CREATE INDEX idx_loan_accession
    ON loans(accession_id);

-- Payments
CREATE INDEX idx_payment_loan
    ON payments(loan_id);

CREATE INDEX idx_payment_date
    ON payments(payment_date);