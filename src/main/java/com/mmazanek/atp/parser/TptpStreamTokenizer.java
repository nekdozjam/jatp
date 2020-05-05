package com.mmazanek.atp.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.Stack;

public class TptpStreamTokenizer extends StreamTokenizer {
	
	private Stack<String> stack = new Stack<String>();
	
	public TptpStreamTokenizer(File file) throws FileNotFoundException {
		super(new FileReader(file));
		resetSyntax(); //we dont want numbers
		commentChar('%');
		eolIsSignificant(false);
        quoteChar('"');
        quoteChar('\'');
        quoteChar(39);
		wordChars('_', '_');
		wordChars('0', '9');
		wordChars('a', 'z');
		wordChars('A', 'Z');
        wordChars(128 + 32, 255);
        whitespaceChars(0, ' ');
		ordinaryChar('!');
		ordinaryChar('?');
		ordinaryChar('[');
		ordinaryChar(']');
		ordinaryChar('(');
		ordinaryChar(')');
		ordinaryChar('=');
		ordinaryChar('>');
		ordinaryChar('|');
		ordinaryChar('~');
		ordinaryChar('$');
	}
	
	public String next() throws IOException {
		if (stack.isEmpty()) {
			int i = nextToken();
			if (i > 0) {
				if (i == '\'' || i == '"') {
					return sval;
				}
				sval = String.valueOf((char)i);
			}
			if ("=".equals(sval)) {
				String n = next();
				if (">".equals(n)) {
					sval = "=>";
				} else {
					push();
					sval = "=";
				}
			} else if ("!".equals(sval)) {
				String n = next();
				if ("=".equals(n)) {
					sval = "!=";
				} else {
					push();
					sval = "!";
				}
			}
			return sval;
		} else {
			String res = stack.pop();
			//System.out.println("next pop - "+res);
			return res;
		}
	}
	
	public void skipOne(String expected) throws IOException {
		if (!next().equals(expected)) {
			throw new IOException("Skipped wrong token!");
		}
	}
	
	// Returns true is skipped expected, false otherwise
	public boolean trySkipOne(String expected) throws IOException {
		if (next().equals(expected)) {
			return true;
		}
		push();
		return false;
	}
	
	private void push() {
		stack.push(sval);
	}
}
