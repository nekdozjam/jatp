package com.mmazanek.atp.model.inference;

import com.mmazanek.atp.model.KnowledgeEntry;

public class AssumeNegation extends Inference {

	private static final String NAME = "assume_negation";
	private static final String STATUS = "cth";
	
	public AssumeNegation(KnowledgeEntry ancestor) {
		super(ancestor);
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
