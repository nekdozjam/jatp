package com.mmazanek.atp.model.inference;

import com.mmazanek.atp.model.KnowledgeEntry;

public class BinaryResolution extends Inference {

	private static final String NAME = "resolution";
	
	public BinaryResolution(KnowledgeEntry... ancestors) {
		super(ancestors);
		if (ancestors == null || ancestors.length != 2) {
			throw new IllegalArgumentException("BinaryResolution requires 2 ancestors.");
		}
	}
	
	@Override
	public String getName() {
		return NAME;
	}
}
