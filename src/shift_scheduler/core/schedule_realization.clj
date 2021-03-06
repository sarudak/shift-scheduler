(ns shift-scheduler.core.schedule-realization
  (:use shift-scheduler.core.date)
  (:require [clj-time.core :as date]
            [clj-time.predicates :as date-check]))

(defn is-recurring? [shift]
  (->> shift
       :recurrence-type
       (= :none)
       not))

(defn shift-for-output [shift]
  (select-keys shift [:start-time :end-time :instance-of ]))

(defn days-after [start-date] (iterate #(date/plus % (date/days 1)) start-date))

(defn days-between [start end]
  (let [before-end? #(date/before? % end)
        days-after-start (days-after (truncate-to-day start))]
  (take-while before-end? days-after-start)))

(defn realize-instances [{:keys [start-time end-time]} recurring-shifts]
  (let [days-in-period (days-between start-time end-time)]
    (for [day days-in-period
          shift recurring-shifts
          :when (same-day-of-week? (:start-time shift) day)]
      {:start-time (date-from-a-time-from-b day (:start-time shift))
       :end-time (date-from-a-time-from-b day (:end-time shift))
       :instance-of (:shift-id shift)})))


(defn realize-schedule [period recurring-shifts non-recurring-shifts]
  (->> recurring-shifts
       (realize-instances period)
       (concat non-recurring-shifts)
       (map shift-for-output)))
