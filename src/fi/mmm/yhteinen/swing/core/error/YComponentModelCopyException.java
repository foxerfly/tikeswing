package fi.mmm.yhteinen.swing.core.error;

import fi.mmm.yhteinen.swing.core.YIComponent;
import fi.mmm.yhteinen.swing.core.YIModelComponent;

/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */

/**
 * Abstract class for the framework copy exceptions.
 * 
 * @author Tomi Tuomainen
 */
public abstract class YComponentModelCopyException extends YException {

	private Throwable originalException;
	private YIComponent component;
	private Object value;
	
	/**
	 * @param msg	the error message
	 */
	public YComponentModelCopyException(String msg) {
		super(msg);

	}
	/**
	 * @return the original exception that occurred when the framework 
	 * 		   was trying to copy data between a view component and a model field
	 */
	public Throwable getOriginalException() {
		return originalException;
	}
	/**
	 * @param originalException the original exception that occurred when the framework 
	 * 		   was trying to copy data between a view component and a model field
	 */
	public void setOriginalException(Throwable originalException) {
		this.originalException = originalException;
	}

	/**
	 * @return the view component involved in copying
	 */
	public YIComponent getComponent() {
		return component;
	}
	
	/**
	 * @param component the view component involved in copying
	 */
	public void setComponent(YIComponent component) {
		this.component = component;
	}
	
	/**
	 * @return the value involved in copying
	 */
	public Object getValue() {
		return value;
	}
	
	/**
	 * @param value the value involved in copying
	 */
	public void setValue(Object value) {
		this.value = value;
	}

}
