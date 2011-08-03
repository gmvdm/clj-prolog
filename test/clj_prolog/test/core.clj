(ns clj-prolog.test.core
  (:use [clj-prolog core unify]
        [clojure.test]))

(deftest vars-in
  (are [x vars] (= (variables-in x) vars)
       '?x '(?x)
       '(?x (?y ?z)) '(?z ?y ?x)
       '(?x ?x) '(?x)))

(deftest simple-prove 
  (clear-db!)
  (<- (likes Sandy Jane))
  (is (= (prove '(likes Sandy Jane) no-bindings) (list no-bindings))))

(deftest prove-var
  (clear-db!)
  (<- (likes Sandy Jane))
  (is (= (prove '(likes Sandy ?who) no-bindings) '(((?who Jane))))))

(defn has-any? [tree e]
  "Test if e is an element of the tree"
  (cond
   (and (coll? tree) (empty? tree)) false
   (= tree e) true
   (coll? tree) (or (has-any? (first tree) e) (has-any? (rest tree) e))
   :else false))

(deftest prove-fail
  (clear-db!)
  (<- (likes Sandy Jane))
  (is (empty? (prove '(likes Sandy Peter) no-bindings))))

(deftest prove-inf
  (clear-db!)
  (<- (likes Kim cats))
  (<- (likes Sandy ?x) (likes ?x cats))
  (is (has-any? (prove '(likes Sandy ?who) no-bindings) 'Kim)))


(deftest prove-all-fail
  (clear-db!)
  (is (= (prove-all '(likes Sandy ?who) fail) fail)))

(deftest prove-all-goal
  (clear-db!)
  (is (= (prove-all '() '(?who Sandy)) '((?who Sandy)))))

(deftest mapcan-can
  (are [f xs result] (= (mapcan f xs) result)
       (fn [x] (if (> x 0) (list x) nil)) '(-4 6 -23 1 0 12 ) '(6 1 12)
       list '(a b c d) '(a b c d)
       (fn [x] (list (+ x 10) 'x)) '(1 2 3 4) '(11 x 12 x 13 x 14 x)
       (fn [x] (if (number? x) (list x) nil)) '(a 1 b c 3 4 d 5) '(1 3 4 5)
       ))

(deftest fact-macro
  (clear-db!)
  (fact (likes Sandy Jim))
  (is (has-any? (prove '(likes Sandy ?who) no-bindings) 'Jim)))

(deftest rule-macro
  (clear-db!)
  (fact (likes Robin cats))
  (rule (likes Sandy ?x) if (likes ?x cats))
  (is (has-any? (prove '(likes Sandy ?who) no-bindings) 'Robin)))
