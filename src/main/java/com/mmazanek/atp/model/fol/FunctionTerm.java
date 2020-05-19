package com.mmazanek.atp.model.fol;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FunctionTerm extends Term {
	
	private FunctionSymbol symbol;
	private List<? extends Term> parameters;
	private Set<Variable> variables = null;
	
	public FunctionTerm(FunctionSymbol symbol, List<? extends Term> parameters) {
		this.symbol = symbol;
		this.parameters = parameters;
	}

	public FunctionSymbol getSymbol() {
		return symbol;
	}
	
	public List<? extends Term> getParameters() {
		return parameters;
	}
	
	public Term replace(Map<Variable, Term> replaceMap) {
		List<Term> parameters2 = new LinkedList<Term>();
		for (Term t : parameters) {
			parameters2.add(t.replace(replaceMap));
		}
		return new FunctionTerm(symbol, parameters2);
	}

	public void setFunctionSymbol(FunctionSymbol symbol) {
		this.symbol = symbol;
	}

	@Override
	public Set<Variable> collectVariables() {
		if (variables == null) {
			variables = new HashSet<>();
			for (Term t : parameters) {
				variables.addAll(t.collectVariables());
			}
		}
		return variables;
	}

	@Override
	public Term rewriteVariables(Map<Variable, Variable> rewriteMap) {
		List<Term> parameters2 = new LinkedList<>();
		for (Term t : parameters) {
			Term t2 = t.rewriteVariables(rewriteMap);
			if (t2 == null) {
				System.out.println("function 123");
			}
			parameters2.add(t2);
		}
		FunctionTerm f = new FunctionTerm(symbol, parameters2);
		if (variables != null) {
			Set<Variable> variables2 = new HashSet<>();
			for (Variable v : variables) {
				variables2.add(rewriteMap.getOrDefault(v, v));
			}
			f.variables = variables2;
		}
		return f;
	}

	@Override
	public boolean deduces(Term other, Map<Variable, Term> replaceMap) {
		if (other instanceof Variable) {
			return false;
		}
		FunctionTerm otherTerm = (FunctionTerm)other;
		if (otherTerm.symbol != symbol) {
			return false;
		}
		Iterator<? extends Term> otherParameters = otherTerm.parameters.iterator();
		for (Term parameter : parameters) {
			if (!parameter.deduces(otherParameters.next(), replaceMap)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public Substitution mgu(Term other, Substitution substitution) {
		if (other instanceof Variable) {
			// fcn Var
			Variable var2 = (Variable) other;
			if (this.collectVariables().contains(var2)) {
				return null;
			}
			substitution.apply(var2, this);
		} else {
			// fnc fnc
			Substitution localSubstitution = new Substitution();
			FunctionTerm fnc2 = (FunctionTerm) other;
			if (!this.getSymbol().equals(fnc2.getSymbol())) {
				return null;
			}
			Iterator<? extends Term> funcTerms2 = fnc2.getParameters().iterator();
			for (Term funcTerm1 : this.getParameters()) {
				localSubstitution = funcTerm1.replace(localSubstitution).mgu(funcTerms2.next().replace(localSubstitution), localSubstitution);
				if (localSubstitution == null) {
					return null;
				}
			}
			substitution.apply(localSubstitution);
		}
		return substitution;
	}

	@Override
	public List<Position> find(Term term) {
		Substitution localSubstitution = mgu(term, new Substitution());
		List<Position> positions = new LinkedList<>();
		
		for (int i = 0; i < parameters.size(); i++) {
			List<Position> positions2 = parameters.get(i).find(term);
			for (Position p : positions2) {
				p.addFirst(i);
				positions.add(p);
			}
		}
		
		if (localSubstitution != null) {
			System.out.println("found " + term.toString() + " in " + toString());
			positions.add(new Position(localSubstitution));
		}		
		return positions;
	}

	@Override
	public Term replaceOrSubstitute(Position position, Term term) {
		if (position.isFinal()) {
			return term;
		} else {
			List<Term> newParameters = new ArrayList<>();
			for (int i = 0; i < parameters.size(); i++) {
				if (i == position.getFirst()) {
					Term t = parameters.get(i).replaceOrSubstitute(position.pop(), term);
					if (t == null) {
						System.out.println("func1");
					}
					newParameters.add(t);
				} else {
					Term t = parameters.get(i).replace(position.getUnifier());
					if (t == null) {
						System.out.println("func1");
					}
					newParameters.add(t);
				}
			}
			return new FunctionTerm(symbol, newParameters);
		}
	}
	
	@Override
	public String toString() {
		return symbol.getName();
	}
}
