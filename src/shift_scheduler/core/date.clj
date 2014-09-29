(ns shift-scheduler.core.date
  (:require [clj-time.core :as date]
            [clj-time.predicates :as date-check]))

(def day-map {:monday date-check/monday?
              :tuesday date-check/tuesday?
              :wednesday date-check/wednesday?
              :thursday date-check/thursday?
              :friday date-check/friday?
              :saturday date-check/saturday?
              :sunday date-check/sunday?})

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

(defn truncate-to-day [a-date]
  (date/date-time
   (date/year a-date)
   (date/month a-date)
   (date/day a-date)))

(defn date-from-a-time-from-b [date-a date-b]
  (date/date-time
   (date/year date-a)
   (date/month date-a)
   (date/day date-a)
   (date/hour date-b)
   (date/minute date-b)
   (date/second date-b)))
