(ns conj2019.full_demo.core
  (:gen-class)
  (:require [environ.core :refer [env]]
            [partsbin.core :as partsbin]
            [taoensso.timbre :as timbre]
            [nrepl.server :refer [start-server stop-server]]
            [conj2019.full_demo.system :refer [sys]]))

(defn -main [& args]
  (let [nrepl-port (some->> :nrepl-port env (re-matches #"\d+") Long/parseLong)
        nrepl-host (env :nrepl-host "0.0.0.0")
        server (when nrepl-port (start-server :bind nrepl-host :port nrepl-port))
        system (partsbin/start sys)]
    (timbre/info "System started!!!")
    (when server (timbre/info (str "nrepl port started on port " nrepl-port ".")))
    (try
      (.addShutdownHook
        (Runtime/getRuntime)
        (let [^Runnable shutdown #(do (partsbin/stop sys) (when server (stop-server server)))]
          (Thread. shutdown)))
      (catch Throwable t
        (timbre/warn t)
        (do
          (partsbin/stop sys)
          (when server (stop-server server)))))))

