(ns shift-scheduler.io.database
  (:require [shift-scheduler.io.io :refer [query command]]
            [clojure.java.jdbc :as j]
            [clj-time.core :as date]
            [shift-scheduler.core.date :refer [timestamp-to-date date-to-timestamp]]
            [shift-scheduler.io.db-convert :as db-convert]))



(def mysql-db {:subprotocol "mysql"
               :subname "//127.0.0.1:3306/shift_scheduler"
               :user "test"
               :password "test"})



(defmethod query :shifts [request]
  ())

(j/insert! mysql-db :shifts
  {:start_time (date-to-timestamp (date/date-time 2014 9 21 4))
   :end_time (date-to-timestamp (date/date-time 2014 9 21 5))
   :recurrence_type 0})
;; ({:generated_key 1} {:generated_key 2})

(def a-shift (first (j/query mysql-db
  ["select * from shifts"])))


(db-convert/out a-shift)
(class (:start_time a-shift))

(type (date/date-time 2014 9 21 4))

(date-convert/from-long (.getTime (:end_time a-shift)))


(s/replace "some_thing" "_" "-")

