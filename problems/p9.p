%fof(q1, axiom, (! [X]: (nul != suc(X)))).
%fof(q2, axiom, (! [X]: ((X != nul) => ( ? [Y] : (X = suc(Y)))) )).
%fof(q3, axiom, (! [X,Y]: ((suc(X) = suc(Y)) => (X = Y)) )).
%fof(q4, axiom, (! [X]: (add(X, nul) = nul) )).
%fof(q5, axiom, (! [X,Y]: ( add(X, suc(Y)) = suc(add(X, Y))  ))).

fof(a0, axiom, (one = suc(nul))).
fof(a1, axiom, (a = one)).

fof(con, conjecture, (suc(a) = suc(suc(nul)) )).