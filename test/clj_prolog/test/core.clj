(ns clj-prolog.test.core
  (:use [clj-prolog core]
        [clojure.test]))

(deftest vars-in
  (are [x vars] (= (variables-in x) vars)
       '?x '(?x)
       '(?x (?y ?z)) '(?z ?y ?x)
       '(?x ?x) '(?x)))
