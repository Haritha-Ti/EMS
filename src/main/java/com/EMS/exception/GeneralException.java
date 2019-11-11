package com.EMS.exception;

public class GeneralException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	private String message;
	private int code;

	public GeneralException(String message) {
		super();
		this.message = message;
	}

	public GeneralException(String message, int code) {
		super();
		this.message = message;
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
}
