(ns clj-prolog.test.core
  (:use [clj-prolog.core])
  (:use [clojure.test]))


(deftest unify-test
  (are [x y bindings] (= (unify x y) bindings)
       '?x '?y '((?x ?y))
       '(?x + 1) '(2 + ?y) '((?y 1) (?x 2))
       ))
