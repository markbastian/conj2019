(ns conj2019.api.ws
  (:require [immutant.web.async :as async]
            [clojure.pprint :as pp]
            [cheshire.core :as ch]))

(defonce channels (atom #{}))

(defn connect! [channel]
  (prn "channel open")
  (swap! channels conj channel))

(defn disconnect! [channel {:keys [code reason]}]
  (prn "close code:" code "reason:" reason)
  (swap! channels #(remove #{channel} %)))

(defn handle-message [channel msg]
  (prn (keys msg)))

(defn notify-clients! [msg]
  (doseq [channel @channels]
    (async/send! channel msg)))

(defn ws-handler [request]
  ; :session/key "5230ecf9-3584-4b83-9e42-6c46b42c1537",
  (pp/pprint (get-in request [:headers "sec-websocket-key"]))
  ;Session key appears to exist after the first connection.
  (pp/pprint (get-in request [:session/key]))
  ;Or use a query param or something
  (when-not (:session/key request)
    (async/as-channel request {:on-open    #'connect!
                               :on-close   #'disconnect!
                               :on-message #'handle-message})))

(def websocket-routes
  ["/ws" ws-handler])