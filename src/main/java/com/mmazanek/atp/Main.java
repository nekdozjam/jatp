package com.mmazanek.atp;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.mmazanek.atp.model.ClauseEntry;
import com.mmazanek.atp.model.FormulaEntry;
import com.mmazanek.atp.model.KnowledgeBase;
import com.mmazanek.atp.parser.TptpMarshaller;
import com.mmazanek.atp.parser.TptpParser;

public class Main {
	
	public static void main(String[] args) {
		Options options = new Options();
		
		options.addOption("b", "basedir", true, "directory where problem files are located");
		options.addOption("i", "includes", true, "directory where included files are located");
		options.addOption("t", "maxtime", true, "maximum runtime in seconds");
		options.addOption("printDepth", false, "print loop depth");
		options.addOption("debug", false, "enable debugging messages");
		options.addOption("maxClauseSize", true, "maximum clause size to keep");
		options.addOption("maxClauseVariables", true, "maximum number of variables in kept clauses");
		
		CommandLineParser p = new DefaultParser();
		CommandLine line = null;
		try {
			line = p.parse(options, args);
		} catch (ParseException e) {
			System.out.println("# SZS status UsageError");
			return;
		}
		
		String basedir = line.getOptionValue("basedir", ".");
		String includes = line.getOptionValue("includes", ".");
		long maxtime = Long.parseLong(line.getOptionValue("maxtime", "60"));
		ProgramProperties.printLoopDepth = line.hasOption("printDepth");
		ProgramProperties.debug = line.hasOption("debug");
		if (line.hasOption("maxClauseSize")) {
			ProgramProperties.maxClauseSize = Integer.parseInt(line.getOptionValue("maxClauseSize"));
		}
		if (line.hasOption("maxClauseVariables")) {
			ProgramProperties.maxClauseVariables = Integer.parseInt(line.getOptionValue("maxClauseVariables"));
		}
		
		System.out.println("# MaxClauseSize: " + ProgramProperties.maxClauseSize);
		System.out.println("# MaxClauseVariables: " + ProgramProperties.maxClauseVariables);
		
		String[] leftover = line.getArgs();
		if (leftover.length != 1) {
			//TODO: SZS Error wrong arguments
			System.out.println("# SZS status UsageError");
			System.out.println("# No input file specified.");
			return;
		}
		String filename = leftover[0];
		
		TptpParser parser = new TptpParser(new File(basedir, filename), includes);
		
		KnowledgeBase kb = parser.load();
		System.out.println("# KB clauses size: " +  kb.getClauses().size());
		System.out.println("# KB formulae size: " +  kb.getFormulae().size());
		
		TptpMarshaller marshaller = new TptpMarshaller(System.out);
		
		System.out.println("# Formulae: ");
		for (FormulaEntry f : kb.getFormulae()) {
			System.out.print("# ");
			marshaller.marshallFormula(f);
		}
		
		System.out.println("# Clauses:");
		for (ClauseEntry c : kb.getClauses()) {
			System.out.print("# ");
			marshaller.marshallClause(c);
		}
		
		System.out.println("# Solving....");
		
		try {
			kb.solve(maxtime);
		} catch (OutOfMemoryError e) {
			kb = null;
			System.gc();
			System.out.println("# SZS status MemoryOut");
		}
		
		System.out.println("# Program end.");
		
	}

}
