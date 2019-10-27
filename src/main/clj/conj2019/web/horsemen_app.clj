(ns conj2019.web.horsemen-app
  (:require [conj2019.rules :as rules]
            [clojure.string :as cs]
            [hiccup.page :refer [html5]]
            [ring.util.http-response :refer [ok]]
            [conj2019.emojis :as emojis]))

(defn render-marker [[i j] cell-dim fill icon]
  (let [cx (* (+ i 0.5) cell-dim)
        cy (* (+ j 0.5) cell-dim)
        R (* cell-dim 0.5)]
    [:g
     [:circle {:fill fill :stroke :black :cx cx :cy cy :r R}]
     [:text {:x cx :y cy :text-anchor :middle :stroke :black :dy ".4em"} icon]]))

(defn render-nav [dir pts]
  [:a {:href (format "/horsemen?move=%s" dir)}
   [:polygon
    {:points (apply format "%s,%s %s,%s %s,%s" (flatten pts))
     :style  "fill:white"}]])

(defn render-navs [center width height cell-dim]
  (let [c (map #(* cell-dim (+ % 0.5)) center)
        ur (map + c [width (- height)])
        ul (map + c [(- width) (- height)])
        ll (map + c [(- width) height])
        lr (map + c [width height])]
    [:g
     (render-nav "up" [c ur ul])
     (render-nav "down" [c ll lr])
     (render-nav "left" [c ul ll])
     (render-nav "right" [c lr ur])]))

(defn render-maze-walls [maze cell-dim]
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
              :y2     (* y2 cell-dim)}])))

(defn maze-svg [{:keys [maze location elements]}]
  (let [cell-dim 25
        width (* cell-dim (count maze))
        height (* cell-dim (count (first maze)))]
    [:svg {:width width :height height :style "border:1px solid black"}
     (render-navs location width height cell-dim)
     (render-maze-walls maze cell-dim)
     (for [[coord {:keys [color emoji] :as element}] elements]
       (render-marker coord cell-dim color emoji))
     (render-marker location cell-dim :green emojis/smiley)]))

(defn step-handler [dir {:keys [session] :as request}]
  (let [{:keys [inventory defeated] :as session} (rules/move session dir)]
    (-> (ok (html5
              (maze-svg session)
              [:ul
               (for [[k {:keys [emoji color] :as item}] inventory]
                 [:li (format "%s: A %s %s" emoji (name color) (name k))])]
              [:ul
               (for [{:keys [emoji color] :as enemy} defeated]
                 [:li (format "%s: You defeated %s" emoji (name (:name enemy)))])]))
        (assoc :session session))))

(defn maze-handler [{:keys [session params] :as request}]
  (if (seq session)
    (case (some-> params :move cs/lower-case keyword)
      :right (step-handler :right request)
      :left (step-handler :left request)
      :up (step-handler :up request)
      :down (step-handler :down request)
      (let [game (rules/new-game)
            session (into session game)]
        (-> (ok (html5 (maze-svg game)))
            (assoc :session session))))
    (let [game (rules/new-game)
          session (into session game)]
      (-> (ok (html5 (maze-svg game)))
          (assoc :session session)))))

(def routes ["/horsemen" {:handler maze-handler}])