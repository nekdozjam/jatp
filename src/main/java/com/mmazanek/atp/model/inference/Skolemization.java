package com.mmazanek.atp.model.inference;

import com.mmazanek.atp.model.KnowledgeEntry;

public class Skolemization extends Inference {

	private static final String NAME = "skolemize";
	private static final String STATUS = "esa";
	
	public Skolemization(KnowledgeEntry ancestor) {
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
