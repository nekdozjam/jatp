package com.mmazanek.atp.model.fol;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

//term - constant / function - FunctionTerm
//		 variable - VariableTerm?
public abstract class Term {
	public abstract Term replace(Map<Variable, Term> replaceMap);
	public abstract Set<Variable> collectVariables();
	public abstract Term rewriteVariables(Map<Variable, Variable> rewriteMap);
	public abstract boolean deduces(Term other, Map<Variable, Term> replaceMap);
	public abstract Substitution mgu(Term other, Substitution substitution);
	public abstract List<Position> find(Term term);
	public abstract Term replaceOrSubstitute(Position position, Term term);
	
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
