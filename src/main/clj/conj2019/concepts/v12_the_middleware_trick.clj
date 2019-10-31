(ns conj2019.concepts.v12-the-middleware-trick
  (:require [immutant.web :as immutant]
            [ring.mock.request :as mock]
            [conj2019.concepts.api :refer [runtime-data]]
            [conj2019.concepts.web :as w]
            [ring.util.http-response :refer [ok not-found resource-response bad-request]]
            [reitit.coercion.spec]
            [muuntaja.middleware :as middleware]
            [ring.middleware.params :as params]
            [reitit.ring :as ring]
            [datascript.core :as d]
            [conj2019.concepts.dsdb :as dsdb]
            [cheshire.core :as ch]))

;This is what I want - the connection in the handler
(defn weapons-query-handler [{{:strs [name]} :params :keys [conn] :as request}]
  (if name
    (ok (dsdb/weapons @conn [name]))
    (bad-request "Something went wrong")))

(defn add-weapon-handler [{:keys [params conn] :as request}]
  (let [data (map (fn [[k v]] {:name k :weapon v}) params)]
    (do
      (d/transact! conn data)
      (ok (dsdb/weapons @conn (keys params))))))

(def db-routes
  [["/weapons" weapons-query-handler]
   ["/add_weapon" add-weapon-handler]])

(def router
  (ring/router
    [w/basic-routes
     db-routes]
    {:data {:coercion   reitit.coercion.spec/coercion
            :middleware [params/wrap-params
                         middleware/wrap-format]}}))

(def handler
  (ring/ring-handler
    router
    (constantly (not-found "Not found"))))

(defn wrap-component [handler component]
  (fn [request]
    (handler
      (into component request))))

(def config {:port 3000 :host "0.0.0.0"})

;I've got generic, server, etc. agnostic functions
;My system is still complicated, though
(def server
  (let [conn (doto
               (d/create-conn dsdb/schema)
               (d/transact! dsdb/sample-data))]
    (immutant/run
      (wrap-component #'handler {:conn conn})
      config)))

(comment
  (immutant/stop server)

  ;Yay! I can mock it up as a function
  ;I now have all the goodness from before
  (let [conn (doto
               (d/create-conn dsdb/schema)
               (d/transact! dsdb/sample-data))]
    (-> (mock/request :get "/weapons?name=Mark")
        (assoc :conn conn)
        handler
        :body
        slurp
        ch/parse-string))
  ;=> ("REPL" "Data")
  )