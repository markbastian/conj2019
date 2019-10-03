(ns conj2019.core
  (:require [partsbin.core :as partsbin]
            [partsbin.immutant.web.core :as web]
            [clojure.pprint :as pp]
            [reitit.ring :as ring]
            [hiccup.page :refer [html5]]
            [ring.util.http-response :refer [ok not-found]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults site-defaults]]
            [drawbridge.core]
            [conj2019.eliza-app :as eliza])
  (:import (java.util Date)))

;(def app (constantly {:status 200 :body "OK"}))

(defn hello-world-handler [request]
  (ok (html5
        [:h1 "Hello World"]
        [:ul
         [:li [:a {:href "/time"} "What time is it?"]]
         [:li [:a {:href "/stats"} "See some system stats"]]
         [:li [:a {:href "/dump"} "Dump the request"]]])))

(defn stats-handler [request]
  (ok
    (let [rt (Runtime/getRuntime) mb (* 1024.0 1024.0)]
      (with-out-str
        (pp/pprint
          {:free-memory-MB  (/ (.freeMemory rt) mb)
           :max-memory-MB   (/ (.maxMemory rt) mb)
           :total-memory-MB (/ (.totalMemory rt) mb)})))))

(def app
  (ring/ring-handler
    (ring/router
      [["/" {:handler hello-world-handler}]
       eliza/routes
       ["/time" {:handler (fn [request] (ok (str "The time is: " (Date.))))}]
       ["/stats" {:handler stats-handler}]
       ["/dump" {:handler (fn [request] (ok (with-out-str (pp/pprint request))))}]
       (let [nrepl-handler (drawbridge.core/ring-handler)]
         ["/repl" {:handler nrepl-handler}])]
      {:data {:middleware [[wrap-defaults
                            (assoc-in api-defaults [:responses :content-types] false)]]}})
    (constantly (not-found "Not found"))))

(def config {::web/server {:port    3000
                           :host    "0.0.0.0"
                           :handler #'app}})

(defonce sys (partsbin/create config))


