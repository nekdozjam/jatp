package com.mmazanek.atp.parser;

import com.mmazanek.atp.model.KnowledgeBase;

public interface LogicParser {
	/**
	 * Creates a knowledge base from specified file.
	 * 
	 * @return populated knowledge base
	 */
	public KnowledgeBase load();
}
