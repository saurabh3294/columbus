package com.proptiger.data.handler;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.authz.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.amazonaws.services.opsworks.model.ResourceNotFoundException;
import com.proptiger.data.constants.ResponseCodes;
import com.proptiger.data.constants.ResponseErrorMessages;
import com.proptiger.data.pojo.ProAPIErrorResponse;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.exception.BadRequestException;
import com.proptiger.exception.ConstraintViolationException;
import com.proptiger.exception.DuplicateNameResourceException;
import com.proptiger.exception.DuplicateResourceException;
import com.proptiger.exception.InvalidResourceException;
import com.proptiger.exception.ProAPIException;
import com.proptiger.exception.ResourceAlreadyExistException;
import com.proptiger.exception.ResourceNotAvailableException;

/**
 * This class is a global exception handler, based on type of exception it
 * constructs the response and return that back to API caller.
 * 
 * @author Rajeev Pandey
 * 
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    protected ProAPIResponse handleGenericException(Exception ex, HttpServletRequest httpRequest) {
        logAPIUrlInLogFile(httpRequest, ex);
        return new ProAPIErrorResponse(ResponseCodes.INTERNAL_SERVER_ERROR, ResponseErrorMessages.SOME_ERROR_OCCURED);
    }

    private void logAPIUrlInLogFile(HttpServletRequest httpRequest, Exception ex) {
        if (httpRequest != null) {
            logger.error(
                    "Exception occured while accessing url {} {} {} {}",
                    httpRequest.getMethod(),
                    httpRequest.getRequestURI(),
                    httpRequest.getQueryString(),
                    httpRequest.getHeader("user-agent"),
                    ex);
        }
    }

    @ExceptionHandler(ProAPIException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    protected ProAPIResponse handleProAPIException(ProAPIException ex, HttpServletRequest httpRequest) {
        logAPIUrlInLogFile(httpRequest, ex);

        return new ProAPIErrorResponse(ResponseCodes.INTERNAL_SERVER_ERROR, ResponseErrorMessages.SOME_ERROR_OCCURED);
    }

    @ExceptionHandler(ConversionNotSupportedException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    protected ProAPIResponse handleConversionNotSupportedException(
            ConversionNotSupportedException ex,
            HttpServletRequest httpRequest) {
        logAPIUrlInLogFile(httpRequest, ex);
        return new ProAPIErrorResponse(ResponseCodes.REQUEST_PARAM_INVALID, ResponseErrorMessages.REQUEST_PARAM_INVALID);

    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    protected ProAPIResponse handleIllegalArgumentException(
            IllegalArgumentException exception,
            HttpServletRequest httpRequest) {
        logAPIUrlInLogFile(httpRequest, exception);
        return new ProAPIErrorResponse(ResponseCodes.BAD_REQUEST, exception.getMessage() == null
                ? ResponseErrorMessages.REQUEST_PARAM_INVALID
                : exception.getMessage());
    }

    @ExceptionHandler(ResourceNotAvailableException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    protected ProAPIResponse handleResourceNotAvailableException(
            ResourceNotAvailableException exception,
            HttpServletRequest httpRequest) {
        logAPIUrlInLogFile(httpRequest, exception);
        return new ProAPIErrorResponse(ResponseCodes.BAD_REQUEST, exception.getMessage() == null
                ? ResponseErrorMessages.REQUEST_PARAM_INVALID
                : exception.getMessage());
    }

    @ExceptionHandler(InvalidResourceException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    protected ProAPIResponse handleInvalidNameException(
            InvalidResourceException exception,
            HttpServletRequest httpRequest) {
        logAPIUrlInLogFile(httpRequest, exception);
        return new ProAPIErrorResponse(ResponseCodes.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(DuplicateNameResourceException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    protected ProAPIResponse handleDuplicateNameResourceException(
            DuplicateNameResourceException exception,
            HttpServletRequest httpRequest) {
        logAPIUrlInLogFile(httpRequest, exception);
        return new ProAPIErrorResponse(ResponseCodes.BAD_REQUEST, ResponseErrorMessages.DUPLICATE_NAME_RESOURCE);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    protected ProAPIResponse handleDuplicateResourceException(
            DuplicateResourceException exception,
            HttpServletRequest httpRequest) {
        logAPIUrlInLogFile(httpRequest, exception);
        return new ProAPIErrorResponse(ResponseCodes.BAD_REQUEST, ResponseErrorMessages.DUPLICATE_RESOURCE);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    protected ProAPIResponse handleConstraintVoilationException(
            ConstraintViolationException exception,
            HttpServletRequest httpRequest) {
        logAPIUrlInLogFile(httpRequest, exception);
        return new ProAPIErrorResponse(ResponseCodes.BAD_REQUEST, exception.getMessage() == null
                ? ResponseErrorMessages.REQUEST_PARAM_INVALID
                : exception.getMessage());
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    protected ProAPIResponse handleHttpMediaTypeNotSupportedException(
            HttpMediaTypeNotSupportedException exception,
            HttpServletRequest httpRequest) {
        logAPIUrlInLogFile(httpRequest, exception);
        return new ProAPIErrorResponse(ResponseCodes.BAD_REQUEST, ResponseErrorMessages.INVALID_CONTENT_TYPE);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    protected ProAPIResponse hanldeHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException exception,
            HttpServletRequest httpRequest) {
        logAPIUrlInLogFile(httpRequest, exception);
        return new ProAPIErrorResponse(
                ResponseCodes.BAD_REQUEST,
                ResponseErrorMessages.INVALID_REQUEST_METHOD_URL_AND_BODY);
    }

    @ExceptionHandler(ResourceAlreadyExistException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    protected ProAPIResponse handleResourceAlreadyExistException(
            ResourceAlreadyExistException exception,
            HttpServletRequest httpRequest) {
        logAPIUrlInLogFile(httpRequest, exception);
        return new ProAPIErrorResponse(exception.getResponseCode(), exception.getMessage(), exception.getData());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    protected ProAPIResponse handleHttpMessageNotReadableException(
            HttpMessageNotReadableException exception,
            HttpServletRequest httpRequest) {
        logAPIUrlInLogFile(httpRequest, exception);
        return new ProAPIErrorResponse(ResponseCodes.BAD_REQUEST, ResponseErrorMessages.INVALID_FORMAT_IN_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    protected ProAPIResponse handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception,
            HttpServletRequest httpRequest) {
        logAPIUrlInLogFile(httpRequest, exception);
        return new ProAPIErrorResponse(ResponseCodes.REQUEST_PARAM_INVALID, exception.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    protected ProAPIResponse handleResourceNotFoundException(
            ResourceNotFoundException exception,
            HttpServletRequest httpRequest) {
        logAPIUrlInLogFile(httpRequest, exception);
        return new ProAPIErrorResponse(ResponseCodes.REQUEST_PARAM_INVALID, exception.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    protected ProAPIResponse handleUnauthorizedException(UnauthorizedException exception, HttpServletRequest httpRequest) {
        logAPIUrlInLogFile(httpRequest, exception);
        return new ProAPIErrorResponse(ResponseCodes.UNAUTHORIZED, ResponseErrorMessages.UNAUTHORIZED);
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    protected ProAPIResponse handleBadRequestException(BadRequestException exception, HttpServletRequest httpRequest) {
        logAPIUrlInLogFile(httpRequest, exception);
        return new ProAPIErrorResponse(ResponseCodes.BAD_REQUEST, exception.getMessage() == null
                ? ResponseErrorMessages.BAD_REQUEST
                : exception.getMessage());
    }
}