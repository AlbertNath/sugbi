(ns sugbi.user-management.routes
  (:require
   [spec-tools.data-spec :as ds]
   [sugbi.user-management.handlers :as users.handlers]))

(def book-loan-info-spec
  {:book-lending-id int?
   :book-id int?
   :user_id int?})

(def routes
  ["/user" {:swagger {:tags ["Users"]}}
   ["/lendings" {:get {:summary "returns all loans of the user with the specified id"
                       :parameters {:query {(ds/opt :q) string?}
                                    :header {:cookie string?}}
                       :responses {200 {:body [book-loan-info-spec]}}
                       :handler users.handlers/get-all-lendings}}]])
