(ns conj2019.full_demo.core
  (:gen-class)
  (:require [environ.core :refer [env]]
            [partsbin.core :as partsbin]
            [taoensso.timbre :as timbre]
            [nrepl.server :refer [start-server stop-server]]
            [conj2019.full_demo.system :refer [sys config]]))

(defn -main [& [port]]
  (let [nrepl-port (some->> :nrepl-port env (re-matches #"\d+") Long/parseLong)
        nrepl-host (env :nrepl-host "0.0.0.0")
        port-actual (or port (env :port) (get-in config [::web/server :port]))
        port-actual (cond-> port-actual (string? port-actual) (Integer/parseInt))
        server (when nrepl-port (start-server :bind nrepl-host :port nrepl-port))
        config-actual (partsbin/swap-config! sys (fn [config] (assoc-in config [::web/server :port] port-actual)))
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

