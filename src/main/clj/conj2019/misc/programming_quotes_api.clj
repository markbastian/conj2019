(ns conj2019.misc.programming-quotes-api
  "Examples built around https://github.com/skolakoda/programming-quotes-api"
  (:require [clj-http.client :as client]
            [clojure.string :as cs]))

(defn all-quotes []
  (let [request {:method :get
                 :as :json-strict
                 :url "https://programming-quotes-api.herokuapp.com/quotes"}]
    (:body (client/request request))))

(comment
  (let [request {:method :get
                 :as :json-strict
                 :url "https://programming-quotes-api.herokuapp.com/quotes"}]
    (:body (client/request request)))

  (->> (all-quotes)
       (filter (fn [{:keys [en]}]
                 (some-> en
                         cs/upper-case
                         (cs/includes? "COMPLEX"))))
       (map (juxt :en :author))))
