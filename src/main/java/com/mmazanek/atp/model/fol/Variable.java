package com.mmazanek.atp.model.fol;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Variable extends Term {
	
	private int id;
	private String name;
	
	public Variable(String name, int id) {
		this.name = name;
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getId() {
		return id;
	}
	
	public Term replace(Map<Variable, Term> replaceMap) {		
		return replaceMap.getOrDefault(this, this);
	}
	
	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		if (other instanceof Variable) {
			return ((Variable)other).id == id;
		}
		if (other instanceof String) {
			return name.equals(other);
		}
		return false;
	}

	@Override
	public Set<Variable> collectVariables() {
		return Collections.singleton(this);
	}

	@Override
	public Term rewriteVariables(Map<Variable, Variable> rewriteMap) {
		return rewriteMap.getOrDefault(this, this);
	}

	@Override
	public boolean deduces(Term other, Map<Variable, Term> replaceMap) {
		if (this.equals(other)) {
			return true;
		}
		Term mapped = replaceMap.get(this);
		if (mapped == null) {
			if (other.collectVariables().contains(this)) {///wtf
				return false;
			}
			replaceMap.put(this, other);
			return true;
		}
		if (mapped instanceof Variable) {
			return other.equals(mapped);
		}
		return mapped.deduces(other, replaceMap);
	}

	@Override
	public Substitution mgu(Term other, Substitution substitution) {
		if (other instanceof Variable) {
			// Var Var
			Variable var2 = (Variable) other;
			if (var2.equals(this)) {
				return substitution;
			} else {
				substitution = substitution.apply(this, other);
			}
		} else {
			// Var fnc
			if (other.collectVariables().contains(this)) {
				return null;
			}
			substitution = substitution.apply(this, other);
		}
		return substitution;
	}

	@Override
	public List<Position> find(Term term) {
		return Collections.emptyList();
	}

	@Override
	public Term replaceOrSubstitute(Position position, Term term) {
		if (position.isFinal()) {
			return term;
		}
		return null;
	}
}
