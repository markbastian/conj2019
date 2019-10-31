(ns conj2019.full_demo.web.eliza-app
  (:require [conj2019.eliza :as eliza]
            [hiccup.page :refer [html5]]
            [ring.util.http-response :refer [ok not-found]]))

(defn handler [{{:keys [prompt]} :params :as request}]
  (ok
    (html5
      [:h1 "Welcome to Eliza."]
      [:form {:autocomplete "off" :action "/eliza" :method :post}
       (if prompt (eliza/respond prompt) "Why are you here?") [:br]
       [:input {:type "text" :name "prompt" :value "" :style "width:100%"}]])))

(def routes ["/eliza" {:handler handler}])