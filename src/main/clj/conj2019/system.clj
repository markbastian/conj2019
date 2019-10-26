(ns conj2019.system
  (:require [hiccup.page :refer [html5 include-js include-css]]
            [partsbin.core :as partsbin]
            [partsbin.immutant.web.core :as web]
            [conj2019.web.eliza-app :as eliza]
            [drawbridge.core]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults site-defaults]]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.util.http-response :refer [ok not-found resource-response]]
            [reitit.ring :as ring]
            [clj.qrgen :as qr]
            [conj2019.api.v0 :as v0]
            [mazegen.core :as maze]
            [clojure.pprint :as pp]
            [clojure.string :as cs]))

;(defonce maze
;         (let [cells 20
;               start [0 0]
;               end [(dec cells) (dec cells)]
;               empty-maze (maze/create-empty cells cells)]
;           (maze/prim-gen empty-maze start end)))

; https://www.quackit.com/character_sets/emoji/emoji_v3.0/emoji_icons_animals_and_nature.cfm
;https://www.quackit.com/character_sets/emoji/emoji_v3.0/unicode_emoji_v3.0_characters_objects.cfm
;https://www.iemoji.com/view/emoji/2655/smileys-people/man-zombie
(def smiley "&#x1F600;")
(def horse-head "&#x1F434;")
(def sword "&#x1F5E1;")
(def bow-and-arrow "&#x1F3F9;")
(def shield "&#x1F6E1;")
(def zombie "&#x1f9df;")
(def money-bag "&#x1F4B0;")
(def dir-map {:right [1 0] :left [-1 0] :up [0 -1] :down [0 1]})

(defn new-maze []
  (let [cells 20
        start [0 0]
        end [(dec cells) (dec cells)]
        empty-maze (maze/create-empty cells cells)]
    (maze/prim-gen empty-maze start end)))

(defn hello-world-handler [request]
  (ok
    (html5
      [:div
       [:h1 "Welcome to my simple conj demo"]
       [:img {:src "/qr" :alt "qr"}]
       [:ul
        [:li [:a {:href "/v0"} "Visit the basic static api"]]
        [:li [:a {:href "/eliza"} "Visit Eliza, a low-tech psychiatrist"]]
        [:li [:a {:href "/maze"} "Visit a maze!"]]]])))

(defn maze-svg [{:keys [maze location]}]
  (let [cell-dim 25
        width (* cell-dim (count maze))
        height (* cell-dim (count (first maze)))]
    [:svg {:width width :height height :style "border:1px solid black"}
     (for [i (range (count maze)) j (range (count (maze i)))
           :let [coords [i j]
                 dirs [[(dec i) j] [i (inc j)] [(inc i) j] [i (dec j)]]
                 walls (vec (take 4 (partition 2 1 (cycle [[i j] [i (inc j)] [(inc i) (inc j)] [(inc i) j]]))))
                 portals (get-in maze coords)]]
       (for [k (range 2)
             :let [[[x1 y1] [x2 y2]] (walls k)]
             :when (not (portals (dirs k)))]
         [:line {:stroke :black
                 :x1     (* x1 cell-dim)
                 :y1     (* y1 cell-dim)
                 :x2     (* x2 cell-dim)
                 :y2     (* y2 cell-dim)}]))
     (for [i (range (count maze)) j (range (count (maze i)))
           :let [coords [i j]
                 cell (get-in maze coords)
                 cx (* (+ i 0.5) cell-dim)
                 cy (* (+ j 0.5) cell-dim)
                 R (* cell-dim 0.5)
                 r (* cell-dim 0.15)]]
       (cond
         (cell :end)
         [:g
          [:circle {:fill :red :stroke :black :cx cx :cy cy :r R}]
          [:text {:x cx :y cy :text-anchor :middle :stroke :black :dy ".4em"} money-bag]]))
     (let [[i j] location
           cx (* (+ i 0.5) cell-dim)
           cy (* (+ j 0.5) cell-dim)
           R (* cell-dim 0.5)
           r (* cell-dim 0.15)]
       [:g
        [:circle {:fill :green :stroke :black :cx cx :cy cy :r R}]
        [:text {:x cx :y cy :text-anchor :middle :stroke :black :dy ".4em"} smiley]
        [:a {:href "/maze?move=right"}
         [:circle {:fill :blue :stroke :black :cx (+ cx R) :cy cy :r r}]]
        [:a {:href "/maze?move=left"}
         [:circle {:fill :blue :stroke :black :cx (- cx R) :cy cy :r r}]]
        [:a {:href "/maze?move=down"}
         [:circle {:fill :blue :stroke :black :cx cx :cy (+ cy R) :r r}]]
        [:a {:href "/maze?move=up"}
         [:circle {:fill :blue :stroke :black :cx cx :cy (- cy R) :r r}]]])]))

(defn current-location [maze]
  (first
    (for [i (range (count maze))
          j (range (count (maze i)))
          :when (get-in maze [i j :start])]
      [i j])))

(defn all-coords [maze]
  (for [i (range (count maze))
        j (range (count (maze i)))]
    [i j]))

(defn maybe-move [{:keys [maze location]} dir]
  (let [new-loc (mapv + location (dir-map dir))]
    (get-in maze (conj location new-loc) location)))

(defn move [game-state dir]
  (assoc game-state :location (maybe-move game-state dir)))

(defn step-handler [dir {:keys [session] :as request}]
  (let [session (move session dir)]
    (-> (ok (html5 (maze-svg session)))
        (assoc :session session))))

(defn new-game []
  (let [maze (new-maze)
        coords (all-coords maze)]
    {:maze       maze
     :location   (first (filter #(get-in maze (conj % :start)) coords))
     :famine     (rand-nth coords)
     :war        (rand-nth coords)
     :pestilence (rand-nth coords)
     :death      (rand-nth coords)}))

(defn maze-handler [{:keys [session params] :as request}]
  (if (seq session)
    (case (some-> params :move cs/lower-case keyword)
      :right (step-handler :right request)
      :left (step-handler :left request)
      :up (step-handler :up request)
      :down (step-handler :down request)
      (let [game (new-game)
            session (into session game)]
        (-> (ok (html5 (maze-svg game)))
            (assoc :session session))))
    (let [game (new-game)
          session (into session game)]
      (-> (ok (html5 (maze-svg game)))
          (assoc :session session)))))

(defn unicode-to-string
  "Turns a hex unicode symbol into a string.
  Deals with such long numbers as 0x1F535 for example."
  [code]
  (-> code Character/toChars String.))

(def app
  (ring/ring-handler
    (ring/router
      [["/" {:handler hello-world-handler}]
       ["/emoji" {:handler (constantly (ok (html5
                                             [:head
                                              [:meta {:charset "utf-8"}]
                                              [:title horse-head]]
                                             [:body [:p horse-head]])))}]
       v0/routes
       eliza/routes
       ["/maze" {:handler maze-handler}]
       ["/qr" (fn [_] (ok (qr/as-input-stream (qr/from "http://localhost:3000"))))]
       ["/public/*" (ring/create-resource-handler)]
       (let [nrepl-handler (drawbridge.core/ring-handler)]
         ["/repl" {:handler nrepl-handler}])]
      {:data {:middleware [[wrap-defaults
                            (-> site-defaults
                                (update :security dissoc :anti-forgery)
                                (update :security dissoc :content-type-options)
                                (update :responses dissoc :content-types))]
                           wrap-json-response]}})
    (constantly (not-found "Not found"))))

(def config {::web/server {:port    3000
                           :host    "0.0.0.0"
                           :handler #'app}})

(defonce sys (partsbin/create config))