(ns shift-scheduler.core.shift-overlap
  (:use shift-scheduler.core.date)
  (:require [clj-time.core :as date]
            [clj-time.predicates :as date-check]))

(defn shift-interval [shift]
    (date/interval (:start-time shift) (:end-time shift)))

(defn overlapping? [shift-a shift-b]
  (date/overlaps?
   (shift-interval shift-a)
   (shift-interval shift-b)))

(defn non-recurring-conflicts [shift-to-check overlapping-shifts]
  (filter #(overlapping? shift-to-check %) overlapping-shifts))

(defn shifts-on-same-day-of-week? [shift-a shift-b]
  (same-day-of-week? (:start-time shift-a) (:start-time shift-b)))

(defn date-agnostic-shift [the-shift]
  (-> the-shift
      (assoc :start-time (date-agnostic (:start-time the-shift)))
      (assoc :end-time (date-agnostic (:end-time the-shift)))))

(defn recurring-conflicts [shift-to-check recurring-shifts]
  (let [shift-on-day-to-check? (partial shifts-on-same-day-of-week? shift-to-check)
        shifts-on-day-to-check (filter shift-on-day-to-check? recurring-shifts)
        agnostic-shift-to-check (date-agnostic-shift shift-to-check)
        agnostic-shifts-on-day-to-check (map date-agnostic-shift shifts-on-day-to-check)]
    (filter #(overlapping? agnostic-shift-to-check %) agnostic-shifts-on-day-to-check)))

(defn get-conflict-checker [shift-to-check]
  (if (= :weekly (:recurrence-type shift-to-check))
    recurring-conflicts
    non-recurring-conflicts))

(defn conflicting-shifts [shift-to-check recurring-shifts overlapping-shifts]
  (let [conflict-checker-against-non-recurring (get-conflict-checker shift-to-check)]
   (concat (recurring-conflicts shift-to-check recurring-shifts)
          (conflict-checker-against-non-recurring shift-to-check overlapping-shifts))))





