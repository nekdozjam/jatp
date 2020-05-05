package com.mmazanek.atp.parser;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mmazanek.atp.model.ClauseEntry;
import com.mmazanek.atp.model.FormulaEntry;
import com.mmazanek.atp.model.KnowledgeBase;
import com.mmazanek.atp.model.KnowledgeEntry.Type;
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

import com.mmazanek.atp.model.fol.QuantifierFormula.Quantifier;

public class TptpParser implements LogicParser {
	
	private File problemFile;
	private String includeDir;
	
	public TptpParser(File problemFile, String includeDir) {
		this.includeDir = includeDir;
		this.problemFile = problemFile;
	}

	private void debug(String debug) {
		//System.out.println(debug);
	}
	
	@Override
	public KnowledgeBase load() {
		debug("KB.load() start");
		try {
			KnowledgeBase.Builder kb = KnowledgeBase.builder();
			return doLoad(kb, problemFile).build();
		} catch (IOException e) {
			System.out.println("SZS Error: asdf");
			e.printStackTrace();
			return null;
		}
	}
	
	private Type getType(String type) {
		if ("conjecture".equalsIgnoreCase(type)) {
			return Type.CONJECTURE;
		} else if ("negated_conjecture".equalsIgnoreCase(type)) {
			return Type.NEGATED_CONJECTURE;
		} else if ("plain".equalsIgnoreCase(type)) {
			return Type.PLAIN;
		} else {
			return Type.AXIOM;
		}
	}
	
	private KnowledgeBase.Builder doLoad(KnowledgeBase.Builder kb, File file) throws IOException {
		debug("KB.doLoad() start");
		TptpStreamTokenizer reader = new TptpStreamTokenizer(file);
		String token = reader.next();
		//Includes first
		while (token.equals("include")) {
			debug("include");
			reader.skipOne("(");
			String incl = reader.next();
			debug(incl);
			reader.skipOne(")");
			reader.skipOne(".");
			token = reader.next();
			doLoad(kb, new File(includeDir, incl));	
		}
		
		//Formulas
		String formulaType = token;
		while ("fof".equals(formulaType) | "cnf".equals(formulaType)) {
			reader.skipOne("(");
			String name = reader.next();
			reader.skipOne(",");
			String type = reader.next();
			reader.skipOne(",");
			//TODO: change so there doesnt have to be a ()
			reader.skipOne("(");
			if (formulaType.equals("fof")) {
				debug("\nfof - " + type + " - " + name);
				Set<Variable> variables = new LinkedHashSet<>();
				Formula formula = loadFof(reader, kb, variables);
				FormulaEntry entry = new FormulaEntry(name, getType(type), formula, variables);
				kb.addFormulaEntry(entry);
			} else if (formulaType.equals("cnf")) {
				debug("\ncnf - " + type + " - " + name);

				Set<Variable> variables = new LinkedHashSet<Variable>();
				Clause clause = loadCnf(reader, kb, variables);
				kb.addClauseEntry(new ClauseEntry(name, getType(type), clause, variables));
			} else {
				System.out.println("Wrong input format - expected cnf or fof");
				return null;
				//TODO: SZS Error wrong format
			}

			reader.skipOne(")");
			reader.skipOne(")");
			reader.skipOne(".");
			formulaType = reader.next();
		}
		
		debug("ttype: " + reader.ttype);
		
		return kb;
	}
	
	//note: CNF has all variables implicitly universally quantified
	//		FOF has to have all variables quantified
	//    => there are no free variables in TPTP
	private Clause loadCnf(TptpStreamTokenizer reader, KnowledgeBase.Builder kb, Set<Variable> variables) throws IOException {//TODO: equals
		debug("loadCnf - start");
		List<Literal> literals = new LinkedList<Literal>();
		do {
			Literal l = loadLiteral(reader, kb, variables);
			literals.add(l);
		} while (reader.trySkipOne("|"));
		debug("loadCnf - end");
		return new Clause(literals);
	}
	
	private Formula loadFof(TptpStreamTokenizer reader, KnowledgeBase.Builder kb, Set<Variable> variables) throws IOException {
		debug("loadFof - start");
		
		Formula first = parseFormula(reader, kb, variables, new HashMap<String, Variable>());
		
		debug("loadFof - end");
		return first;
	}
	
	private Formula parseOne(TptpStreamTokenizer reader, KnowledgeBase.Builder kb, Set<Variable> variables, Map<String, Variable> scopedVariables) throws IOException {
		debug("parseOne");
		String token = reader.next();
		if ("~".equals(token)) {
			Formula[] formulas = new Formula[1];
			formulas[0] = parseOne(reader, kb, variables, scopedVariables);
			return new LogicalFormula(Connective.NOT, formulas);
		} else if ("!".equals(token) || "?".equals(token)) {
			reader.skipOne("[");
			
			List<Variable> fvariables = new LinkedList<Variable>();
			do {
				String name = reader.next();
				if (!Character.isUpperCase(name.charAt(0))) {
					//TODO: error
					System.err.println(name + " not a variable");
				}
				if (scopedVariables.containsKey(name)) {
					System.err.println(name + " already scoped");
					//TODO: SZS Error
				}
				Variable v = new Variable(name, kb.generateVarId());
				fvariables.add(v);
				variables.add(v);
				scopedVariables.put(name, v);
			} while (reader.trySkipOne(","));
			
			reader.skipOne("]");
			reader.skipOne(":");
			
			Formula f = parseOne(reader, kb, variables, scopedVariables);
			
			for (Variable v : fvariables) {
				scopedVariables.remove(v.getName());
			}
			
			return new QuantifierFormula(token.equals("?") ? Quantifier.EXISTS : Quantifier.FORALL, fvariables, f);
		} else if (Character.isLowerCase(token.charAt(0))) {
			// predicate & | => <=>
			// function symbol = ...
			//
			String name = token;
			List<Term> terms1 = new LinkedList<Term>();
			if (reader.trySkipOne("(")) {
				do {
					Term term = loadTerm(reader, kb, variables, scopedVariables);
					terms1.add(term);
				} while (reader.trySkipOne(","));
				reader.skipOne(")");
			}
			
			if (reader.trySkipOne("=")) { // This means previous symbol is function
				//function
				FunctionSymbol fs1 = kb.addFunctionSymbol(name, terms1.size());
				
				List<Term> terms2 = new LinkedList<>();
				terms2.add(new FunctionTerm(fs1, terms1));
				terms2.add(loadTerm(reader, kb, variables, scopedVariables));
				
				return new Literal(PredicateSymbol.EQUALS, false, terms2);
			} else if (reader.trySkipOne("!=")) { // This means previous symbol is function
				//function
				FunctionSymbol fs1 = kb.addFunctionSymbol(name, terms1.size());
				
				List<Term> terms2 = new LinkedList<>();
				terms2.add(new FunctionTerm(fs1, terms1));
				terms2.add(loadTerm(reader, kb, variables, scopedVariables));
				
				return new Literal(PredicateSymbol.EQUALS, true, terms2);
			} else {
				PredicateSymbol ps = kb.addPredicateSymbol(name, terms1.size());
				
				return new Literal(ps, false, terms1);
			}
		} else if (Character.isUpperCase(token.charAt(0))) {
			// Var - expecting = or !=
			debug("var " + token + " =");
			List<Term> terms = new LinkedList<Term>();
			
			Variable variable = scopedVariables.get(token);
			if (variable == null) {
				//TODO: SZS Variable not quantified
				System.err.println("Variable not quantified");
			}
			terms.add(variable);
			
			if (reader.trySkipOne("=")) {
				terms.add(loadTerm(reader, kb, variables, scopedVariables));
				
				return new Literal(PredicateSymbol.EQUALS, false, terms);
			} else {
				reader.skipOne("!=");
				terms.add(loadTerm(reader, kb, variables, scopedVariables));
				
				return new Literal(PredicateSymbol.EQUALS, true, terms);
			}
		} else if ("(".equals(token)) {
			Formula f = parseFormula(reader, kb, variables, scopedVariables);
			reader.skipOne(")");
			return f;
		} else {
			throw new RuntimeException("Wrong format!");
		}
	}
	
	private Formula parseFormula(TptpStreamTokenizer reader, KnowledgeBase.Builder kb, Set<Variable> variables, Map<String, Variable> scopedVariables) throws IOException {
		debug("parseFormula");
		
		Formula first = parseOne(reader, kb, variables, scopedVariables);
		Formula[] formulas = new Formula[2];
		formulas[0] = first;
		Formula res = null;
		//TODO: more connectives, precedence
		if (reader.trySkipOne("&")) {
			formulas[1] = parseFormula(reader, kb, variables, scopedVariables);
			res = new LogicalFormula(Connective.AND, formulas);
		} else if (reader.trySkipOne("|")) {
			formulas[1] = parseFormula(reader, kb, variables, scopedVariables);
			res = new LogicalFormula(Connective.OR, formulas);
		} else if (reader.trySkipOne("=>")) {
			formulas[1] = parseFormula(reader, kb, variables, scopedVariables);
			res = new LogicalFormula(Connective.IMPLIES, formulas);
		} else {
			res = first;
		}
		
		return res;
	}
	
	/* FOF terms
	<fof_term>             ::= <fof_function_term> | <variable>
	<fof_function_term>    ::= <constant> | <functor>(<fof_arguments>)
	<fof_arguments>        ::= <fof_term> | <fof_term>,<fof_arguments>
	 */
	@SuppressWarnings("unlikely-arg-type")
	private Term loadTerm(TptpStreamTokenizer reader, KnowledgeBase.Builder kb, Set<Variable> variables, Map<String, Variable> scopedVariables) throws IOException {
		String name = reader.next();
		debug("loadTerm - " + name);
		//variable
		if (Character.isUpperCase(name.codePointAt(0))) {
			Variable variable = null;
			if (scopedVariables != null) { // fof - need to take care of scope
				variable = scopedVariables.get(name);
				if (variable == null) {
					debug("Variable " + name + " not under any scope!");
					//TODO: SZS Error
				}
			} else { // cnf - everything with implicitely universaly quantified
				for (Variable v : variables) {
					if (v.equals(name)) {
						variable = v;
						break;
					}
				}
				if (variable == null) {
					variable = new Variable(name, kb.generateVarId());
					variables.add(variable);
				}
			}
			
			return variable;
		} else {
			if (reader.trySkipOne("(")) {
				// n-ary function
				List<Term> parameters = new LinkedList<Term>();
				do {
					parameters.add(loadTerm(reader, kb, variables, scopedVariables));
				} while(reader.trySkipOne(","));
				reader.skipOne(")");

				debug("   adding "+parameters.size()+"-ary symbol " + name);
				FunctionSymbol symbol = kb.addFunctionSymbol(name, parameters.size());
				return new FunctionTerm(symbol, parameters);
			} else {
				// 0-ary function - constant
				debug("   adding 0-ary symbol " + name);
				FunctionSymbol symbol = kb.addFunctionSymbol(name, 0);
				return new FunctionTerm(symbol, Collections.emptyList());
			}
		}
	}
	
	private Literal loadLiteral(TptpStreamTokenizer reader, KnowledgeBase.Builder kb, Set<Variable> variables) throws IOException {
		boolean negative = false;
		if (reader.trySkipOne("~")) {
			negative = true;
		}
		
		String predicateSymbol = reader.next();
		debug("loadLiteral - " + (negative ? "~" : "") + predicateSymbol);
		if (reader.trySkipOne("(")) {
			List<Term> terms = new LinkedList<Term>();
			do {
				Term t = loadTerm(reader, kb, variables, null);
				terms.add(t);
			} while (reader.trySkipOne(","));
			reader.skipOne(")");
			PredicateSymbol symbol = kb.addPredicateSymbol(predicateSymbol, terms.size());
			return new Literal(symbol, negative, terms);
		} else {
			PredicateSymbol symbol = kb.addPredicateSymbol(predicateSymbol, 0);
			return new Literal(symbol, negative, Collections.emptyList());
		}
	}
}
