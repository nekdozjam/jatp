package com.mmazanek.atp.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mmazanek.atp.ProgramProperties;
import com.mmazanek.atp.model.KnowledgeEntry.Type;
import com.mmazanek.atp.model.fol.Clause;
import com.mmazanek.atp.model.fol.Formula;
import com.mmazanek.atp.model.fol.FunctionSymbol;
import com.mmazanek.atp.model.fol.FunctionTerm;
import com.mmazanek.atp.model.fol.Literal;
import com.mmazanek.atp.model.fol.LogicalFormula;
import com.mmazanek.atp.model.fol.LogicalFormula.Connective;
import com.mmazanek.atp.model.fol.PredicateSymbol;
import com.mmazanek.atp.model.fol.RewriteRule;
import com.mmazanek.atp.model.fol.Substitution;
import com.mmazanek.atp.model.fol.Symbol;
import com.mmazanek.atp.model.fol.Term;
import com.mmazanek.atp.model.fol.Variable;
import com.mmazanek.atp.model.inference.AssumeNegation;
import com.mmazanek.atp.model.inference.BinaryResolution;
import com.mmazanek.atp.model.inference.CNFConversion;
import com.mmazanek.atp.model.inference.EResolution;
import com.mmazanek.atp.model.inference.Factoring;
import com.mmazanek.atp.model.inference.Paramodulation;
import com.mmazanek.atp.model.inference.UnitDeletion;
import com.mmazanek.atp.parser.TptpMarshaller;

/**
 * 
 * @author Martin Mazanek
 *
 */
public class KnowledgeBase {
	private Map<String, Symbol> symbolMap = new HashMap<>();
	
	private List<FormulaEntry> formulae;
	private List<ClauseEntry> clauses;

	private List<ClauseEntry> active = new ArrayList<>();
	private List<ClauseEntry> waiting = new LinkedList<>();
	
	private List<ClauseEntry> units = new LinkedList<>();
	private List<RewriteRule> rewriteRules = new LinkedList<>();
	
	private ClauseEntry selectedConjecture = null;
	
	private static final String VARIABLE_PREFIX = "X";
	private static final String SKOLEM_PREFIX = "fsk_n";
	private static final String ENTRY_PREFIX = "f__";
	
	private int nextSymbolId;
	private int nextSkolemFnc = 1;
	private int nextEntryId = 1;
	private int nextVarId;
	
	private long startTime = 0;
	
	private TptpMarshaller marshaller = new TptpMarshaller(System.out);
	
	protected KnowledgeBase(KnowledgeBase.Builder builder) {
		formulae = builder.formulae;
		clauses = builder.clauses;
		symbolMap = builder.symbolMap;
		nextSymbolId = builder.nextSymbolId;
		nextVarId = builder.nextVarId;
	}
	
	public List<FormulaEntry> getFormulae() {
		return this.formulae;
	}
	
	public List<ClauseEntry> getClauses() {
		return this.clauses;
	}
	
	private String generateEntryName() {
		return ENTRY_PREFIX + (nextEntryId++);
	}

	private int generateVarId() {
		return nextVarId++;
	}
	
	private Formula rewriteVariables(Formula formula) {
		Set<Variable> currentVariables = formula.collectVariables();
		Map<Variable, Variable> rewriteMap = new HashMap<>();
		int varid = 1;
		for (Variable v : currentVariables) {
			Variable newVar = new Variable(VARIABLE_PREFIX + (varid++), generateVarId());
			rewriteMap.put(v, newVar);
		}
		return formula.rewriteVariables(rewriteMap);
	}
	
	/*
	 * Add a newly generated clause
	 * 
	 * Checks contradiction - emptyness
	 * Checks duplicity
	 *  - against active
	 *  - against wating
	 * Adds it to waiting list
	 * Calculates unary inferences
	 * 	- factorization
	 * 	- e-resolution
	 * Adds new unary inferences - recursion
	 */
	private void addGenerated(ClauseEntry entry) {
		
		// Check for contradiction
		if (entry.getClause().isEmpty()) {
			//Proof found
			System.out.println("# Proof found!");
			System.out.println("# SZS status Unsatisfiable");
			System.out.println("# SZS output start CNFRefutation");
			new TptpMarshaller(System.out).marshallKnowledgeEntryWithAncestors(entry);
			System.out.println("# SZS output end CNFRefutation");
			System.out.println("# Run time: " + (System.currentTimeMillis() - startTime) + "ms");
			dumpActive();
			System.exit(0);
		}
		
		
		if (ProgramProperties.maxClauseSize != 0) {
			if (entry.getClause().getLiterals().size() > ProgramProperties.maxClauseSize) {
				return;
			}
		}
		
		if (ProgramProperties.maxClauseVariables != 0) {
			int variables = entry.getClause().collectVariables().size();
			if (variables > ProgramProperties.maxClauseVariables) {
				return;
			}
		}
		
		// Duplicity checks
		// duplicity check - forward subsumption
		// duplicity check - remove deductible active clauses
		for (ClauseEntry activeEntry : active) {
			if (activeEntry.getClause().deduces(entry.getClause())) {
				//We can substitute existing clause to get the new one, so we dont need to save it
				return;
			}
		}
		// duplicity check 2 - remove deductible waiting clauses - maybe move to begining of clause inference
		for (ClauseEntry waitingEntry : waiting) {
			if (waitingEntry.getClause().deduces(entry.getClause())) {
				//We can substitute existing clause to get the new one, so we dont need to save it
				//System.out.println("saved: " + (++saved));
				return;
			}
		}
		
		// Unit deletion
		//TODO: change to iterator
		//TODO: delete only non-substituted
		for (ClauseEntry e : units) {
			Literal unitLiteral = e.getClause().getLiterals().get(0);
			List<Literal> newLiterals = new ArrayList<>(entry.getClause().getLiterals().size());
			for (Literal currentLiteral : entry.getClause().getLiterals()) {
				if (unitLiteral.deduces(currentLiteral, new HashMap<>())) {
					// dont add this clause if it contains already known unit
					if (unitLiteral.isNegated() == currentLiteral.isNegated()) {
						return;
					}
				} else {
					//TODO: stack substitution?
					newLiterals.add(currentLiteral);
				}
			}
			if (newLiterals.size() != entry.getClause().getLiterals().size()) {
				if (ProgramProperties.debug) {
					System.out.println("Deleting");
				}
				entry = new ClauseEntry(generateEntryName(), entry.getType(), new Clause(newLiterals), null, new UnitDeletion(entry, e));
			}
		}
		
		waiting.add(entry);
		
		List<ClauseEntry> factored = factor(entry);
		if (factored != null) {
			for (ClauseEntry e : factored) {
				addGenerated(e);
			}
		}

		// E resolution
		List<ClauseEntry> eresolvents = eresolution(entry);
		for (ClauseEntry e : eresolvents) {
			addGenerated(e);
		}
		
		if (entry.getClause().getLiterals().size() == 1) {
			units.add(entry);
			if (ProgramProperties.debug) {
				System.out.print("# Adding unit: ");
				marshaller.marshallClause(entry);
			}
			
			/*
			// TODO: back unit deletion, back delete noninformative clauses
			for (int i = 0; i < active.size(); i++) {
				Literal unitLiteral = entry.getClause().getLiterals().get(0);
				
				ClauseEntry backClauseEntry = active.get(i);
				ClauseEntry backDeletedEntry = null;
				
				List<Literal> newLiterals = new ArrayList<>(backClauseEntry.getClause().getLiterals().size());
				for (Literal currentLiteral : backClauseEntry.getClause().getLiterals()) {
					if (unitLiteral.isNegated() == currentLiteral.isNegated() || !unitLiteral.deduces(currentLiteral, new HashMap<>())) {
						newLiterals.add(currentLiteral);
					}
				}
				if (newLiterals.size() != entry.getClause().getLiterals().size()) {
					if (ProgramProperties.debug) {
						System.out.println("# Deleting");
					}
					backDeletedEntry = new ClauseEntry(generateEntryName(), entry.getType(), new Clause(newLiterals), null, new UnitDeletion(backClauseEntry, entry));
				}
				if (backDeletedEntry != null) {
					active.set(i, backDeletedEntry);
				}
			}
			*/
		}
	}
	
	private void dumpActive() {
		if (ProgramProperties.verbose) {
			System.out.println("# Active size: " + active.size());
			System.out.println("# Waiting size: " + waiting.size());
			System.out.println("# Units size: " + units.size());
			System.out.println("# Rewrite rules size: " + rewriteRules.size());
		}
		if (ProgramProperties.dumpActive) {
			System.out.println("# Active dump:");
			for (ClauseEntry c : active) {
				marshaller.marshallClause(c);
			}
			System.out.println("# Active dump end");
		}
		if (ProgramProperties.dumpActive) {
			System.out.println("# Units dump:");
			for (ClauseEntry c : units) {
				marshaller.marshallClause(c);
			}
			System.out.println("# Units dump end");
		}
		if (ProgramProperties.dumpActive) {
			System.out.println("# Rewrite dump:");
			for (RewriteRule r : rewriteRules) {
				System.out.print("From: ");
				try {
					marshaller.marshallTerm(r.getFrom());
					System.out.print(" To: ");
					marshaller.marshallTerm(r.getTo());
					System.out.print(" Lits: ");
					for (Literal l : r.getAddedLiterals()) {
						marshaller.doMarshallFormula(l);
						System.out.print(" ");
					}
					System.out.println(" Entry: " + r.getEntry().getName());
					System.out.println();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
			System.out.println("# Rewrite dump end");
		}
		if (ProgramProperties.profile) {
			System.out.println("# clausesGenerated: " + Clause.clausesGenerated);
			System.out.println("# clausesDeduces: " + Clause.clausesDeduces);
			System.out.println("# clausesFind: " + Clause.clausesFind);
			System.out.println("# clausesReplaceOrSubstitute: " + Clause.clausesReplaceOrSubstitute);
			System.out.println("# literalsFind: " + Literal.literalsFind);
			System.out.println("# literalsDeduces: " + Literal.literalsDeduces);
			System.out.println("# literalsMgu: " + Literal.literalsMgu);
			System.out.println("# literalsReplaceOrSubstitute: " + Literal.literalsReplaceOrSubstitute);
		}
	}
	
	// LOOP
	//  - select current clause
	//  - select second clause
	//  - resolution, paramodulation
	//  - factoring, eresolution
	//  - check for contradiction
	//  - repeat
	public void solve(long maxtime) {
		
		startTime = System.currentTimeMillis();
		long endTime = startTime + maxtime * 1000;
		
		for (ClauseEntry e : clauses) {
			if (e.getClause().getLiterals().size() == 1) {
				units.add(e);
				if (ProgramProperties.debug) {
					System.out.print("# Adding unit: ");
					marshaller.marshallClause(e);
				}
				// TODO: back unit deletion
			}
			waiting.add(e);
		}
		
		clausifyAll();
		
		for (ClauseEntry e : waiting) {
			if (e.getType() == Type.NEGATED_CONJECTURE) {
				selectedConjecture = e;
				break;
			}
		}
		if (selectedConjecture == null) {
			System.out.println("# solver start error - no conjecture found");
			return;
		}
		
		//waiting.add(selectedConjecture);
		

		int currentTarget = 0;
		int counter = 0;
		int depth = 0;

		int remainingAge = ProgramProperties.selectAge;
		int remainingSmallest = ProgramProperties.selectSmallest;
		
		while(true) {
			if (waiting.size() < 1) {
				System.out.println("# proof not found - no more clauses to solve");
				System.out.println("# active size: " + active.size());
				dumpActive();
				//TODO: result not found
				break;
			}
			
			ClauseEntry currentEntry = null;
			
			if (remainingAge == 0 && remainingSmallest == 0) {
				remainingAge = ProgramProperties.selectAge;
				remainingSmallest = ProgramProperties.selectSmallest;
			}
			
			// select current given clause and add it to active
			if (remainingAge > 0 ) {
				// select oldest clause
				currentEntry = waiting.get(0);
				active.add(currentEntry);
				waiting.remove(0);
				remainingAge--;
			} else if (remainingSmallest > 0) {
				// select smallest clause
				for (ClauseEntry entry : waiting) {
					if (currentEntry == null || currentEntry.getClause().getLiterals().size() > entry.getClause().getLiterals().size()) {
						currentEntry = entry;
					}
				}
				active.add(currentEntry);
				waiting.remove(currentEntry);
				remainingSmallest--;
			} else {
				System.out.println("% SZS status Error");
				System.out.println("Error in select loop");
				dumpActive();
			}
			
			
			if (ProgramProperties.printLoopDepth) {
				counter++;
				if (counter >= currentTarget) {
					counter = 0;
					System.out.println("# LOOP DEPTH: " + (depth++));
					System.out.println("# WAITING SIZE: " + waiting.size());
					System.out.println("# ACTIVE SIZE: " + active.size());
					currentTarget = waiting.size();
				}
			}
			
			//System.out.print("Current: ");
			//marshaller.marshallClause(currentEntry);
			
			List<RewriteRule> newRewriteRules = new LinkedList<>();
			
			for (Literal l : currentEntry.getClause().getLiterals()) {
				if (l.getPredicate().equals(PredicateSymbol.EQUALS) && !l.isNegated()) {
					// possible optimisation - term ordering
					Term left = l.getTerms().get(0);
					Term right = l.getTerms().get(1);
					
					// if (left instanceof Variable && right instanceof Variable) continue;
					
					List<Literal> newLiterals = new ArrayList<>(currentEntry.getClause().getLiterals().size());
					
					for (Literal ll : currentEntry.getClause().getLiterals()) {
						if (ll != l) {
							newLiterals.add(ll);
						}
					}

					// this is added before redundancy check - fix
					// this gets called multiple times with ther same currentEntry
					
					RewriteRule leftRule = new RewriteRule(left, right, newLiterals, currentEntry);
					RewriteRule rightRule = new RewriteRule(right, left, newLiterals, currentEntry);

					newRewriteRules.add(leftRule);
					newRewriteRules.add(rightRule);
				}
			}
		
			
			for (ClauseEntry secondEntry : active) {
				if (System.currentTimeMillis() > endTime) {
					System.out.println("# SZS status Timeout");
					System.out.println("# waiting clauses: " + waiting.size());
					System.out.println("# active clauses: " + active.size());
					int[] sizes = new int[100];
					for (ClauseEntry e : active) {
						sizes[e.getClause().getLiterals().size()]++;
					}
					for (int i = 0; i < 100; i++) {
						if (sizes[i] != 0) {
							System.out.println("Clauses size["+i+"]: "+sizes[i]);
						}
					}
					dumpActive();
					return;
				}
				
				//infer current with active entries
				List<ClauseEntry> resolvents = resolve(currentEntry, secondEntry);
				
				List<ClauseEntry> modulated = paramodulate(currentEntry, secondEntry, newRewriteRules);
				resolvents.addAll(modulated);
				
				//apply postinference rules
				for (ClauseEntry newlyInferred : resolvents) {
					addGenerated(newlyInferred);
				}
				
			}
			
			rewriteRules.addAll(newRewriteRules);
		}
		
	}
	
	
	
	
	//////////////////////////////////////////////////////
	//                                                  //
	//	              Inference rules                   //
	//                                                  //
	//////////////////////////////////////////////////////

	private List<ClauseEntry> paramodulate(ClauseEntry currentEntry, ClauseEntry secondEntry, List<RewriteRule> newRewriteRules) {
		List<ClauseEntry> res = new LinkedList<>();
		
		for (RewriteRule r : rewriteRules) {
			// this gets called multiple times with the same currentEntry
			List<Clause> generated = r.apply(currentEntry.getClause());
			
			for (Clause c : generated) {
				c = (Clause) rewriteVariables(c);
				res.add(new ClauseEntry(generateEntryName(), Type.PLAIN, c, null, new Paramodulation(r.getEntry(), currentEntry)));
			}
		}
		
		
		for (RewriteRule r : newRewriteRules) {
			List<Clause> generated = r.apply(secondEntry.getClause());
			
			for (Clause c : generated) {
				c = (Clause) rewriteVariables(c);
				res.add(new ClauseEntry(generateEntryName(), Type.PLAIN, c, null, new Paramodulation(currentEntry, secondEntry)));
			}
		}
		
		
		return res;
	}
	
	private List<ClauseEntry> eresolution(ClauseEntry entry) {
		List<ClauseEntry> res = new LinkedList<>();
		
		for (Literal l : entry.getClause().getLiterals()) {
			if (l.isNegated() && l.getPredicate().equals(PredicateSymbol.EQUALS)) {
								Term left = l.getTerms().get(0);
				Term right = l.getTerms().get(1);
				
				Substitution unifier = left.mgu(right, new Substitution());
				
				if (unifier != null) {
					List<Literal> others = new ArrayList<>(entry.getClause().getLiterals().size()-1);

					for (Literal l2 : entry.getClause().getLiterals()) {
						if (l2 != l) {
							others.add(unifier.isEmpty() ? l2 : l2.replace(unifier));
						}
					}
					
					Clause newClause = (Clause) rewriteVariables(new Clause(others));
					
					res.add(new ClauseEntry(generateEntryName(), entry.getType(), newClause, null, new EResolution(entry)));
				}
			}
		}
		
		return res;
	}
	
	private FormulaEntry assumeNegation(FormulaEntry e) {
		return new FormulaEntry(generateEntryName(), Type.NEGATED_CONJECTURE, new LogicalFormula(Connective.NOT, new Formula[] {e.getFormula()}), e.getVariables(), new AssumeNegation(e)); //TODO: recreate variables
	}
	
	// Attempt to resolve two clauses - binary resolution
	//
	//each clause has its own variables
	//we can unify complementary literals only, as the resulting substitution wont collide.
	// 1. - find complementary literals
	// 2. - find mgu of those literals
	// 3. - if mgu exist, return mgu, else 1
	// 4. - if there are no more complementary literals, return null
	private List<ClauseEntry> resolve(ClauseEntry c1, ClauseEntry c2) {
		List<ClauseEntry> resolvents = new LinkedList<>();
		
		for (Literal left : c1.getClause().getLiterals()) {
			boolean ln = left.isNegated();
			PredicateSymbol ls = left.getPredicate();
			
			// leave equals fo paramodulation
			if (ls.equals(PredicateSymbol.EQUALS)) {
				continue;
			}
			
			for (Literal right : c2.getClause().getLiterals()) {
				if (right.isNegated() != ln && right.getPredicate().equals(ls)) {
					Substitution s = left.mgu(right);
					if (s != null) {
						List<Literal> newList = new LinkedList<>();
						newList.addAll(c1.getClause().getLiterals());
						newList.remove(left);
						newList.addAll(c2.getClause().getLiterals());
						newList.remove(right);
						Clause newClause = new Clause(newList);
						newClause = (Clause) newClause.replace(s);
						newClause = (Clause) rewriteVariables(newClause);
						ClauseEntry cnew = new ClauseEntry(generateEntryName(), c1.getType() == Type.NEGATED_CONJECTURE || c2.getType() == Type.NEGATED_CONJECTURE ? Type.NEGATED_CONJECTURE : Type.PLAIN, newClause, null, new BinaryResolution(c1, c2));
						resolvents.add(cnew);
						//return cnew;
					}
				}
			}
		}
		return resolvents;
	}
	
	// Attempt to factor a clause
	//
	//unlike resolution, now we can have variable conflicts
	// 1. find two literals with same predicate symbol
	// 2. try to unify them
	// note: possibly order dependent?
	private List<ClauseEntry> factor(ClauseEntry clause) {
		List<Literal> literals = clause.getClause().getLiterals();
		List<ClauseEntry> newEntries = new LinkedList<>();
		for (int i = 0; i < literals.size(); i++) {
			for (int j = i+1; j < literals.size(); j++) {
				Literal left = literals.get(i);
				Literal right = literals.get(j);
				if (left.isNegated() != right.isNegated()) {
					continue;
				}
				Substitution s = left.mgu(right);
				if (s != null) {
					List<Literal> literals2 = new LinkedList<>();
					for (int k = 0; k < literals.size(); k++) {
						if (i != k) {
							literals2.add(literals.get(k).replace(s));
						}
					}
					Clause newClause = (Clause) rewriteVariables(new Clause(literals2));
					newEntries.add(new ClauseEntry(generateEntryName(), clause.getType() == Type.NEGATED_CONJECTURE ? Type.NEGATED_CONJECTURE : Type.PLAIN, newClause, null, new Factoring(clause)));
				}
			}
		}
		return newEntries;
	}
	
	private FunctionSymbol generateSkolemFunctionSymbol(int arity) {
		String name = null;
		do {
			name = SKOLEM_PREFIX + (nextSkolemFnc++);
		} while (symbolMap.containsKey(name));
		
		FunctionSymbol s = new FunctionSymbol(name, nextSymbolId++, arity);
		symbolMap.put(name, s);
	
		return s;
	}
	
	private void clausify(FormulaEntry formulaEntry) {
		// Rename variables
		int varid = 1;
		for (Variable v : formulaEntry.getVariables()) {
			v.setName("X"+(varid++));
		}
		
		//negate conjecture
		if (formulaEntry.getType() == Type.CONJECTURE) {
			formulaEntry = assumeNegation(formulaEntry);
		}
		
		Formula formula = formulaEntry.getFormula();
		// replace <=> and =>
		//TODO: more connectives
		formula = formula.replaceConnectives();
		// move negations to predicates
		formula = formula.pushNegations(false);
		//Now in NNF
		// skolemize
		List<FunctionTerm> generatedSkolemTerms = new LinkedList<>();
		formula = formula.skolemize(new LinkedList<>(), generatedSkolemTerms);
		for (FunctionTerm ft : generatedSkolemTerms) {
			ft.setFunctionSymbol(generateSkolemFunctionSymbol(ft.getParameters().size()));
		}
		
		List<Clause> clauses = formula.flatten();
		
		for (Clause c : clauses) {
			Clause cc = (Clause) rewriteVariables(c);
			ClauseEntry newEntry = new ClauseEntry(generateEntryName(), formulaEntry.getType() == Type.NEGATED_CONJECTURE ? Type.NEGATED_CONJECTURE : Type.PLAIN, cc, cc.collectVariables(), new CNFConversion(formulaEntry));
			waiting.add(newEntry);
		}
	}
	
	public void clausifyAll() {
		for (ClauseEntry c : clauses) {
			if (c.getType() == Type.CONJECTURE) {
				clausify(new FormulaEntry(generateEntryName(), Type.NEGATED_CONJECTURE, c.getClause().pushNegations(true), null, new AssumeNegation(c)));
			}
		}
		
		for (FormulaEntry f : formulae) {
			if (f.getType() == Type.CONJECTURE) {
				clausify(new FormulaEntry(generateEntryName(), Type.NEGATED_CONJECTURE, f.getFormula().pushNegations(true), null, new AssumeNegation(f)));
			} else {
				clausify(f);
			}
		}
	}

	
	public static class Builder {
		
		private HashMap<String, Symbol> symbolMap = new HashMap<>();
		private LinkedList<ClauseEntry> clauses = new LinkedList<>();
		private LinkedList<FormulaEntry> formulae = new LinkedList<>();
		private int nextSymbolId = 1;
		private int nextVarId = 1;
		
		private Builder() {}
		
		public void addFormulaEntry(FormulaEntry entry) {
			formulae.add(entry);
		}
		
		public void addClauseEntry(ClauseEntry entry) {
			clauses.add(entry);
		}

		public int generateVarId() {
			return nextVarId++;
		}
		
		private int generateSymbolId() {
			return nextSymbolId++;
		}
		
		public Symbol getSymbol(String name) {
			return symbolMap.get(name);
		}
		
		public PredicateSymbol addPredicateSymbol(String name, int arity) {
			if (symbolMap.containsKey(name)) {
				Symbol s = symbolMap.get(name);
				if (s instanceof PredicateSymbol) {
					if (arity != s.getArity()) {
						System.err.println("mismatchin arity of " + name);
						return null;
					}
					return (PredicateSymbol) s;
				} else {
					System.err.println(name + " referenced as predicate is already a function symbol");
					return null;
				}
			} else {
				PredicateSymbol s = new PredicateSymbol(name, generateSymbolId(), arity);
				symbolMap.put(name, s);
				return s;
			}
		}
		
		public FunctionSymbol addFunctionSymbol(String name, int arity) {
			if (symbolMap.containsKey(name)) {
				Symbol s = symbolMap.get(name);
				if (s instanceof FunctionSymbol) {
					if (arity != s.getArity()) {
						return null;
					}
					return (FunctionSymbol) s;
				} else {
					return null;
				}
			} else {
				FunctionSymbol s = new FunctionSymbol(name, generateSymbolId(), arity);
				symbolMap.put(name, s);
				return s;
			}
		}
		
		public KnowledgeBase build() {
			return new KnowledgeBase(this);
		}
	}
	
	/**
	 * Creates new Builder instance.
	 * 
	 * @return new Builder
	 */
	public static Builder builder() {
		return new Builder();
	}
}
