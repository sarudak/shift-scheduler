(ns shift-scheduler.core.shifts
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
    (if (seq (:overlapping-shifts context))
      [{:command-type :error
        :message "Cannot create overlapping shifts"}]
      [{:command-type :create-shift
      :shift request}])
      )

