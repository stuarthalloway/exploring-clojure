(ns exploring.data-formats
  (:require
   [clojure.repl :refer :all]
   [clojure.data.json :as json]))

(defrecord Person
  [name dob interests])

(def einstein (Person. "Albert Einstein"
                       #inst "1979-03-14"
                       #{:thermodynamics :relativity}))

(defmulti diminish
  "Reduce data to things JSON can handle"
  (fn [k v] (class v)))

(defmethod diminish java.util.Date [k v] (str v))
(defmethod diminish :default [k v] v)

;; JSON loses record, keyword, set, and date types
(println (json/write-str einstein :value-fn diminish))






