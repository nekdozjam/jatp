package com.mmazanek.atp.model;

import com.mmazanek.atp.model.inference.Inference;

/**
 * Wrapper for logical formulas for storage of metadata
 * 
 * @author Martin Mazanek
 */
public interface KnowledgeEntry {
	public static enum Type {
		AXIOM,
		CONJECTURE,
		NEGATED_CONJECTURE,
		PLAIN,
		UNKNOWN
	}
	
	/**
	 * Get name of this entry
	 * @return name
	 */
	public String getName();
	
	/**
	 * Get the Inference instance describing origin of wrapped formula
	 * @return inference
	 */
	public Inference getAncestors();
	
	/**
	 * Get the formula type of the wrapped formula
	 * @return type
	 */
	public Type getType();
	
	// unused -- foundation for marking entries for back deletion
	public boolean isActive();
}
