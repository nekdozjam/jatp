package com.mmazanek.atp.model.inference;

import com.mmazanek.atp.model.KnowledgeEntry;

/**
 * Binary resolution inference step
 * 
 * @author Martin Mazanek
 */
public class BinaryResolution extends Inference {

	private static final String NAME = "resolution";
	private static final String STATUS = "thm";
	
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

	@Override
	public String getStatus() {
		return STATUS;
	}
}
