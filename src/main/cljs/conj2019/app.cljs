(ns conj2019.app
  (:require [reagent.core :as r]))

(defonce state (r/atom {}))

(defn log [m] (.log js/console m))

(defn render [state]
  [:div
   [:h1 "Hello World"]
   [:ul
    [:li [:a {:href "/time"} "What time is it?"]]
    [:li [:a {:href "/stats"} "See some system stats"]]
    [:li [:a {:href "/dump"} "Dump the request"]]]])

(defn ^:dev/after-load ui-root []
  (r/render [render state] (.getElementById js/document "ui-root")))

(defn init []
  (let [root (.getElementById js/document "ui-root")]
    (.log js/console root)
    (r/render [render state] root)))