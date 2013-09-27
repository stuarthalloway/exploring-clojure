(ns exploring.atoms)

;; compare to swap-when! from
;; http://pragprog.com/book/pb7con/seven-concurrency-models-in-seven-weeks
(defn swap-if!
  "If (pred current-value-of-atom) is true, atomically swaps the
   value of the atom to become (apply f current-value-of-atom args).
   Note that both pred and f may be called multiple times, and thus
   should be free of side-effects. Returns the 'new' value of the
   atom."
  [a pred f & args]
  (swap! a #(if (pred %)
              (apply f % args)
              %)))




