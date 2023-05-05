(ns sugbi.catalog.handlers
  (:require
   [ring.util.http-response :as response]
   [sugbi.catalog.db :as catalog.db]
   [sugbi.catalog.core :as catalog.core]))


(defn search-books
  [request]
  (if-let [criteria (get-in request [:parameters :query :q])]
    (response/ok
     (catalog.core/enriched-search-books-by-title
      criteria
      catalog.core/available-fields))
    (response/ok
     (catalog.core/get-books
      catalog.core/available-fields))))


(defn insert-book!
  [request]
  (let [{:keys [_isbn _title]
         :as book-info} (get-in request [:parameters :body])
        is-librarian?   (get-in request [:session :is-librarian?])]
    (if is-librarian?
      (response/ok
       (select-keys (catalog.db/insert-book! book-info) [:isbn :title]))
      (response/forbidden {:message "Operation restricted to librarians"}))))


(defn delete-book!
  [request]
  (let [isbn          (get-in request [:parameters :path :isbn])
        is-librarian? (get-in request [:session :is-librarian?])]
    (if is-librarian?
      (response/ok
       {:deleted (catalog.db/delete-book! {:isbn isbn})})
      (response/forbidden {:message "Operation restricted to librarians"}))))


(defn get-book
  [request]
  (let [isbn (get-in request [:parameters :path :isbn])]
    (if-let [book-info (catalog.core/get-book
                        isbn
                        catalog.core/available-fields)]
      (response/ok book-info)
      (response/not-found {:isbn isbn}))))


(defn create-loan!
  [request]
  (let [id (get-in request [:book-item-id])
        usr-id (get-in request [:session :sub])
        book-id (catalog.db/extract-book {:book-id id})]
    (if-let [loaned (catalog.db/is-loaned {:book-item-id id})]
      (response/conflict {:message "book already on loan"})
      ((if-let [new-loan (catalog.db/create-loan! {:user-id usr-id :book-id book-id})]
         (response/ok id)
         (response/not-found))))))


(defn return-book!
  [request]
  ((if-let [usr-id (get-in request [:session :sub])]
     (let [id (get-in request [:book-item-id])]
       (if-let [returned (catalog.db/delete-loan! {:book-id id})]
         (response/ok id)
         (response/not-found {:book-item-id id :message "book not on loan"})))
     (response/forbidden {:message "no active session"}))))
