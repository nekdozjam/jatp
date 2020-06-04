package com.mmazanek.atp.model.inference;

import com.mmazanek.atp.model.KnowledgeEntry;

/**
 * Factoring inference step
 * 
 * @author Martin Mazanek
 */
public class Factoring extends Inference {

	private static final String NAME = "factor";
	private static final String STATUS = "thm";
	
	public Factoring(KnowledgeEntry... ancestors) {
		super(ancestors);
		if (ancestors == null || ancestors.length != 1) {
			throw new IllegalArgumentException("Factoring requires 1 ancestor.");
		}
	}
	
	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getStatus() {
		return STATUS;
	}
	
}
