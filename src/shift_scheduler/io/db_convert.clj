(ns shift-scheduler.io.db-convert
  (:require [clj-time.core :as date]
            [shift-scheduler.core.date :refer [timestamp-to-date date-to-timestamp]]
            [clojure.string :as s]))

(defmulti db-out-convert-value class)

(defmethod db-out-convert-value :default [item] item)

(defmethod db-out-convert-value java.sql.Timestamp [item]
  (timestamp-to-date item))

(defn _to- [a-key]
  (-> a-key
      str
      (s/replace ":" "")
      (s/replace \_ \-)
      keyword))


(defn db-out-convert-item [[key value]]
  [(_to- key) (db-out-convert-value value)])

(defn out [record]
  (into {} (map db-out-convert-item record)))
