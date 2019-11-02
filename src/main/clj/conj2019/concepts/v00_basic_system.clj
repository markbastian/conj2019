(ns conj2019.concepts.v00-basic-system
  (:require [immutant.web :as immutant]))

;The "simplest" web app ever
(comment
  (def server
    (immutant/run
      ;Handler
      (fn [request] {:status 200 :body "OK"})
      ;Configuration
      {:port 3000 :host "0.0.0.0"}))

  (immutant/stop server))