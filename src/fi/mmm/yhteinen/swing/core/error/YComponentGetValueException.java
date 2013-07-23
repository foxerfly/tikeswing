/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.error;

import fi.mmm.yhteinen.swing.core.YIComponent;
import fi.mmm.yhteinen.swing.core.YIModelComponent;

/**
 * This exception is thrown if a value for some reason
 * cannot be get from a component. (maybe user has
 * given an invalid value to the component)
 * 
 * The <code>originalException</code> contains the exception
 * that is thrown by <code>YIModelComponent</code> in the
 * <code>getModelValue</code> method.
 * 
 * @author Tomi Tuomainen
 */
public class YComponentGetValueException extends YComponentModelCopyException {

	/**
	 * @param originalException	the exception that is thrown by a component
	 * @param component			the component throwing the exception
	 */
	public YComponentGetValueException(Throwable originalException,
			YIComponent component) {
		super("Error in getting value from a component:" +
				" MVC_NAME=" + component.getYProperty().get(YIComponent.MVC_NAME) +
				" component=" + component);
		this.setOriginalException(originalException);
		this.setComponent(component);
	}


}
