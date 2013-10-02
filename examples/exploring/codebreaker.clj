(ns exploring.codebreaker
  (:require
   [clojure.repl :refer :all]
   [clojure.java.io :as io]
   [clojure.math.combinatorics :as comb]
   [clojure.pprint :as pp]
   [clojure.set :as set]
   [clojure.data.generators :as gen]
   [clojure.test.generative :refer (defspec)]
   [clojure.test.generative.runner :as runner]))

(def secret [:r :g :b :y])
(def guess  [:g :g :g :r])

;; 1 black, 1 white
(defn exact-matches
  "Returns the count of matches in the same position in
   both a and b."
  [a b]
  (->> (map = a b)
       (filter identity)
       count))

(exact-matches secret guess)

(def zeros {:r 0 :g 0 :b 0 :y 0})

(->> (merge-with
      min
      (merge zeros (frequencies guess))
      (merge zeros (frequencies secret)))
     vals
     (apply +))

(->> (merge-with
      min
      (merge zeros (frequencies secret))
      (merge zeros (frequencies guess)))
     vals
     (apply +))

(->> (merge-with
      min
      (select-keys (frequencies secret) guess)
      (select-keys (frequencies guess) secret))
     vals
     (reduce +))

(defn all-matches
  "Returns the count of matches regardless of position in
   a and b."
  [a b]
  (->> (merge-with min
                   (select-keys (frequencies a) b)
                   (select-keys (frequencies b) a))
       vals
       (reduce +)))

(all-matches secret guess)

(defn create-scorer
  "Given a secret, return a function of a guess that scores
   that guess."
  [secret]
  (fn [guess]
    (let [exact (exact-matches secret guess)
          all (all-matches secret guess)]
      {:exact exact :unordered (- all exact)})))

(def scorer (create-scorer secret))
(scorer guess)
(scorer secret)

(defn validate-score
  [a b {:keys [exact unordered]}]
  (let [desc {:a a :b b :exact exact :unordered unordered}
        fail #(throw (ex-info % desc))]
    (when-not (<= 0 exact)
      (fail "Exact matches should not be negative"))
    (when-not (<= 0 unordered)
      (fail "Unordered matches should not be negative"))
    (when-not (<= (+ exact unordered) (count a))
      (fail "Total matches should be <= size of secret"))
    (let [match-set (set/intersection (into #{} a) (into #{} b))]
      (when-not (<= (count match-set) (+ exact unordered))
        (fail "Set intersection count should be <= total match count")))))

(validate-score secret guess (scorer secret))

(set! *print-length* 100)

(defn all-turns
  "Returns all possible pairs of score, guess for a codebreaker
   game with colors and n columns"
  [colors n]
  (-> (map vec (comb/selections colors n))
      (comb/selections 2)))

(all-turns [:r :b] 3)

(defn test-scoring
  [[secret guess]]
  (let [scorer (create-scorer secret)
        score (scorer guess)]
    ;; validates the score
    (validate-score secret guess score)
    ;; returns secret, guess, and score
    {:secret secret
     :guess guess
     :score score}))

(->> (all-turns [:r :g :b] 2)
     (map test-scoring)
     pp/print-table)

(time (->> (all-turns [:r :g :b :y] 3)
           (map test-scoring)
           count))

;; ~15 seconds to test 6x4
(time (->> (all-turns [:r :g :b :y :p :o] 4)
           (map test-scoring)
           count))

(def colors [:r :g :b :y :p :o])

(defn gen-guess
  [colors n]
  (gen/vec #(gen/rand-nth colors) n))

(gen-guess colors 6)

(defn gen-turn
  "Returns a [secret, guess] pair."
  [colors n]
  [(gen-guess colors n)
   (gen-guess colors n)])

(gen-turn colors 6)

;; more than 1000x bigger job than 6x4
(defn turn-6x6
  []
  (gen-turn colors 6))

(def t (turn-6x6))

(defn score-turn
  [[secret guess]]
  (let [scorer (create-scorer secret)]
    (scorer guess)))

(score-turn t)

(defspec test-scoring
  score-turn  
  [^{:tag `turn-6x6} turn]
  (let [[secret guess] turn]
    (validate-score secret guess %)
    {:secret secret
     :guess guess
     :score %}))

(test-scoring t)

;; interactive
(runner/run 1 1000 #'test-scoring)
(ex-data *e)
(runner/run 2 1000 #'test-scoring)
(runner/run 16 1000 #'test-scoring)

;; ci usage
(runner/run-suite {:nthreads 2 :msec 1000}
                  (runner/get-tests #'test-scoring))

(defn win-6x6
  "Returns a winning 6x6 turn."
  []
  (let [g (gen-guess colors 6)]
    [g g]))

(def win (win-6x6))

(defspec test-winning-score
  score-turn
  [^{:tag `win-6x6} turn]
  (when-not (= % {:exact 6 :unordered 0})
    (throw (ex-info "Winning turn did not get a winning score" {:turn turn :score %}))))

(test-winning-score win)

(runner/run 2 1000 #'test-winning-score)
