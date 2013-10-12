;; Most of the code here is stripped down and modified from David
;; Nolen's excellent JavaScript examples.
;;
;; See:
;; http://swannodette.github.io/2013/07/12/communicating-sequential-processes/
;; https://github.com/swannodette/swannodette.github.com

(ns scriptbowl2013.core
  (:require [goog.events :as events]
            [goog.events.EventType]
            [cljs.core.async :refer [>! <! chan put! close! timeout take!]])
  (:require-macros [cljs.core.async.macros :refer [go alt!]]
                   [scriptbowl2013.macros :refer [dochan]]))

(defn map-ch
  "Return a channel that maps f over in."
  [f in]
  (let [out (chan)]
    (go (loop [x (<! in)]
          (if (nil? x)
            (close! out)
            (do (>! out (f x))
                (recur (<! in))))))
    out))

(def keyword->event-type
  "Map keyword names to goog event constants."
  {:keyup goog.events.EventType.KEYUP
   :keydown goog.events.EventType.KEYDOWN
   :keypress goog.events.EventType.KEYPRESS
   :click goog.events.EventType.CLICK
   :dblclick goog.events.EventType.DBLCLICK
   :mousedown goog.events.EventType.MOUSEDOWN
   :mouseup goog.events.EventType.MOUSEUP
   :mouseover goog.events.EventType.MOUSEOVER
   :mouseout goog.events.EventType.MOUSEOUT
   :mousemove goog.events.EventType.MOUSEMOVE
   :focus goog.events.EventType.FOCUS
   :blur goog.events.EventType.BLUR})

(defn listen
  "Put DOM events on a channel"
  ([el type] (listen el type (chan)))
  ([el type out]
     (events/listen el (keyword->event-type type)
                    (partial put! out))
    out))

(defn by-id [id]
  (.getElementById js/document id))

(defn set-html! [el s]
  (set! (.-innerHTML el) s))

(defn event-target-id
  [e]
  (-> e .-currentTarget .-id))

(defn log
  "Return a channel that logs events from in and passes
   them on."
  [in]
  (let [out (chan)]
    (dochan [e in]
      (.log js/console e)
      (>! out e))
    out))

(defn render
  [coll]
  (apply str
    (for [p coll]
      (str "<div class='proc-" p "'>" p "</div>"))))

(defn update-loop
  "Render a sliding-buffer view of the elements appearing
   on channel ch."
  [ch]
  (go (loop [coll (list "Ready")]
        (set-html! (by-id "results")
                   (render coll))
        (recur
         (->> coll (cons (<! ch)) (take 10))))))

;; people unfamiliar with CSP have negative connotations for 'timeout'.
(def sleep timeout)

(defn control-loop
  "Create new go blocks writing to update-ch based on
   user commands via control-ch."
  [control-ch update-ch]
  (let [ch update-ch]
    (go
     (while true
            (case (<! control-ch)
                  "go1" (go (while true
                                   (<! (sleep 250))
                                   (>! ch "Red")))
                  "go2" (go (while true
                                   (<! (sleep 1000))
                                   (>! ch "Blue")))
                  "go3" (go (while true
                                   (<! (sleep 2000))
                                   (>! ch "Green"))))))))

(defn ^:export start
  []
  (.log js/console "Starting")
  (let [buttons (mapv by-id ["go1" "go2" "go3"])
        control-ch (chan)
        update-ch (chan)]
    (control-loop (->> control-ch (map-ch event-target-id) log)
                  update-ch)
    (update-loop update-ch)
    (doseq [b buttons]
      (listen b :click control-ch)))
  (.log js/console "Started"))



