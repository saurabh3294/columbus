package com.proptiger.exception;

/**
 * This is a wrapper over RuntimeException.
 * To make code cleaner, for all non recoverable exception this class should be used
 * 
 * @author Rajeev Pandey
 *
 */
public class ProAPIException extends RuntimeException{

	private static final long serialVersionUID = 4182555505392936914L;
	
	public ProAPIException(Throwable ex){
		super(ex);
	}
	
	public ProAPIException(String message, Throwable ex){
		super(message, ex);
	}

}
