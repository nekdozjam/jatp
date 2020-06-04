fof(q1, axiom, (! [X]: (~eq(nul, suc(X))))).
fof(q2, axiom, (! [X]: (~eq(X, nul) => ( ? [Y] : (eq(X, suc(Y))))) )).
fof(q3, axiom, (! [X,Y]: ( eq(suc(X), suc(Y)) => eq(X, Y)) )).
fof(q4, axiom, (! [X]: ( eq(add(X, nul), nul)) )).
fof(q5, axiom, (! [X,Y]: ( eq(add(X, suc(Y)), suc(add(X, Y)))  ))).

fof(e1, axiom, (! [X]: (eq(X, X))) ).
fof(e2, axiom, (! [X,Y]: (eq(X, Y) => eq(Y, X))) ).
fof(e3, axiom, (! [X,Y]: (eq(X,Y) => eq(suc(X), suc(Y)))) ).
fof(e4, axiom, (! [X,Y,Z]: (eq(X,Y) => eq(add(X,Z), add(Y,Z)))) ).

fof(con, conjecture, (eq(add(suc(nul), suc(nul)), suc(suc(nul))) )).

