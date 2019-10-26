(ns conj2019.misc.fibonacci)

(defn nth-fib [n]
  (loop [i 0 j 1 step 0]
    (if (= step n)
      i
      (recur j (+ i j) (inc step)))))


(defn n-fibs [n]
  (loop [i 0 j 1 step 0 res []]
    (if (= step n)
      res
      (recur j (+ i j) (inc step) (conj res i)))))

;Step
(defn fib-step [[i j]] [j (+ i j)])

;Iterate
(def fib-seq (map first (iterate fib-step [0N 1N])))

;Terminate
(def nth-fib (partial nth fib-seq))

;Terminate
(defn n-fibs [n] (take n fib-seq))