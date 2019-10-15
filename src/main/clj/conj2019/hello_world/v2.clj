(ns conj2019.hello-world.v2
  "Introduce output functions."
  (:require [immutant.web :as immutant]
            [cheshire.core :as ch]
            [ring.middleware.params :refer [wrap-params]]
            [ring.util.http-response :refer [ok not-found]]))

(defonce state (atom 0))

(defn wrap-json [handler]
  (fn [request]
    (update (handler request) :body ch/encode)))

;(def app
;  (ring/ring-handler
;    (ring/router
;      [["/greeting" greeting-handler]]
;      {:data {:middleware [[wrap-defaults api-defaults]
;                           wrap-json-response]}})))

(def app
  (-> (fn [{:keys [params path-info] :as request}]
        (let [{:strs [name] :or {name "World"}} params]
          (case path-info
            "/greeting" {:response 200
                         :body     {:id      (swap! state inc)
                                    :content (format "Hello, %s!" name)}}
            "/reset" {:response 200
                      :body     {:id (reset! state 0)}}
            {:response 404
             :body     (format "Unknown route: %s" path-info)})))
      wrap-json
      wrap-params))

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