(ns shift-scheduler.core.shifts
  (:use shift-scheduler.core.shift-overlap)
  (:require [clj-time.core :as date]))

  (defn create-shift-data [request]
    [{:request-type :shifts
      :context-id :overlapping-shifts
      :start-time [<= (:end-time request)]
      :end-time [>= (:start request)]}
     {:request-type :shifts
      :context-id :recurring-shifts
      :recurrence-type :weekly}])

  (defn create-shift-script [{:keys [request context]}]
    (if (empty? (conflicting-shifts  request
                                 (:recurring-shifts context)
                                 (:overlapping-shifts context)))
      [{:command-type :create-shift
        :shift request}]
      [{:command-type :error
        :message "Cannot create overlapping shifts"}])
      )

