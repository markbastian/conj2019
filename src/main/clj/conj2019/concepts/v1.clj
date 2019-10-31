(ns conj2019.concepts.v1
  (:require [integrant.core :as ig]
            [immutant.web :as immutant]))

(defmethod ig/init-key :web/server [_ {:keys [handler host port]}]
  (immutant/run handler :host host :port port))

(defmethod ig/halt-key! :web/server [_ server]
  (immutant/stop server))

(defn handler [request]
  {:status 200 :body "OK"})

(def config
  {::web/server {:host     "0.0.0.0"
                 :port     3000
                 :handler  #'handler}})

(comment
  (def sys (ig/init config))
  (ig/halt! sys))
