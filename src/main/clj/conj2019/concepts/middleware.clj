(ns conj2019.concepts.middleware)

(defn wrap-component [handler component]
  (fn [request]
    (handler
      (into component request))))