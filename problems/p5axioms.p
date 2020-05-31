% pokud plati a = b, b = c a plati f(a), plati i f(c) ?
fof(e1, axiom, (! [X]: (eq(X, X))) ).
fof(e2, axiom, (! [X,Y]: (eq(X, Y) => eq(Y, X))) ).

fof(e3, axiom, (! [X,Y]: (eq(X, Y) => (f(X) => f(Y)))) ).

fof(f1, axiom, ( eq(a, b) ) ).
fof(f2, axiom, ( eq(b, c) ) ).
fof(f3, axiom, ( f(a) ) ).
fof(con, conjecture, (f(c)) ).