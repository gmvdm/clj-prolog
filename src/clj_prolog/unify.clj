(ns clj-prolog.unify)

(def
  ^{:doc "Should unify do an occurs check?"}
  *occurs-check* true)

(def
  ^{:doc "Indicates a pattern match fail"}
  fail
  nil)

(def
  ^{:doc "Indicates a pattern match success with no bindings"}
  no-bindings
  '((true true)))

(defn variable? [x]
  (and (symbol? x) (= (first (str x)) \?)))

(defn get-binding [var bindings]
  "Find a (var value) pair in the binding list"
  (first (filter #(= (first %) var) bindings)))

(defn binding-val [binding]
  "Given a binding pair, return the value"
  (second binding))

(defn lookup [var bindings]
  "Get the value part (for var) from a binding list"
  (binding-val (get-binding var bindings)))

(defn extend-bindings [var val bindings]
  (cons (list var val)
        (if (= bindings no-bindings)
          nil
          bindings)))

(defn match-variable [var input bindings]
  "Does Var match input? Uses (or updates) and returns bindings"
  (let [binding (get-binding var bindings)]
    (cond (not binding) (extend-bindings var input bindings)
          (= input (binding-val binding)) bindings
          :else fail)))

(defn occurs-check [var x bindings]
  "Does var occur anywhere inside x?"
  (cond (= var x) true
        (and (variable? x)
             (get-binding x bindings)) (occurs-check var
                                                     (lookup x bindings) bindings)
             (coll? x) (or (occurs-check var (first x) bindings)
                           (occurs-check var (rest x) bindings))
             :else false))

(def unify) ;; forward declaration

(defn unify-variable [var x bindings]
  "Unify var with x, using (and maybe extending) bindings"
  (cond
   (get-binding var bindings) (unify (lookup var bindings) x bindings)
   (and (variable? x)
        (get-binding x bindings)) (unify var (lookup x bindings) bindings)
   ;; occurs
   (and *occurs-check* (occurs-check var x bindings)) fail
   :else (extend-bindings var x bindings)))

(defn unify 
  "See if x and y match given bindings"
  ([x y] (unify x y no-bindings))
  ([x y bindings]
     (cond (= bindings fail) fail
           (= x y) bindings
           (variable? x) (unify-variable x y bindings)
           (variable? y) (unify-variable y x bindings)
           ;; lists
           (and (coll? x) (coll? y))
           (unify (rest x) (rest y)
                  (unify (first x) (first y) bindings))
           :else fail)))

(defn subst-bindings [bindings x]
  "Substitute the value of vars in bindings into x, considering recursive bindings"
  (cond (= bindings fail) fail
        (= bindings no-bindings) x
        (and (variable? x)
             (get-binding x bindings)) (subst-bindings bindings (lookup x bindings))
        (and (coll? x) (not (empty? x))) (cons (subst-bindings bindings (first x))
                                               (subst-bindings bindings (rest x)))
        :else x))

(defn unifier [x y]
  "Return something that unifies with both x and y or fail."
  (subst-bindings (unify x y) x))
