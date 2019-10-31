(ns conj2019.concepts.dsdb
  (:require [datascript.core :as d]))

(def schema
  {:name   {:db/unique :db.unique/identity}
   :weapon {:db/cardinality :db.cardinality/many}})

(def weapons-query
  '[:find [?weapon ...]
    :in $ [?name]
    :where
    [?e :name ?name]
    [?e :weapon ?weapon]])

(defn weapons [db names]
  (d/q weapons-query db names))

(def sample-data
  [{:name "Pestilence" :weapon "Bow"}
   {:name "Pestilence" :weapon "Arrow"}
   {:name "War" :weapon "Sword"}
   {:name "Famine" :weapon "Scales"}
   {:name "Death"}
   {:name "Mark" :weapon "REPL"}
   {:name "Mark" :weapon "Data"}
   {:name "Complexity" :weapon "Spring"}
   {:name "Unfamiliarity" :weapon "Ignorance"}
   {:name "Opacity" :weapon "Objects"}
   {:name "Distance" :weapon "Cloud"}
   {:name "Distance" :weapon "Distributed Computing"}])