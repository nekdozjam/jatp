package com.mmazanek.atp.model.fol;

public class PredicateSymbol extends Symbol {
	public static final PredicateSymbol EQUALS = new PredicateSymbol("=", 0, 2);
	public static final PredicateSymbol TRUE = new PredicateSymbol("$true", -1, 0);
	public static final PredicateSymbol FALSE = new PredicateSymbol("$false", -2, 0);
	
	public PredicateSymbol(String name, int id, int arity) {
		super(name, id, arity);
	}
}
