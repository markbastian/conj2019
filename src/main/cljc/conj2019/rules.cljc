(ns conj2019.rules
  (:require [mazegen.core :as maze]
            [conj2019.emojis :as emojis]
            [clojure.pprint :as pp]))

(def dir-map {:right [1 0] :left [-1 0] :up [0 -1] :down [0 1]})

(defn new-maze []
  (let [cells 20
        start [0 0]
        end [(dec cells) (dec cells)]
        empty-maze (maze/create-empty cells cells)]
    (maze/prim-gen empty-maze start end)))

(defn- all-coords [maze]
  (for [i (range (count maze))
        j (range (count (maze i)))]
    [i j]))

(defn new-game []
  (let [maze (new-maze)
        elements [{:type :enemy :name :pestilence :color :white :weapon :bow-and-arrow :emoji emojis/horse-head}
                  {:type :enemy :name :famine :color :black :weapon :scales :emoji emojis/horse-head}
                  {:type :enemy :name :war :color :red :weapon :sword :emoji emojis/horse-head}
                  {:type :enemy :name :death :color :gray :weapon :shield :emoji emojis/horse-head}
                  {:type :item :name :sword :color :red :emoji emojis/sword}
                  {:type :item :name :bow-and-arrow :color :white :emoji emojis/bow-and-arrow}
                  {:type :item :name :scales :color :black :emoji emojis/scales}
                  {:type :item :name :shield :color :gray :emoji emojis/shield}]
        [l & coords] (shuffle (all-coords maze))]
    {:maze     maze
     :location l
     :elements (zipmap coords elements)}))

(defn current-location [maze]
  (first
    (for [i (range (count maze))
          j (range (count (maze i)))
          :when (get-in maze [i j :start])]
      [i j])))

(defn maybe-move [{:keys [maze location]} dir]
  (let [new-loc (mapv + location (dir-map dir))]
    (get-in maze (conj location new-loc) location)))

(defmulti interact (fn [_ {:keys [type]}] type))

(defmethod interact :item [{:keys [location] :as game-state} {:keys [name] :as item}]
  (-> game-state
      (update :inventory assoc name item)
      (update :elements dissoc location)))

(defmethod interact :enemy [{:keys [location inventory] :as game-state} {:keys [weapon] :as enemy}]
  (if (get inventory weapon)
    (-> game-state
        (update :defeated conj enemy)
        (update :elements dissoc location))
    (new-game)))

(defn move [{:keys [elements] :as game-state} dir]
  (let [loc (maybe-move game-state dir)
        element (elements loc)
        new-game-state (assoc game-state :location loc)]
    (cond-> new-game-state element (interact element))))