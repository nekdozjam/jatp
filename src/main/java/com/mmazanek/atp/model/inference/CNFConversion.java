package com.mmazanek.atp.model.inference;

import com.mmazanek.atp.model.KnowledgeEntry;


/**
 * CNF conversion inference step
 * 
 * @author Martin Mazanek
 */
public class CNFConversion extends Inference {

	private static final String NAME = "cnf";
	private static final String STATUS = "thm";
	
	public CNFConversion(KnowledgeEntry ancestor) {
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
