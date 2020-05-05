package com.mmazanek.atp.model.inference;

import com.mmazanek.atp.model.KnowledgeEntry;

public class Skolemization extends Inference {

	private static final String NAME = "skolemize";
	
	public Skolemization(KnowledgeEntry ancestor) {
		super(ancestor);
	}
	
	@Override
	public String getName() {
		return NAME;
	}
	

}
