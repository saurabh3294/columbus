package com.proptiger.exception;

public class DuplicateNameResourceException extends DuplicateResourceException{

	private static final long serialVersionUID = 5282369332501755220L;
	public DuplicateNameResourceException(String msg){
		super(msg);
	}
}