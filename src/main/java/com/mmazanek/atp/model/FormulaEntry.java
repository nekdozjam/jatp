package com.mmazanek.atp.model;

import java.util.Set;

import com.mmazanek.atp.model.fol.Formula;
import com.mmazanek.atp.model.fol.Variable;
import com.mmazanek.atp.model.inference.Inference;

/**
 * KnowledgeEntry wrapper for a Formula
 * 
 * @author Martin Mazanek
 */
public class FormulaEntry implements KnowledgeEntry {
	
	private String name;
	private Type type;
	private Formula formula;
	private Inference ancestors;
	private Set<Variable> variables;
	private boolean active = true;
	
	public FormulaEntry(String name, Type type, Formula formula, Set<Variable> variables, Inference ancestors) {
		this.name = name;
		this.type = type;
		this.formula = formula;
		this.variables = variables;
		this.ancestors = ancestors;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public Inference getAncestors() {
		return ancestors;
	}

	@Override
	public Type getType() {
		return type;
	}
	
	/**
	 * Delegate function for collecting variables of wrapped Formula
	 * @return set of variables of wrapped formula
	 */
	public Set<Variable> getVariables() {
		if (variables == null) {
			variables = formula.collectVariables();
		}
		return variables;
	}

	/**
	 * Get the wrapped formula
	 * @return wrapped formula
	 */
	public Formula getFormula() {
		return formula;
	}

	@Override
	public boolean isActive() {
		return active;
	}
	
}
