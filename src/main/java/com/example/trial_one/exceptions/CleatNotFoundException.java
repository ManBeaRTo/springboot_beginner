package com.example.trial_one.exceptions;

public class CleatNotFoundException extends RuntimeException{
	private static final long serialVersionID = 1;
	
	public CleatNotFoundException(String message)
	{
		super(message);
		
	}
}
