(ns conj2019.core
  (:require [partsbin.core :as partsbin]
            [partsbin.immutant.web.core :as web]
            [conj2019.api.v0 :as v0]
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
            [conj2019.api.ws :as ws]))

(defn hello-world-handler [request]
  (ok
    (html5
      [:h1 "Welcome to my simple conj demo"]
      [:ul
       [:li [:a {:href "/v0"} "Visit the basic static api"]]
       [:li [:a {:href "/eliza"} "Visit Eliza, a low-tech psychiatrist"]]])))

(defn index-handler [_]
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

(def app
  (ring/ring-handler
    (ring/router
      [["/" {:handler hello-world-handler}]
       v0/routes
       eliza/routes
       ["/index" index-handler]
       ws/websocket-routes
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


