/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.component;

import java.awt.Component;

import javax.swing.JScrollPane;

import fi.mmm.yhteinen.swing.core.YIComponent;
import fi.mmm.yhteinen.swing.core.tools.YProperty;

/**
 * The scroll pane. No extended TikeSwing functionality so far.
 * 
 * @author Tomi Tuomainen
 */
public class YScrollPane extends JScrollPane implements YIComponent {

	private YProperty myProperty = new YProperty();

	/*
	 *  (non-Javadoc)
	 * @see fi.mmm.yhteinen.swing.core.YIComponent#getYProperty()
	 */
	public YProperty getYProperty() {
	 	return myProperty;
	}


	public YScrollPane() {
		super();
	}
	
	/**
	 * @param comp the component that is showed in this scroll pane
	 */
	public YScrollPane(Component comp) {
		super(comp);
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
