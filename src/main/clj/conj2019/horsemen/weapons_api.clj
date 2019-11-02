(ns conj2019.horsemen.weapons-api
  (:require [datascript.core :as d]
            [clojure.string :as cs]))

(def schema
  {:name   {:db/unique :db.unique/identity}
   :weapon {:db/cardinality :db.cardinality/many}})

(def weapons-query
  '[:find ?name ?weapon
    :in $ [?name ...]
    :where
    [?e :name ?name]
    [?e :weapon ?weapon]])

(def everybodys-weapons-query
  '[:find ?name ?weapon
    :in $
    :where
    [?e :name ?name]
    [?e :weapon ?weapon]])

(def value-query
  '[:find [?v ...]
    :in $ ?a
    :where
    [_ ?a ?v]])

(defn weapons [db names]
  (->> (d/q weapons-query db names)
       (reduce (fn [m [k v]] (update m k conj v)) {})))

(defn everybodys-weapons [db]
  (->> (d/q everybodys-weapons-query db)
       (reduce (fn [m [k v]] (update m k conj v)) {})))

(defn values [db attribute]
  (d/q value-query db attribute))

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

(comment
  (->> sample-data
       (group-by :name)
       (map (fn [[k v]] (cs/join "," (filter identity (cons k (map :weapon v))))))
       (cs/join "\n"))

  (let [db (d/db-with (d/empty-db schema) sample-data)]
    (d/q
      '[:find [?name ...]
        :in $
        :where
        [_ :name ?name]]
      db))

  (let [db (d/db-with (d/empty-db schema) sample-data)]
    (d/q
      '[:find [?v ...]
        :in $ ?a
        :where
        [_ ?a ?v]]
      db :name))

  (let [db (d/db-with (d/empty-db schema) sample-data)]
    (weapons db ["Mark" "Complexity"]))

  (let [db (d/db-with (d/empty-db schema) sample-data)]
    (everybodys-weapons db))

  )