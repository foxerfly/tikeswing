/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core;

/**
 * Interface for components that validate values. 
 * <p>
 * YIValidatorComponent implementation should call YController componentValidationFailed and
 * componentValidationSucceeded methods.
 * <p>
 * @see YController#getInvalidComponents
 * @author Tomi Tuomainen
 */
public interface YIValidatorComponent {
	
	/**
	 * @return true if value is valid, otherwise false
	 */
	public boolean valueValid();

}

