cnf(c1, axiom, (~equalish(X3, subtract(add(X1, X2), X2)) | equalish(X3, X1)) ).
cnf(c2, axiom,  (equalish(add(subtract(A, B), C), subtract(add(A, C), B))) ).
cnf(c3, negated_conjecture, (~equalish(add(subtract(X1, X2), X3), X1)) ).