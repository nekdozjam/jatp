package com.mmazanek.atp.model.fol;

/**
 * Representation of a predicate symbol
 * 
 * @author Martin Mazanek
 */
public class PredicateSymbol extends Symbol {
	/**
	 * Constant predicate symbol for equality
	 */
	public static final PredicateSymbol EQUALS = new PredicateSymbol("=", 0, 2);
	/**
	 * Constant predicate symbol for TRUE
	 */
	public static final PredicateSymbol TRUE = new PredicateSymbol("$true", -1, 0);
	
	/**
	 * Constant predicate symbol for FALSE
	 */
	public static final PredicateSymbol FALSE = new PredicateSymbol("$false", -2, 0);
	
	public PredicateSymbol(String name, int id, int arity) {
		super(name, id, arity);
	}
}
