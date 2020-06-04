package com.mmazanek.atp.parser;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import com.mmazanek.atp.model.ClauseEntry;
import com.mmazanek.atp.model.FormulaEntry;
import com.mmazanek.atp.model.KnowledgeEntry;
import com.mmazanek.atp.model.fol.Clause;
import com.mmazanek.atp.model.fol.Formula;
import com.mmazanek.atp.model.fol.FunctionSymbol;
import com.mmazanek.atp.model.fol.FunctionTerm;
import com.mmazanek.atp.model.fol.Literal;
import com.mmazanek.atp.model.fol.LogicalFormula;
import com.mmazanek.atp.model.fol.LogicalFormula.Connective;
import com.mmazanek.atp.model.fol.PredicateSymbol;
import com.mmazanek.atp.model.fol.QuantifierFormula;
import com.mmazanek.atp.model.fol.Term;
import com.mmazanek.atp.model.fol.Variable;
import com.mmazanek.atp.model.inference.FileLoad;
import com.mmazanek.atp.model.inference.Inference;
import com.mmazanek.atp.model.fol.QuantifierFormula.Quantifier;

/**
 * TPTP format marshaller
 * 
 * @author Martin Mazanek
 */
public class TptpMarshaller implements LogicMarshaller {

	private PrintStream writer;
	
	public TptpMarshaller(PrintStream writer) {
		this.writer = writer;
	}
	
	public void doMarshallFormula(Formula formula) throws IOException {
		if (formula instanceof Literal) {
			Literal af = (Literal) formula;
			PredicateSymbol s = af.getPredicate();
			if (s.equals(PredicateSymbol.EQUALS)) {
				writer.print("(");
				marshallTerm(af.getTerms().get(0));
				if (af.isNegated()) {
					writer.print(" != ");
				} else {
					writer.print(" = ");
				}
				marshallTerm(af.getTerms().get(1));
				writer.print(")");
			} else {
				if (af.isNegated()) {
					writer.print("~");
				}
				writer.print(s.getName());
				if (s.getArity() > 0) {
					writer.print("(");
					for (int i = 0; i < s.getArity(); i++) {
						if (i != 0) {
							writer.print(", ");
						}
						marshallTerm(af.getTerms().get(i));
					}
					writer.print(")");
				}
			}
		} else if (formula instanceof LogicalFormula) {
			LogicalFormula lf = (LogicalFormula) formula;
			LogicalFormula.Connective con = lf.getConnective();
			if (con == Connective.NOT) {
				writer.print("~");
				doMarshallFormula(lf.getFormulas()[0]);
			} else {
				writer.print("(");
				doMarshallFormula(lf.getFormulas()[0]);
				if (con == Connective.AND) {
					writer.print(" & ");
				} else if (con == Connective.OR) {
					writer.print(" | ");
				} else if (con == Connective.IMPLIES) {
					writer.print(" => ");
				} else if (con == Connective.EQUIVALENT) {
					writer.print(" <=> ");
				} else {
					System.err.println("Unknown Connective");
				}
				doMarshallFormula(lf.getFormulas()[1]);
				writer.print(")");
			}
			
		} else if (formula instanceof QuantifierFormula) {
			QuantifierFormula qf = (QuantifierFormula) formula;
			if (qf.getQuantifier() == Quantifier.FORALL) {
				writer.print("!");
			} else {
				writer.print("?");
			}
			writer.print(" [");
			List<Variable> variables = qf.getVariables();
			for (int i = 0; i < variables.size(); i++) {
				if (i != 0) {
					writer.print(",");
				}
				writer.print(variables.get(i).getName());
			}
			writer.print("] : (");
			doMarshallFormula(qf.getInnerFormula());
			writer.print(")");
		} else {
			//TODO: Error
		}
	}
	
	public void marshallTerm(Term term) throws IOException {
		if (term instanceof Variable) {
			writer.print(((Variable)term).getName());
		} else if (term instanceof FunctionTerm) {
			//TODO: equals
			FunctionTerm fnc = (FunctionTerm) term;
			FunctionSymbol s = fnc.getSymbol();
			writer.print(s.getName());
			if (s.getArity() > 0) {
				writer.print("(");
				for (int i = 0; i < s.getArity(); i++) {
					if (i != 0) {
						writer.print(", ");
					}
					marshallTerm(fnc.getParameters().get(i));
				}
				writer.print(")");	
			}
		} else {
			//TODO: SZS Error
		}
	}
	
	private void doMarshallClause(Clause clause) throws IOException {
		if (clause.isEmpty()) {
			writer.print("($false)");
		} else {
			List<Literal> literals = clause.getLiterals();
			for (int i = 0; i < literals.size(); i++) {
				if (i != 0) {
					writer.print(" | ");
				}
				doMarshallFormula(literals.get(i));
			}
		}
	}

	
	private void marshallInference(KnowledgeEntry entry) {
		Inference inference = entry.getAncestors();
		if (inference instanceof FileLoad) {
			writer.print("file('");
			writer.print(((FileLoad)inference).getFilename());
			writer.print("', ");
			writer.print(entry.getName());
			writer.print(")");
		} else {
			writer.print("inference(");
			writer.print(inference.getName());
			writer.print(",[status(");
			writer.print(inference.getStatus());
			writer.print(")], [");
			for (int i = 0; i < inference.getAncestors().length; i++) {
				if (i != 0) {
					writer.print(",");
				}
				writer.print(inference.getAncestors()[i].getName());
			}
			writer.print("])");
		}
	}

	@Override
	public void marshallFormula(FormulaEntry formula) {
		try {
			writer.print("fof(");
			writer.print(formula.getName());
			writer.print(", ");
			writer.print(formula.getType().toString().toLowerCase());
			writer.print(", ");
			doMarshallFormula(formula.getFormula());
			if (formula.getAncestors() != null) {
				writer.print(", ");
				marshallInference(formula);
			}
			writer.print(").\n");
		} catch (Exception e) {
			//TODO: SZS ERROR
		}
	}
	
	@Override
	public void marshallClause(ClauseEntry clause) {
		try {
			writer.print("cnf(");
			writer.print(clause.getName());
			writer.print(", ");
			writer.print(clause.getType().toString().toLowerCase());
			writer.print(", ");
			doMarshallClause(clause.getClause());
			if (clause.getAncestors() != null) {
				writer.print(", ");
				marshallInference(clause);
			}
			writer.print(").\n");
		} catch (Exception e) {
			System.err.println("marshallClause");
			e.printStackTrace();
			//TODO: SZS ERROR
		}
	}

	public void marshallKnowledgeEntryWithAncestors(KnowledgeEntry entry) {
		Inference inference = entry.getAncestors();
		if (inference != null) {
			for (KnowledgeEntry e : inference.getAncestors()) {
				marshallKnowledgeEntryWithAncestors(e);
			}
		}
		if (entry instanceof FormulaEntry) {
			marshallFormula((FormulaEntry) entry);
		} else {
			marshallClause((ClauseEntry) entry);
		}
	}
}
