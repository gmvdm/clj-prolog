(ns clj-prolog.test.unify
  (:use [clj-prolog.unify])
  (:use [clojure.test]))


(deftest variable-test
  (are [var result] (= (variable? var) result)
       '?x true
       '?y true
       "?y" false
       nil false
       ['?x] false))

(deftest lookup-test
  (are [var bindings value] (= (lookup var bindings) value)
       '?x '((?x 2) (?y 1)) 2
       '?x '((?y 1) (?z 2)) nil))

(deftest unify-basecases
  (are [x y bindings result] (= (unify x y bindings) result)
       '?x 1 fail fail
       1 1 '((?x 2)) '((?x 2))
       '?x 1 no-bindings '((?x 1))
       1 '?y no-bindings '((?y 1))
       ;; TODO test recursive case
       1 2 no-bindings fail
       ))

(deftest occurs-test
  (are [var x bindings result] (= (occurs-check var x bindings) result)
       '?x '(f ?x) no-bindings true
       '?x '?y no-bindings false))

(deftest unify-test
  (are [x y bindings] (= (unify x y) bindings)
       '(f ?x) '(f ?y) '((?x ?y))
       '?x '?y '((?x ?y))
       '(?x + 1) '(2 + ?y) '((?y 1) (?x 2))
       '(?x ?x) '(?y ?y) '((?x ?y))
       '(?x ?y) '(?y ?x) '((?x ?y))
       '(?x ?x ?x) '(?y ?y ?y) '((?x ?y))
       '(?x ?x a) '(?y ?y ?y) '((?y a) (?x ?y))
       ))
