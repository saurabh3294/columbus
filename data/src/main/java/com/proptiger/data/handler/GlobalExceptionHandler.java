package com.proptiger.data.handler;

import javax.persistence.PersistenceException;

import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.proptiger.data.constants.ResponseCodes;
import com.proptiger.data.constants.ResponseErrorMessages;
import com.proptiger.data.pojo.ProAPIErrorResponse;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.exception.ConstraintViolationException;
import com.proptiger.exception.DuplicateResourceException;
import com.proptiger.exception.InvalidResourceNameException;
import com.proptiger.exception.ResourceNotAvailableException;


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
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	protected ProAPIResponse handleGenericException(Exception ex){
		logger.error("handleGenericException - Caching ",ex);
		
		return new ProAPIErrorResponse(ResponseCodes.INTERNAL_SERVER_ERROR,
				ResponseErrorMessages.SOME_ERROR_OCCURED);
	}
	
	@ExceptionHandler(PersistenceException.class)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	protected ProAPIResponse handleDatabaseException(PersistenceException ex) {
		logger.error("handleDatabaseException - Caching ", ex);
		return new ProAPIErrorResponse(ResponseCodes.DATABASE_CONNECTION_ERROR,
				ResponseErrorMessages.DATABASE_CONNECTION_ERROR);
	}
	
	@ExceptionHandler(ConversionNotSupportedException.class)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	protected ProAPIResponse handleConversionNotSupportedException(ConversionNotSupportedException ex){
		logger.error("handleConversionNotSupportedException - Caching ", ex);
		return new ProAPIErrorResponse(ResponseCodes.REQUEST_PARAM_INVALID,
				ResponseErrorMessages.REQUEST_PARAM_INVALID);
		
	}
	
	@ExceptionHandler(SolrServerException.class)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	protected ProAPIResponse handleSolrException(SolrServerException exception) {
		logger.error("handleSolrException - Caching ", exception);
		return new ProAPIErrorResponse(ResponseCodes.INTERNAL_SERVER_ERROR,
				ResponseErrorMessages.SOLR_DOWN);
	}
	
	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	protected ProAPIResponse handleIllegalArgumentException(IllegalArgumentException exception) {
		logger.error("handleIllegalArgumentException - Caching ", exception);
		return new ProAPIErrorResponse(ResponseCodes.BAD_REQUEST,
				exception.getMessage() == null ? ResponseErrorMessages.REQUEST_PARAM_INVALID: exception.getMessage());
	}
	
	@ExceptionHandler(ResourceNotAvailableException.class)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	protected ProAPIResponse handleResourceNotAvailableException(ResourceNotAvailableException exception) {
		logger.error("handle ResourceNotAvailableException - Caching ", exception);
		return new ProAPIErrorResponse(ResponseCodes.BAD_REQUEST,
				exception.getMessage() == null ? ResponseErrorMessages.REQUEST_PARAM_INVALID: exception.getMessage());
	}
	
	@ExceptionHandler(InvalidResourceNameException.class)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	protected ProAPIResponse handleInvalidNameException(InvalidResourceNameException exception) {
		logger.error("handle InvalidResourceNameException - Caching ", exception);
		return new ProAPIErrorResponse(ResponseCodes.BAD_REQUEST, ResponseErrorMessages.INVALID_NAME_ATTRIBUTE);
	}
	
	@ExceptionHandler(DuplicateResourceException.class)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	protected ProAPIResponse handleDuplicateResourceException(DuplicateResourceException exception) {
		logger.error("handle DuplicateResourceException - Caching ", exception);
		return new ProAPIErrorResponse(ResponseCodes.BAD_REQUEST, ResponseErrorMessages.DUPLICATE_RESOURCE);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	protected ProAPIResponse handleConstraintVoilationException(ConstraintViolationException exception) {
		logger.error("handle ConstraintViolationException - Caching ", exception);
		return new ProAPIErrorResponse(ResponseCodes.BAD_REQUEST, ResponseErrorMessages.REQUEST_PARAM_INVALID);
	}
	
	/*@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	protected ProAPIResponse handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
		logger.error("handle ConstraintViolationException - Caching ", exception);
		return new ProAPIErrorResponse(ResponseCodes.BAD_REQUEST, ResponseErrorMessages.REQUEST_PARAM_INVALID);
	}*/
}
