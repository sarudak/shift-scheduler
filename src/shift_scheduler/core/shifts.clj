(ns shift-scheduler.core.shifts
  (:use shift-scheduler.core.shift-overlap shift-scheduler.core.schedule-realization)
  (:require [clj-time.core :as date]))

  (defn create-shift-data [request]
    [{:request-type :shifts
      :context-id :overlapping-shifts
      :where [:and [:<= :start-time  (:end-time request)]
                   [:>= :end-time  (:start request)]]
      }
     {:request-type :shifts
      :context-id :recurring-shifts
      :where [:= :recurrence-type :weekly]}])

  (defn create-shift-script [{:keys [request context]}]
    (if (empty? (conflicting-shifts  request
                                 (:recurring-shifts context)
                                 (:overlapping-shifts context)))
      [{:command-type :create-shift
        :shift request}]
      [{:command-type :error
        :message "Cannot create overlapping shifts"}]))

  (defn get-shifts-data [request]
    [{:request-type :shifts
      :context-id :non-recurring-shifts
      :where [:and [:<= :start-time  (:end-time request)]
                   [:>= :end-time  (:start request)]
                   [:= :recurrence-type :none]]}
     {:request-type :shifts
      :context-id :recurring-shifts
      :where [:= :recurrence-type :weekly]}])

  (defn get-shifts-script [{:keys [request context]}]
    [{:return-type :shift-data
     :data (realize-schedule request
                             (:recurring-shifts context)
                             (:non-recurring-shifts context))}])

  (defn delete-shift-process [{:keys [request]}]
    [{:command-type :delete-shift
      :id (:id request)}])

