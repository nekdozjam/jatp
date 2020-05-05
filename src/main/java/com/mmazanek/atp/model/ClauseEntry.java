package com.mmazanek.atp.model;

import java.util.List;
import java.util.Set;

import com.mmazanek.atp.model.fol.Clause;
import com.mmazanek.atp.model.fol.Variable;

public class ClauseEntry implements KnowledgeEntry {
	private String name;
	private Type type;
	private Clause clause;
	private KnowledgeEntry[] ancestors;
	private Set<Variable> variables;
	private boolean active = true;
	
	public ClauseEntry(String name, Type type, Clause clause, Set<Variable> variables, KnowledgeEntry... ancestors) {
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
	public KnowledgeEntry[] getAncestors() {
		return ancestors;
	}

	@Override
	public Type getType() {
		return type;
	}

	public Set<Variable> getVariables() {
		if (variables == null) {
			variables = clause.collectVariables();
		}
		return variables;
	}
	
	public Clause getClause() {
		return clause;
	}
	
	@Override
	public boolean isActive() {
		return active;
	}
}
