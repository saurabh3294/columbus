package com.proptiger.exception;

public class InvalidResourceNameException extends ProAPIException {

	private static final long serialVersionUID = 3916295250911467642L;
	
	public InvalidResourceNameException(String name){
		super(name);
	}
	
}
