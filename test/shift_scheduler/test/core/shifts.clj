(ns shift-scheduler.test.core.shifts
  (:use midje.sweet shift-scheduler.core.shifts)
  (:require [clj-time.core :as date]))

;9/14/2014 and 9/21/2014 are both sundays

(def shift-7to9
  {:start-time (date/date-time 2014 9 21 4)
   :end-time (date/date-time 2014 9 21 5)
   :recurrence-type :none
   })


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
   :context {:overlapping-shifts []
             :recurring-shifts []}})

(def overlapping-shift
  {:start-time (date/date-time 2014 9 21 4 30)
   :end-time (date/date-time 2014 9 21 5 30)
   :recurrence-type :none
   })

(def overlapping-request
  {:request shift-7to9-request
   :context {:overlapping-shifts [overlapping-shift]
             :recurring-shifts []}})

(def create-7to9-shift-response [{:command-type :create-shift
                                  :shift shift-7to9}])

(def create-shift-error-response [{:command-type :error
                                   :message "Cannot create overlapping shifts"}])

(fact "When creating shifts if there are no overlapping shifts the shift is created"
      (create-shift-script no-overlapping-request) => create-7to9-shift-response
      (create-shift-script overlapping-request) => create-shift-error-response)


(def overlapping-recurring-shift
  {:start-time (date/date-time 2014 9 14 4 30)
   :end-time (date/date-time 2014 9 14 5 30)
   :recurrence-type :weekly
   })

(def overlapping-recurring-request
  {:request shift-7to9-request
   :context {:overlapping-shifts []
             :recurring-shifts [overlapping-recurring-shift]}})

(def non-overlapping-recurring-shift-different-day
  {:start-time (date/date-time 2014 9 15 4 30)
   :end-time (date/date-time 2014 9 15 5 30)
   :recurrence-type :weekly
   })

(def non-overlapping-recurring-shift-different-time
  {:start-time (date/date-time 2014 9 14 6 30)
   :end-time (date/date-time 2014 9 14 7 30)
   :recurrence-type :weekly
   })

(def recurring-request-different-day
  {:request shift-7to9-request
   :context {:overlapping-shifts []
             :recurring-shifts [non-overlapping-recurring-shift-different-day]}})

(def recurring-request-different-time
  {:request shift-7to9-request
   :context {:overlapping-shifts []
             :recurring-shifts [non-overlapping-recurring-shift-different-time]}})

(fact "When creating shifts if there are recurrings shifts that overlap the shift is not created"
      (create-shift-script overlapping-recurring-request) => create-shift-error-response
      (create-shift-script recurring-request-different-day) => create-7to9-shift-response
      (create-shift-script recurring-request-different-time) => create-7to9-shift-response)



(def shift-7to9-recurring
  {:start-time (date/date-time 2014 9 7 4)
   :end-time (date/date-time 2014 9 7 5)
   :recurrence-type :weekly
   })

(defn recurring [request] (assoc-in request [:request] shift-7to9-recurring ))

(def create-7to9-recurring-shift-response [{:command-type :create-shift
                                  :shift shift-7to9-recurring}])


(fact "When creating recurring shifts if there are overlapping recurring shifts the shift is not created"
      (create-shift-script (recurring overlapping-recurring-request)) => create-shift-error-response
      (create-shift-script (recurring recurring-request-different-day)) => create-7to9-recurring-shift-response
      (create-shift-script (recurring recurring-request-different-time)) => create-7to9-recurring-shift-response)









