/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.component.buttongroup;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JRadioButton;

import fi.mmm.yhteinen.swing.core.YController;
import fi.mmm.yhteinen.swing.core.YIComponent;
import fi.mmm.yhteinen.swing.core.YIControllerComponent;
import fi.mmm.yhteinen.swing.core.tools.YProperty;
import fi.mmm.yhteinen.swing.core.tools.YUIToolkit;

/**
 * The radio button. Radio button is linked to view model via YButtonGroup's 
 * MVC-name. View model values are set to YRadioButton with setSelectionId method.
 * <p>
 * Controller event methods, where yyy is component's name:
 * 
 * <code>public void yyySelectionChanged()</code> executed radio button selection is changed
 * 
 * @author Tomi Tuomainen
 */
public class YRadioButton extends JRadioButton implements YIControllerComponent {
	
	private Object selectionId;
	
	/**
	 * 
	 */
	public YRadioButton() {
		super();
	}

	/**
	 * @param text
	 */
	public YRadioButton(String text) {
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
	
	/**
	 * @return 	the selection id of this radio button
	 */
	public Object getSelectionId() {
		return selectionId;
	}
	/**
	 * @param selectionId the selection id of this radio button
	 */
	public void setSelectionId(Object selectionId) {
		this.selectionId = selectionId;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see fi.mmm.yhteinen.swing.core.YIControllerComponent#addViewListener(fi.mmm.yhteinen.swing.core.YController)
	 */
	public void addViewListener(final YController controller) {
	    this.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	            String methodName = YUIToolkit.createMVCMethodName(YRadioButton.this, "SelectionChanged");
	            if (methodName != null) {
	                Boolean selected = new Boolean(YRadioButton.this.isSelected());
	                controller.invokeMethodIfFound(methodName, new Object[] {selected}, new Class[] {selected.getClass()});
	            }
	        }        
	    });
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





