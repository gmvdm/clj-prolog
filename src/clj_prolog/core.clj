(ns clj-prolog.core)

(def
  ^{:doc "Should unify do an occurs check?"}
  *occurs-check* true)

(def
  ^{:doc "Indicates a pattern match fail"}
  fail
  nil)

;; TODO is this the best data structure?
(def
  ^{:doc "Indicates a pattern match success with no bindings"}
  no-bindings
  '((true true)))

(defn variable? [x]
  (and (symbol? x) (= (first (str x)) \?)))

;; TODO figure out binding lists, maybe as a map?
;; TODO re-factor
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
  false)

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

;; TODO default argument is a bit odd
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
