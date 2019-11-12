(ns conj2019.horsemen.core-test
  (:require [clojure.test :refer :all]
            [conj2019.horsemen.core :refer :all]
            [reitit.core :as r]
            [cheshire.core :as ch]
            [ring.mock.request :as mock]
            [conj2019.horsemen.weapons-api :as w]
            [datascript.core :as d]))

(deftest router-weapons-test
  (testing "Test the weapons route to make sure it works"
    (is (some? (r/match-by-path router "/weapons")))
    (is (nil? (r/match-by-path router "/weapon")))))

(deftest get-weapons-test
  (testing "Testing the no-arg implementation of the weapons endpoint")
  (let [conn (doto (d/create-conn w/schema)
               (d/transact! w/sample-data))
        request (assoc
                  (mock/request :get "/weapons")
                  :conn conn)
        response (handler request)]
    (is = (-> response
              :body
              slurp
              (ch/parse-string true)
              {:Mark          ["Data" "REPL"]
               :Famine        ["Scales"]
               :Pestilence    ["Bow" "Arrow"]
               :Complexity    ["Spring"]
               :Distance      ["Cloud" "Distributed Computing"]
               :Unfamiliarity ["Ignorance"]
               :Opacity       ["Objects"]
               :War           ["Sword"]}))))