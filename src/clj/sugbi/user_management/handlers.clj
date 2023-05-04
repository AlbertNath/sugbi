(ns sugbi.user-management.handlers
  (:require
   [sugbi.catalog.db :as catalog.db]
   [ring.util.http-response :as response]))

(defn get-all-lendings
  [request]
  ((let [usr-id get-in request [:session :sub]]
     (if-let [librarian (get-in request [:session :is-librarian?])]
       (if-let [query (get-in request [:parameters :query :q])]
         (response/ok (catalog.db/get-loans {:user-id query}))
         (response/ok (catalog.db/get-loans {:user-id usr-id})))
       (response/ok (catalog.db/get-loans {:user-id usr-id}))))))
