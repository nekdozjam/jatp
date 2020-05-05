package com.mmazanek.atp.model.inference;

import com.mmazanek.atp.model.KnowledgeEntry;

public class Factoring extends Inference {

	private static final String NAME = "factor";
	
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
	
}
