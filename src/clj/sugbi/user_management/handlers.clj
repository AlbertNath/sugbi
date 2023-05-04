(ns sugbi.user-management.handlers
  (:require
   [ring.util.http-response :as response]))

(defn get-all-lendings
  [request]
  (if-let [librarian (get-in request [:session :is-librarian?])]
    (if-let [query (get-in request [:parameters :query :q])]
      (response/ok
       ())))
  )
