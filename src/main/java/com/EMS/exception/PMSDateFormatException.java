/**
* @author  Jinu Shaji
* @version 1.0
* @since   2020-01-15 
*/


package com.EMS.exception;

public class PMSDateFormatException extends PMSException{

	public static final int defaultAppErrorCode = 12505;
	public static final String defaultMessage = "Invalid date format!";
	
	public String errorMessage;
	public int errorCode;
	
	public PMSDateFormatException() {
		setErrorCode(errorCode);
		setErrorMessage(defaultMessage);  
	}
	
	public PMSDateFormatException(int errorCode, String message) {
		setErrorCode(errorCode);
		setErrorMessage(message);  
	}
	
	public PMSDateFormatException(String message) {
		setErrorCode(defaultAppErrorCode);
		setErrorMessage(message);  
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

}
