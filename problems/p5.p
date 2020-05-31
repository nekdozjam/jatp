% pokud plati a = b, b = c a plati f(a), plati i f(c) ?
fof(f1, axiom, ( a = b ) ).
fof(f2, axiom, ( b = c ) ).
fof(f3, axiom, ( f(a) ) ).
fof(con, conjecture, (f(c)) ).