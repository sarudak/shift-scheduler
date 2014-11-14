(ns shift-scheduler.io.db-convert
  (:require [clj-time.core :as date]
            [shift-scheduler.core.date :refer [timestamp-to-date date-to-timestamp]]
            [clojure.string :as s]))

(def enums-in {:recurrence-type {:none 0
                                 :weekly 1}})

(def enums-out {:recurrence_type {0 :none
                                  1 :weekly}})

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
  [(_to- key)
   (db-out-convert-value (or (get-in enums-out [key value]) value))])

(defn db-in-convert-item [[key value]]
  [(-to_ key)
   (db-in-convert-value (or (get-in enums-in [key value]) value))])

(get-in enums-out [:recurrence-type 0])

(defn out [record]
  (into {} (map db-out-convert-item record)))

(defn in [record]
  (into {} (map db-in-convert-item record)))

(defn type-of-first [item _] (type item))

(defn map-enums [enums item]
  (let [enum-key (second item)
        applicable-enum (enums enum-key)
        enum-value (nth item 2)]
    (if applicable-enum
      (assoc item 2 (enum-value applicable-enum))
      item)))

(defmulti walk-replace type-of-first)
(defmethod walk-replace clojure.lang.Sequential [item replace-map]
  (->> item
       (map-enums (:enums replace-map))
       (map #(walk-replace % replace-map))))

(defmethod walk-replace clojure.lang.Keyword [item replace-map] (-to_ item))
(defmethod walk-replace :default [item replace-map] (db-in-convert-value item))

(defn where-clause [base] (walk-replace base {:enums enums-in}))


;(def get-shifts-request {:request-type :shifts
;  :context-id :overlapping-shifts
;  :where [:and
;          [:<= :end-time (date/date-time 2014 9 21 4)]
;          [:>= :start-time (date/date-time 2014 9 21 5)]]})
;(def get-recurring-shifts-request {:request-type :shifts
;  :context-id :recurring-shifts
;  :where [:= :recurrence-type :weekly]
;  })
;
