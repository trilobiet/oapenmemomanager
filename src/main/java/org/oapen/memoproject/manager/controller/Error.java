package org.oapen.memoproject.manager.controller;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Error {
	
	private final int code;
	private final String message;
	
	public Error(int code, String message) {
		super();
		this.code = code;
		this.message = message;
	}

	@JsonProperty("code")
	public int getCode() {
		return code;
	}

	@JsonProperty("error")
	public String getMessage() {
		return message;
	}

}
