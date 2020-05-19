cnf(it_is_a_day,axiom,
    ( monday(X)
    | tuesday(X)
    | wednesday(X)
    | thursday(X)
    | friday(X)
    | saturday(X)
    | sunday(X) )).

cnf(monday_not_tuesday,axiom,
    ( ~ monday(X)
    | ~ tuesday(X) )).

cnf(monday_not_wednesday,axiom,
    ( ~ monday(X)
    | ~ wednesday(X) )).

cnf(monday_not_thursday,axiom,
    ( ~ monday(X)
    | ~ thursday(X) )).

cnf(monday_not_friday,axiom,
    ( ~ monday(X)
    | ~ friday(X) )).

cnf(monday_not_saturday,axiom,
    ( ~ monday(X)
    | ~ saturday(X) )).

cnf(monday_not_sunday,axiom,
    ( ~ monday(X)
    | ~ sunday(X) )).

cnf(tuesday_not_wednesday,axiom,
    ( ~ tuesday(X)
    | ~ wednesday(X) )).

cnf(tuesday_not_thursday,axiom,
    ( ~ tuesday(X)
    | ~ thursday(X) )).

cnf(tuesday_not_friday,axiom,
    ( ~ tuesday(X)
    | ~ friday(X) )).

cnf(tuesday_not_saturday,axiom,
    ( ~ tuesday(X)
    | ~ saturday(X) )).

cnf(tuesday_not_sunday,axiom,
    ( ~ tuesday(X)
    | ~ sunday(X) )).

cnf(wednesday_not_thursday,axiom,
    ( ~ wednesday(X)
    | ~ thursday(X) )).

cnf(wednesday_not_friday,axiom,
    ( ~ wednesday(X)
    | ~ friday(X) )).

cnf(wednesday_not_saturday,axiom,
    ( ~ wednesday(X)
    | ~ saturday(X) )).

cnf(wednesday_not_sunday,axiom,
    ( ~ wednesday(X)
    | ~ sunday(X) )).

cnf(thursday_not_friday,axiom,
    ( ~ thursday(X)
    | ~ friday(X) )).

cnf(thursday_not_saturday,axiom,
    ( ~ thursday(X)
    | ~ saturday(X) )).

cnf(thursday_not_sunday,axiom,
    ( ~ thursday(X)
    | ~ sunday(X) )).

cnf(friday_not_saturday,axiom,
    ( ~ friday(X)
    | ~ saturday(X) )).

cnf(friday_not_sunday,axiom,
    ( ~ friday(X)
    | ~ sunday(X) )).

cnf(saturday_not_sunday,axiom,
    ( ~ saturday(X)
    | ~ sunday(X) )).

cnf(monday_yesterday,axiom,
    ( ~ monday(yesterday(X))
    | tuesday(X) )).

cnf(tuesday_yesterday,axiom,
    ( ~ tuesday(yesterday(X))
    | wednesday(X) )).

cnf(wednesday_yesterday,axiom,
    ( ~ wednesday(yesterday(X))
    | thursday(X) )).

cnf(thursday_yesterday,axiom,
    ( ~ thursday(yesterday(X))
    | friday(X) )).

cnf(friday_yesterday,axiom,
    ( ~ friday(yesterday(X))
    | saturday(X) )).

cnf(saturday_yesterday,axiom,
    ( ~ saturday(yesterday(X))
    | sunday(X) )).

cnf(sunday_yesterday,axiom,
    ( ~ sunday(yesterday(X))
    | monday(X) )).

cnf(yesterday_monday,axiom,
    ( monday(yesterday(X))
    | ~ tuesday(X) )).

cnf(yesterday_tuesday,axiom,
    ( tuesday(yesterday(X))
    | ~ wednesday(X) )).

cnf(yesterday_wednesday,axiom,
    ( wednesday(yesterday(X))
    | ~ thursday(X) )).

cnf(yesterday_thursday,axiom,
    ( thursday(yesterday(X))
    | ~ friday(X) )).

cnf(yesterday_friday,axiom,
    ( friday(yesterday(X))
    | ~ saturday(X) )).

cnf(yesterday_saturday,axiom,
    ( saturday(yesterday(X))
    | ~ sunday(X) )).

cnf(yesterday_sunday,axiom,
    ( sunday(yesterday(X))
    | ~ monday(X) )).

cnf(lions_lying_days,axiom,
    ( ~ member(X,lying_days(lion))
    | monday(X)
    | tuesday(X)
    | wednesday(X) )).

cnf(unicorns_lying_days,axiom,
    ( ~ member(X,lying_days(unicorn))
    | thursday(X)
    | friday(X)
    | saturday(X) )).

cnf(lion_lies_on_monday,axiom,
    ( ~ monday(X)
    | member(X,lying_days(lion)) )).

cnf(lion_lies_on_tuesday,axiom,
    ( ~ tuesday(X)
    | member(X,lying_days(lion)) )).

cnf(lion_lies_on_wednesday,axiom,
    ( ~ wednesday(X)
    | member(X,lying_days(lion)) )).

cnf(unicorn_lies_on_thursday,axiom,
    ( ~ thursday(X)
    | member(X,lying_days(unicorn)) )).

cnf(unicorn_lies_on_friday,axiom,
    ( ~ friday(X)
    | member(X,lying_days(unicorn)) )).

cnf(unicorn_lies_on_saturday,axiom,
    ( ~ saturday(X)
    | member(X,lying_days(unicorn)) )).

cnf(admissions1,axiom,
    ( member(X,lying_days(T))
    | ~ admits(T,X,Y)
    | member(Y,lying_days(T)) )).

cnf(admissions2,axiom,
    ( member(X,lying_days(T))
    | admits(T,X,Y)
    | ~ member(Y,lying_days(T)) )).

cnf(admissions3,axiom,
    ( ~ member(X,lying_days(T))
    | ~ admits(T,X,Y)
    | ~ member(Y,lying_days(T)) )).

cnf(admissions4,axiom,
    ( ~ member(X,lying_days(T))
    | admits(T,X,Y)
    | member(Y,lying_days(T)) )).

cnf(admissions5,axiom,
    ( admits(lion,today,yesterday(today)) )).

cnf(admissions6,axiom,
    ( admits(unicorn,today,yesterday(today)) )).

cnf(prove_today_is_thursday,negated_conjecture,
    ( ~ thursday(today) )).