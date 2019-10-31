(ns conj2019.concepts.v1-intro-to-integrant
  (:require [integrant.core :as ig]
            [immutant.web :as immutant]
            [clojure.pprint :as pp])
  (:import (java.util Date)))

(defmethod ig/init-key :web/server [_ {:keys [handler host port]}]
  (immutant/run handler :host host :port port))

(defmethod ig/halt-key! :web/server [_ server]
  (immutant/stop server))

(defn runtime-data []
  (let [rt (Runtime/getRuntime)
        mb (* 1024.0 1024.0)]
    {:free-memory-MB  (/ (.freeMemory rt) mb)
     :max-memory-MB   (/ (.maxMemory rt) mb)
     :total-memory-MB (/ (.totalMemory rt) mb)}))

(defn handler [{:keys [uri] :as request}]
  (case uri
    "/time" {:status 200 :body (format "The time is %s" (Date.))}
    "/stats" {:status 200 :body (pr-str (runtime-data))}
    "/dump" {:status 200 :body (with-out-str (pp/pprint request))}
    {:status 404 :body (format "Unknown path: %s" uri)}))

(def config
  {:web/server {:host    "0.0.0.0"
                :port    3000
                :handler #'handler}})

(defonce sys (atom nil))

(defn start [sys]
  (if @sys
    @sys
    (reset! sys (ig/init config))))

(defn stop [sys]
  (when @sys
    (swap! sys ig/halt!)))