CREATE TABLE catalog.loan (
   loan_id BIGINT NOT NULL GENERATED always AS IDENTITY PRIMARY KEY,
   book_id BIGINT NOT NULL REFERENCES catalog.book,
   user_id BIGINT NOT NULL UNIQUE,
   loan_init_date DATE NOT NULL,
   loan_due_date DATE NOT NULL
);
