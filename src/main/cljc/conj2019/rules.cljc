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

(defn won? [{:keys [elements]}]
  (empty? (filter (fn [[_ {:keys [type]}]] (= type :enemy)) elements)))

(defmethod interact :enemy [{:keys [location inventory elements] :as game-state}
                            {:keys [weapon] :as enemy}]
  (if (get inventory weapon)
    (let [post-defeat (-> game-state
                          (update :defeated conj enemy)
                          (update :elements dissoc location))]
      (cond-> post-defeat (won? post-defeat) (assoc :end-condition "You Won!")))
    (assoc game-state :end-condition (format "Defeated by %s" (-> enemy :name name)))))

(defmethod interact :default [game-state _]
  game-state)

(defn move [{:keys [elements end-condition] :as game-state} dir]
  (if-not end-condition
    (let [new-game-state (assoc game-state :location (maybe-move game-state dir))]
      (interact new-game-state (elements (:location new-game-state))))
    game-state))

(comment
  (letfn [(insteract [g [l i]] (interact (assoc g :location l) i))]
    (let [{:keys [elements] :as game-state} (new-game)
          items (filter (fn [[_ {:keys [type]}]] (= type :item)) elements)
          enemies (filter (fn [[_ {:keys [type]}]] (= type :enemy)) elements)
          all-items-state (reduce insteract game-state items)
          all-wins-state (reduce insteract all-items-state enemies)]
      all-wins-state)))
