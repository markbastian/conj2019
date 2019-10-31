(ns conj2019.concepts.v20-intro-to-integrant
  (:require [immutant.web :as immutant]
            [integrant.core :as ig]
            [datascript.core :as d]
            [conj2019.concepts.web :as w]
            [conj2019.concepts.dsdb :as dsdb]
            [conj2019.concepts.middleware :as mw]))

(defmethod ig/init-key :web/server [_ {:keys [handler host port] :as m}]
  (immutant/run
    (mw/wrap-component handler m)
    :host host
    :port port))

(defmethod ig/halt-key! :web/server [_ server]
  (immutant/stop server))

(defmethod ig/init-key :datascript/conn [_ schema]
  (d/create-conn schema))

(def config
  {:web/server      {:host    "0.0.0.0"
                     :port    3000
                     :handler #'w/handler
                     :conn    (ig/ref :datascript/conn)}
   :datascript/conn dsdb/schema})

;Convenience functions for launching a system from a config
(defn start [sys]
  (if @sys
    @sys
    (reset! sys (ig/init config))))

(defn stop [sys]
  (when @sys
    (swap! sys ig/halt!)))

(defn restart [sys]
  (doto sys stop start))

;This is a ns local system, but I can do any number of implementations
;Under the covers it is all ig/init
(defonce sys (atom nil))

(comment
  (start sys)

  ;Populate the db
  (-> @sys :datascript/conn (d/transact! dsdb/sample-data))

  (stop sys)
  (restart sys))