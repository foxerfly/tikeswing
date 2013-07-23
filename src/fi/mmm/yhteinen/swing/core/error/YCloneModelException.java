/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.error;

/**
  * This exception is thrown if cloneModel of the view component
  * cannot be executed.
  * 
  * @author Tomi Tuomainen
  */
public class YCloneModelException extends YException {

	/**
	 * @param message the error message
	 */
	public YCloneModelException(String message) {
		super(message);
	}

}
