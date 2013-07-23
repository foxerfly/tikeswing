/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.error;

import fi.mmm.yhteinen.swing.core.YIComponent;
import fi.mmm.yhteinen.swing.core.YIModelComponent;

/**
 * This exception is thrown if a value cannot be copied 
 * from the view model. 
 * 
 * The <code>originalException</code> contains the exception
 * that occurred when the framework tried to copy model value to
 * a component. Usually this is IllegalAccessException, 
 * InvocationTargetException or NoSuchMethodException (thrown
 * by Java reflection mechanism). An exception thrown by a model 
 * setter method can be solved via 
 * <code>InvocationTargetException.getCause()</code>
 * method.
 * 
 * @author Tomi Tuomainen
 */
public class YModelGetValueException extends YComponentModelCopyException {

	
	/**
	 * @param originalException	the exception that occurred when trying to get value from the model
	 * @param component			the component involved in copying
	 */
	public YModelGetValueException(Throwable originalException,
			YIComponent component) {
		super("Error in getting value from a model:" +
				" MVC_NAME=" + component.getYProperty().get(YIComponent.MVC_NAME) +
				" component=" + component);
		this.setOriginalException(originalException);
		this.setComponent(component);
	}

}
