/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.component;

import javax.swing.border.TitledBorder;

import fi.mmm.yhteinen.swing.core.YIComponent;
import fi.mmm.yhteinen.swing.core.tools.YProperty;

/**
 * The titled border. No controller event methods (so far).
 * 
 * @author Tomi Tuomainen
 */
public class YTitledBorder extends TitledBorder implements YIComponent {

	public YTitledBorder() {
		super("");
	}
	/**
	 * @param title the title text for this border
	 */
	public YTitledBorder(String title) {
		super(title);
	}
	
	private YProperty myProperty = new YProperty();
	
	/*
	 *  (non-Javadoc)
	 * @see fi.mmm.yhteinen.swing.core.YIComponent#getYProperty()
	 */
	public YProperty getYProperty() {
	 	return myProperty;
	}
    
    /**
     * This is a convenience method for setting MVC-name.
     * 
     * @param mvcName   the YIComponent.MVC_NAME value
     */
    public void setMvcName (String mvcName) {
        getYProperty().put(YIComponent.MVC_NAME, mvcName);
    }
	

}
