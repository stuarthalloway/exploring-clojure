(ns exploring-herbert
  (:require [miner.herbert :as h]
            [miner.herbert.generators :as hg])
  (:use [clojure.repl]))

(doc h/conforms?)

;; conforms
(h/conforms? 'int 42) 

;; does not comform
(h/conforms? 'kw 42)

;; validate by JVM class
(h/conforms? [(class java.util.Date)] #inst "2012")

;; nesting shapes
(h/conforms? '{kw int} {:foo 1})
(h/conforms? '{kw kw} {:foo 1})

(defrecord Person
  [fname lname])

(def person {:fname "Stu" :lname "Halloway"})
(def person-rec (map->Person person))

;; shape of map / record
(h/conforms? '{:fname str :lname str} person)
(h/conforms? '{:fname str :lname str} person-rec)

;; type of record
(h/conforms? '(tag exploring-herbert/Person) person)
(h/conforms? '(tag exploring-herbert/Person) person-rec)

;; type *and* shape of record 
(h/conforms? '(tag exploring-herbert/Person {:fname str :lname str}) person-rec)
(h/conforms? '(tag exploring-herbert/Person {:fname str}) person-rec)
(h/conforms? '(tag exploring-herbert/Person {:fname str :lname "Bean"}) person-rec)

;; sequences with positional options/requirements
(h/conforms? '(seq int any*) [1 :blah 3 4 5])
(h/conforms? '(seq int kw any*) [1 :blah 3 4 5])
(h/conforms? '(seq int kw str any*) [1 :blah 3 4 5])

;; what happened when things went wrong?
(h/blame '(seq int any*) [1 :blah 3 4 5])
(h/blame '(seq int kw str) [1 :blah 3 4 5])

;; peek at the PEG internals
(defn gory-details [schema] 
  (let [grammar (h/schema->grammar schema)
        con-fn (h/constraint-fn schema)]
    (fn 
      ([] grammar)
      ([x] (let [res (con-fn x)]
                (when (squarepeg.core/failure? res)
                  res))))))
((gory-details '(seq int kw str)) [1 :blah 3 4 5])

;; make examples
(hg/sample '(seq int kw*) 1)

