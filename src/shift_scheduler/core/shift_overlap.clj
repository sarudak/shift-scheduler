(ns shift-scheduler.core.shift-overlap
  (:require [clj-time.core :as date]
            [clj-time.predicates :as date-check]))

(def day-map {:monday date-check/monday?
              :tuesday date-check/tuesday?
              :wednesday date-check/wednesday?
              :thursday date-check/thursday?
              :friday date-check/friday?
              :saturday date-check/saturday?
              :sunday date-check/sunday?})

(defn shift-interval [shift]
    (date/interval (:start-time shift) (:end-time shift)))

(defn overlapping? [shift-a shift-b]
  (date/overlaps?
   (shift-interval shift-a)
   (shift-interval shift-b)))

(defn non-recurring-conflicts [shift-to-check overlapping-shifts]
  (filter #(overlapping? shift-to-check %) overlapping-shifts))

(defn get-day-of-week [the-date]
  (->> day-map
       (filter #((second %) the-date))
       (map first)
       first))

(defn same-day-of-week? [date-a date-b]
  (= (get-day-of-week date-a) (get-day-of-week date-b)))

(defn date-agnostic [the-date]
  (date/date-time 2000 1 1
                  (date/hour the-date)
                  (date/minute the-date)
                  (date/second the-date)))

(defn date-agnostic-shift [the-shift]
  (-> the-shift
      (assoc :start-time (date-agnostic (:start-time the-shift)))
      (assoc :end-time (date-agnostic (:end-time the-shift)))))

(defn recurring-conflicts [shift-to-check recurring-shifts]
  (let [shift-on-day-to-check? #(same-day-of-week? (:start-time shift-to-check) (:start-time %))
        shifts-on-day-to-check (filter shift-on-day-to-check? recurring-shifts)
        agnostic-shift-to-check (date-agnostic-shift shift-to-check)
        agnostic-shifts-on-day-to-check (map date-agnostic-shift shifts-on-day-to-check)]
    (filter #(overlapping? agnostic-shift-to-check %) agnostic-shifts-on-day-to-check)))

(defn conflicting-shifts [shift-to-check recurring-shifts overlapping-shifts]
  (concat (recurring-conflicts shift-to-check recurring-shifts)
          (non-recurring-conflicts shift-to-check overlapping-shifts)))





