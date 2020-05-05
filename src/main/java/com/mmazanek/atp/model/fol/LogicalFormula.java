package com.mmazanek.atp.model.fol;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

//connective
//1 or 2 formulas
public class LogicalFormula implements Formula {
	
	public static enum Connective {
		NOT(1, "~"),
		OR(2, "|"),
		AND(2, "&"),
		IMPLIES(2, "->"),
		EQUIVALENT(2, "<=>");
		
		protected int arity;
		protected String symbol;
		Connective(int arity, String symbol) {
			this.arity = arity;
			this.symbol = symbol;
		}
	}
	
	private Connective connective;
	private Formula[] formulas;
	private Set<Variable> variables = null;
	
	public LogicalFormula(Connective connective, Formula[] formulas) {
		if (connective.arity != formulas.length) {
			throw new IllegalArgumentException("Connective arity " + connective.arity + " does not equal number of formulas!");
		}
		this.formulas = formulas;
		this.connective = connective;
	}
	
	public Connective getConnective() {
		return connective;
	}
	
	public Formula[] getFormulas() {
		return formulas;
	}
	
	@Override
	public Formula replace(Map<Variable, Term> replaceMap) {
		Formula[] formulas2 = new Formula[formulas.length];
		for (int i = 0; i < formulas.length; i++) {
			formulas2[i] = formulas[i].replace(replaceMap);
		}
		return new LogicalFormula(connective, formulas2);
	}
	
	@Override
	public Formula replaceConnectives() {
		switch (connective) {
			case IMPLIES: {
				Formula[] formulas2 = new Formula[2];
				formulas2[0] = new LogicalFormula(Connective.NOT,  new Formula[]{formulas[0].replaceConnectives()});
				formulas2[1] = formulas[1].replaceConnectives();
				return new LogicalFormula(Connective.OR, formulas2);
			}
			case EQUIVALENT: {
				Formula[] formulas2 = new Formula[2];
				Formula[] formulas3 = new Formula[2];
				formulas3[0] = formulas[1];
				formulas3[1] = formulas[0];
				formulas2[0] = new LogicalFormula(Connective.IMPLIES, formulas).replaceConnectives();
				formulas2[1] = new LogicalFormula(Connective.IMPLIES, formulas3).replaceConnectives();
				return new LogicalFormula(Connective.AND, formulas2);
			}
			default: {
				Formula[] formulas2 = new Formula[formulas.length];
				for (int i = 0; i < formulas.length; i++) {
					formulas2[i] = formulas[i].replaceConnectives();
				}
				return new LogicalFormula(connective, formulas2);
			}
		}
	}

	@Override
	public Formula pushNegations(boolean negate) {
		if (connective == Connective.NOT) {
			return formulas[0].pushNegations(!negate);
		} else if (connective == Connective.AND) {
			if (negate) {
				return new LogicalFormula(Connective.OR, new Formula[] {formulas[0].pushNegations(true), formulas[1].pushNegations(true)});
			} else {
				return new LogicalFormula(Connective.AND, new Formula[] {formulas[0].pushNegations(false), formulas[1].pushNegations(false)});
			}
		} else if (connective == Connective.OR) {
			if (negate) {
				return new LogicalFormula(Connective.AND, new Formula[] {formulas[0].pushNegations(true), formulas[1].pushNegations(true)});
			} else {
				return new LogicalFormula(Connective.OR, new Formula[] {formulas[0].pushNegations(false), formulas[1].pushNegations(false)});
			}
		} else if (connective == Connective.IMPLIES) {
			//TODO:
		} else if (connective == Connective.EQUIVALENT) {
			
		}
		return null;
	}
	
	@Override
	public Formula skolemize(List<Variable> scopedVariables, List<FunctionTerm> generatedSkolelmFuncs) {
		Formula[] formulas2 = new Formula[formulas.length];
		for (int i = 0; i < formulas2.length; i++) {
			formulas2[i] = formulas[i].skolemize(scopedVariables, generatedSkolelmFuncs);
		}
		return new LogicalFormula(connective, formulas2);
	}

	@Override
	public List<Clause> flatten() {
		if (connective == Connective.AND) {
			List<Clause> f1 = formulas[0].flatten();
			List<Clause> f2 = formulas[1].flatten();
			List<Clause> res = new LinkedList<>();
			res.addAll(f1);
			res.addAll(f2);
			return res;
		} else if (connective == Connective.OR) {
			List<Clause> f1 = formulas[0].flatten();
			List<Clause> f2 = formulas[1].flatten();
			List<Clause> res = new LinkedList<>();
			
			for (Clause c1 : f1) {
				for (Clause c2 : f2) {
					res.add(c1.join(c2));
				}
			}
			
			return res;
		} else {
			throw new RuntimeException("Flattening supported only for NNF formulas!");
		}
	}

	@Override
	public Set<Variable> collectVariables() {
		if (variables == null) {
			variables = new HashSet<>();
			for (Formula f : formulas) {
				variables.addAll(f.collectVariables());
			}
		}
		return variables;
	}

	@Override
	public Formula rewriteVariables(Map<Variable, Variable> rewriteMap) {
		Formula[] formulas2 = new Formula[formulas.length];
		for (int i = 0; i < formulas.length; i++) {
			formulas2[i] = formulas[i].rewriteVariables(rewriteMap);
		}
		LogicalFormula f = new LogicalFormula(connective, formulas2);
		if (variables != null) {
			Set<Variable> variables2 = new HashSet<>();
			for (Variable v : variables) {
				variables2.add(rewriteMap.getOrDefault(v, v));
			}
			f.variables = variables2;
		}
		return f;
	}
}
