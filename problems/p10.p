cnf(commutativity_of_addition,axiom,
    ( add(A,B) = add(B,A) )).

cnf(associativity_of_addition,axiom,
    ( add(A,add(B,C)) = add(add(A,B),C) )).

cnf(addition_inverts_subtraction1,axiom,
    ( subtract(add(A,B),B) = A )).

cnf(addition_inverts_subtraction2,axiom,
    ( A = subtract(add(A,B),B)  )).

cnf(commutativity1,axiom,
    ( add(subtract(A,B),C) = subtract(add(A,C),B) )).

cnf(commutativity2,axiom,
    ( subtract(add(A,B),C) = add(subtract(A,C),B) )).

cnf(add_substitution1,axiom,
    ( A != B
    | C != add(A,D)
    | C = add(B,D) )).

cnf(add_substitution2,axiom,
    ( A != B
    | C != add(D,A)
    | C != add(D,B) )).

cnf(prove_equation,negated_conjecture,
    ( add(add(a,b),c) != add(a,add(b,c)) )).