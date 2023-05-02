(ns sugbi.catalog.core
 (:require
  [clojure.set :as set]
  [sugbi.catalog.db :as db]
  [sugbi.catalog.open-library-books :as olb]))


(defn merge-on-key
  [k x y]
  (->> (concat x y)
       (group-by k)
       (map val)
       (mapv (fn [[x y]] (merge x y)))))


(def available-fields olb/relevant-fields)

(defn is-available
   [isbn]
    (> (db/book-stock {:isbn isbn}) 0))

(defn get-book
  [isbn fields]
  (when-let [db-book (db/get-book {:isbn isbn})]
    (when-let [available (is-available isbn)]
      (let [open-library-book-info (olb/book-info isbn fields)]
        (merge db-book open-library-book-info {:available available})))))


(defn get-books
  [fields]
  (let [db-books                (db/get-books {})
        isbns                   (map :isbn db-books)
        open-library-book-infos (olb/multiple-book-info isbns fields)]
    (merge-on-key
     :isbn
     db-books
     open-library-book-infos)))


(defn enriched-search-books-by-title
  [title fields]
  (let [db-book-infos           (db/matching-books title)
        isbns                   (map :isbn db-book-infos)
        available               (map is-available isbns)
        open-library-book-infos (olb/multiple-book-info isbns fields)]
    (merge-on-key
     :isbn
     db-book-infos
     open-library-book-infos)))


 (defn checkout-book
  [user-id book-item-id]
    (db/create-loan! {:user-id user-id
                    :book-id book-item-id}))


(defn return-book
  [user-id book-item-id]
  (let [late (db/is-late {:book-id book-item-id})]
    (if late
    "Book is due late"
    (db/delete-loan! {:book-id book-item-id}))))


(defn get-book-lendings
  [user-id]
  db/get-loans {:user-id user-id})
