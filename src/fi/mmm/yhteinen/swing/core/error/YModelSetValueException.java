/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.error;


import fi.mmm.yhteinen.swing.core.YIComponent;
import fi.mmm.yhteinen.swing.core.YIModelComponent;

/**
 * This exception is thrown if a value cannot be copied 
 * to the view model. 
 * 
 * The <code>originalException</code> contains the exception
 * that occurred when the framework tried to copy component value
 * to the model. Usually this is IllegalAccessException, 
 * InvocationTargetException or NoSuchMethodException (thrown
 * by Java reflection mechanism). An exception throw by a model 
 * getter method can be solved via 
 * <code>InvocationTargetException.getCause()</code>
 * method.
 * 
 * @author Tomi Tuomainen
 */
public class YModelSetValueException extends YComponentModelCopyException {

	/**
	 * @param originalException	the exception that occurred when trying to set value to the model
	 * @param component			the component involved in copying
	 * @param value				the value that couldn't be set
	 */
	public YModelSetValueException(Throwable originalException,
			YIComponent component, Object value) {
		super("Error in copying value into the model:" +
				" value= " + value + 
				" MVC_NAME= " + component.getYProperty().get(YIComponent.MVC_NAME) +
				" component= " + component);
		this.setOriginalException(originalException);
		this.setValue(value);
		this.setComponent(component);
	}
	
}
