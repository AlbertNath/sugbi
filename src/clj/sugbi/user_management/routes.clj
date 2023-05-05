(ns sugbi.user-management.routes
  (:require
   [spec-tools.data-spec :as ds]
   [sugbi.user-management.handlers :as users.handlers])
  (:import java.io.BufferedInputStream
           java.time.LocalDate))

(defn date?
  [o]
  (instance? java.time.LocalDate o))

(def book-loan-info-spec
  {:book-lending-id int?
   :book-id int?
   :user-id int?
   :loan-init-date date?
   :loan-due-date date?})

(def routes
  ["/user" {:swagger {:tags ["Users"]}}
   ["/lendings" {:get {:summary "returns all loans of the user with the specified id.
                                 Optionally, can take a query but only for librarians"
                       :parameters {:query {(ds/opt :q) string?}
                                    :header {:cookie string?}}
                       :responses {200 {:body [book-loan-info-spec]}
                                   404 {:body {:user-id int?}}
                                   403 {:body {:message string?}}}
                       :handler users.handlers/get-all-lendings}}]])
