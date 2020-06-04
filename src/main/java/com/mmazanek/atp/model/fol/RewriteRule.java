package com.mmazanek.atp.model.fol;

import java.util.LinkedList;
import java.util.List;

import com.mmazanek.atp.model.KnowledgeEntry;

/**
 * Representation of a paramodulation rewriting rule
 * 
 * This class contains data for a single rewrite rule, so the equalities dont have to be looked up over and over again.
 * 
 * @author Martin Mazanek
 */
public class RewriteRule {
	private Term from;
	private Term to;
	private List<Literal> addedLiterals;
	private KnowledgeEntry entry;
	
	public RewriteRule(Term from, Term to, List<Literal> literals, KnowledgeEntry entry) {
		this.from = from;
		this.to = to;
		this.addedLiterals = literals;
		this.entry = entry;
	}
	
	/**
	 * Get the term to be rewritten
	 * @return term to be rewritten
	 */
	public Term getFrom() {
		return from;
	}
	
	/**
	 * Get the new term
	 * @return term
	 */
	public Term getTo() {
		return to;
	}
	
	/**
	 * Get a List of literals that should be added after rewriting
	 * @return list of literals
	 */
	public List<Literal> getAddedLiterals() {
		return addedLiterals;
	}
	
	/**
	 * Apply this rule to a clause
	 * 
	 * This method rewrites all positions of from term to to term.
	 * 
	 * @param clause
	 * @return List of all rewriten clauses
	 */
	public List<Clause> apply(Clause clause) {
		List<Clause> res = new LinkedList<>();
		List<Term.Position> positions = clause.find(from);
		if (positions != null) {
			for (Term.Position position : positions) {
				Clause newClause = clause.replaceOrSubstitute(position, to.replace(position.getUnifier()));
				if (newClause != null) {
					for (Literal l : addedLiterals) {
						l = l.replace(position.getUnifier());
						if (l == null) {
							System.out.println("rewrite");
						}
						newClause.getLiterals().add(l);
					}
					res.add(newClause);
				}
			}
		}
		return res;
	}

	public KnowledgeEntry getEntry() {
		return entry;
	}
}
