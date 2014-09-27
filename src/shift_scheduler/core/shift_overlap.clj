(ns shift-scheduler.core.shift-overlap
  (:require [clj-time.core :as date]))

(defn shift-interval [shift]
    (date/interval (:start-time shift) (:end-time shift)))

(defn overlapping? [shift-a shift-b]
  (date/overlaps?
   (shift-interval shift-a)
   (shift-interval shift-b)))

(defn non-recurring-conflicts [shift-to-check overlapping-shifts]
  (filter #(overlapping? shift-to-check %) overlapping-shifts))

(defn recurring-conflicts [shift-to-check recurring-shifts] [])

(defn conflicting-shifts [shift-to-check recurring-shifts overlapping-shifts]
  (concat (recurring-conflicts shift-to-check recurring-shifts)
          (non-recurring-conflicts shift-to-check overlapping-shifts)))




