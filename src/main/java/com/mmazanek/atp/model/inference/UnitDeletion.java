package com.mmazanek.atp.model.inference;

import com.mmazanek.atp.model.KnowledgeEntry;

public class UnitDeletion extends Inference {

	private static final String NAME = "unit_deletion";
	private static final String STATUS = "thm";
	
	public UnitDeletion(KnowledgeEntry delegate, KnowledgeEntry unit) {
		super(delegate, unit);
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
