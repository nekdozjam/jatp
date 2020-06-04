package com.mmazanek.atp.model.fol;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Representation of a term
 * 
 * @author Martin Mazanek
 */
public abstract class Term {
	
	/**
	 * Get a new term created by replacing all variables in the replaceMap to their specific mappings
	 * 
	 * @param replaceMap
	 * @return new replaced term
	 */
	public abstract Term replace(Map<Variable, Term> replaceMap);
	
	/**
	 * Collect all variables it this term and its subterms
	 * 
	 * @return Set of collected variables
	 */
	public abstract Set<Variable> collectVariables();
	
	/**
	 * Rewrite variables to variables
	 * 
	 * @param rewriteMap
	 * @return new term with rewritten variables
	 */
	public abstract Term rewriteVariables(Map<Variable, Variable> rewriteMap);
	
	/**
	 * Compute if other term can be created by substitution of this term
	 * 
	 * @param other other term
	 * @param replaceMap applied substitution
	 * @return true if other term can be created of substituting this
	 */
	public abstract boolean deduces(Term other, Map<Variable, Term> replaceMap);
	
	/**
	 * Get the most general unifier of this term and other term
	 * @param other
	 * @param substitution
	 * @return most general unifier or {@code null}
	 */
	public abstract Substitution mgu(Term other, Substitution substitution);
	
	/**
	 * Find all positions of the term term in this term or its subterms
	 * 
	 * @param term
	 * @return all found positions
	 */
	public abstract List<Position> find(Term term);
	
	/**
	 * Replace term on position position by the term term and substitute everything else
	 * 
	 * @param position
	 * @param term
	 * @return replaced term
	 */
	public abstract Term replaceOrSubstitute(Position position, Term term);
	
	/**
	 * Class for storing term position in a term
	 */
	public static class Position {
		private List<Integer> list;
		private Substitution substitution;
		
		private Position(List<Integer> list, Substitution substitution) {
			this.list = list;
			this.substitution = substitution;
		}
		
		public Position(Substitution substitution) {
			list = new LinkedList<>();
			this.substitution = substitution;
		}
		
		public Position pop() {
			return new Position(list.subList(1, list.size()), substitution);
		}
		
		public void addFirst(int i) {
			list.add(0, i);
		}
		
		public boolean isFinal() {
			return list.size() == 0;
		}
		
		public Substitution getUnifier() {
			return substitution;
		}
		
		public int getFirst() {
			return list.get(0);
		}
	}
}
