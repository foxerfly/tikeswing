/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.tools;

import java.util.HashMap;

import fi.mmm.yhteinen.swing.core.YIComponent;

/**
 * Class for storing YIComponent properties. 
 * 
 * @see YIComponent
 * @author Tomi Tuomainen
 */
public class YProperty extends HashMap {
	
	public YProperty() {
		
	}
	
	/**
	 * @param name  the name of the property
	 * @param value	the value to be stored
	 */
	public YProperty (String name, Object value) {
		put(name, value);
	}
	
	
}
