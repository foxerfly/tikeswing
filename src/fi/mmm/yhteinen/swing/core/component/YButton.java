
/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import fi.mmm.yhteinen.swing.core.YController;
import fi.mmm.yhteinen.swing.core.YIComponent;
import fi.mmm.yhteinen.swing.core.YIControllerComponent;
import fi.mmm.yhteinen.swing.core.savechanges.YSaveChangesHandler;
import fi.mmm.yhteinen.swing.core.tools.YProperty;
import fi.mmm.yhteinen.swing.core.tools.YUIToolkit;


/**
 * The button. 
 * <p>
 * Controller event methods, where yyy is component's name:
 * 
 * <code>public void yyyPressed()</code> executed when the button is pressed
 * <p>
 * Component checks unsaved changes before invoking yyyPressed event,
 * if component property YIComponent.CHECK_CHANGES has
 * Boolean value true.
 *
 * @author Tomi Tuomainen
 *
 */
public class YButton extends JButton implements YIControllerComponent {
      
    
	public YButton() {
		super();
	}

	public YButton(String text) {
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
	 * @see fi.mmm.yhteinen.swing.component.YIComponent#addViewListener(fi.mmm.yhteinen.swing.YController, fi.mmm.yhteinen.swing.YModel)
	 */
	public void addViewListener(final YController controller) {
       	this.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
//            	 checking unsaved changes before invoking event...
            	if (YSaveChangesHandler.changesSavedInCurrentWindow(YButton.this)) {
            		String methodName = YUIToolkit.createMVCMethodName(YButton.this, "Pressed");
            		if (methodName != null) {
            			controller.invokeMethodIfFound(methodName);
            		}
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
