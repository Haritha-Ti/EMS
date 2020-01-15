
package com.EMS.exception;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.LinkedHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import com.EMS.dto.StatusResponse;



@ControllerAdvice
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	@SuppressWarnings("rawtypes")
	@ExceptionHandler(PMSException.class)
	public final ResponseEntity<LinkedHashMap> handlePMSException(PMSException ex, WebRequest request) {

		LinkedHashMap responseEntity = new LinkedHashMap();
		StringWriter errors = new StringWriter();
		ex.printStackTrace(new PrintWriter(errors));	
		
		responseEntity=getResponseEntity(request, HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
				ex.getErrorMessage(),ex.getErrorCode(), errors.toString());
		
		return new ResponseEntity<LinkedHashMap>(responseEntity,HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@SuppressWarnings("rawtypes")
	@ExceptionHandler(PMSNotFoundException.class)
	public final ResponseEntity<LinkedHashMap> handlePMSNotFoundException(PMSNotFoundException ex, WebRequest request) {

		LinkedHashMap responseEntity = new LinkedHashMap();
		StringWriter errors = new StringWriter();
		ex.printStackTrace(new PrintWriter(errors));	
		
		responseEntity=getResponseEntity(request, HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(),
				ex.getErrorMessage(),ex.getErrorCode(), errors.toString());
		
		return new ResponseEntity<LinkedHashMap>(responseEntity,HttpStatus.NOT_FOUND);
	}
	
	@SuppressWarnings("rawtypes")
	@ExceptionHandler(PMSDateFormatException.class)
	public final ResponseEntity<LinkedHashMap> handlePMSDateFormatException(PMSDateFormatException ex, WebRequest request) {

		LinkedHashMap responseEntity = new LinkedHashMap();
		StringWriter errors = new StringWriter();
		ex.printStackTrace(new PrintWriter(errors));	
		
		responseEntity=getResponseEntity(request, HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
				ex.getErrorMessage(),ex.getErrorCode(), errors.toString());
		
		return new ResponseEntity<LinkedHashMap>(responseEntity,HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public LinkedHashMap getResponseEntity(WebRequest request,int status,String error,
			String message,int appErrorCode,String stacktrace) {

		LinkedHashMap responseEntity = new LinkedHashMap();
		responseEntity.put("timestamp", new Date());
		responseEntity.put("status", status);
		responseEntity.put("error", error);
		responseEntity.put("message", message);
		responseEntity.put("appErrorCode", appErrorCode);
		responseEntity.put("stacktrace", stacktrace);

		return responseEntity;
	}
	
	@SuppressWarnings("rawtypes")
	@ExceptionHandler(GeneralException.class)
	public final ResponseEntity<StatusResponse> handleGenericNotFoundException(
			GeneralException ex, WebRequest request) {
		StatusResponse exceptionResponse = new StatusResponse<>(500, ex.getCode(), "Failed", ex.getLocalizedMessage());
		return new ResponseEntity<>(exceptionResponse, HttpStatus.OK);
	}
	
	
}
