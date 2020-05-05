package com.mmazanek.atp.model;

import java.util.List;
import java.util.Set;

import com.mmazanek.atp.model.fol.Formula;
import com.mmazanek.atp.model.fol.Variable;

public class FormulaEntry implements KnowledgeEntry {
	
	private String name;
	private Type type;
	private Formula formula;
	private KnowledgeEntry[] ancestors;
	private Set<Variable> variables;
	private boolean active = true;
	
	public FormulaEntry(String name, Type type, Formula formula, Set<Variable> variables, KnowledgeEntry... ancestors) {
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
	public KnowledgeEntry[] getAncestors() {
		return ancestors;
	}

	@Override
	public Type getType() {
		return type;
	}
	
	public Set<Variable> getVariables() {
		if (variables == null) {
			variables = formula.collectVariables();
		}
		return variables;
	}

	public Formula getFormula() {
		return formula;
	}

	@Override
	public boolean isActive() {
		return active;
	}
	
}
