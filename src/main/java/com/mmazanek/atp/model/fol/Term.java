package com.mmazanek.atp.model.fol;

import java.util.Map;
import java.util.Set;

//term - constant / function - FunctionTerm
//		 variable - VariableTerm?
public abstract class Term {
	public abstract Term replace(Map<Variable, Term> replaceMap);
	public abstract Set<Variable> collectVariables();
	public abstract Term rewriteVariables(Map<Variable, Variable> rewriteMap);
}
