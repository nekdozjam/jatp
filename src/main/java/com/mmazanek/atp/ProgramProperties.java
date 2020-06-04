package com.mmazanek.atp;

/**
 * Static class for storing various program properties
 * 
 * @author Martin Mazanek
 */
public class ProgramProperties {
	public static boolean debug = false;
	public static boolean printLoopDepth = false;
	public static boolean unitDeletion = true;
	public static boolean verbose = false;
	public static boolean dumpActive = false;
	public static boolean dumpUnits = false;
	public static boolean dumpRewrite = false;
	public static boolean profile = false;
	public static int maxClauseSize = 0;
	public static int maxClauseVariables = 0;
	
	public static int selectAge = 1;
	public static int selectSmallest = 1;
}
