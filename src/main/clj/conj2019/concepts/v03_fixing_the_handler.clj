(ns conj2019.concepts.v03-fixing-the-handler
  (:require [clojure.pprint :as pp]
            [immutant.web :as immutant]
            [ring.mock.request :as mock]
            [conj2019.concepts.api :refer [runtime-data]]
            [ring.util.http-response :refer [ok not-found resource-response]]
            [reitit.coercion.spec]
            [muuntaja.middleware :as middleware]
            [ring.middleware.params :as params]
            [reitit.ring :as ring]
            [reitit.core :as r]
            [clj-http.client :as client]
            [cheshire.core :as ch])
  (:import (java.util Date)))

;Standalone functions
(defn time-str [] (format "The time is %s" (Date.)))
(defn add [a b] (+ a b))

;Independent handlers. I can reuse these!
(defn time-handler [_] (ok (time-str)))

(defn add-handler [{{:strs [a b]} :params :as request}]
  (let [x (Long/parseLong a)
        y (Long/parseLong b)]
    (ok {:x x :y y :sum (add x y)})))

(defn stats-handler [_] (ok (runtime-data)))

(defn dump-handler [request] (ok (with-out-str (pp/pprint request))))

;Routes as data!
(def route-data
  [["/time" {:get time-handler}]
   ["/add" {:get add-handler}]
   ["/stats" {:get stats-handler}]
   ["/dump" {:get dump-handler}]])

;Router - Independent concern from the handler
(def router
  (ring/router
    route-data
    {:data {:coercion   reitit.coercion.spec/coercion
            :middleware [params/wrap-params
                         middleware/wrap-format]}}))

;Finally, I put it together to create a handler
(def handler
  (ring/ring-handler
    router
    (constantly (not-found "Not found"))))

(def config {:port 3000 :host "0.0.0.0"})

(comment
  (def server (immutant/run #'handler config))

  (immutant/stop server)

  ;Execute functions independently
  (time-str)
  ;Test function independently
  (assert (= 42 (add 40 2)))
  (= 42 (add 40 2))

  ;Test my handlers (global or individual) in isolation
  (handler (mock/request :get "/time"))
  (time-handler (mock/request :get "/time"))
  (-> (mock/request :get "/add?a=1&b=42")
      handler
      :body
      slurp
      (ch/parse-string true))

  ;Test my routing logic in isolation
  (some? (r/match-by-path router "/time"))
  (nil? (r/match-by-path router "/foo"))

  ;Test the server
  (:body
    (client/request
      {:method       :get
       :as           :json
       :url          "http://localhost:3000/add"
       :query-params {:a 40 :b 2}}))
  )

