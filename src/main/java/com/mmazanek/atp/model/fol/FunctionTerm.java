package com.mmazanek.atp.model.fol;

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
			parameters2.add(t.rewriteVariables(rewriteMap));
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
}
