package com.proptiger.exception;

public class ResourceNotAvailableException extends ProAPIException{

	private static final long serialVersionUID = 6402255527485347856L;

	public ResourceNotAvailableException(Throwable ex){
		super(ex);
	}
	
	public ResourceNotAvailableException(String msg){
		super(msg);
	}
	
	public ResourceNotAvailableException(String message, Throwable ex){
		super(message, ex);
	}
}
