;; http://www.allisons.org/ll/Logic/Prolog/Examples/solar/

;; Facts
(<- (orbits Mercury Sun))
(<- (orbits Venus Sun))
(<- (orbits Earth Sun))
(<- (orbits Mars Sun))
(<- (orbits Jupiter Sun))
(<- (orbits Saturn Sun))
(<- (orbits Uranus Sun))
(<- (orbits Neptune Sun))

(<- (orbits Moon Earth))

(<- (orbits Deimos Mars))
(<- (orbits Phobos Mars))

(<- (orbits Ganymede Jupiter))
(<- (orbits Callisto Jupiter))
(<- (orbits Io       Jupiter))
(<- (orbits Europa   Jupiter))

(<- (orbits Titan     Saturn))
(<- (orbits Enceladus Saturn))

(<- (orbits Titania Uranus))
(<- (orbits Oberon  Uranus))
(<- (orbits Umbriel Uranus))
(<- (orbits Ariel   Uranus))
(<- (orbits Miranda Uranus))

(<- (orbits Triton Neptune))

;; Rules
(<- (planet ?p) (orbits ?p Sun))
(<- (satellite ?s) (orbits ?s ?p) (planet ?p))

;; Query
(?- (satellite ?s))

