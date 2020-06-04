package com.mmazanek.atp.model;

import java.util.Set;

import com.mmazanek.atp.model.fol.Clause;
import com.mmazanek.atp.model.fol.Variable;
import com.mmazanek.atp.model.inference.Inference;

/**
 * KnowledgeEntry wrapper for a Clause
 * 
 * @author Martin Mazanek
 */
public class ClauseEntry implements KnowledgeEntry {
	private String name;
	private Type type;
	private Clause clause;
	private Inference ancestors;
	private Set<Variable> variables;
	// unused
	private boolean active = true;
	
	public ClauseEntry(String name, Type type, Clause clause, Set<Variable> variables, Inference ancestors) {
		this.name = name;
		this.type = type;
		this.clause = clause;
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
			variables = clause.collectVariables();
		}
		return variables;
	}
	
	/**
	 * Get the wrapped clause
	 * @return wrapped clause
	 */
	public Clause getClause() {
		return clause;
	}
	
	@Override
	public boolean isActive() {
		return active;
	}
}
