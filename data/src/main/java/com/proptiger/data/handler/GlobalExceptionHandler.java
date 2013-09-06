package com.proptiger.data.handler;

import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.proptiger.data.constants.ResponseErrorMessages;


/**
 * This class is a global exception handler, based on type of exception
 * it constructs the response and return that back to API caller.
 * 
 * @author Rajeev Pandey
 *
 */
@ControllerAdvice
public class GlobalExceptionHandler {

	private Logger logger = LoggerFactory.getLogger("exception.handler");
	
	@ExceptionHandler(Exception.class)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	protected String handleGenericException(Exception ex){
		logger.error("handleGenericException - Caching "+ex);
		return ResponseErrorMessages.SOME_ERROR_OCCURED;
	}
	
	@ExceptionHandler(PersistenceException.class)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	protected String handleDatabaseException(PersistenceException ex){
		logger.error("handleDatabaseException - Caching "+ex);
		return ResponseErrorMessages.DATABASE_CONNECTION_ERROR;
	}
	
	@ExceptionHandler(ConversionNotSupportedException.class)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	protected String handleConversionNotSupportedException(ConversionNotSupportedException ex){
		logger.error("handleConversionNotSupportedException - Caching "+ex);
		return ResponseErrorMessages.REQUEST_PARAM_CONVERSION_ERROR;
		
	}
	
	
}
