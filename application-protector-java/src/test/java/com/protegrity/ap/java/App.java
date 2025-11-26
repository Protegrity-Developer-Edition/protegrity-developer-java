package com.protegrity.ap.java;

import com.protegrity.ap.java.Protector;
import com.protegrity.ap.java.ProtectorException;
import com.protegrity.ap.java.SessionObject;

public class App {

    public static void main(String[] args) throws Exception {
	Protector protector = Protector.getProtector();
	SessionObject session = protector.createSession("superuser");
	String[] input = new String[] {"Protegrity", "HelloWorld"};
	String[] output = new String[input.length];
	String[] org = new String[input.length];	
	System.out.println("Input Strings:");
	for(int i=0; i<input.length; i++) {
	    System.out.print(input[i] + " ");
	}
	System.out.println();
	boolean res = protector.protect(session, "string", input, output);
	if(res) {
	    System.out.println("Output Strings:");
	    for(int i=0; i<input.length; i++) {
		System.out.print(output[i] + " ");
	    }
	    System.out.println();
	} else {
	    System.out.println(protector.getLastError(session));
	}
	res = protector.unprotect(session, "string", output, org);
	if(res) {
	    System.out.println("Original Strings:");
	    for(int i=0; i<input.length; i++) {
		System.out.print(org[i] + " ");
	    }
	    System.out.println();
	} else {
	    System.out.println(protector.getLastError(session));
	}	
    }
}
