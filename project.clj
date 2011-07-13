(defproject clj-prolog "1.0.0-SNAPSHOT"
  :description "FIXME: write description"
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]]
  :dev-dependencies [[swank-clojure "1.3.0"]
                     [com.stuartsierra/lazytest "1.1.2"]
                     [lein-autotest "1.1.0"]
                     [lein-tar "1.0.2"]
                     [lein-run "1.0.1-SNAPSHOT"]
                     [lein-difftest "1.3.1"]]
  :hooks [leiningen.hooks.difftest])
