package com.mmazanek.atp.model.inference;

import com.mmazanek.atp.model.KnowledgeEntry;

public abstract class Inference {
	private KnowledgeEntry[] ancestors;
	
	public Inference(KnowledgeEntry... ancestors) {
		this.ancestors = ancestors;
	}
	
	public KnowledgeEntry[] getAncestors() {
		return ancestors;
	}
	
	public abstract String getName();
}
