package com.mmazanek.atp.model.inference;

/**
 * Inference step describing that given clause was loaded from a file.
 * 
 * @author Martin Mazanek
 */
public class FileLoad extends Inference {

	private static final String NAME = "file";
	private static final String STATUS = "";
	
	private String filename;
	
	public FileLoad(String filename) {
		this.filename = filename;
	}
	
	public String getFilename() {
		return filename;
	}
	
	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getStatus() {
		return STATUS;
	}

}
