(ns shift-scheduler.test.core.shifts
  (:use midje.sweet shift-scheduler.core.shifts)
  (:require [clj-time.core :as date]))

;9/14/2014 and 9/21/2014 are both sundays

(def shift-7to9
  {:start-time (date/date-time 2014 9 21 4)
   :end-time (date/date-time 2014 9 21 5)
   :recurrence-type :none
   })

(def shift )

(def shift-7to9-request shift-7to9)

(fact "When creating a new shift we need to know about shifts that overlap and all recurring shifts"
      (create-shift-data shift-7to9-request) => [{:request-type :shifts
                                          :context-id :overlapping-shifts
                                          :start-time [<= (:end-time shift-7to9-request)]
                                          :end-time [>= (:start shift-7to9-request)]}
                                          {:request-type :shifts
                                          :context-id :recurring-shifts
                                          :recurrence-type :weekly}])

(def no-overlapping-request
  {:request shift-7to9-request
   :context {:overlapping-shifts []}})

(fact "When creating shifts if there are no overlapping shifts the shift is created"
      (create-shift-script no-overlapping-request) => [{:command-type :create-shift
                                                        :shift shift-7to9}])

(def no-overlapping-request
  {:request shift-7to9-request
   :context {:overlapping-shifts []}})

(def overlapping-shift
  {:start-time (date/date-time 2014 9 21 4 30)
   :end-time (date/date-time 2014 9 21 5 30)
   :recurrence-type :none
   })

(def overlapping-request
  {:request shift-7to9-request
   :context {:overlapping-shifts [overlapping-shift]}})

(def overlapping-recurring-shift
  {:start-time (date/date-time 2014 9 14 4 30)
   :end-time (date/date-time 2014 9 14 5 30)
   :recurrence-type :weekly
   })

(fact "When creating shifts if there are overlapping shifts the shift is not created"
      (create-shift-script overlapping-request) => [{:command-type :error
                                                     :message "Cannot create overlapping shifts"}])




