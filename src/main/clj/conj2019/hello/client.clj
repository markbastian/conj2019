(ns conj2019.hello.client
  (:require [clj-http.client :as client]))

(def greeting-request
  {:method       :get
   :as           :json
   :url          "http://localhost:3000/greeting"})

(defn greeting [& [name]]
  (client/request
    (cond-> greeting-request
            name (assoc :query-params {:name name}))))

(comment
  (greeting)
  (greeting "Mark"))