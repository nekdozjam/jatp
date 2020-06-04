package com.mmazanek.atp.model.fol;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mmazanek.atp.model.fol.LogicalFormula.Connective;

/**
 * Representation of a clause.
 * 
 * @author Martin Mazanek
 */
public class Clause implements Formula {
	
	private List<Literal> literals;
	private Set<Variable> variables = null;

	public static long clausesGenerated = 0;
	public static long clausesFind = 0;
	public static long clausesDeduces = 0;
	public static long clausesReplaceOrSubstitute = 0;
	
	public Clause(List<Literal> literals) {
		Clause.clausesGenerated++;
		//sort!
		this.literals = literals;
		literals.sort((l1, l2) -> l1.getPredicate().getId() - l2.getPredicate().getId());
	}
	
	public List<Literal> getLiterals() {
		return literals;
	}
	
	/**
	 * Create new clause by joining with other
	 * 
	 * @param other
	 * @return new Clause containing literals from this and other clause
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
	
	/**
	 * Whenever this clause has no literals
	 * @return true this has no literals
	 */
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
	
	/**
	 * Compute if other clause can be created by substitution of this clause
	 * 
	 * @param other
	 * @return whenever other clauses can be created by substitution of this
	 */
	public boolean deduces(Clause other) {
		Clause.clausesDeduces++;
		//this has variables - substitution can only happen on this
		List<Literal> otherLiterals = other.getLiterals();
		
		if (otherLiterals.size() != literals.size()) {
			return false;
		}
		
		//fast check first
		Iterator<Literal> otherLiteralsIterator = otherLiterals.iterator();
		for (Literal literal : literals) {
			Literal otherLiteral = otherLiteralsIterator.next();
			if (literal.isNegated() != otherLiteral.isNegated()) {
				return false;
			}
			if (!literal.getPredicate().equals(otherLiteral.getPredicate())) {
				return false;
			}
		}
		
		Map<Variable, Term> replaceMap = new HashMap<>();
		
		otherLiteralsIterator = otherLiterals.iterator();
		for (Literal literal : literals) {
			if (!literal.deduces(otherLiteralsIterator.next(), replaceMap)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Find all possible positions of a given term
	 * 
	 * @param term term to find
	 * @return all positions of term
	 */
	public List<Term.Position> find(Term term) {
		Clause.clausesFind++;
		List<Term.Position> termPositions = new LinkedList<>();
		
		for (int i = 0; i < literals.size(); i++) {
			List<Term.Position> termPositions2 = literals.get(i).find(term);
			if (termPositions2 != null) {
				for (Term.Position termPosition2 : termPositions2) {
					termPosition2.addFirst(i);
					termPositions.add(termPosition2);
				}
			}
		}
		
		return termPositions;
	}
	
	/**
	 * Replace term on position position by the term term and substitute everything else
	 * 
	 * @param position
	 * @param term
	 * @return replaced clause
	 */
	public Clause replaceOrSubstitute(Term.Position position, Term term) {
		Clause.clausesReplaceOrSubstitute++;
		if (position.isFinal()) {
			return null;
		}
		
		List<Literal> newLiterals = new LinkedList<>();
		for (int i = 0; i < literals.size(); i++) {
			if (i == position.getFirst()) {
				Literal l = literals.get(i).replaceOrSubstitute(position.pop(), term);
				if (l == null) {
					return null;
				}
				newLiterals.add(l);
			} else {
				Literal l = literals.get(i).replace(position.getUnifier());
				if (l == null) {
					return null;
				}
				newLiterals.add(l);
			}
		}
		
		return new Clause(newLiterals);
	}
}
