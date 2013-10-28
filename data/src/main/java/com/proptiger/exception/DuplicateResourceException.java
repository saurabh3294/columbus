package com.proptiger.exception;

/**
 * @author Rajeev Pandey
 *
 */
public class DuplicateResourceException extends ProAPIException{

	private static final long serialVersionUID = 5282369332501755220L;
	public DuplicateResourceException(String msg){
		super(msg);
	}
}
