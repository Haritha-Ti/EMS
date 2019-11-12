package com.EMS.dto;

/**
 * 
 * DTO for StatusReponse
 *
 */
public class StatusResponse<T> {
	
	private int code;
	
	private int internalCode;
	
	private String status;
	
	private String message;
	
	

	
	
	public StatusResponse() {
		super();
	}
	
	public StatusResponse(int code, String status) {
		super();
		this.code = code;
		this.status = status;
	}

	public StatusResponse(int code, String status, String message) {
		super();
		this.code = code;
		this.status = status;
		this.message = message;
	}

	

	public StatusResponse(int code, int  internalCode, String status, String message) {
		super();
		this.code = code;
		this.status = status;
		this.message = message;
		this.internalCode=internalCode;
	}

	
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	

	public int getErrcode() {
		return internalCode;
	}

	public void setErrcode(int errcode) {
		this.internalCode = errcode;
	}
	
}
