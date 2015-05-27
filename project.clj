(defproject exploring-clojure "0.1.0-SNAPSHOT"
  :description "Template project for interactive exploration of Clojure"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0-RC1"]
                 [org.clojure/clojurescript "0.0-1889"]
                 [org.clojure/core.async "0.1.278.0-76b25b-alpha" #_"0.1.242.0-44b1e3-alpha" #_"0.1.222.0-83d0c2-alpha"]
                 [org.clojure/core.logic "0.6.6"]
                 [org.clojure/core.match "0.2.0-rc6"]
                 #_[org.clojure/core.typed "0.2.13"]
                 [org.clojure/data.json "0.2.3"]
                 [org.clojure/math.combinatorics "0.0.4"]
                 [org.clojure/test.generative "0.5.1"]
                 [org.clojure/test.check "0.5.9"]
                 [com.datomic/datomic-free "0.9.4699"]
                 [com.datomic/simulant "0.1.7"]
                 [ring/ring-jetty-adapter "1.3.2" :exclusions [org.clojure/clojure]]
                 [gorilla-repl "0.3.3"]
                 [clj-webdriver "0.6.1"]
                 [liberator "0.12.2"]
                 [clj-time "0.6.0"]
                 [criterium/criterium "0.3.1"]
                 [dorothy "0.0.3"]
                 [enlive/enlive "1.1.4"]
                 [com.velisco/herbert "0.6.2"]
                 [prismatic/schema "0.1.5"]
                 [quil "1.6.0"]
                 [rhizome "0.2.0"]]
  :plugins [#_[lein-typed "0.3.0"] [lein-cljsbuild "0.3.2"]]
  :source-paths ["src/clj" "src/cljs"]
  :core.typed {:check [exploring.type-checked-namespace]}
  :profiles {:dev {:jvm-opts ["-Xmx6g" "-server"]}}
  :cljsbuild {:builds
              {:dev {:source-paths ["src/cljs"]
                     :compiler {:output-to "resources/public/javascript/exploring-clojure.js"
                                :pretty-print true 
                                :optimizations :simple}}
               :prod {:source-paths ["src/cljs"]
                      :compiler { :output-to "resources/public/javascript/exploring-clojure-min.js"
                                 :pretty-print false 
                                 :optimizations :advanced}}}})
