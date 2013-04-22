package org.agius.lowtime.exception;

public class LowtimeSettingsNullException extends Exception{

	private static final long serialVersionUID = 1L;

	public LowtimeSettingsNullException() { 
		super(); 
	}
  
	public LowtimeSettingsNullException(String message) { 
		super(message); 
	}
	
	public LowtimeSettingsNullException(String message, Throwable cause) { 
		super(message, cause); 
	}
	
	public LowtimeSettingsNullException(Throwable cause) { 
		super(cause); 
	}
	  
}