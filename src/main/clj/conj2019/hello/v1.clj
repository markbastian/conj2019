(ns conj2019.hello.v1
  (:require [immutant.web :as immutant]
            [cheshire.core :as ch]
            [integrant.core :as ig]
            [ring.middleware.params :refer [wrap-params]]
            [ring.util.http-response :refer [ok]]))

(defn handler [{:keys [path-info state query-params] :as request}]
  (let [{:strs [name] :or {name "World"}} query-params]
    (case path-info
      "/greeting" (ok (ch/encode {:id      (swap! state inc)
                                  :content (format "Hello, %s!" name)})))))

(defn wrap-component [handler config]
  (fn [request]
    (handler (into config request))))

(defmethod ig/init-key :web/server [_ {:keys [handler host port] :as config}]
  (immutant/run
    (-> handler
        (wrap-component config)
        wrap-params)
    {:host host :port port}))

(defmethod ig/halt-key! :web/server [_ server]
  (immutant/stop server))

(def config
  {:web/server {:handler #'handler
                :host    "0.0.0.0"
                :port    3000
                :state   (atom 0)}})

(def ^:dynamic *system* nil)

(defn start []
  (if *system*
    *system*
    (alter-var-root #'*system* (fn [_] (ig/init config)))))

(defn stop []
  (when *system*
    (alter-var-root #'*system* (fn [system] (ig/halt! system)))))

(defn restart []
  (do
    (stop)
    (start)))