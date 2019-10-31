(ns conj2019.concepts.api
  (:import (java.util Date)))

(defn runtime-data []
  (let [rt (Runtime/getRuntime)
        mb (* 1024.0 1024.0)]
    {:free-memory-MB  (/ (.freeMemory rt) mb)
     :max-memory-MB   (/ (.maxMemory rt) mb)
     :total-memory-MB (/ (.totalMemory rt) mb)}))

(defn time-str [] (format "The time is %s" (Date.)))

(defn add [a b] (+ a b))