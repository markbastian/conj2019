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

(defn ws-send [state message]
  (when-let [sink (get-in @state [:stream :sink])]
    (put! sink message)))

(defn handle-ws-traffic [state]
  (go-loop
    [message (<! (get-in @state [:stream :source]))]
      (when message
        (do
          (prn message)
          (recur (<! (get-in @state [:stream :source])))))))

(defn connect-ws [state]
  (let [url (str "ws://" (.-host (.-location js/window)) "/ws")]
    (go-loop
      [stream (<! (ws/connect url {:format hfmt/edn}))]
        (swap! state assoc :stream stream)
        (handle-ws-traffic state)
        (let [close-status (<! (get-in @state [:stream :close-status]))]
          (prn (str "Socket closing due to: " close-status ". Reconnecting..."))
          (recur (<! (ws/connect url {:format hfmt/edn})))))))

(defn render-time [state]
  (let [time (r/cursor state [:time])
        request-time (fn [] (GET "/v0/time" :handler (fn [response] (reset! time response))))]
    (fn [state]
      (js/setTimeout request-time 1000)
      [:h4 @time])))

(defn render [state]
  [:div
   [:h1 "Hello World"]
   [:ul
    [:li [render-time state]]
    [:li [:button.btn.btn-primary
          {:type     "button"
           :on-click (fn [event] (connect-ws state))}
          "Connect WS"]]
    [:li [:a {:href "v0/stats"} "See some system stats"]]
    [:li [:a {:href "v0//dump"} "Dump the request"]]]])

(defn ^:dev/after-load ui-root []
  (r/render [render state] (.getElementById js/document "ui-root")))

(defn init []
  (r/render [render state] (.getElementById js/document "ui-root")))