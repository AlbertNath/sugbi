-- :name insert-book! :! :1
insert into catalog.book (title, isbn) values (:title, :isbn)
returning *;

-- :name delete-book! :! :n
delete from catalog.book where isbn = :isbn;

-- :name search :? :*
select isbn, true as "available"
from catalog.book
where lower(title) like :title;

-- :name get-book :? :1
select isbn, true as "available"
from catalog.book
where isbn = :isbn;

-- :name get-books :? :*
select isbn, true as "available"
from catalog.book;

-- :name get-total-loans :? :1
SELECT user_id, COUNT(*) AS "total_loans"
FROM catalog.loan
WHERE user_id = :user-id
GROUP BY user_id;

-- :name get-loan-elapsed-weeks :? :2
SELECT
TRUNC(DATE_PART('day', CURRENT_TIMESTAMP - loan_init_date)/7) AS "elapsed_weeks"
FROM catalog.loan
WHERE user_id = :user-id AND book_id = :book-id
GROUP BY user_id;

-- :name book-stock :? :1
SELECT title, COUNT(*) AS "total_copies"
FROM catalog.book_item
WHERE lib_id = :book-id
GROUP BY lib_id;

-- :name is-late :? :1
SELECT TRUE AS "is_late"
FROM catalog.loan
WHERE book_id = :book-id AND loan_due_date < CURRENT_TIMESTAMP;

-- :name create-loan! :! :2
INSERT INTO catalog.loan (book_id, user_id, loan_init_date, loan_due_date)
VALUES (:book-id, :user-id, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '2 weeks')
returning *;

-- :name delete-loan! :! :1
DELETE FROM catalog.loan WHERE book_id = :book-id returning *;

-- :name get-loans :? :1
SELECT user_id, book_id, title
FROM catalog.loan JOIN catalog.book ON catalog.loan.book_id = catalog.book.book_id
WHERE user_id = :user-id;
