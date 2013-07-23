/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.component.label;


import javax.swing.JLabel;

import fi.mmm.yhteinen.swing.core.YController;
import fi.mmm.yhteinen.swing.core.YIComponent;
import fi.mmm.yhteinen.swing.core.YIModelComponent;
import fi.mmm.yhteinen.swing.core.tools.YProperty;

/**
 * The label that shows view model value with default String presentation.
 * (toString()).
 * 
 * @author Tomi Tuomainen
 */
public class YLabel extends JLabel implements YIModelComponent {

	private YProperty myProperty = new YProperty();
	
	public YLabel() {
		super();
	}
	
	/**
	 * @param text the label text
	 */
	public YLabel(String text) {
		super(text);

	}
	/*
	 *  (non-Javadoc)
	 * @see fi.mmm.yhteinen.swing.core.YIComponent#getYProperty()
	 */
	public YProperty getYProperty() {
	 	return myProperty;
	}
	
	 /* (non-Javadoc)
     * @see fi.mmm.yhteinen.swing.component.YIComponent#addViewListener(fi.mmm.yhteinen.swing.YController)
     */
    public void addViewListener(final YController controller) {
    }
    
    /**
	 * Sets view model value into this label. 
	 * 
	 * If obj is null, an empty String is set. 
	 * 
	 * @param obj the object which String presentation is showed 
	 */
	public void setModelValue(Object obj) {
        if (obj == null) {
        	setText("");
        } else {
        	setText(obj.toString());
        }
    }
    
	/**
	 * Gets value of this label for view model.
	 * 
	 * If text in this label is an empty String, null is returned.
	 * @return the text in this label
	 */
    public Object getModelValue() {
    	String text = getText();
        if (text == null || text.trim().equals("")) {
            return null;
        } else {
            return text;
        }
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
