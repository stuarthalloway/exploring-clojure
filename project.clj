(defproject exploring-clojure "0.1.0-SNAPSHOT"
  :description "Template project for interactive exploration of Clojure"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-1889"]
                 [org.clojure/core.async
                  #_"0.1.0-SNAPSHOT"
                  #_"0.1.222.0-83d0c2-alpha"
                  "0.1.226.0-697bc8-alpha"]
                 [org.clojure/core.logic "0.6.6"]
                 [org.clojure/core.match "0.2.0-rc6"]
                 [org.clojure/core.typed "0.2.13"]
                 [org.clojure/data.json "0.2.3"]
                 [org.clojure/math.combinatorics "0.0.4"]
                 [org.clojure/test.generative "0.5.1"]
                 [com.datomic/datomic-free "0.8.4159"]
                 [clj-time "0.6.0"]
                 [dorothy "0.0.3"]
                 [enlive/enlive "1.1.4"]]
  :source-paths ["src/clj" "src/cljs"]
  :cljsbuild {:builds
              {:dev {:source-paths ["src/cljs"]
                     :compiler {:output-to "resources/public/javascript/exploring-clojure.js"
                                :pretty-print true 
                                :optimizations :simple}}
               :prod {:source-paths ["src/cljs"]
                      :compiler { :output-to "resources/public/javascript/exploring-clojure-min.js"
                                 :pretty-print false 
                                 :optimizations :advanced}}}})
