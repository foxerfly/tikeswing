/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.error;

/**
 * This exception is thrown if mvc name set to a view component does not
 * match any field in view model. 
 * 
 * @author Tomi Tuomainen
 */
public class YInvalidMVCNameException extends YException {

	/**
	 * @param mvcName	the invalid mvc name
	 * @param className	the view class name
	 */
	public YInvalidMVCNameException(String mvcName, String className) {
		super("Invalid mvc name(s) in view " + className + ": " + mvcName);

	}
}
