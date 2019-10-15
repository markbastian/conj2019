(ns conj2019.hello-world.v1
  "Introduce middleware pattern."
  (:require [immutant.web :as immutant]
    ;This is baked in, no need to roll out own
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.middleware.params :refer [wrap-params]]
    ;[ring.middleware.content-type :refer [wrap-content-type]]
    ;More data manipulation functions
            [ring.util.http-response :refer [ok not-found]]))

(defonce state (atom 0))

;(ok {:test :map})

(def app
  (-> (fn [{:keys [params path-info] :as request}]
        (let [{:strs [name] :or {name "World"}} params]
          (case path-info
            "/greeting" (ok {:id (swap! state inc) :content (format "Hello, %s!" name)})
            "/reset" (ok {:id (reset! state 0)})
            (not-found (format "Unknown route: %s" path-info)))))
      wrap-json-response
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