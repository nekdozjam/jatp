package com.mmazanek.atp.model.inference;

import com.mmazanek.atp.model.KnowledgeEntry;

/**
 * Interface for a single inference step
 * 
 * @author Martin Mazanek
 */
public abstract class Inference {
	private KnowledgeEntry[] ancestors;
	
	public Inference(KnowledgeEntry... ancestors) {
		this.ancestors = ancestors;
	}
	
	/**
	 * Get an array of entries on which the inference was made
	 * @return
	 */
	public KnowledgeEntry[] getAncestors() {
		return ancestors;
	}
	
	/**
	 * Get name of the inference rule
	 * @return name
	 */
	public abstract String getName();
	
	/**
	 * Get status of the inference rule
	 * 
	 * 
	 * 
	 * 
	 * @return name
	 */
	public abstract String getStatus();
}
