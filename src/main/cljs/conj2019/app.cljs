;(shadow.cljs.devtools.api/nrepl-select :app)
(ns conj2019.app
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [ajax.core :refer [GET POST]]
            [reagent.core :as r]
            [haslett.client :as ws]
            [haslett.format :as hfmt]
            [cljs.core.async :as a :refer [<! >! put!]]))

(defonce state (r/atom {}))

(defn log [m] (.log js/console m))

(defn handle-ws-traffic [state]
      (go-loop [message (<! (get-in @state [:stream :source]))]
               (when message
                     (do
                       (prn message)
                       (recur (<! (get-in @state [:stream :source])))))))

(defn connect-ws [state]
  (let [url (str "ws://" (.-host (.-location js/window)) "/ws")]
    (go-loop [stream (<! (ws/connect url {:format hfmt/json}))]
             (swap! state assoc :stream stream)
             (handle-ws-traffic state)
             (let [close-status (<! (get-in @state [:stream :close-status]))]
               (prn (str "Socket closing due to: " close-status ". Reconnecting..."))
               (recur (<! (ws/connect url {:format hfmt/json})))))))

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