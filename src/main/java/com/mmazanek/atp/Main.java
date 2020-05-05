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
		
		CommandLineParser p = new DefaultParser();
		CommandLine line = null;
		try {
			line = p.parse(options, args);
		} catch (ParseException e) {
			System.out.println("% SZS status UsageError");
			return;
		}
		
		String basedir = line.getOptionValue("basedir", ".");
		String includes = line.getOptionValue("includes", ".");
		long maxtime = Long.parseLong(line.getOptionValue("maxtime", "60"));
		
		String[] leftover = line.getArgs();
		if (leftover.length != 1) {
			//TODO: SZS Error wrong arguments
			System.out.println("% SZS status UsageError");
			return;
		}
		String filename = leftover[0];
		
		TptpParser parser = new TptpParser(new File(basedir, filename), includes);
		
		KnowledgeBase kb = parser.load();
		System.out.println("% KB clauses size: " +  kb.getClauses().size());
		System.out.println("% KB formulae size: " +  kb.getFormulae().size());
		
		TptpMarshaller marshaller = new TptpMarshaller(System.out);
		
		System.out.println("% Formulae: ");
		for (FormulaEntry f : kb.getFormulae()) {
			System.out.print("% ");
			marshaller.marshallFormula(f);
		}
		
		System.out.println("\n% Clauses:");
		for (ClauseEntry c : kb.getClauses()) {
			System.out.print("% ");
			marshaller.marshallClause(c);
		}
		
		System.out.println("% Solving....");
		
		try {
			kb.solve(maxtime);
		} catch (OutOfMemoryError e) {
			kb = null;
			System.gc();
			System.out.println("% SZS status MemoryOut for ");
		}
		
		System.out.println("% Program end.");
		
	}

}
