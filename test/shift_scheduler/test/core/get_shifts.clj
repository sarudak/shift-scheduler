(ns shift-scheduler.test.core.get-shifts
  (:use midje.sweet shift-scheduler.core.shifts)
  (:require [clj-time.core :as date]))

(def get-shifts-for-9-21-request
  {:start-time (date/date-time 2014 9 21)
   :end-time (date/date-time 2014 9 22)})

(def non-recurring-shift-raw
  {:start-time (date/date-time 2014 9 21 4 30)
   :end-time (date/date-time 2014 9 21 5 30)
   :shift-id 4
   :recurrence-type :none})

(def non-recurring-shift (select-keys non-recurring-shift-raw [:start-time :end-time]))

(def get-shifts-no-recurring-request {:request get-shifts-for-9-21-request
                                      :context {:non-recurring-shifts [non-recurring-shift-raw]
                                                :recurring-shifts []}})

(def get-shifts-no-recurring-response [{:return-type :shift-data
                                        :data [non-recurring-shift]}])


(fact "When getting shifts non-recurring shifts show up as non-recurring"
      (get-shifts-script get-shifts-no-recurring-request) => get-shifts-no-recurring-response)


(def recurring-shift-raw
  {:start-time (date/date-time 2014 9 14 4 30)
   :end-time (date/date-time 2014 9 14 5 30)
   :recurrence-type :weekly
   :shift-id 3})

(def recurring-shift-instance
  {:start-time (date/date-time 2014 9 21 4 30)
   :end-time (date/date-time 2014 9 21 5 30)
   :instance-of 3})

(def recurring-different-day-shift-raw
  {:start-time (date/date-time 2014 9 15 4 30)
   :end-time (date/date-time 2014 9 15 5 30)
   :recurrence-type :weekly
   :shift-id 2})

(def get-shifts-with-recurring-request {:request get-shifts-for-9-21-request
                                        :context {:non-recurring-shifts []
                                                  :recurring-shifts [recurring-shift-raw]}})

(def get-shifts-with-recurring-response [{:return-type :shift-data
                                        :data [recurring-shift-instance]}])

(def with-recurring-different-day-request {:request get-shifts-for-9-21-request
                                        :context {:non-recurring-shifts []
                                                  :recurring-shifts [recurring-different-day-shift-raw]}})

(def no-data-response [{:return-type :shift-data
                                        :data []}])


(fact "When getting shifts recurring shifts are included as instances"
      (get-shifts-script get-shifts-with-recurring-request) => get-shifts-with-recurring-response
      (get-shifts-script with-recurring-different-day-request) => no-data-response)
