(ns conj2019.complex-fib)

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