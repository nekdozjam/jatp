cnf(someone_in_mansion,axiom,
    ( lives_at_dreadsbury(someone) )).

cnf(someone_killed_agatha,axiom,
    ( killed(someone,aunt_agatha) )).

cnf(agatha_lives_at_mansion,axiom,
    ( lives_at_dreadsbury(aunt_agatha) )).

cnf(butler_lives_at_mansion,axiom,
    ( lives_at_dreadsbury(butler) )).

cnf(charles_lives_at_mansion,axiom,
    ( lives_at_dreadsbury(charles) )).

cnf(noone_else_lives_at_mansion,axiom,
    ( ~ lives_at_dreadsbury(Person)
    | Person = aunt_agatha
    | Person = butler
    | Person = charles )).

cnf(killer_hates_victim,axiom,
    ( ~ killed(Killer,Victim)
    | hates(Killer,Victim) )).

cnf(killer_poorer_than_victim,axiom,
    ( ~ killed(Killer,Victim)
    | ~ richer(Killer,Victim) )).

cnf(charles_and_agatha_hate_different_people,axiom,
    ( ~ hates(aunt_agatha,Person)
    | ~ hates(charles,Person) )).

cnf(agatha_likes_only_butler,axiom,
    ( Person = butler
    | hates(aunt_agatha,Person) )).

cnf(butler_hates_poor_people,axiom,
    ( richer(Person,aunt_agatha)
    | hates(butler,Person) )).

cnf(butler_and_agatha_hate_the_same_people,axiom,
    ( ~ hates(aunt_agatha,Person)
    | hates(butler,Person) )).

cnf(noone_hates_everyone,axiom,
    ( ~ hates(Person,every_one_but(Person)) )).

cnf(agatha_is_not_the_butler,axiom,
    (  aunt_agatha != butler )).

cnf(prove_agatha_killed_herself,negated_conjecture,
    ( ~ killed(aunt_agatha,aunt_agatha) )).