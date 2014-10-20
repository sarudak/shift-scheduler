(ns shift-scheduler.io.database
  (:require [shift-scheduler.io.io :refer [query command]]
            [clojure.java.jdbc :as j]
            [honeysql.core :as sql]
            [clj-time.core :as date]
            [shift-scheduler.core.date :refer [timestamp-to-date date-to-timestamp]]
            [shift-scheduler.io.db-convert :as db-convert]))



(def mysql-db {:subprotocol "mysql"
               :subname "//127.0.0.1:3306/shift_scheduler"
               :user "test"
               :password "test"})

(defn query-from [request]
  (sql/select :*)
  (sql/from (:request-type request)
  ))

(defmethod query :shifts [request]
  (->>
   (j/query mysql-db [(query-from request)])
   (map db-convert/out)))

(j/insert! mysql-db :shifts
  {:start_time (date-to-timestamp (date/date-time 2014 9 21 4))
   :end_time (date-to-timestamp (date/date-time 2014 9 21 5))
   :recurrence_type 0})
;; ({:generated_key 1} {:generated_key 2})

(query {:request-type :shifts})

(def get-shifts-request {:request-type :shifts
  :context-id :overlapping-shifts
  :where [:and
          [:<= :end-time (date/date-time 2014 9 21 4)]
          [:>= :start-time (date/date-time 2014 9 21 5)]]})
(def get-recurring-shifts-request {:request-type :shifts
  :context-id :recurring-shifts
  :where [:= :recurrence-type :weekly]
  })

