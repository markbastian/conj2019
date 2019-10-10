;(shadow.cljs.devtools.api/nrepl-select :app)
(ns conj2019.app
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [ajax.core :refer [GET POST]]
            [reagent.core :as r]
            [haslett.client :as ws]
            [haslett.format :as hfmt]
            [cljs.core.async :as a :refer [<! >! put!]]
            [clojure.walk :refer [keywordize-keys]]))

(defonce state (r/atom {}))

(defn log [m] (.log js/console m))

(defmulti handle-message (fn [{:keys [fn]}] (keyword fn)))

(defmethod handle-message :eliza [{:keys [message]}]
  (log message))

(defn ws-send [state message]
  (when-let [sink (get-in @state [:stream :sink])]
    (put! sink message)))

(defn handle-ws-traffic [state]
  (go-loop
    [message (<! (get-in @state [:stream :source]))]
    (when message
      (do
        (handle-message (keywordize-keys message))
        (recur (<! (get-in @state [:stream :source])))))))

(defn connect-ws [state]
  (let [s (r/cursor state [:stream])]
    (when-not @s
      (let [url (str "ws://" (.-host (.-location js/window)) "/ws")]
        (go-loop
          [stream (<! (ws/connect url {:format hfmt/transit}))]
          (reset! s stream)
          (handle-ws-traffic state)
          (let [close-status (<! (:close-status @s))]
            (prn (str "Socket closing due to: " close-status ". Reconnecting..."))
            (recur (<! (ws/connect url {:format hfmt/transit})))))))))

(defn render-time [state]
  (let [time (r/cursor state [:time])
        request-time (fn [] (GET "/v0/time" :handler (fn [response] (reset! time response))))]
    (fn [state]
      (js/setTimeout request-time 1000)
      [:h4 @time])))

(defn render [state]
  (let [stream (r/cursor state [:stream])]
    (fn [state]
      [:div
       [:h1 [:span "Hello World"
             (if @stream
               [:i.fas.fa-link.text-success]
               [:i.fas.fa-link.text-warning])]]
       [:div.form-group
        [:label {} "Email address"]
        [:input.form-control {:on-blur (fn [x] (prn "fesaff"))
                              :type "text"
                              :placeholder "Enter your response"}]]
       [:ul
        [:li [render-time state]]
        [:li [:a {:href "v0/stats"} "See some system stats"]]
        [:li [:a {:href "v0//dump"} "Dump the request"]]]])))

(defn ^:dev/after-load ui-root []
  (connect-ws state)
  (r/render [render state] (.getElementById js/document "ui-root")))

(defn init []
  (connect-ws state)
  (r/render [render state] (.getElementById js/document "ui-root")))