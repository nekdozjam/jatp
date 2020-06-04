package com.mmazanek.atp.model.fol;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Formula {
	/**
	 * Replace variables by terms.
	 * 
	 * @param variable to be replaced
	 * @param term
	 * @return new replaced formula
	 */
	public abstract Formula replace(Map<Variable, Term> replaceMap);
	
	/**
	 * Rewrite variables.
	 * 
	 * @param rewriteMap
	 * @return new rewritten formula
	 */
	public Formula rewriteVariables(Map<Variable, Variable> rewriteMap);
	
	/**
	 * Replaces equivalence and implication by conjunction and disjunction.
	 * 
	 * @return Replaced formula
	 */
	default Formula replaceConnectives() {
		return this;
	}
	
	/**
	 * Moves negations to predicates.
	 * 
	 * Results in Negation Normal Form if the formula doesn't contain any implications or equivalences.
	 * 
	 * @return Formula with negations only immediately at predicates.
	 */
	public Formula pushNegations(boolean negate);
	
	/**
	 * Replaces existential quantifiers with skolem functions and removes universal quantifiers.
	 * 
	 * @param scopedVariables list of scoped universal quantifiers
	 * @param generatedSkolelmFuncs list of newly generated skolem functions
	 * @return skolemized formula
	 */
	public Formula skolemize(List<Variable> scopedVariables, List<FunctionTerm> generatedSkolelmFuncs);
	
	/**
	 * Transforms formula to Clausal Normal Form.
	 * 
	 * @return
	 */
	public List<Clause> flatten();
	
	/**
	 * Collect all Variables of this formula and its subformulas.
	 * 
	 * @return Set of collected variables
	 */
	public Set<Variable> collectVariables();
	
}
