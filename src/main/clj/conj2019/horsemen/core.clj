(ns conj2019.horsemen.core
  (:require [partsbin.core :refer [create start stop restart system reset-config!]]
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
            [reitit.ring :as ring]
            [reitit.coercion.spec]
            [reitit.core :as r]
            [ring.middleware.params :as params]
            [ring.mock.request :as mock]
            [muuntaja.middleware :as middleware]
            [ring.util.http-response :refer [ok not-found resource-response bad-request]]
            [conj2019.horsemen.weapons-api :as w]
            [conj2019.horsemen.files-api :as f]
            [cheshire.core :as ch]
            [clj-http.client :as client]
            [clojure.edn :as edn])
  (:import (java.io File)
           (java.util Date)))

;Handlers
(defn echo-handler [request]
  (ok (with-out-str (pp/pprint request))))

(defn weapons-query-handler [{{:strs [name]} :params :keys [conn] :as request}]
  (ok
    (if name
      (w/weapons @conn [name])
      (w/everybodys-weapons @conn))))

(defn add-weapons-handler [{:keys [params conn] :as request}]
  (println params)
  (let [data (map (fn [[k v]] {:name k :weapon v}) params)]
    (do
      (d/transact! conn data)
      (ok (w/weapons @conn (keys params))))))

(defn files-handler [{:keys [sql-conn]}]
  (ok (f/all-processed-files sql-conn)))

;Routes - All data
(def basic-routes
  [["/echo" {:get echo-handler}]
   ["/files" {:get files-handler}]])

(def weapons-routes
  [["/weapons" weapons-query-handler]
   ["/add_weapon" add-weapons-handler]])

;Router
(def router
  (ring/router
    ;We were able to compose the routes here
    [basic-routes weapons-routes]
    {:data {:coercion   reitit.coercion.spec/coercion
            :middleware [params/wrap-params
                         middleware/wrap-format]}}))

;Global handler
(def handler
  (ring/ring-handler
    router
    (constantly (not-found "Not found"))))

(defmethod ig/init-key ::jdbc/init [_ {:keys [conn]}]
  (f/setup conn))

(defn line->record [line]
  (let [[name & weapons] (map cs/trim (cs/split line #","))]
    (cond-> {:name name}
            (seq weapons)
            (assoc :weapon weapons))))

(defn file-handler [{:keys [queue conn] :as ctx} {:keys [^File file kind] :as event}]
  (when (and (#{:modify :create} kind)
             (.exists file)
             (.isFile file)
             (cs/ends-with? (.getName file) ".csv"))
    (do
      (timbre/debug (str "Detected change to file: " (.getName file)))
      (with-open [r (io/reader file)]
        (timbre/debug "Adding data to queue.")
        (doseq [line (line-seq r)]
          (dq/put! queue :my-queue line)))
      (f/insert-file conn {:name (.getName file) :processed (Date.)})))
  ctx)

(defn queue->dsdb [{:keys [queue-name queue dsdb]}]
  ;(timbre/debug "Checking for new items in queue...")
  (when-some [task (dq/take! queue queue-name 10 nil)]
    (do
      (timbre/debug "Putting data into datascript")
      (d/transact! dsdb [(line->record @task)])
      (dq/complete! task))))

(def config
  {::jdbc/connection       {:connection-uri "jdbc:h2:mem:mem_only"}
   ::jdbc/init             {:conn (ig/ref ::jdbc/connection)}
   ::datascript/connection w/schema
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
                            :conn     (ig/ref ::datascript/connection)
                            :handler  #'handler}})

(defonce sys (create config))

(comment
  ;;Access live system components
  (let [{:keys [::jdbc/connection]} (system sys)]
    (j/query connection "SELECT 1"))

  (let [{:keys [::datascript/connection]} (system sys)]
    connection)

  ;;Develop queries against the live system
  (let [{:keys [::datascript/connection]} (system sys)
        db @connection]
    (d/q
      '[:find ?name ?weapon
        :in $
        :where
        [?e :weapon ?weapon]
        [?e :name ?name]]
      db))

  (let [{:keys [::datascript/connection]} (system sys)
        db @connection]
    (w/weapons db ["Mark" "War" "Complexity"]))

  ;Test route matching
  ;Has *NO* knowledge of function implementation, handler, etc.
  (r/match-by-path router "/weapons")
  (r/match-by-path router "/weapon")

  ;Test global handler
  (handler (mock/request :get "/echo"))

  ;Even test a handler that requires a component
  (let [conn (doto (d/create-conn w/schema)
               (d/transact! w/sample-data))]
    (-> (mock/request :get "/weapons")
        (assoc :conn conn)
        handler
        :body
        slurp
        (ch/parse-string true)))

  ;Global handler with query params
  (let [conn (doto (d/create-conn w/schema)
               (d/transact! w/sample-data))]
    (-> (mock/request :get "/weapons?name=Complexity")
        (assoc :conn conn)
        handler
        :body
        slurp
        (ch/parse-string true)))

  ;Mock a specific handler (not the global)
  (let [conn (doto (d/create-conn w/schema)
               (d/transact! w/sample-data))]
    (-> (mock/request :get "/weapons")
        (assoc :conn conn)
        weapons-query-handler
        :body))

  ;Business logic/API - Create sample db
  (let [db (d/db-with
             (d/empty-db w/schema)
             w/sample-data)]
    (w/weapons db ["Mark" "Opacity"]))

  ;;Finally, I can test against the entire system
  (let [request {:method :get
                 :as     :json
                 :url    "http://localhost:3000/weapons"}]
    (->> (client/request request)
         :body
         (mapv (fn [[k v]]
                 {:name    k
                  :weapons v}))))

  (let [request {:method :get
                 :as     :json
                 :url    "http://localhost:3000/weapons"}]
    (->> (client/request request)
         :body))

  (let [request {:method       :get
                 :as           :json
                 :url          "http://localhost:3000/weapons"
                 :query-params {:name "Mark"}}]
    (get (client/request request) :body))

  (let [request {:method       :get
                 :as           :json
                 :url          "http://localhost:3000/weapons"
                 :query-params {:name "Famine"}}]
    (->> (client/request request)
         :body
         println))

  (require '[clj-http.client :as client])

  (let [request {:method       :get
                 :as           :json-string-keys
                 :url          "http://localhost:3000/weapons"
                 :query-params {:name "Famine"}}
        response (:body (client/request request))]
    (println response))
  )