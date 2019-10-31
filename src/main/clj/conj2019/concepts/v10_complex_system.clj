(ns conj2019.concepts.v10-complex-system
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

;Problem - Global stateful objects.
; I got away with this before because there was only one (*system* wasn't complex)
(def conn (doto
            (d/create-conn dsdb/schema)
            (d/transact! dsdb/sample-data)))

;Ugh! Global conn!
(defn weapons-query-handler [{{:strs [name]} :params :as request}]
  (if name
    (ok (dsdb/weapons @conn [name]))
    (bad-request "Something went wrong")))

(defn add-weapon-handler [{:keys [params] :as request}]
  (let [data (map (fn [[k v]] {:name k :weapon v}) params)]
    (do
      (d/transact! conn data)
      (ok (dsdb/weapons @conn (keys params))))))

(def db-routes
  [["/weapons" weapons-query-handler]
   ["/add_weapon" add-weapon-handler]])

(def router
  (ring/router
    ;Combine old and new routes - Easy to add
    [w/basic-routes
     db-routes]
    {:data {:coercion   reitit.coercion.spec/coercion
            :middleware [params/wrap-params
                         middleware/wrap-format]}}))

(def handler
  (ring/ring-handler
    router
    (constantly (not-found "Not found"))))

(def config {:port 3000 :host "0.0.0.0"})

(def server (immutant/run #'handler config))

(comment
  (immutant/stop server)

  ;Hmmm, stateful. Not so good for testing
  (-> (mock/request :get "/weapons?name=Mark")
      handler
      :body
      slurp
      ch/parse-string)
  ;=> ("REPL" "Data")
  (-> (mock/request :get "/add_weapon?Mark=REBL")
      handler
      :body
      slurp
      ch/parse-string)
  ;=> ("REPL" "Data" "REBL")
  (-> (mock/request :get "/weapons?name=Mark")
      handler
      :body
      slurp
      ch/parse-string)
  ;=> ("REPL" "Data" "REBL")
  )