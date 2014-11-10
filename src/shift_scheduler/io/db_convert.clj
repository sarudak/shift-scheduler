(ns shift-scheduler.io.db-convert
  (:require [clj-time.core :as date]
            [shift-scheduler.core.date :refer [timestamp-to-date date-to-timestamp]]
            [clojure.string :as s]))

(defmulti db-out-convert-value type)

(defmethod db-out-convert-value :default [item] item)

(defmethod db-out-convert-value java.sql.Timestamp [item]
  (timestamp-to-date item))

(defmulti db-in-convert-value type)

(defmethod db-in-convert-value :default [item] item)

(defmethod db-in-convert-value org.joda.time.DateTime [item]
  (date-to-timestamp item))

(defn replace-in-keyword [a-key from to]
    (-> a-key
      str
      (s/replace ":" "")
      (s/replace from to)
      keyword))

(defn _to- [a-key]
  (replace-in-keyword a-key \_ \-))

(defn -to_ [a-key]
  (replace-in-keyword a-key \- \_))

(defn db-out-convert-item [[key value]]
  [(_to- key) (db-out-convert-value value)])

(defn out [record]
  (into {} (map db-out-convert-item record)))

(defn where-clause [base] base)

(def where-clause [:and
          [:<= :end-time (date/date-time 2014 9 21 4)]
          [:>= :start-time (date/date-time 2014 9 21 5)]
          [:= :recurrence-type :weekly]])

(defn type-of-first [item _] (type item))

(defmulti walk-replace type-of-first)

(defmethod walk-replace clojure.lang.Sequential [item replace-map] (map #(walk-replace % replace-map) item))
(defmethod walk-replace clojure.lang.Keyword [item replace-map] (-to_ item))
(defmethod walk-replace :default [item replace-map] (db-in-convert-value item))

(walk-replace where-clause {})


