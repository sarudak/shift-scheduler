(ns shift-scheduler.test.core.create-shifts-test
  (:use clojure.test shift-scheduler.core.shifts)
  (:require [clj-time.core :as date]))

(def shift-7to9
  {:start-time (date/date-time 2014 9 21 4)
   :end-time (date/date-time 2014 9 21 5)
   :recurrence-type :none
   })


(defn shift-7to9-request [context]
  {:request shift-7to9-request
   :context (merge
             {:overlapping-shifts []
             :recurring-shifts []}
             context)})

(defn success? [result]
  (= result [{:command-type :create-shift
              :shift shift-7to9}]))

(testing "With non-recurring functions"
  (testing "when there are no overlapping shifts"
    (let [result (create-shift-script (shift-7to9-request {}))]
      (is (success? result)))))
