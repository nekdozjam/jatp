package com.mmazanek.atp.model.fol;

public abstract class Symbol {

	private final String name;
	private final int id;
	private final int arity;
	
	public Symbol(String name, int id, int arity) {
		this.name = name;
		this.id = id;
		this.arity = arity;
	}
	
	public String getName() {
		return name;
	}
	
	public int getId() {
		return id;
	}
	
	public int getArity() {
		return arity;
	}
	
	public boolean equals(Object other) {
		if (other == null || !(other instanceof Symbol)) {
			return false;
		}
		return ((Symbol)other).id == id;
	}
}
