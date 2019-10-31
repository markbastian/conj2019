(ns conj2019.concepts.v0
  (:require [immutant.web :as immutant]))

(comment
  (def server (immutant/run
                (fn [request]
                  {:status 200 :body "OK"})
                {:port 3000 :host "0.0.0.0"}))
  (immutant/stop server))

(defn handler [request]
  {:status 200 :body "OK"})

(comment
  (def server (immutant/run
                handler
                {:port 3000 :host "0.0.0.0"}))
  (immutant/stop server))

(comment
  (def server (immutant/run
                #'handler
                {:port 3000 :host "0.0.0.0"}))
  (immutant/stop server))