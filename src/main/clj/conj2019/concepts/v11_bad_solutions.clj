(ns conj2019.concepts.v11-bad-solutions
  (:require [ring.util.http-response :refer [ok not-found resource-response bad-request]]
            [conj2019.concepts.dsdb :as dsdb]))

;Recall that a handler has the signature (defn handler [request])
;I need to get my db connection in that handler somehow
;Even if I could do this, how would I pass it in?
(defn not-a-handler-weapons-query-handler [conn {{:strs [name]} :params :as request}]
  (if name
    (ok (dsdb/weapons @conn [name]))
    (bad-request "Something went wrong")))

