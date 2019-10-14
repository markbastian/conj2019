(ns conj2019.hello-world.v0
  "A raw 1:1 implementation of our Spring Boot hello world app."
  (:require [immutant.web :as immutant]
            [clojure.string :as cs]
            [cheshire.core :as ch]))

(defonce state (atom 0))

(defn parse-query-params [query-string]
  (let [kvs (some-> query-string (cs/split #"&"))
        pairs (map #(cs/split % #"=") kvs)]
    (into {} pairs)))

(defn app [{:keys [query-string path-info] :as request}]
  (let [{:strs [name] :or {name "World"}} (parse-query-params query-string)]
    (case path-info
      "/greeting" {:response 200
                   :body     (ch/encode {:id      (swap! state inc)
                                         :content (format "Hello, %s!" name)})}
      "/reset" {:response 200
                :body     (ch/encode {:id (reset! state 0)})}
      {:response 404
       :body     (format "Unknown route: %s" path-info)})))

(comment
  (def server (immutant/run #'app {:port 3000 :host "0.0.0.0"}))
  (immutant/stop server))

;(defn greeting-handler [{:keys [params] :as request}]
;  (let [{:keys [name] :or {name "World"}} params]
;    (ok {:id (swap! state inc)
;         :content (format "Hello, %s!" name)})))

;(def app
;  (ring/ring-handler
;    (ring/router
;      [["/greeting" greeting-handler]]
;      {:data {:middleware [[wrap-defaults api-defaults]
;                           wrap-json-response]}})))

;[partsbin.core :as partsbin]
;[partsbin.immutant.web.core :as web]

;(def config {::web/server {:port    3000
;                           :host    "0.0.0.0"
;                           :handler #'app}})

;(defonce sys (partsbin/create config))