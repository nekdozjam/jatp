package com.mmazanek.atp.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mmazanek.atp.model.KnowledgeEntry.Type;
import com.mmazanek.atp.model.fol.Clause;
import com.mmazanek.atp.model.fol.Formula;
import com.mmazanek.atp.model.fol.FunctionSymbol;
import com.mmazanek.atp.model.fol.FunctionTerm;
import com.mmazanek.atp.model.fol.Literal;
import com.mmazanek.atp.model.fol.LogicalFormula;
import com.mmazanek.atp.model.fol.LogicalFormula.Connective;
import com.mmazanek.atp.model.fol.PredicateSymbol;
import com.mmazanek.atp.model.fol.Substitution;
import com.mmazanek.atp.model.fol.Symbol;
import com.mmazanek.atp.model.fol.Term;
import com.mmazanek.atp.model.fol.Variable;
import com.mmazanek.atp.model.inference.AssumeNegation;
import com.mmazanek.atp.model.inference.BinaryResolution;
import com.mmazanek.atp.model.inference.CNFConversion;
import com.mmazanek.atp.model.inference.Factoring;
import com.mmazanek.atp.parser.TptpMarshaller;

/**
 * 
 * @author Martin Mazanek
 *
 */
public class KnowledgeBase {
	private HashMap<String, Symbol> symbolMap = new HashMap<>();
	
	private List<FormulaEntry> formulae;
	private List<ClauseEntry> clauses;

	private List<ClauseEntry> active = new LinkedList<>();
	private List<ClauseEntry> waiting = new LinkedList<>();
	
	private ClauseEntry selectedConjecture = null;
	
	private static final String VARIABLE_PREFIX = "X";
	private static final String SKOLEM_PREFIX = "fsk_n";
	private static final String ENTRY_PREFIX = "f__";
	
	private int nextSymbolId;
	private int nextSkolemFnc = 1;
	private int nextEntryId = 1;
	private int nextVarId;
	
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
	
	private void addClause(ClauseEntry clause) {
		waiting.add(clause);
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
		//List<Variable> 
		
		for (Clause c : clauses) {
			Clause cc = (Clause) rewriteVariables(c);
			addClause(new ClauseEntry(generateEntryName(), formulaEntry.getType() == Type.NEGATED_CONJECTURE ? Type.NEGATED_CONJECTURE : Type.PLAIN, cc, cc.collectVariables(), new CNFConversion(formulaEntry)));
		}
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
	
	private void addGenerated(ClauseEntry entry) {
		//TODO: optimisation
		
		waiting.add(entry);
	}
	
	// LOOP
	//  - select current clause
	//  - select second clause
	//  - resolution step
	//  - factoring
	//  - check for contradiction
	//  - repeat
	public void solve(long maxtime) {
		
		long startTime = System.currentTimeMillis();
		long endTime = startTime + maxtime * 1000;

		TptpMarshaller marshaller = new TptpMarshaller(System.out);
		
		for (ClauseEntry e : clauses) {
			waiting.add(e);
		}
		
		clausifyAll();
		
		for (ClauseEntry e : waiting) {
			if (e.getType() == Type.NEGATED_CONJECTURE) {
				selectedConjecture = e;
				break;
				//TODO: handle multiple conjectures
			}
		}
		if (selectedConjecture == null) {
			System.out.println("solver start error - no conjecture found");
			return;
		}
		waiting.add(selectedConjecture);
		
		while(true) {
			if (waiting.size() < 1) {
				System.out.println("proof not found - no more clauses to solve");
				System.out.println("active size: " + active.size());
				//TODO: result not found
				break;
			}
			
			ClauseEntry currentEntry = waiting.get(0);
			active.add(currentEntry);
			waiting.remove(0);
			
			for (ClauseEntry secondEntry : active) {
				if (System.currentTimeMillis() > endTime) {
					System.out.println("proof not found - timeout");
					System.out.println("waiting clauses: " + waiting.size());
					return;
				}
				
				
				//infer current with active entries
				List<ClauseEntry> resolvents = resolve(currentEntry, secondEntry);
				//TODO: more inference rules
				
				//apply postinference rules
				if (resolvents == null || resolvents.size() == 0) {
					continue;
				}
				for (ClauseEntry newlyInferred : resolvents) {
					//check for contradiction
					if (newlyInferred.getClause().isEmpty()) {
						//Proof found
						System.out.println("% ***Proof found!***");
						System.out.println("% Run time: " + (System.currentTimeMillis() - startTime) + "ms");
						new TptpMarshaller(System.out).marshallKnowledgeEntryWithAncestors(newlyInferred);
						return;
					}
					
					List<ClauseEntry> factoredNewlyInferred = factor(newlyInferred);
					if (factoredNewlyInferred != null) {
						for (ClauseEntry e : factoredNewlyInferred) {
							if (e.getClause().isEmpty()) {
								//Proof found
								System.out.println("% ***Proof found!***");
								System.out.println("% Run time: " + (System.currentTimeMillis() - startTime) + "ms");
								new TptpMarshaller(System.out).marshallKnowledgeEntryWithAncestors(e);
								return;
							}
							//System.out.print("% Adding ");
							//marshaller.marshallClause(e);
							addGenerated(e);
						}
					}

					//System.out.print("% Adding ");
					//marshaller.marshallClause(newlyInferred);
					addGenerated(newlyInferred);
				}
			}
		}
		
	}
	
	private FormulaEntry assumeNegation(FormulaEntry e) {
		return new FormulaEntry(generateEntryName(), Type.NEGATED_CONJECTURE, new LogicalFormula(Connective.NOT, new Formula[] {e.getFormula()}), e.getVariables(), new AssumeNegation(e)); //TODO: recreate variables
	}
	
	private Substitution mgu(Term t1, Term t2, Substitution substitution) {
		if (t1 instanceof Variable) {
			Variable var1 = (Variable) t1;
			
			if (t2 instanceof Variable) {
				// Var Var
				Variable var2 = (Variable) t2;
				if (var2.equals(var1)) {
					return substitution;
				} else {
					substitution.apply(var1, t2);
				}
			} else {
				// Var fnc
				if (t2.collectVariables().contains(var1)) {
					return null;
				}
				substitution.apply(var1, t2);
			}
		} else {
			if (t2 instanceof Variable) {
				// fcn Var
				Variable var2 = (Variable) t2;
				if (t1.collectVariables().contains(var2)) {
					return null;
				}
				substitution.apply(var2, t1);
			} else {
				// fnc fnc
				Substitution localSubstitution = new Substitution();
				FunctionTerm fnc1 = (FunctionTerm) t1;
				FunctionTerm fnc2 = (FunctionTerm) t2;
				if (!fnc1.getSymbol().equals(fnc2.getSymbol())) {
					return null;
				}
				Iterator<? extends Term> funcTerms2 = fnc2.getParameters().iterator();
				for (Term funcTerm1 : fnc1.getParameters()) {
					localSubstitution = mgu(funcTerm1.replace(localSubstitution), funcTerms2.next().replace(localSubstitution), localSubstitution);
					if (localSubstitution == null) {
						return null;
					}
				}
				substitution.apply(localSubstitution);
			}
		}
		
		return substitution;
	}
	
	private Substitution mgu(Literal l1, Literal l2) {
		Substitution s = new Substitution();
		
		if (!l1.getPredicate().equals(l2.getPredicate())) {
			return null;
		}
		
		Iterator<Term> l2iter = l2.getTerms().iterator();
		for (Term t : l1.getTerms()) {
			Term t2 = l2iter.next();
			s = mgu(t.replace(s), t2.replace(s), s);
			if (s == null) {
				return null;
			}
		}
		
		return s;
	}
	
	//TODO: generate all resolvents!
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
			for (Literal right : c2.getClause().getLiterals()) {
				if (right.isNegated() != ln && right.getPredicate().equals(ls)) {
					Substitution s = mgu(left, right);
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
				Substitution s = mgu(left, right);
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
	
	public void clausifyAll() {
		for (ClauseEntry c : clauses) {
			if (c.getType() == Type.CONJECTURE) {
				clausify(new FormulaEntry(generateEntryName(), Type.NEGATED_CONJECTURE, c.getClause().pushNegations(true), null, new AssumeNegation(c)));
			}
		}
		
		for (FormulaEntry f : formulae) {
			clausify(f);
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
						//TODO: wrong arity
						return null;
					}
					return (PredicateSymbol) s;
				} else {
					//TODO: error wrong symbol type
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
						//TODO: wrong arity
						return null;
					}
					return (FunctionSymbol) s;
				} else {
					//TODO: error wrong symbol type
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
