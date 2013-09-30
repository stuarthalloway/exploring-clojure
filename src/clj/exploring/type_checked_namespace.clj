(ns exploring.type-checked-namespace
  (:require [clojure.set :as set]
            [clojure.core.typed :refer (ann Set) :as typed]))

(ann ^:no-check clojure.set/difference [(Set Any) (Set Any) -> (Set Any)])
(ann check-me [-> (Set Any)])

(typed/tc-ignore
 (defn dont-check-me
   []
   :blah))

(defn check-me
  []
  (set/difference #{1 2} #{1})
  ;; uncomment to see an error with 'lein typed check'
  #_(set/difference #{1 2} [1 2]))




