(defproject conj2019 "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [markbastian/partsbin "0.1.3-SNAPSHOT"]
                 [com.taoensso/timbre "4.10.0"]
                 [environ "1.1.0"]
                 [hiccup "1.0.5"]
                 [org.immutant/web "2.1.10"]
                 [integrant "0.7.0"]
                 [metosin/ring-http-response "0.9.1"]
                 [ring/ring-defaults "0.3.2"]
                 [ring/ring-json "0.5.0"]
                 [ring/ring-mock "0.4.0"]
                 [nrepl "0.6.0"]
                 [nrepl/drawbridge "0.2.1"]
                 [metosin/reitit "0.3.10"]
                 [datascript "0.18.6"]
                 [cheshire "5.9.0"]
                 [clj.qrgen "0.4.0"]
                 ;Mazes!
                 [mazegen "0.1.0-SNAPSHOT"]
                 ;For spring
                 [org.springframework.boot/spring-boot-starter-web "2.1.9.RELEASE"]
                 ]

  :main conj2019.core

  :plugins [[lein-cljsbuild "1.1.5" :exclusions [[org.clojure/clojure]]]]

  :source-paths ["src/main/clj" "src/main/cljc"]
  :test-paths ["src/test/clj" "src/test/cljc"]
  :resource-paths ["src/main/resources"]
  :java-source-paths ["src/main/java"]

  :cljsbuild {:builds [{:id           "dev"
                        :source-paths ["src/main/cljs" "src/main/cljc"]}]}

  :profiles {:cljs {:dependencies [[org.clojure/clojurescript "1.10.520"]
                                   [reagent "0.9.0-rc1"]
                                   [haslett "0.1.6"]
                                   [cljs-ajax "0.8.0"]]}
             :uberjar {:aot :all}}

  :repl-options {:init-ns conj2019.system})
