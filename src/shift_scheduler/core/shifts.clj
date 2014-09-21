(ns shift-scheduler.core.shifts
  (:require [clj-time.core :as date]))


  (defn create-shift-data [request]
    [{:request-type :shifts
      :start-time [<= (:end-time request)]
      :end-time [>= (:start request)]}])

  (defn create-shift-script [{:keys [request context]}]
    (if (seq (:shifts context))
      [{:command-type :error
        :message "Cannot create overlapping shifts"}]
      [{:command-type :create-shift
      :shift request}])
      )

