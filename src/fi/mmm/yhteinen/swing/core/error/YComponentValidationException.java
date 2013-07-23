/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.error;

import fi.mmm.yhteinen.swing.core.YIValidatorComponent;

/**
 * This exception should be thrown by a component, if data in 
 * it's field is considered to be invalid and cannot be copied
 * to a view model.
 * 
 * @author Tomi Tuomainen
 */
public class YComponentValidationException extends YException {

	private Object invalidValue;
	private YIValidatorComponent component;

	/**
	 * @param message		the error message
	 * @param component		the component 
	 * @param invalidValue	the invalid value that was rejected by the component
	 */
	public YComponentValidationException(String message, YIValidatorComponent component, 
			Object invalidValue) {
		super(message);
		this.component = component;
		this.invalidValue = invalidValue;
	}

	
	public YIValidatorComponent getComponent() {
		return component;
	}

	public void setComponent(YIValidatorComponent component) {
		this.component = component;
	}
	
	public Object getInvalidValue() {
		return invalidValue;
	}
	
	public void setInvalidValue(Object invalidValue) {
		this.invalidValue = invalidValue;
	}
}
