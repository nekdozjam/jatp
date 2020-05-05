package com.mmazanek.atp.model.inference;

public class FileLoad extends Inference {

	private static final String NAME = "file";
	
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

}
