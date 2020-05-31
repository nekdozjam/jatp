package com.mmazanek.atp.model.fol;

import java.util.LinkedList;
import java.util.List;

import com.mmazanek.atp.model.KnowledgeEntry;

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
	
	public Term getFrom() {
		return from;
	}
	
	public Term getTo() {
		return to;
	}
	
	public List<Literal> getAddedLiterals() {
		return addedLiterals;
	}
	
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
