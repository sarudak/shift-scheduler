(ns shift-scheduler.control.controller

  (:require [shift-scheduler.core.shifts :as shifts]
            [shift-scheduler.io.io :refer query command]))

(def request-map
  :create-shift {:data shifts/create-shift-data
                 :script shifts/create-shift-script}
  :get-shifts {:data shifts/get-shifts-data
               :script shifts/get-shifts-script})


(defn do-request [request request-type]
  (let [core-functions (request-type request-map)
        requested-data ((:data core-functions) request)
        data (apply merge (map query requested-data))
        result ((:script core-functions) {:request request :context data})]
    (do (map command (:commands result))
      (:response result))))
