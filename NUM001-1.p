cnf(reflexivity,axiom,
    ( equalish(A,A) )).

cnf(transitivity,axiom,
    ( ~ equalish(A,B)
    | ~ equalish(B,C)
    | equalish(A,C) )).

cnf(commutativity_of_addition,axiom,
    ( equalish(add(A,B),add(B,A)) )).

cnf(associativity_of_addition,axiom,
    ( equalish(add(A,add(B,C)),add(add(A,B),C)) )).

cnf(addition_inverts_subtraction1,axiom,
    ( equalish(subtract(add(A,B),B),A) )).

cnf(addition_inverts_subtraction2,axiom,
    ( equalish(A,subtract(add(A,B),B)) )).

cnf(commutativity1,axiom,
    ( equalish(add(subtract(A,B),C),subtract(add(A,C),B)) )).

cnf(commutativity2,axiom,
    ( equalish(subtract(add(A,B),C),add(subtract(A,C),B)) )).

cnf(add_substitution1,axiom,
    ( ~ equalish(A,B)
    | ~ equalish(C,add(A,D))
    | equalish(C,add(B,D)) )).

cnf(add_substitution2,axiom,
    ( ~ equalish(A,B)
    | ~ equalish(C,add(D,A))
    | equalish(C,add(D,B)) )).

cnf(prove_equation,negated_conjecture,
    ( ~ equalish(add(add(a,b),c),add(a,add(b,c))) )).