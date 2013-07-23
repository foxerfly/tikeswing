/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.error;

/**
 * This exception is thrown if equalsModel of the view component
 * cannot be executed.
 * 
 * @author Tomi Tuomainen
 */
public class YEqualsModelException extends YException {

	/**
	 * 
	 */
	public YEqualsModelException(String message) {
		super(message);
	}

}
