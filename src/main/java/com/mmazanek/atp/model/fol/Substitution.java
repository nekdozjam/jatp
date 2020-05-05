package com.mmazanek.atp.model.fol;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Substitution extends HashMap<Variable, Term> {
	
	private static final long serialVersionUID = 1766134542496746435L;

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
