package com.mmazanek.atp.model.fol;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Substitution extends HashMap<Variable, Term> {
	
	private static final long serialVersionUID = 1766134542496746435L;

	/*
	 
	 	cnf(f__24, plain,          ~equalish(X3,                     subtract(add(X1, X2), X2))       |  equalish(X3, X1),     inference(resolution,[status(thm)], [addition_inverts_subtraction1,transitivity])).
		cnf(commutativity1, axiom,  equalish(add(subtract(A, B), C), subtract(add(A, C), B)),                                  file('D:\Dokumenty\school\bakalářka\ws\jatp\.\NUM001-1.p', commutativity1)).
		cnf(f__1078, plain,         equalish(add(subtract(X1, X2), X3), X1),                                                   inference(resolution,[status(thm)], [f__24,commutativity1])).

	 */
	
	public Substitution apply(Variable variable, Term term) {
		replaceAll((ignorekey, term1) -> term1.replace(Collections.singletonMap(variable, term)));
		if (put(variable, term) != null) {
			return null;
		}
		return this;
	}
	
	public Substitution apply(Map<Variable, Term> replaceMap) {
		replaceAll((ignorekey, term1) -> term1.replace(replaceMap));
		for (Map.Entry<Variable, Term> entry : replaceMap.entrySet()) {
			if (put(entry.getKey(), entry.getValue()) == null) {
				return null;
			}
		}
		return this;
	}
}
