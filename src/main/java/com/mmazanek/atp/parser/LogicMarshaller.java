package com.mmazanek.atp.parser;

import com.mmazanek.atp.model.ClauseEntry;
import com.mmazanek.atp.model.FormulaEntry;
import com.mmazanek.atp.model.fol.Clause;
import com.mmazanek.atp.model.fol.Formula;

public interface LogicMarshaller {
	public void marshallFormula(FormulaEntry formula);
	public void marshallClause(ClauseEntry clause);
}
