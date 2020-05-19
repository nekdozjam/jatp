package com.mmazanek.atp.model.fol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mmazanek.atp.model.fol.Term.Position;

public class Literal implements Formula {
	
	private boolean negated;
	private PredicateSymbol predicate;
	private List<Term> terms;
	private Set<Variable> variables = null;
	
	public static final Literal TRUE = new Literal(PredicateSymbol.TRUE, false, Collections.emptyList()) {
		@Override
		public Formula pushNegations(boolean negate) {
			return negate ? FALSE : TRUE;
		}
	};
	
	public static final Literal FALSE = new Literal(PredicateSymbol.FALSE, false, Collections.emptyList()) {
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
			Term term2 = t.replace(replaceMap);
			if (term2 == null) {
				System.out.println("literal replace");
			}
			terms2.add(term2);
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
				if (t == null) {
					System.out.println("literal 0");
				}
				variables.addAll(t.collectVariables());
			}
		}
		return variables;
	}

	@Override
	public Formula rewriteVariables(Map<Variable, Variable> rewriteMap) {
		List<Term> terms2 = new LinkedList<>();
		for (Term t : terms) {
			Term t2 = t.rewriteVariables(rewriteMap);
			if(t2 == null) {
				System.out.println("literal 54");
			}
			terms2.add(t2);
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
	
	public boolean deduces(Literal literal, Map<Variable, Term> replaceMap) {
		if (this.negated != literal.negated) {
			return false;
		}
		if (this.predicate != literal.predicate) {
			return false;
		}
		Iterator<Term> otherTerms = literal.terms.iterator();
		for (Term term : terms) {
			if (!term.deduces(otherTerms.next(), replaceMap)) {
				return false;
			}
		}
		return true;
	}
	
	public Substitution mgu(Literal other) {
		Substitution s = new Substitution();
		if (!this.getPredicate().equals(other.getPredicate())) {
			return null;
		}
		Iterator<Term> l2iter = other.getTerms().iterator();
		for (Term t : this.getTerms()) {
			Term t2 = l2iter.next();
			s = t.replace(s).mgu(t2.replace(s), s);
			if (s == null) {
				return null;
			}
		}
		return s;
	}
	
	public List<Term.Position> find(Term term) {
		List<Term.Position> termPositions = new LinkedList<>();
		
		for (int i = 0; i < terms.size(); i++) {
			List<Term.Position> termPositions2 = terms.get(i).find(term);
			if (termPositions2 != null) {
				for (Term.Position termPosition2 : termPositions2) {
					termPosition2.addFirst(i);
					termPositions.add(termPosition2);
				}
			}
		}
		
		return termPositions;
	}
	

	public Literal replaceOrSubstitute(Term.Position position, Term term) {
		if (position.isFinal()) {
			return null;
		}
		
		List<Term> newTerms = new ArrayList<>(terms.size());
		for (int i = 0; i < terms.size(); i++) {
			if (i == position.getFirst()) {
				Term t = terms.get(i).replaceOrSubstitute(position.pop(), term);
				if (t == null) {
					System.out.println("literal 1");
					return null;
				}
				newTerms.add(t);
			} else {
				Term t = terms.get(i).replace(position.getUnifier());
				if (t == null) {
					System.out.println("literal 2");
					return null;
				}
				newTerms.add(t);
			}
		}
		return new Literal(predicate, negated, newTerms);
	}
}
