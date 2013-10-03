(ns exploring-schema
  (:require [schema.core :as s]
            [schema.macros :as sm])
  (:use [clojure.repl]))

(doc s/check)

;; data first, the work up to exception-throwing fns
(s/check s/Number 42)

;; what does an error look like?
(s/check s/Keyword 42)

;; should ValidationError be a value (defrecord)?
(class (s/check s/Keyword 42))

;; validate by JVM class
(s/validate java.util.Date #inst "2012")

;; nil is fine
(try
 (s/validate java.util.Date nil)
 (catch Throwable t (def validation-error t)))

;; wish this was an ex-info
(class validation-error)

;; check structural schema
;; check is eager (finds all the problems)
(s/check {long long} {1 2 3 :c 4 :d})

(sm/defrecord Person
  [^String fname ^String lname])

(s/explain Person)
(doc strict-map->Person)

;; metadata form for fn schema
(sm/defn
  ^{:tag [[s/Number]]}       ;; metadata must be on symbol, not arg list
  paired
  [^{:tag [s/Number]} nums]
  (partition 2 nums))

(s/fn-schema paired)

;; this will throw if args don't match schema
(s/with-fn-validation
  (paired [1 2 3 4]))

;; "must satisfy" form
(sm/defn
  tripped :- [[s/Number]]
  [nums :- [s/Number]]
  (partition 3 nums))

(s/with-fn-validation
  (tripped [1 2 3 4 5 6]))

;; maps with requirements
(s/check {(s/required-key :foo) String
          long long}
         {1 2 :foo "wow" 3 4})


;; map with value type requirement
(s/check {(s/optional-key :foo) Number}
         {1 2 :foo "wow" 3 4})


;; sequences with positional options/requirements
(doc s/one)
(s/check [(s/one s/String :first)
          (s/one s/Keyword :second)
          s/Number]
         [1 :blah 3 4 5])
