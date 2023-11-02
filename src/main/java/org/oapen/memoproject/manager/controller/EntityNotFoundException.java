package org.oapen.memoproject.manager.controller;

public class EntityNotFoundException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;
	private final String message;

	public EntityNotFoundException(String message) {
		this.message = message;
	}
	 
	public String getMessage() {
		return message;
	}
	
	
}
