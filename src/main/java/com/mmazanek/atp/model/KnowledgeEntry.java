package com.mmazanek.atp.model;

import com.mmazanek.atp.model.inference.Inference;

public interface KnowledgeEntry {
	public static enum Type {
		AXIOM,
		CONJECTURE,
		NEGATED_CONJECTURE,
		PLAIN,
		UNKNOWN
	}
	
	public String getName();
	public Inference getAncestors();
	public Type getType();
	public boolean isActive();
}
