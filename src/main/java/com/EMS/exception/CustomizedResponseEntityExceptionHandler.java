/**
 * 
 */
package com.EMS.exception;

import java.io.IOException;
import java.net.ConnectException;
import java.text.ParseException;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.EMS.dto.StatusResponse;

/**
 * 
 * @author Muhammed Aboobacker
 *
 */
@ControllerAdvice
@RestController
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	private final Logger log = LoggerFactory.getLogger(CustomizedResponseEntityExceptionHandler.class);

	/**
	 * 
	 * @param ex
	 * @param request
	 * @return
	 */
	
	
	@SuppressWarnings("rawtypes")
	@ExceptionHandler(GeneralException.class)
	public final ResponseEntity<StatusResponse> handleGenericNotFoundException(
			GeneralException ex, WebRequest request) {
		log.error("Exception: {}", ex);
		StatusResponse exceptionResponse = new StatusResponse<>(500, ex.getCode(), "Failed", ex.getLocalizedMessage());
		return new ResponseEntity<>(exceptionResponse, HttpStatus.OK);
	}
	
	
}
