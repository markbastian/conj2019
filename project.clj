(defproject conj2019 "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}

  :exclusions [com.fasterxml.jackson.core/jackson-core
               org.clojure/tools.reader]

  :dependencies [[org.clojure/clojure "1.10.1"]
                 [markbastian/partsbin "0.1.3-SNAPSHOT"]
                 [com.taoensso/timbre "4.10.0"]
                 [clj-http "3.10.0"]
                 [environ "1.1.0"]
                 [hawk "0.2.11"]
                 [hiccup "1.0.5"]
                 [integrant "0.7.0"]
                 [org.immutant/web "2.1.10"]
                 [org.immutant/scheduling "2.1.10"]
                 [metosin/ring-http-response "0.9.1"]
                 [ring/ring-defaults "0.3.2"]
                 [ring/ring-json "0.5.0"]
                 [ring/ring-mock "0.4.0"]
                 [nrepl "0.6.0"]
                 [nrepl/drawbridge "0.2.1"]
                 [metosin/reitit "0.3.10"]
                 [datascript "0.18.7"]
                 [cheshire "5.9.0"]
                 [clj.qrgen "0.4.0"]
                 ;Mazes!
                 [mazegen "0.1.0-SNAPSHOT"]
                 [com.h2database/h2 "1.4.200"]
                 [com.squareup/tape "1.2.3"]
                 [factual/durable-queue "0.1.6"]
                 [org.clojure/java.jdbc "0.7.10"]
                 ;For spring
                 [org.springframework.boot/spring-boot-starter-web "2.2.1.RELEASE"]
                 [org.springframework.boot/spring-boot-starter-data-jpa "2.2.1.RELEASE"]
                 ;jetbrains Contract annotation
                 [org.jetbrains/annotations "18.0.0"]
                 ;
                 [org.clojure/tools.reader "1.3.2"]
                 [com.fasterxml.jackson.core/jackson-core "2.10.1"]
                 ]

  :main conj2019.full_demo.core

  :min-lein-version "2.9.1"

  :plugins [[lein-cljsbuild "1.1.5" :exclusions [[org.clojure/clojure]]]
            [lein-uberwar "0.2.1"]
            [lein-beanstalk "0.2.7"]
            [juxt/lein-dockerstalk "0.1.0"]
            [lein-zip "0.1.1"]]

  :source-paths ["src/main/clj" "src/main/cljc"]
  :test-paths ["src/test/clj" "src/test/cljc"]
  :resource-paths ["src/main/resources"]
  :java-source-paths ["src/main/java"]

  :cljsbuild {:builds [{:id           "dev"
                        :source-paths ["src/main/cljs" "src/main/cljc"]}]}

  :profiles {:cljs     {:dependencies [[org.clojure/clojurescript "1.10.520"]
                                       [reagent "0.9.0-rc1"]
                                       [haslett "0.1.6"]
                                       [cljs-ajax "0.8.0"]]}
             :uberjar  {:aot :all}
             :ebs-java {:zip ["target/conj2019-0.1.0-SNAPSHOT-standalone.jar"]
                        :aws {:beanstalk
                              {:region       "us-east-1"
                               :stack-name   "64bit Amazon Linux 2018.03 v2.10.0 running Java 8"
                               :s3-bucket    "conj2019"
                               :environments [{:name    "conj2019"
                                               :options {"aws:autoscaling:asg"
                                                         {"MinSize" "1" "MaxSize" "1"}
                                                         "aws:autoscaling:launchconfiguration"
                                                         {"InstanceType" "t2.micro"}}}]}}}}

  :repl-options {:init-ns conj2019.full_demo.system}

  :aliases {"deploy-ebs-java" ["do"
                               ["clean"]
                               ["uberjar"]
                               ["with-profile"
                                "+ebs-java"
                                "zip"]
                               ["with-profile"
                                "+ebs-java"
                                "dockerstalk"
                                "deploy"
                                "conj2019"
                                "target/conj2019-0.1.0-SNAPSHOT.zip"]]})
