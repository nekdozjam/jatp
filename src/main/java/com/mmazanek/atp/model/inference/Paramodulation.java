package com.mmazanek.atp.model.inference;

import com.mmazanek.atp.model.KnowledgeEntry;

/**
 * Paramodulation inference step
 * 
 * @author Martin Mazanek
 */
public class Paramodulation extends Inference {
	
	private static final String NAME = "pm";
	private static final String STATUS = "thm";
	
	public Paramodulation(KnowledgeEntry from, KnowledgeEntry to) {
		super(from, to);
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
