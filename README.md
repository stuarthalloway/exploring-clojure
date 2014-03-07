# exploring-clojure

Template project for interactive exploration of Clojure.  Will gather a
kitchen sink of dependencies over time.

## Usage

Start a REPL and try something.

## Clojure in Ten Big Ideas

Code samples that follow [Clojure in Ten Big Ideas]().

Except where otherwise noted, these are all in the examples/exploring
directory, and intended to be interactively invoked, one form at a
time, from the REPL.

* edn: data\_formats.clj
* persistent data structures: persistent\_data\_structures.clj
* unified succession model:
* sequences:
* protocols: rps\_protocols.clj
* ClojureScript: see below
* reducers: reducing\_apple\_pie.clj
* core.logic: rock\_paper\_scissors.clj
* datalog: clone the [day of datomic](https://github.com/Datomic/day-of-datomic) repo and follow the README instructions
* core.async: clone the
  [core.async](https://github.com/clojure/core.async) repo and work
  through the examples directory.

## Generative Testing

Run generative tests

    lein run -m run-tests

## Other things to try

* Keep a copy of the [Clojure
  cheatsheet](http://clojure.org/cheatsheet) handy.
* [TryClojure](http://tryclj.com/) will let you try Clojure in the
  browser, without any local setup required.
* [4Clojure](http://www.4clojure.com/) provides step-by-step
  interaction with immediate feedback.

## ClojureScript

* Himera[http://himera.herokuapp.com/index.html] will let you try
  ClojureScript in the browser, without any local setup required.
* The [modern-cljs
  tutorials](https://github.com/magomimmo/modern-cljs) are suitable
  for preparing to use ClojureScript in anger, covering toolchain and
  libs in addition to the language.

## License

Copyright © 2013 Stuart Halloway

Distributed under the Eclipse Public License, the same as Clojure.
