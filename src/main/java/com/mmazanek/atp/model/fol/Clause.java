package com.mmazanek.atp.model.fol;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mmazanek.atp.model.fol.LogicalFormula.Connective;

public class Clause implements Formula {
	
	private List<Literal> literals;
	private Set<Variable> variables = null;
	
	public Clause(List<Literal> literals) {
		//sort!
		this.literals = literals;
		literals.sort((l1, l2) -> l1.getPredicate().getId() - l2.getPredicate().getId());
	}
	
	public List<Literal> getLiterals() {
		return literals;
	}
	
	/**
	 * 
	 * @param other
	 * @return
	 */
	public Clause join(Clause other) {
		List<Literal> literals2 = new LinkedList<>();
		literals2.addAll(literals);
		literals2.addAll(other.literals);
		return new Clause(literals2);
	}

	@Override
	public Formula replace(Map<Variable, Term> replaceMap) {
		List<Literal> literals2 = new LinkedList<>();
		for (Literal l : literals) {
			literals2.add(l.replace(replaceMap));
		}
		return new Clause(literals2);
	}
	
	@Override
	public Formula pushNegations(boolean negate) {
		Formula f = literals.get(0);
		for (int i = 1; i < literals.size(); i++) {
			f = new LogicalFormula(Connective.OR, new Formula[]{f, literals.get(i)});
		}
		
		return f.pushNegations(negate);
	}

	@Override
	public Formula skolemize(List<Variable> scopedVariables, List<FunctionTerm> generatedSkolelmFuncs) {
		throw new RuntimeException("Clause skolemization not implemented");
	}

	@Override
	public List<Clause> flatten() {
		throw new RuntimeException("Clause flattening not implemented");
	}
	
	public boolean isEmpty() {
		return literals.isEmpty();
	}
	
	@Override
	public Set<Variable> collectVariables() {
		if (variables == null) {
			variables = new HashSet<>();
			for (Literal l : literals) {
				variables.addAll(l.collectVariables());
			}
		}
		return variables;
	}

	@Override
	public Formula rewriteVariables(Map<Variable, Variable> rewriteMap) {
		List<Literal> literals2 = new LinkedList<>();
		for (Literal l : literals) {
			literals2.add((Literal)l.rewriteVariables(rewriteMap));
		}
		Clause c = new Clause(literals2);
		if (variables != null) {
			Set<Variable> variables2 = new HashSet<>();
			for (Variable v : variables) {
				variables2.add(rewriteMap.getOrDefault(v, v));
			}
			c.variables = variables2;
		}
		return c;
	}
	
	public boolean deduces(Clause clause) {
		//this has variables - substitution can only happen on this
		List<Literal> otherLiterals = clause.getLiterals();
		
		if (otherLiterals.size() != literals.size()) {
			return false;
		}
		
		//fast check first
		Iterator<Literal> otherLiteralsIterator = otherLiterals.iterator();
		for (Literal literal : literals) {
			Literal other = otherLiteralsIterator.next();
			if (literal.isNegated() != other.isNegated()) {
				return false;
			}
			if (! literal.getPredicate().equals(other.getPredicate())) {
				return false;
			}
		}
		
		Map<Variable, Term> replaceMap = new HashMap<>();
		
		//we need sort the literals first!
		
		otherLiteralsIterator = otherLiterals.iterator();
		for (Literal literal : literals) {
			if (!literal.deduces(otherLiteralsIterator.next(), replaceMap)) {
				return false;
			}
		}
		return true;
	}
}
