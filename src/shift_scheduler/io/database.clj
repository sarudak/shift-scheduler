(ns shift-scheduler.io.database
  (:require [shift-scheduler.io.io :refer [query command]]
            [clojure.java.jdbc :as j]
            [honeysql.core :as sql]
            [honeysql.helpers :refer [select where from insert-into values]]
            [clj-time.core :as date]
            [shift-scheduler.core.date :refer [timestamp-to-date date-to-timestamp]]
            [shift-scheduler.io.db-convert :as db-convert]))

(def mysql-db {:subprotocol "mysql"
               :subname "//127.0.0.1:3306/shift_scheduler"
               :user "test"
               :password "test"})

(defn query-from [request]
  (->
  (select :*)
  (from (:request-type request))
  (where (db-convert/where-clause (:where request)))))

(defmethod query :database [request]
  (->>
   (query-from request)
   sql/format
   (j/query mysql-db)
   (map db-convert/out)))

(defmethod command :create-shift [request]
  (j/insert! mysql-db :shifts
    (:shift (dbconvert/in request))))


(def shift-7to9
  {:start-time (date/date-time 2014 9 21 4)
   :end-time (date/date-time 2014 9 21 5)
   :recurrence-type :none})

(def create-7to9-shift-response {:command-type :create-shift
                                  :shift shift-7to9})

(command create-7to9-shift-response)
;;(j/insert! mysql-db :shifts
;;  {:start_time (date-to-timestamp (date/date-time 2014 9 21 4))
;;   :end_time (date-to-timestamp (date/date-time 2014 9 21 5))
;;   :recurrence_type 0})
;; ({:generated_key 1} {:generated_key 2})


(def request {:request-type :shifts
      :context-id :non-recurring-shifts
      :where [:= :recurrence-type :none]})

