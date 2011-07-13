(ns clj-prolog.core)


(def
  ^{:doc "Indicates a pattern match fail"}
  fail
  '((false false)))

;; TODO is this the best data structure?
(def
  ^{:doc "Indicates a pattern match success with no bindings"}
  no-bindings
  '((true true)))

(defn variable? [x]
  (and (symbol? x) (= (first (str x)) "?")))

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

(def unify)

(defn unify-variable [var x bindings]
  "Unify var with x, using (and maybe extending) bindings"
  (if (get-binding var bindings)
    (unify (lookup var bindings) x bindings)
    (extend-bindings var x bindings)))

(defn unify [x y & bindings]
  "See if x and y match given bindings"
  (let [bindings (if (nil? bindings) no-bindings bindings)]
    (cond (= bindings fail) fail
          (variable? x) (unify-variable x y bindings)
          (variable? y) (unify-variable y x bindings)
          (= x y) bindings
          (and (coll? x) (coll? y)) (unify
                                     (first x) (first y) bindings)
          :else fail)))
