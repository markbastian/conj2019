(ns conj2019.concepts.web
  (:require [clojure.pprint :as pp]
            [conj2019.concepts.api :as api]
            [ring.util.http-response :refer [ok not-found resource-response bad-request]]
            [conj2019.concepts.dsdb :as dsdb]
            [datascript.core :as d]
            [reitit.ring :as ring]
            [muuntaja.middleware :as middleware]
            [ring.middleware.params :as params]))

;Independent handlers. I can reuse these!
(defn time-handler [_] (ok (api/time-str)))

(defn add-handler [{{:strs [a b]} :params :as request}]
  (let [x (Long/parseLong a)
        y (Long/parseLong b)]
    (ok {:x x :y y :sum (api/add x y)})))

(defn stats-handler [_] (ok (api/runtime-data)))

(defn dump-handler [request] (ok (with-out-str (pp/pprint request))))

(defn weapons-query-handler [{{:strs [name]} :params :keys [conn] :as request}]
  (if name
    (ok (dsdb/weapons @conn [name]))
    (bad-request "Something went wrong")))

(defn add-weapon-handler [{:keys [params conn] :as request}]
  (let [data (map (fn [[k v]] {:name k :weapon v}) params)]
    (do
      (d/transact! conn data)
      (ok (dsdb/weapons @conn (keys params))))))

;Routes as data!
(def basic-routes
  [["/time" {:get time-handler}]
   ["/add" {:get add-handler}]
   ["/stats" {:get stats-handler}]
   ["/dump" {:get dump-handler}]])

(def db-routes
  [["/weapons" weapons-query-handler]
   ["/add_weapon" add-weapon-handler]])

;The router
(def router
  (ring/router
    [basic-routes
     db-routes]
    {:data {:coercion   reitit.coercion.spec/coercion
            :middleware [params/wrap-params
                         middleware/wrap-format]}}))

;The handler
(def handler
  (ring/ring-handler
    router
    (constantly (not-found "Not found"))))