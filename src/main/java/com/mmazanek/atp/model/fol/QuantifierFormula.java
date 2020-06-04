package com.mmazanek.atp.model.fol;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Representation of a formula with quantifier
 * 
 * @author Martin Mazanek
 */
public class QuantifierFormula implements Formula {
	public static enum Quantifier {
		FORALL,
		EXISTS
	}
	
	private Quantifier quantifier;
	private List<Variable> variables;
	private Formula formula;
	
	public QuantifierFormula(Quantifier quantifier, List<Variable> variables, Formula formula) {
		this.quantifier = quantifier;
		this.variables = variables;
		this.formula = formula;
	}
	
	public Formula getInnerFormula() {
		return formula;
	}
	
	public Quantifier getQuantifier() {
		return quantifier;
	}
	
	public List<Variable> getVariables() {
		return variables;
	}
	
	@Override
	public Formula replaceConnectives() {
		return new QuantifierFormula(quantifier, variables, formula.replaceConnectives());
	}
	
	@Override
	public Formula replace(Map<Variable, Term> replaceMap) {
		return new QuantifierFormula(quantifier, variables, formula.replace(replaceMap));//TODO:
	}

	@Override
	public Formula pushNegations(boolean negate) {
		if (!negate) {
			return new QuantifierFormula(quantifier, variables, formula.pushNegations(false));
		} else {
			Quantifier q = quantifier == Quantifier.FORALL ? Quantifier.EXISTS : Quantifier.FORALL;
			return new QuantifierFormula(q, variables, formula.pushNegations(true));
		}
	}

	@Override
	public Formula skolemize(List<Variable> scopedVariables, List<FunctionTerm> generatedSkolelmFuncs) {
		Formula res;
		if (quantifier == Quantifier.FORALL) {
			scopedVariables.addAll(variables);
			res = formula.skolemize(scopedVariables, generatedSkolelmFuncs);
			scopedVariables.removeAll(variables);
		} else {
			Map<Variable, Term> replaceMap = new HashMap<>();
			for (Variable v : variables) {
				FunctionTerm skolemTerm = new FunctionTerm(null, scopedVariables);
				replaceMap.put(v, skolemTerm);
				System.out.println("replacing " + v.getName());
				generatedSkolelmFuncs.add(skolemTerm);
			}
			res = formula.replace(replaceMap).skolemize(scopedVariables, generatedSkolelmFuncs);
		}
		
		return res;
	}

	@Override
	public List<Clause> flatten() {
		throw new RuntimeException("Unable to flatten quantifier formula.");
	}
	
	@Override
	public Set<Variable> collectVariables() {
		return formula.collectVariables();
	}

	@Override
	public Formula rewriteVariables(Map<Variable, Variable> rewriteMap) {
		List<Variable> variables2 = new LinkedList<>();
		for (Variable v : variables) {
			variables2.add(rewriteMap.getOrDefault(v, v));
		}
		return new QuantifierFormula(quantifier, variables2, formula.rewriteVariables(rewriteMap));
	}
}
