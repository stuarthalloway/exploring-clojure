(ns exploring.reducing-apple-pie
  (:use clojure.repl)
  (:require
   [clojure.data.generators :as gen]
   [clojure.core.reducers :as r]
   [clojure.pprint :as pprint]
   [criterium.core :as crit]))

(.maxMemory (Runtime/getRuntime))

(set! *print-length* 20)

(defn gen-apples
  "Generate a lazy seq of n apples of type type, where stickers is
   the proporition of apples that have stickers, and edible
   is the proportion of apples that are edible"
  [{:keys [n type stickers edible]}]
  (repeatedly n (fn [] {:type type
                        :sticker? (< (gen/double) stickers)
                        :edible? (< (gen/double) edible)})))

(def apples (gen-apples {:n 100 :type "Del" :stickers 0.8 :edible 0.8}))

(def map-and-filter
  (comp (partial filter :edible?)
        (partial map #(dissoc % :sticker?))))

(defn prepare-with-seqs
  "Prepare the apples by simply composing operations on seqs.
   Returns count of apples prepared"
  [apples]
  (->> apples
       (filter :edible?)
       (map #(dissoc % :sticker?))
       count))

(prepare-with-seqs apples)

(defn counter
  ([] 0)
  ([x _] (inc x)))

(defn prepare-with-reduce
  "Prepare the apples by reducing.  Returns count of apples prepared."
  [apples]
  (->> apples
       (r/filter :edible?)
       (r/map #(dissoc % :sticker?))
       (r/reduce counter)))

(prepare-with-reduce apples)

(defn prepare-with-fold
  [apples]
  (->> apples
       (r/filter :edible?)
       (r/map #(dissoc % :sticker?))
       (r/fold counter)))

(prepare-with-fold apples)

(defn prepare-with-partition-then-fold
  "Prepare the apples by paritioning, then folding the partitions, then
   reducing the fold results.  This demonstrates using fold as part of
   a larger strategy, but does *not* demonstrate doing anything particularly
   clever to feed the folds, e.g. with a data file in hand you could do
   better than starting with a sequence."
  ([apples] (prepare-with-partition-then-fold apples 100000))
  ([apples n]
     (->> apples
          (partition-all n)
          (map #(prepare-with-fold (into [] %)))
          (reduce +))))

(prepare-with-partition-then-fold apples 3)

(defn prepare-with-partition-pmap
  ([apples] (prepare-with-partition-then-fold apples 100000))
  ([apples n]
     (->> apples
          (partition-all n)
          (pmap prepare-with-seqs)
          (reduce +))))

(defn bench
  "Wrap criterium so that it does not use stdout"
  [f]
  (let [s (java.io.StringWriter.)]
    (binding [*out* s]
      (assoc (crit/quick-benchmark* f)
        :stdout (str s)))))

(defn bench-preparers
  []
  (let [mean-msec #(long (* 1000 (first (:sample-mean %))))]
    (->> (for [napples [100000 1000000]
               :let [apples (into [] (gen-apples {:n napples :type :golden :stickers 1 :edible 0.8}))]
               sym '[prepare-with-seqs prepare-with-reduce prepare-with-fold]]
           (do
             (print "Testing " sym " " napples ": ") (flush)
             (let [result (bench #((resolve sym) apples))]
               (println (mean-msec result) " msec")
               {:op sym
                :napples napples
                :result result})))
         (map
          (fn [{:keys [op napples result]}]
            {"Operation" op
             "Apples" napples
             "Mean Time (msec)" (mean-msec result)}))
         (pprint/print-table))))

(bench-preparers)

(doseq [n (range 5 9)]
  (doseq [v [#'prepare-with-partition-then-fold #'prepare-with-partition-pmap]]
    (dotimes [_ 2]
      (println "Timing " v " with 1e" n)
      (time
       (prepare-with-partition-then-fold
         (gen-apples {:n (long (Math/pow 10 n)) :type :golden :stickers 1 :edible 0.8})
         1000000)))))



