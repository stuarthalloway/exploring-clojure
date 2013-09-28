;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Exploration of some ideas presented in
;; http://pragprog.com/book/pb7con/seven-concurrency-models-in-seven-weeks

(ns exploring.seven-concurrency-models
  (:require
   [clojure.core.async :refer (go go-loop <! >! map< remove< chan put!
                                  close! pub sub) :as async]
   [clojure.core.match :refer (match)]
   [clojure.repl :refer :all]))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; talker 1: using channels, go, and closed dispatch
(defn process-1
  [item]
  (match [item]
         [[:greet & [name]]] (str "Hello " name)
         [[:praise & [name]]] (str name ", you're amazing")
         [[:celebrate & [name age]]] (str "Here's to another " age " years, " name)))

;; test fn first 
(process-1 [:greet "Huey"])
(process-1 [:praise "Dewey"])
(process-1 [:celebrate "Louie" 16])

;; then go async
(def talker-ch-1 (chan))

(def composed-ch-1
  (->> talker-ch-1 (map< process-1)))

(def loop-1
  (go-loop [msg (<! composed-ch-1)]
           (when msg
             (println msg)
             (recur (<! composed-ch-1)))))

(put! talker-ch-1 [:greet "Huey"])
(put! talker-ch-1 [:praise "Dewey"])
(put! talker-ch-1 [:celebrate "Louie" 16])

(close! talker-ch-1)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; talker 2: using channels, go, and open dispatch
(defmulti process-2 (fn [[k & more]] k))

(defmethod process-2 :greet
  [[_ & [name]]]
  (str "Hello " name))

(defmethod process-2 :praise
  [[_ & [name]]]
  (str name ", you're amazing"))

(defmethod process-2 :celebrate
  [[_ & [name age]]]
  (str "Here's to another " age " years, " name))

;; test fn first 
(process-2 [:greet "Huey"])
(process-2 [:praise "Dewey"])
(process-2 [:celebrate "Louie" 16])

;; then go async
(def talker-ch-2 (chan))

(def composed-ch-2
  (->> talker-ch-2 (map< process-2)))

(def loop-2
  (go-loop [msg (<! composed-ch-2)]
           (when msg
             (println msg)
             (recur (<! composed-ch-2)))))

(put! talker-ch-2 [:greet "Huey"])
(put! talker-ch-2 [:praise "Dewey"])
(put! talker-ch-2 [:celebrate "Louie" 16])

(close! talker-ch-2)

;; let's abstract a little
(defmacro go-dochan
  "Consume channel ch with a go block, apply f (for side
   effects) to each item"
  [ch f]
  `(go-loop [msg# (<! ~ch)]
            (when msg#
              (~f msg#)
              (recur (<! ~ch)))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; talker 3, using async pub/sub
(def talker-ch-3 (chan))
(def talker-pub-3 (pub talker-ch-3 first))
(def greet-ch (chan))
(sub talker-pub-3 :greet greet-ch)
(go-dochan greet-ch (fn [[_ name]] (println "Hello " name)))

(put! talker-ch-3 [:greet "Huey"])
;; note that praise not subscribed yet
(put! talker-ch-3 [:praise "Huey"])

(def praise-ch (chan))
(sub talker-pub-3 :praise praise-ch)
(put! talker-ch-3 [:praise "Dewey"])
;; define impl for work after work already published
(go-dochan praise-ch (fn [[_ name]] (println name ", you're awesome")))

(def celebrate-ch (chan))
(sub talker-pub-3 :celebrate celebrate-ch)
(go-dochan celebrate-ch (fn [[_ name age]]
                          (println (str "Here's to another " age " years, " name))))
(put! talker-ch-3 [:celebrate "Louie" 16])
(doseq [ch [talker-ch-3 greet-ch praise-ch celebrate-ch]]
  (close! ch))


