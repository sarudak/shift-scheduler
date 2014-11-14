(ns shift-scheduler.io.io)

(def query-route-table
  {:shifts :database})

(defn route-query [request]
  (let [request-type (:request-type request)]
    (or (request-type query-route-table) request-type)))

(defmulti query route-query)

(defmulti command :command-type)

