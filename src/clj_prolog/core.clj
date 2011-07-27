(ns clj-prolog.core
  (:use (clj-prolog unify)
        clojure.walk))

(defn clause-head [clause]
  (first clause))

(defn clause-body [clause]
  (rest clause))
(def *db-predicates* (ref {}))

(defn get-clauses [pred]
  (dosync
   (get @*db-predicates* pred)))

(defn predicate [relation]
  (first relation))

(defmacro <- [& clause]
  `(add-clause! '~clause))

(defn add-clause! [clause]
  "Add a clause to the DB"
  (let [pred (predicate (clause-head clause))]
    (assert (and (symbol? pred) (not (variable? pred))))
    (dosync
     (alter
      *db-predicates*
      #(assoc % pred (conj (get-clauses pred) clause))))
    pred))

(defn clear-db! []
  "Empty the database"
  (dosync
   (ref-set *db-predicates* {})))

(defn unique-find-anywhere-if [predicate tree & found-so-far-list]
  "Return set of leaves of tree satisfying predicate, without duplicates"
  (let [found-so-far (first found-so-far-list)]
    (if (and (coll? tree) (seq tree))
     (unique-find-anywhere-if
      predicate
      (first tree)
      (unique-find-anywhere-if predicate (rest tree) found-so-far))
     (if ((eval predicate) tree)
       (if (empty? found-so-far)
         #{tree}
         (conj found-so-far tree))
       found-so-far))))

(defn variables-in [exp]
  "Return a list of all the variables in exp"
  (seq (unique-find-anywhere-if 'clj-prolog.unify/variable? exp)))

(defn rename-variables [x]
  "Replace all variables in x with new ones"
  (let [var-map (reduce merge (map (fn [var]
                                     {var (gensym var)})
                                   (variables-in x)))]
    (postwalk-replace var-map x)))

(def prove-all)

(defn prove [goal bindings]
  "Return a list of possible solutions to goal"
  (map (fn [clause]
         (let [new-clause (rename-variables clause)]
           (prove-all (clause-body new-clause)
                      (unify goal (clause-head new-clause) bindings))))
       (get-clauses (predicate goal))))

(defn prove-all [goals bindings]
  "Return a list of solutions to the conjunction of goals"
  (cond (= bindings fail) fail
        (nil? goals) (list bindings)
        :else (map (fn [goal1-solution]
                     (prove-all (rest goals) goal1-solution))
                   (prove (first goals) bindings))))

(defmacro ?-- [& goals]
  `(prove-all '~goals no-bindings))

(defn show-prolog-vars [vars bindings]
  "Print each var with its binding"
  (if (empty? vars)
    (print "Yes")
    (for [var vars]
      (print (str "\n" var " = " (subst-bindings bindings var)))))
  (print ";\n"))

(defn show-prolog-solutions [vars solutions]
  (if (empty? solutions)
    (print "No")
    (map #(show-prolog-vars vars %) solutions)))

(defmacro ?- [& goals]
  `(top-level-prove '~goals))

(defn top-level-prove [goals]
  "Prove the goals and print the variables readably"
  (println goals)
  (show-prolog-solutions
   (variables-in goals)
   (prove-all goals no-bindings)))

;; example

(<- (likes Kim Robin))
(<- (likes Sandy Lee))
(<- (likes Sandy Kim))
(<- (likes Robin cats))
(<- (likes Sandy ?x) (likes ?x cats))
(<- (likes Kim ?x) (likes ?x Lee) (likes ?x Kim))
(<- (likes ?x ?x))

