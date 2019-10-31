(ns conj2019.concepts.v2
  (:require [partsbin.core :refer [create start stop restart system reset-config!]]
            [partsbin.immutant.web.core :as web]))

(defn app [{:keys [message] :as request}]
  {:status 200 :body message})

(def config
  {::web/server {:host    "0.0.0.0"
                 :port    3000
                 :message "I <3 Clojure!"
                 :handler #'app}})

(defonce sys (create config))
