(ns conj2019.system
  (:require [hiccup.page :refer [html5 include-js include-css]]
            [partsbin.core :as partsbin]
            [partsbin.immutant.web.core :as web]
            [conj2019.web.eliza-app :as eliza]
            [drawbridge.core]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults site-defaults]]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.util.http-response :refer [ok not-found resource-response]]
            [reitit.ring :as ring]
            [clj.qrgen :as qr]
            [conj2019.api.v0 :as v0]
            [conj2019.web.horsemen-app :as horsemen]))

(defn hello-world-handler [request]
  (ok
    (html5
      [:div
       [:h1 "Welcome to my simple conj demo"]
       [:img {:src "/qr" :alt "qr"}]
       [:ul
        [:li [:a {:href "/v0"} "Visit the basic static api"]]
        [:li [:a {:href "/eliza"} "Visit Eliza, a low-tech psychiatrist"]]
        [:li [:a {:href "/horsemen"} "Defeat the 4 horsemen!"]]]])))

(def app
  (ring/ring-handler
    (ring/router
      [["/" {:handler hello-world-handler}]
       v0/routes
       eliza/routes
       horsemen/routes
       ["/qr" (fn [_] (ok (qr/as-input-stream (qr/from "http://localhost:3000"))))]
       ["/public/*" (ring/create-resource-handler)]
       (let [nrepl-handler (drawbridge.core/ring-handler)]
         ["/repl" {:handler nrepl-handler}])]
      {:data {:middleware [[wrap-defaults
                            (-> site-defaults
                                (update :security dissoc :anti-forgery)
                                (update :security dissoc :content-type-options)
                                (update :responses dissoc :content-types))]
                           wrap-json-response]}})
    (constantly (not-found "Not found"))))

(def config {::web/server {:port    3000
                           :host    "0.0.0.0"
                           :handler #'app}})

(defonce sys (partsbin/create config))