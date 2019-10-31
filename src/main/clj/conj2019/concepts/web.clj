(ns conj2019.concepts.web
  (:require [clojure.pprint :as pp]
            [conj2019.concepts.api :as api]
            [ring.util.http-response :refer [ok not-found resource-response]]))

;Independent handlers. I can reuse these!
(defn time-handler [_] (ok (api/time-str)))

(defn add-handler [{{:strs [a b]} :params :as request}]
  (let [x (Long/parseLong a)
        y (Long/parseLong b)]
    (ok {:x x :y y :sum (api/add x y)})))

(defn stats-handler [_] (ok (api/runtime-data)))

(defn dump-handler [request] (ok (with-out-str (pp/pprint request))))

;Routes as data!
(def basic-routes
  [["/time" {:get time-handler}]
   ["/add" {:get add-handler}]
   ["/stats" {:get stats-handler}]
   ["/dump" {:get dump-handler}]])