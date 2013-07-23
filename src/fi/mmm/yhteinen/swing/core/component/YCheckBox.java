/*
 * Created on Jan 7, 2005
 *
 */
/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.component;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;

import fi.mmm.yhteinen.swing.core.YController;
import fi.mmm.yhteinen.swing.core.YIComponent;
import fi.mmm.yhteinen.swing.core.YIModelComponent;
import fi.mmm.yhteinen.swing.core.error.YException;
import fi.mmm.yhteinen.swing.core.tools.YProperty;

/**
 * The check box. Field in connected view model must be Boolean.
 * 
 * @author Tomi Tuomainen
 *
 */
public class YCheckBox extends JCheckBox implements YIModelComponent {
	
    public YCheckBox() {
        super();
    }
    
    public YCheckBox(String text) {
        super(text);
    }

	private YProperty myProperty = new YProperty();
	
	/*
	 *  (non-Javadoc)
	 * @see fi.mmm.yhteinen.swing.core.YIComponent#getYProperty()
	 */
	public YProperty getYProperty() {
	 	return myProperty;
	}
	
	/* (non-Javadoc)
	 * @see fi.mmm.yhteinen.swing.component.YIControllerComponent#addViewListener(fi.mmm.yhteinen.swing.YController, fi.mmm.yhteinen.swing.YModel)
	 */
	public void addViewListener(final YController controller) {
		this.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent ev) {
            	controller.updateModelAndController(YCheckBox.this);
            }
        });
	}

	/**
	 * Gets value of this field for view model.
	 * 
	 * @return Boolean with true if this is selected; 
	 * 			otherwise Boolean with false is returned
	 */
	public Object getModelValue() {
		return new Boolean(this.isSelected());
	}

	/**
	 * Sets view model value into this field. 
	 * 
	 * @param obj the Boolean object which indicates if this 
	 * 			  should be selected
	 */
	public void setModelValue(Object obj) throws YException {
	    if (obj != null) {
	        if (!(obj instanceof Boolean)) {
	            throw new YException(
	            "Check box model value must be Boolean.");
	        } else {
	            this.setSelected(((Boolean)obj).booleanValue());
	        }
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
