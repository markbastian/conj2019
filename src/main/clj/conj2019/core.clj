(ns conj2019.core
  (:require [partsbin.core :as partsbin]
            [partsbin.immutant.web.core :as web]
            [clojure.pprint :as pp]
            [reitit.ring :as ring]
            [hiccup.page :refer [html5 include-js include-css]]
            [ring.util.http-response :refer [ok not-found]]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.middleware.file :refer [wrap-file]]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults site-defaults]]
            [drawbridge.core]
            [conj2019.eliza-app :as eliza]
    ;For ws
            [immutant.web.async :as async]
            )
  (:import (java.util Date)))

(defonce channels (atom #{}))

(defn connect! [channel]
  (prn "channel open")
  (swap! channels conj channel))

(defn disconnect! [channel {:keys [code reason]}]
  (prn "close code:" code "reason:" reason)
  (swap! channels #(remove #{channel} %)))

(defn notify-clients! [msg]
  (doseq [channel @channels]
    (async/send! channel msg)))

(def websocket-callbacks
  "WebSocket callback functions"
  {:on-open    connect!
   :on-close   disconnect!
   :on-message notify-clients!})

(defn ws-handler [request]
  (pp/pprint request)
  (async/as-channel request websocket-callbacks))

(def websocket-routes
  [["/ws" ws-handler]])

;(def app (constantly {:status 200 :body "OK"}))

(defn hello-world-handler [request]
  (ok
    (html5
      [:h1 "Hello World"]
      [:ul
       [:li [:a {:href "/time"} "What time is it?"]]
       [:li [:a {:href "/stats"} "See some system stats"]]
       [:li [:a {:href "/dump"} "Dump the request"]]])))

(defn index-handler [request]
  (ok
    (html5
      (include-css
        "https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css"
        "https://use.fontawesome.com/releases/v5.8.2/css/all.css")
      [:div#ui-root] [:script {:src "public/main.js"}]
      (include-js
        "https://code.jquery.com/jquery-3.2.1.slim.min.js"
        "https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js"
        "https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js"))))

(defn stats-handler [request]
  (ok
    (let [rt (Runtime/getRuntime) mb (* 1024.0 1024.0)]
      {:free-memory-MB  (/ (.freeMemory rt) mb)
       :max-memory-MB   (/ (.maxMemory rt) mb)
       :total-memory-MB (/ (.totalMemory rt) mb)})))

(def app
  (ring/ring-handler
    (ring/router
      [websocket-routes
       ["/" {:handler hello-world-handler}]
       ["/index" index-handler]
       eliza/routes
       ["/public/*" (ring/create-resource-handler)]
       ["/time" {:handler (fn [request] (ok (str "The time is: " (Date.))))}]
       ["/stats" {:handler stats-handler}]
       ["/dump" {:handler (fn [request] (ok (with-out-str (pp/pprint request))))}]
       (let [nrepl-handler (drawbridge.core/ring-handler)]
         ["/repl" {:handler nrepl-handler}])]
      {:data {:middleware [[wrap-defaults
                            (-> site-defaults
                                ;(assoc :static {:file "public" :files "public"})
                                (update :security dissoc :content-type-options)
                                (update :responses dissoc :content-types))]
                           wrap-json-response]}})
    (constantly (not-found "Not found"))))

(def config {::web/server {:port    3000
                           :host    "0.0.0.0"
                           :handler #'app}})

(defonce sys (partsbin/create config))


