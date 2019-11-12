(ns conj2019.horsemen.files-api
  (:require [clojure.java.jdbc :as j]))

(def file-table-ddl
  (j/create-table-ddl
    :files
    [[:name "varchar"]
     [:processed :timestamp]]))

(defn setup [conn]
  (j/db-do-commands
    conn
    [file-table-ddl
     "CREATE INDEX name_ix ON files ( name );"]))

(defn insert-file [conn file-info]
  (j/insert! conn :files file-info))

(defn all-processed-files [conn]
  (j/query conn "SELECT * FROM FILES"))

(comment
  ;;Create DB, set it up, insert data, get the data
  (let [conn-spec {:connection-uri "jdbc:h2:mem:comment-test-1"}]
    (with-open [c (j/get-connection conn-spec)]
      (let [conn {:connection c}]
        (setup conn)
        (insert-file conn {:name "My test file.csv" :processed #inst "2019-11-21"})
        (insert-file conn {:name "My second test file.csv" :processed #inst "2019-11-21"})
        (all-processed-files conn)
        )))

  )