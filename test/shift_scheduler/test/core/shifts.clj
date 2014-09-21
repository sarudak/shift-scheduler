(ns shift-scheduler.test.core.shifts
  (:use midje.sweet shift-scheduler.core.shifts)
  (:require [clj-time.core :as date]))


(def shift-7to9
  {:start-time (date/date-time 1986 10 14 4)
   :end-time (date/date-time 1986 10 14 5)
   :recurrence-type :none
   })

(def shift-7to9-request shift-7to9)

(fact "When creating a new shift we need to know about shifts that overlap"
      (create-shift-data shift-7to9-request) => [{:request-type :shifts
                                          :start-time [<= (:end-time shift-7to9-request)]
                                          :end-time [>= (:start shift-7to9-request)]}])

(def no-overlapping-request
  {:request shift-7to9-request
   :context {:shifts []}   })

(fact "When creating shifts if there are no overlapping shifts the shift is created"
      (create-shift-script no-overlapping-request) => [{:command-type :create-shift
                                                        :shift shift-7to9}])

(def overlapping-shift
  {:start-time (date/date-time 1986 10 14 4)
   :end-time (date/date-time 1986 10 14 5)
   :recurrence-type :none
   })

(def overlapping-request
  {:request shift-7to9-request
   :context {:shifts [overlapping-shift]}})

(fact "When creating shifts if there are overlapping shifts the shift is not created"
      (create-shift-script overlapping-request) => [{:command-type :error
                                                        :message "Cannot create overlapping shifts"}])
