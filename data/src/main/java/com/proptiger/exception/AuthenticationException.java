package com.proptiger.exception;

/**
 * @author Rajeev Pandey
 *
 */
public class AuthenticationException  extends ProAPIException{
	
	private static final long serialVersionUID = 6084344121002100376L;

	public AuthenticationException(String msg){
		super(msg);
	}
}
