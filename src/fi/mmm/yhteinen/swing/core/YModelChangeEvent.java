/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core;

/**
 * Used for notifying other controllers about model changes.
 * 
 * @author Tomi Tuomainen
 * @see YController#update(Observable, Object)
 */
public class YModelChangeEvent {

	private String fieldName;
	private boolean userChange;
	
	/**
	 * 
	 */
	public YModelChangeEvent() {
		super();
	}
	
	/**
	 * @return mvc-name of the changed field
	 */
	public String getFieldName() {
		return fieldName;
	}
	/**
	 * @param fieldName mvc-name of the changed field
	 */
	void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	/**
	 * @return if change occurred because of user action
	 */
	public boolean isUserChange() {
		return userChange;
	}
	/**
	 * @param userChange if change occurred because of user action
	 */
	void setUserChange(boolean userChange) {
		this.userChange = userChange;
	}
}
