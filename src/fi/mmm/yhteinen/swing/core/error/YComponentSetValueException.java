/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.error;

import fi.mmm.yhteinen.swing.core.YIComponent;
import fi.mmm.yhteinen.swing.core.YIModelComponent;

/**
 * This exception is thrown if a view model value for some reason
 * cannot be set to a component. 
 * 
 * The <code>originalException</code> contains the exception
 * that is thrown by <code>YIModelComponent</code> in the
 * <code>setModelValue</code> method.
 * 
 * @author Tomi Tuomainen
 */
public class YComponentSetValueException extends YComponentModelCopyException {

	/**
	 * @param originalException	the exception that is thrown by a component
	 * @param component			the component throwing the exception
	 * @param value				the value that cannot be set
	 */
	public YComponentSetValueException(Throwable originalException,
			YIModelComponent component, Object value) {
		super("Error in copying value into a component:" +
				" value=" + value + 
				" MVC_NAME=" + component.getYProperty().get(YIComponent.MVC_NAME) +
				" component=" + component);
		this.setOriginalException(originalException);
		this.setValue(value);
		this.setComponent(component);
	}


}
