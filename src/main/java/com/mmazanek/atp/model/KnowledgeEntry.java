package com.mmazanek.atp.model;

public interface KnowledgeEntry {
	public static enum Type {
		AXIOM,
		CONJECTURE,
		NEGATED_CONJECTURE,
		PLAIN,
		UNKNOWN
	}
	
	public String getName();
	public KnowledgeEntry[] getAncestors();
	public Type getType();
	public boolean isActive();
}
