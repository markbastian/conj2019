(ns conj2019.api.v0
  (:require [hiccup.page :refer [html5 include-js include-css]]
            [ring.util.http-response :refer [ok not-found]]
            [clojure.pprint :as pp])
  (:import (java.util Date)))

(def web-page
  "A data"
  (html5
    [:h1 "Welcome to my API, Version 0"]
    [:h2 "This is just a static web page that doesn't do anything super interesting."]
    [:h3 "Design-wise, it does demonstrate good, simple architecture, though."]
    [:ul
     [:li [:a {:href "/v0/time"} "What time is it?"]]
     [:li [:a {:href "/v0/stats"} "See some system stats"]]
     [:li [:a {:href "/v0/dump"} "Dump the request"]]]))

(defn runtime-data
  "An 'API' function to be wrapped. Note that this has nothing to do with the web/REST or anything else. It's its own
  function and should be treated as such. In a larger project, it would go in another namespace."
  []
  (let [rt (Runtime/getRuntime) mb (* 1024.0 1024.0)]
    {:free-memory-MB  (/ (.freeMemory rt) mb)
     :max-memory-MB   (/ (.maxMemory rt) mb)
     :total-memory-MB (/ (.totalMemory rt) mb)}))

(def routes
  "Data-driven routes."
  [["/v0" {:handler (constantly (ok web-page))}]
   ["/v0"
    ["/time" {:handler (fn [_] (ok (str "The time is: " (Date.))))}]
    ["/stats" {:handler (fn [_] (ok (runtime-data)))}]
    ["/dump" {:handler (fn [request] (ok (with-out-str (pp/pprint request))))}]]])

(comment
  (require '[reitit.ring :as ring])
  (require '[reitit.core :as r])
  (let [router (ring/router routes)]
    (r/match-by-path router "/v0")))