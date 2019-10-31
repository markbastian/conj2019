(ns conj2019.concepts.dsdb
  (:require [datascript.core :as d]))

(def schema
  {:name  {:db/unique :db.unique/identity}
   :items {:db/cardinality :db.cardinality/many}})

(def item-query
  '[:find [?item ...]
    :in $ ?name
    :where
    [?e :name ?name]
    [?e :item ?item]])

(defn items [db name]
  (d/q item-query db name))