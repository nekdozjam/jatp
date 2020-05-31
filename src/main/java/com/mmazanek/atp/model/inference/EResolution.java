package com.mmazanek.atp.model.inference;

import com.mmazanek.atp.model.KnowledgeEntry;

public class EResolution extends Inference {

	private static final String NAME = "eresolution";
	private static final String STATUS = "thm";
	
	public EResolution(KnowledgeEntry e) {
		super(e);
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
