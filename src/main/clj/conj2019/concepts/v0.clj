(ns conj2019.concepts.v0
  (:require [immutant.web :as immutant]
            [clojure.pprint :as pp]
            [ring.mock.request :as mock]
            [datascript.core :as d]
            [clojure.string :as cs]
            [conj2019.concepts.dsdb :as dsdb])
  (:import (java.util Date)))

(comment
  (def server
    (immutant/run
      (fn [request] {:status 200 :body "OK"})
      {:port 3000 :host "0.0.0.0"}))
  (immutant/stop server))

(defn runtime-data []
  (let [rt (Runtime/getRuntime)
        mb (* 1024.0 1024.0)]
    {:free-memory-MB  (/ (.freeMemory rt) mb)
     :max-memory-MB   (/ (.maxMemory rt) mb)
     :total-memory-MB (/ (.totalMemory rt) mb)}))

(defn handler [{:keys [uri] :as request}]
  (case uri
    "/time" {:status 200 :body (format "The time is %s" (Date.))}
    "/stats" {:status 200 :body (pr-str (runtime-data))}
    "/dump" {:status 200 :body (with-out-str (pp/pprint request))}
    {:status 404 :body (format "Unknown path: %s" uri)}))

(comment
  (def server
    (immutant/run
      #'handler
      {:port 3000 :host "0.0.0.0"}))
  (immutant/stop server))

(comment
  (def conn
    (doto
      (d/create-conn)
      (d/transact! [{:name "Pestilence" :item "Bow"}
                    {:name "Pestilence" :item "Arrow"}
                    {:name "War" :item "Sword"}
                    {:name "Famine" :item "Scales"}
                    {:name "Death"}
                    {:name "Mark" :item "REPL"}
                    {:name "Mark" :item "Data"}])))

  (defn handler [{:keys [uri query-string] :as request}]
    (case uri
      "/items" (if-some [name (second (cs/split query-string #"="))]
                 {:status 200 :body (pr-str (items @conn name))}
                 {:status 400 :body "Bad or no name specified."})
      "/time" {:status 200 :body (format "The time is %s" (Date.))}
      "/stats" {:status 200 :body (pr-str (runtime-data))}
      "/dump" {:status 200 :body (with-out-str (pp/pprint request))}
      {:status 404 :body (format "Unknown path: %s" uri)})))

;Janky one-off handler
(comment
  (defn handler [conn {:keys [uri query-string] :as request}]
    (case uri
      "/items" (if-some [name (second (cs/split query-string #"="))]
                 {:status 200 :body (pr-str (items @conn name))}
                 {:status 400 :body "Bad or no name specified."})
      "/time" {:status 200 :body (format "The time is %s" (Date.))}
      "/stats" {:status 200 :body (pr-str (runtime-data))}
      "/dump" {:status 200 :body (with-out-str (pp/pprint request))}
      {:status 404 :body (format "Unknown path: %s" uri)}))

  (def server
    (let [conn (doto
                 (d/create-conn)
                 (d/transact! [{:name "Pestilence" :item "Bow"}
                               {:name "Pestilence" :item "Arrow"}
                               {:name "War" :item "Sword"}
                               {:name "Famine" :item "Scales"}
                               {:name "Death"}
                               {:name "Mark" :item "REPL"}
                               {:name "Mark" :item "Data"}]))
          inner-handler (fn [request] (handler conn request))]
      (immutant/run
        inner-handler
        {:port 3000 :host "0.0.0.0"}))))

(comment
  (def server
    (let [conn (doto
                 (d/create-conn)
                 (d/transact! [{:name "Pestilence" :item "Bow"}
                               {:name "Pestilence" :item "Arrow"}
                               {:name "War" :item "Sword"}
                               {:name "Famine" :item "Scales"}
                               {:name "Death"}
                               {:name "Mark" :item "REPL"}
                               {:name "Mark" :item "Data"}]))
          inner-handler (fn [request] (handler conn request))]
      (immutant/run
        inner-handler
        {:port 3000 :host "0.0.0.0"}))))

(defn handler [{:keys [uri conn query-string] :as request}]
  (case uri
    "/items" (if-some [name (second (cs/split query-string #"="))]
               {:status 200 :body (pr-str (dsdb/items @conn name))}
               {:status 400 :body "Bad or no name specified."})
    "/time" {:status 200 :body (format "The time is %s" (Date.))}
    "/stats" {:status 200 :body (pr-str (runtime-data))}
    "/dump" {:status 200 :body (with-out-str (pp/pprint request))}
    {:status 404 :body (format "Unknown path: %s" uri)}))

(defn wrap-component [handler component]
  (fn [request]
    (handler (into component request))))

(comment
  (let [conn (doto
               (d/create-conn dsdb/schema)
               (d/transact! [{:name "Pestilence" :item "Bow"}
                             {:name "Pestilence" :item "Arrow"}
                             {:name "War" :item "Sword"}
                             {:name "Famine" :item "Scales"}
                             {:name "Death"}
                             {:name "Mark" :item "REPL"}
                             {:name "Mark" :item "Data"}]))
        inner-handler (wrap-component #'handler {:conn conn})]
    (immutant/run
      inner-handler
      {:port 3000 :host "0.0.0.0"}))

  (immutant/stop server))