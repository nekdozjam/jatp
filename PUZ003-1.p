cnf(one_shaved_then_all_shaved,axiom,
    ( ~ member(X)
    | ~ member(Y)
    | ~ shaved(X,Y)
    | shaved(members,X) )).

cnf(all_shaved_then_one_shaved,axiom,
    ( ~ shaved(members,X)
    | ~ member(Y)
    | shaved(Y,X) )).

cnf(guido,hypothesis,
    ( member(guido) )).

cnf(lorenzo,hypothesis,
    ( member(lorenzo) )).

cnf(petruchio,hypothesis,
    ( member(petruchio) )).

cnf(cesare,hypothesis,
    ( member(cesare) )).

cnf(guido_has_shaved_cesare,hypothesis,
    ( shaved(guido,cesare) )).

cnf(prove_petruchio_has_shaved_lorenzo,negated_conjecture,
    ( ~ shaved(petruchio,lorenzo) )).
