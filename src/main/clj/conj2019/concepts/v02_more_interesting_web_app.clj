(ns conj2019.concepts.v02-more-interesting-web-app
  (:require [clojure.pprint :as pp]
            [immutant.web :as immutant]
            [ring.mock.request :as mock])
  (:import (java.util Date)))

;This is a separate function that is useful all by itself
;It has no encumbrances and can go in its own ns
(defn runtime-data []
  (let [rt (Runtime/getRuntime)
        mb (* 1024.0 1024.0)]
    {:free-memory-MB  (/ (.freeMemory rt) mb)
     :max-memory-MB   (/ (.maxMemory rt) mb)
     :total-memory-MB (/ (.totalMemory rt) mb)}))

;Externalize the handler:
;It is now independent of its calling scope
(defn handler [{:keys [uri] :as request}]
  (case uri
    "/time" {:status 200 :body (format "The time is %s" (Date.))}
    "/stats" {:status 200 :body (pr-str (runtime-data))}
    "/dump" {:status 200 :body (with-out-str (pp/pprint request))}
    {:status 404 :body (format "Unknown path: %s" uri)}))

(def config {:port 3000 :host "0.0.0.0"})

;Still have this ns-global def that runs when loaded :(
(def server
  (immutant/run
    ;Var quote to allow refreshing of the handler
    #'handler
    config))

(comment
  (immutant/stop server)
  ;I can execute and test this all by itself!
  (handler (mock/request :get "/time")))
