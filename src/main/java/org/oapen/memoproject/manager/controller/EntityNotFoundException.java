package org.oapen.memoproject.manager.controller;

public class EntityNotFoundException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;
	private final String id;

	public EntityNotFoundException(String id) {
		this.id = id;
	}
	 
	public String getId() {
		return id;
	}
	
	
}
