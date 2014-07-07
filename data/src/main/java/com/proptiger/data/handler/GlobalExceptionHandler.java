package com.proptiger.data.handler;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.proptiger.app.config.security.ResponseErrorWriter;
import com.proptiger.data.constants.ResponseCodes;
import com.proptiger.data.constants.ResponseErrorMessages;
import com.proptiger.data.pojo.response.APIResponse;
import com.proptiger.exception.BadRequestException;
import com.proptiger.exception.ConstraintViolationException;
import com.proptiger.exception.DuplicateNameResourceException;
import com.proptiger.exception.DuplicateResourceException;
import com.proptiger.exception.InvalidResourceException;
import com.proptiger.exception.ProAPIException;
import com.proptiger.exception.ResourceAlreadyExistException;
import com.proptiger.exception.ResourceNotAvailableException;
import com.proptiger.exception.ResourceNotFoundException;
import com.proptiger.exception.UnauthorizedException;

/**
 * This class is a global exception handler, based on type of exception it
 * constructs the response and return that back to API caller.
 * 
 * @author Rajeev Pandey
 * 
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    protected APIResponse handleGenericException(Exception ex, HttpServletRequest httpRequest) {
        ResponseErrorWriter.logAPIUrlInLogFile(httpRequest, ex);
        return new APIResponse(ResponseCodes.INTERNAL_SERVER_ERROR, ResponseErrorMessages.SOME_ERROR_OCCURED);
    }

    @ExceptionHandler(ProAPIException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    protected APIResponse handleProAPIException(ProAPIException ex, HttpServletRequest httpRequest) {
        ResponseErrorWriter.logAPIUrlInLogFile(httpRequest, ex);

        return new APIResponse(ResponseCodes.INTERNAL_SERVER_ERROR, ResponseErrorMessages.SOME_ERROR_OCCURED);
    }

    @ExceptionHandler(ConversionNotSupportedException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    protected APIResponse handleConversionNotSupportedException(
            ConversionNotSupportedException ex,
            HttpServletRequest httpRequest) {
        ResponseErrorWriter.logAPIUrlInLogFile(httpRequest, ex);
        return new APIResponse(ResponseCodes.REQUEST_PARAM_INVALID, ResponseErrorMessages.REQUEST_PARAM_INVALID);

    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    protected APIResponse handleIllegalArgumentException(
            IllegalArgumentException exception,
            HttpServletRequest httpRequest) {
        ResponseErrorWriter.logAPIUrlInLogFile(httpRequest, exception);
        return new APIResponse(ResponseCodes.BAD_REQUEST, exception.getMessage() == null
                ? ResponseErrorMessages.REQUEST_PARAM_INVALID
                : exception.getMessage());
    }

    @ExceptionHandler(ResourceNotAvailableException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    protected APIResponse handleResourceNotAvailableException(
            ResourceNotAvailableException exception,
            HttpServletRequest httpRequest) {
        ResponseErrorWriter.logAPIUrlInLogFile(httpRequest, exception);
        return new APIResponse(ResponseCodes.BAD_REQUEST, exception.getMessage() == null
                ? ResponseErrorMessages.REQUEST_PARAM_INVALID
                : exception.getMessage());
    }

    @ExceptionHandler(InvalidResourceException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    protected APIResponse handleInvalidNameException(InvalidResourceException exception, HttpServletRequest httpRequest) {
        ResponseErrorWriter.logAPIUrlInLogFile(httpRequest, exception);
        return new APIResponse(ResponseCodes.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(DuplicateNameResourceException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    protected APIResponse handleDuplicateNameResourceException(
            DuplicateNameResourceException exception,
            HttpServletRequest httpRequest) {
        ResponseErrorWriter.logAPIUrlInLogFile(httpRequest, exception);
        return new APIResponse(ResponseCodes.BAD_REQUEST, ResponseErrorMessages.DUPLICATE_NAME_RESOURCE);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    protected APIResponse handleDuplicateResourceException(
            DuplicateResourceException exception,
            HttpServletRequest httpRequest) {
        ResponseErrorWriter.logAPIUrlInLogFile(httpRequest, exception);
        return new APIResponse(ResponseCodes.BAD_REQUEST, ResponseErrorMessages.DUPLICATE_RESOURCE);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    protected APIResponse handleConstraintVoilationException(
            ConstraintViolationException exception,
            HttpServletRequest httpRequest) {
        ResponseErrorWriter.logAPIUrlInLogFile(httpRequest, exception);
        return new APIResponse(ResponseCodes.BAD_REQUEST, exception.getMessage() == null
                ? ResponseErrorMessages.REQUEST_PARAM_INVALID
                : exception.getMessage());
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    protected APIResponse handleHttpMediaTypeNotSupportedException(
            HttpMediaTypeNotSupportedException exception,
            HttpServletRequest httpRequest) {
        ResponseErrorWriter.logAPIUrlInLogFile(httpRequest, exception);
        return new APIResponse(ResponseCodes.BAD_REQUEST, ResponseErrorMessages.INVALID_CONTENT_TYPE);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    protected APIResponse hanldeHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException exception,
            HttpServletRequest httpRequest) {
        ResponseErrorWriter.logAPIUrlInLogFile(httpRequest, exception);
        return new APIResponse(ResponseCodes.BAD_REQUEST, ResponseErrorMessages.INVALID_REQUEST_METHOD_URL_AND_BODY);
    }

    @ExceptionHandler(ResourceAlreadyExistException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    protected APIResponse handleResourceAlreadyExistException(
            ResourceAlreadyExistException exception,
            HttpServletRequest httpRequest) {
        ResponseErrorWriter.logAPIUrlInLogFile(httpRequest, exception);
        return new APIResponse(exception.getResponseCode(), exception.getMessage()).setData(exception.getData());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    protected APIResponse handleHttpMessageNotReadableException(
            HttpMessageNotReadableException exception,
            HttpServletRequest httpRequest) {
        ResponseErrorWriter.logAPIUrlInLogFile(httpRequest, exception);
        return new APIResponse(ResponseCodes.BAD_REQUEST, ResponseErrorMessages.INVALID_FORMAT_IN_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    protected APIResponse handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception,
            HttpServletRequest httpRequest) {
        ResponseErrorWriter.logAPIUrlInLogFile(httpRequest, exception);
        return new APIResponse(ResponseCodes.REQUEST_PARAM_INVALID, exception.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    protected APIResponse handleResourceNotFoundException(
            ResourceNotFoundException exception,
            HttpServletRequest httpRequest) {
        ResponseErrorWriter.logAPIUrlInLogFile(httpRequest, exception);
        return new APIResponse(ResponseCodes.RESOURCE_NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    protected APIResponse handleUnauthorizedException(UnauthorizedException exception, HttpServletRequest httpRequest) {
        ResponseErrorWriter.logAPIUrlInLogFile(httpRequest, exception);
        return new APIResponse(ResponseCodes.UNAUTHORIZED, ResponseErrorMessages.UNAUTHORIZED);
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    protected APIResponse handleBadRequestException(BadRequestException exception, HttpServletRequest httpRequest) {
        ResponseErrorWriter.logAPIUrlInLogFile(httpRequest, exception);
        return new APIResponse(exception.getResponseCode() != null
                ? exception.getResponseCode()
                : ResponseCodes.BAD_REQUEST, exception.getMessage() == null ? exception.getMessage() != null
                ? exception.getMessage()
                : ResponseErrorMessages.BAD_REQUEST : exception.getMessage());
    }

    @ExceptionHandler(BindException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    protected APIResponse handleBindException(BindException exception, HttpServletRequest httpRequest) {
        ResponseErrorWriter.logAPIUrlInLogFile(httpRequest, exception);
        return new APIResponse(ResponseCodes.BAD_REQUEST, exception.getMessage());
    }
}