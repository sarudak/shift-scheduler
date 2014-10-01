(ns shift-scheduler.io.io)

(defmulti query :request-type)

(defmulti command :request-type)
