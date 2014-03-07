(ns exploring.convert-to-generative
  (:require
   [clojure.set :as set]
   [clojure.test :as test :refer (deftest is are)]
   [clojure.test.generative :refer (defspec)]))

;; TODO: design generative tests that cover the same domains as the
;; clojure.test tests below.

(deftest test-union
  (are [x y] (= x y)
      (set/union) #{}

      ; identity
      (set/union #{}) #{}
      (set/union #{1}) #{1}
      (set/union #{1 2 3}) #{1 2 3}

      ; 2 sets, at least one is empty
      (set/union #{} #{}) #{}
      (set/union #{} #{1}) #{1}
      (set/union #{} #{1 2 3}) #{1 2 3}
      (set/union #{1} #{}) #{1}
      (set/union #{1 2 3} #{}) #{1 2 3}

      ; 2 sets
      (set/union #{1} #{2}) #{1 2}
      (set/union #{1} #{1 2}) #{1 2}
      (set/union #{2} #{1 2}) #{1 2}
      (set/union #{1 2} #{3}) #{1 2 3}
      (set/union #{1 2} #{2 3}) #{1 2 3}

      ; 3 sets, some are empty
      (set/union #{} #{} #{}) #{}
      (set/union #{1} #{} #{}) #{1}
      (set/union #{} #{1} #{}) #{1}
      (set/union #{} #{} #{1}) #{1}
      (set/union #{1 2} #{2 3} #{}) #{1 2 3}

      ; 3 sets
      (set/union #{1 2} #{3 4} #{5 6}) #{1 2 3 4 5 6}
      (set/union #{1 2} #{2 3} #{1 3 4}) #{1 2 3 4}

      ; different data types
      (set/union #{1 2} #{:a :b} #{nil} #{false true} #{\c "abc"} #{[] [1 2]}
        #{{} {:a 1}} #{#{} #{1 2}})
          #{1 2 :a :b nil false true \c "abc" [] [1 2] {} {:a 1} #{} #{1 2}}

      ; different types of sets
      (set/union (hash-set) (hash-set 1 2) (hash-set 2 3))
          (hash-set 1 2 3)
      (set/union (sorted-set) (sorted-set 1 2) (sorted-set 2 3))
          (sorted-set 1 2 3)
      (set/union (hash-set) (hash-set 1 2) (hash-set 2 3)
        (sorted-set) (sorted-set 4 5) (sorted-set 5 6))
          (hash-set 1 2 3 4 5 6)))

(def ^:dynamic *test-value* 1)

(deftest future-fn-properly-retains-conveyed-bindings
  (let [a (atom [])]
    (binding [*test-value* 2]
      @(future (dotimes [_ 3]
                 ;; we need some binding to trigger binding pop
                 (binding [*print-dup* false]
                   (swap! a conj *test-value*))))
      (is (= [2 2 2] @a)))))

(deftest namespaced-keys-in-destructuring
  (let [{:keys [a/b c/d]} {:a/b 1 :c/d 2}]
    (is (= 1 b))
    (is (= 2 d))))
