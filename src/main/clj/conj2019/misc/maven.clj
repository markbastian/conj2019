(ns conj2019.misc.maven
  (:require [clj-http.client :as client]
            [clojure.string :as cs]
            [clojure.set :refer [rename-keys]]))

(def options
  {:group           :g
   :artifact        :a
   :version         :v
   :packaging       :p
   :classifier      :l
   :class-name      :c
   :full-class-name :fc
   :checksum        :1})

(def basic-search
  {:method           :get
   :url              "http://search.maven.org/solrsearch/select"
   :throw-exceptions false
   :as               :json})

(defn build-query [m]
  (cs/join " " (map (fn [[k v]] (str (name k) ":" v)) (rename-keys m options))))

(defn build-request [m]
  (assoc-in basic-search [:query-params :q] (build-query m)))

(defn find-artifacts [m]
  (->> m build-request client/request :body :response :docs))

(defn compact-form [{:keys [g a v]}]
  [(symbol (cond-> g (not= g a) (str "/" a))) v])

(defn find-artifact [m]
  (->> m
       find-artifacts
       (map (fn [{:keys [g a latestVersion]}]
              [(symbol (cond-> g (not= g a) (str "/" a))) latestVersion]))
       sort
       first))

(comment
  (build-request {:group "org.clojure" :artifact "clojure"})
  (find-artifact {:group "org.clojure" :artifact "clojure"})
  (find-artifact {:group "org.clojure"})
  (find-artifact {:artifact "clojure"})
  (->> {:a "clojure" :class-name "PersistentQueue"}
       find-artifacts
       (map compact-form)))
