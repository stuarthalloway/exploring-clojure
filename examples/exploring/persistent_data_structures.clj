(ns exploring.persistent-data-structures
  (:require
   [clojure.repl :refer :all]
   [clojure.set :as set]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; vectors

(def v [42 :rabbit [1 2 3]])

(vector? v)

(v 1)

(get v 1)

(peek v)

(pop v)

(subvec v 1)

(assoc v 1 :badger)

(vector 42 :rabbit)

(vec [42 :rabbit])

;; N.B. keys are indexes
(contains? v 0)

(rseq v)

((juxt first second) v)

(mapv odd? (range 10))

(filterv odd? (range 10))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; maps

(def m {:a 1 :b 2 :c 3})

(map? m)

(contains? m :b)

(get m :d 42)

(m :b)

(:b m)

(keys m)

(assoc m :d 4 :c 42)

(dissoc m :b)

(merge-with + m {:a 2 :b 3})

(hash-map :a 1 :b 2)

(sorted-map :c 3 :b 2 :a 1)

(sorted-map-by > 1 :a 2 :b 3 :c)

(select-keys m [:a :d])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; sets

(def colors #{:red :green :blue})
(def moods #{:happy :blue})

(disj colors :red)

(set/difference colors moods)

(set/intersection colors moods)

(set/union colors moods)

(hash-set 1 2 3)

(set [1 2 3])

(sorted-set 3 2 1)

(sorted-set-by > 1 2 3)


