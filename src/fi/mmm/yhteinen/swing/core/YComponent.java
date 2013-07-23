/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core;

import fi.mmm.yhteinen.swing.core.tools.YProperty;

/**
 * A class implementing YIComponent. Classes which
 * are not extending other classes and need to use
 * YProperty, may extends this class. 
 * 
 * @author Tomi Tuomainen
 */
public class YComponent implements YIComponent {

	private YProperty myProperty = new YProperty();
	
	/*
	 *  (non-Javadoc)
	 * @see fi.mmm.yhteinen.swing.core.YIComponent#getYProperty()
	 */
	public YProperty getYProperty() {
	 	return myProperty;
	}

}
