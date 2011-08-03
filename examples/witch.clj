;; http://www.allisons.org/ll/Logic/Prolog/Examples/witch/

;; A witch is a female who burns. Witches burn - because they're made
;; of wood. Wood floats. What else floats on water? A duck; if
;; something has the same weight as a duck it must float. A duck and
;; scales are fetched. The girl and the duck balance perfectly. "It's
;; a fair cop."

;; Prolog
;; witch(X)  <= burns(X) and female(X).
;; burns(X)  <= wooden(X).
;; wooden(X) <= floats(X).
;; floats(X) <= sameweight(duck, X).

;; female(girl).          {by observation}
;; sameweight(duck,girl). {by experiment }

;; ? witch(girl).

(<- (witch ?x) (burns ?x) (female ?x))
(<- (burns ?x) (wooden ?x))
(<- (wooden ?x) (floats ?x))
(<- (floats ?x) (sameweight Duck ?x))

(<- (female Girl))
(<- (sameweight Duck Girl))

(?- (witch Girl))

;; Yes;
