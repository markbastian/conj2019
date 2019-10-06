(ns conj2019.sandwich
  (:require [datascript.core :as d]))

(def sandwich-db
  (-> (d/empty-db {:name       {:db/unique :db.unique/identity}
                   :attributes {:db/cardinality :db.cardinality/many}})
      (d/db-with [{:name :taco :attributes [:crunchy :nobread :tortilla]}
                  {:name :toast :attributes [:crunchy :bread]}
                  {:name :pb-and-j :attributes [:bread :twoslices]}
                  {:name :blt :attributes [:bread :twoslices]}
                  {:name :monte-cristo :attributes [:bread :twoslices :fried]}
                  {:name :chimichanga :attributes [:tortilla :fried]}])))

(->> (d/q
       '[:find ?tn ?a
         :in $ ?n
         :where
         [?s :name ?n]
         [?s :attributes ?a]
         [?t :attributes ?a]
         [?t :name ?tn]
         [(not= ?t ?s)]]
       sandwich-db
       :monte-cristo)
     (reduce (fn [m [k v]] (update m k conj v)) {}))