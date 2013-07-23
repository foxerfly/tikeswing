/*
 * Created on 21.12.2004
 *
 */
/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.error;

/**
 * The exception base class of the framework.
 * Framework internal errors are often YExceptions.
 * 
 * 
 * @author Tomi Tuomainen
 *
 */
public class YException extends RuntimeException {

	
	
	public YException() {
	}
	
	/**
	 * 
	 * @param msg the exception message
	 */
    public YException (String msg) {
        super(msg);
    }
    
    /**
	 * @param cause the cause of the exception
	 */
	public YException(Throwable cause) {
		super(cause);
	}
	
}
