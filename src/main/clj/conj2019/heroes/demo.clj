(ns conj2019.heroes.demo
  (:require [partsbin.core :refer [create start stop restart system reset-config!]]
            [partsbin.immutant.web.core :as web]))

(defn app [request]
  {:status 200 :body "OK"})

(def config
  {::web/server {:host     "0.0.0.0"
                 :port     3000
                 ; 1. Add in this key and change the body
                 :message "I <3 Clojure!"
                 :handler  #'app}})

(defonce sys (create config))
