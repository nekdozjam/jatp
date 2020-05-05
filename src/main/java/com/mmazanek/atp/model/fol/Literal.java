package com.mmazanek.atp.model.fol;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Literal implements Formula {
	
	private boolean negated;
	private PredicateSymbol predicate;
	private List<Term> terms;
	private Set<Variable> variables = null;
	
	public static final Literal TRUE = new Literal(PredicateSymbol.TRUE, false, Collections.EMPTY_LIST) {
		@Override
		public Formula pushNegations(boolean negate) {
			return negate ? FALSE : TRUE;
		}
	};
	
	public static final Literal FALSE = new Literal(PredicateSymbol.FALSE, false, Collections.EMPTY_LIST) {
		@Override
		public Formula pushNegations(boolean negate) {
			return negate ? TRUE : FALSE;
		}
	};
	
	public Literal(PredicateSymbol predicate, boolean negated, List<Term> terms) {
		if (predicate.getArity() != terms.size()) {
			throw new IllegalArgumentException("Predicate symbol arity does not correspond to the number of terms provided!");
		}
		this.negated = negated;
		this.terms = terms;
		this.predicate = predicate;
	}
	
	public PredicateSymbol getPredicate() {
		return predicate;
	}
	
	public List<Term> getTerms() {
		return terms;
	}
	
	public Literal replace(Map<Variable, Term> replaceMap) {
		List<Term> terms2 = new LinkedList<>();
		for (Term t : terms) {
			terms2.add(t.replace(replaceMap));
		}
		return new Literal(predicate, negated, terms2);
	}
	
	public boolean isNegated() {
		return negated;
	}
	
	@Override
	public Formula pushNegations(boolean negate) {
		return negate ? new Literal(predicate, !negated, terms) : this;
	}

	@Override
	public Formula skolemize(List<Variable> scopedVariables, List<FunctionTerm> generatedSkolelmFuncs) {
		return this;
	}

	@Override
	public List<Clause> flatten() {
		return Collections.singletonList(new Clause(Collections.singletonList(this)));
	}
	
	@Override
	public Set<Variable> collectVariables() {
		if (variables == null) {
			variables = new HashSet<>();
			for (Term t : terms) {
				variables.addAll(t.collectVariables());
			}
		}
		return variables;
	}

	@Override
	public Formula rewriteVariables(Map<Variable, Variable> rewriteMap) {
		List<Term> terms2 = new LinkedList<>();
		for (Term t : terms) {
			terms2.add(t.rewriteVariables(rewriteMap));
		}
		Literal newLiteral = new Literal(predicate, negated, terms2);
		if (variables != null) {
			Set<Variable> variables2 = new HashSet<>();
			for (Variable v : variables) {
				variables2.add(rewriteMap.getOrDefault(v, v));
			}
			newLiteral.variables = variables2;
		}
		return newLiteral;
	}
}
