(ns run-tests
  (:require [clojure.test.generative.runner :as runner]))

(defn -main
  [& _]
  (System/setProperty "clojure.test.generative.msec" "10000")
  (System/setProperty "java.awt.headless" "true")
  (runner/-main "test"))
