(ns conj2019.supers.core
  (:require [partsbin.core :refer [create start stop restart system reset-config! restart-with]]
            [partsbin.hawk.core.core :as hawk]
            [partsbin.immutant.web.core :as web]
            [partsbin.clojure.java.jdbc.core :as jdbc]
            [partsbin.datascript.core.core :as datascript]
            [partsbin.durable-queue.core :as durable]
            [partsbin.immutant.scheduling.core :as scheduling]
            [durable-queue :as dq]
            [clojure.java.jdbc :as j]
            [integrant.core :as ig]
            [datascript.core :as d]
            [clojure.string :as cs]
            [clojure.pprint :as pp]
            [clojure.java.io :as io]
            [taoensso.timbre :as timbre]
            [reitit.core :as r]
            [reitit.ring :as ring]
            [reitit.coercion.spec]
            [ring.middleware.params :as params]
            [ring.mock.request :as mock]
            [muuntaja.middleware :as middleware]
            [ring.util.http-response :refer [ok not-found resource-response]]
            [clojure.edn :as edn])
  (:import (java.io File)
           (java.util Date)))

(def universe-query
  '[:find ?name ?universe
    :in $
    :where
    [?e :name ?name]
    [?e :universe ?universe]])

(defn supers-by-universe [dsdb]
  (d/q universe-query dsdb))

(defn echo-handler [request]
  (ok (with-out-str (pp/pprint request))))

(defn supers-handler [{:keys [dsdb]}]
  (ok (supers-by-universe @dsdb)))

(defn files-handler [{:keys [sql-conn]}]
  (ok (j/query sql-conn "SELECT * FROM FILES")))

(def routes
  [["/echo" {:get echo-handler}]
   ["/supers" {:get supers-handler}]
   ["/files" {:get files-handler}]])

(def router
  (ring/router
    routes
    {:data {:coercion   reitit.coercion.spec/coercion
            :middleware [params/wrap-params
                         middleware/wrap-format]}}))

(def handler
  (ring/ring-handler
    router
    (constantly (not-found "Not found"))))

(def file-table-ddl
  (j/create-table-ddl
    :files
    [[:name "varchar"]
     [:processed :time]]))

(defn setup [conn]
  (j/db-do-commands
    conn
    [file-table-ddl
     "CREATE INDEX name_ix ON files ( name );"]))

(defmethod ig/init-key ::jdbc/init [_ {:keys [conn]}]
  (setup conn))

(defn file-handler [{:keys [queue conn] :as ctx} {:keys [^File file kind] :as event}]
  (when (and (#{:modify :create} kind)
             (.exists file)
             (.isFile file)
             (cs/ends-with? (.getName file) ".csv"))
    (do
      (timbre/debug (str "Detected change to file: " (.getName file)))
      (with-open [r (io/reader file)]
        (timbre/debug "Adding data to queue.")
        (doseq [line (line-seq r)
                :let [[name universe & powers] (map cs/trim (cs/split line #","))]]
          (dq/put! queue :my-queue {:name     name
                                    :universe universe
                                    :powers   powers})))
      (j/insert! conn :files {:name (.getName file) :processed (Date.)})))
  ctx)

(defn queue->dsdb [{:keys [queue-name queue dsdb]}]
  ;(timbre/debug "Checking for new items in queue...")
  (when-some [task (dq/take! queue queue-name 10 nil)]
    (do
      (timbre/debug "Putting data into datascript")
      (d/transact! dsdb [@task])
      (dq/complete! task))))

(def schema {:name   {:db/unique :db.unique/identity}
             :powers {:db/cardinality :db.cardinality/many}})

(def config
  {::jdbc/connection       {:connection-uri "jdbc:h2:mem:mem_only"}
   ::jdbc/init             {:conn (ig/ref ::jdbc/connection)}
   ::datascript/connection schema
   ::hawk/watch            {:groups [{:paths   ["example"]
                                      :handler #'file-handler}]
                            :queue  (ig/ref ::durable/queues)
                            :conn   (ig/ref ::jdbc/connection)}
   ::durable/queues        {:delete-on-halt? true
                            :directory       "/tmp"}
   ::scheduling/job        {:job        #'queue->dsdb
                            :queue-name :my-queue
                            :schedule   {:in [5 :seconds] :every :second}
                            :queue      (ig/ref ::durable/queues)
                            :dsdb       (ig/ref ::datascript/connection)}
   ::web/server            {:host     "0.0.0.0"
                            :port     3000
                            :sql-conn (ig/ref ::jdbc/connection)
                            :dsdb     (ig/ref ::datascript/connection)
                            :handler  #'handler}})

(defonce sys (create config))

(comment
  (let [{:keys [::jdbc/connection]} (system sys)]
    (j/query connection "SELECT 1"))

  ;Test route matching
  (let [router (ring/router routes)]
    (r/match-by-path router "/supers"))

  (require '[ring.mock.request :as mock])

  (handler (mock/request :get "/echo"))

  ;Independently test routing
  ;Has *NO* knowledge of function implementation, handler, etc.
  (let [router (ring/router routes)]
    (r/match-by-path router "/supers"))

  (def sample-data
    [{:name     "Spiderman"
      :universe "Marvel"
      :powers   ["Peter Tingle" "Spidersense"]}
     {:name     "Batman"
      :universe "DC"
      :powers   ["Utility Belt" "Rich"]}
     {:name     "Superman"
      :universe "DC"
      :powers   ["Flight" "Strength"]}
     {:name     "Hulk"
      :universe "Marvel"
      :powers   ["Strength"]}
     {:name     "Vision"
      :universe "Marvel"
      :powers   ["Flight"]}])

  ;Independently test the logic being employed by the service
  ;Has *NO* knowlege of the service
  (let [db (d/db-with
             (d/empty-db schema)
             sample-data)]
    (supers-by-universe db))

  ;Independently test the handler
  ;Has *NO* knowlege of the server or the rest of the system
  (let [dsdb (doto
               (d/create-conn)
               (d/transact! sample-data))
        request (-> (mock/request :get "/supers")
                    (assoc :dsdb dsdb))]
    (-> request
        handler
        :body
        slurp
        edn/read-string)))