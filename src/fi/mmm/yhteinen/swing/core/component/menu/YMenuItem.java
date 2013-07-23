/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.component.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import fi.mmm.yhteinen.swing.core.YController;
import fi.mmm.yhteinen.swing.core.YIComponent;
import fi.mmm.yhteinen.swing.core.YIControllerComponent;
import fi.mmm.yhteinen.swing.core.savechanges.YSaveChangesHandler;
import fi.mmm.yhteinen.swing.core.tools.YProperty;
import fi.mmm.yhteinen.swing.core.tools.YUIToolkit;

/**
 * The menu item. 
 * <p>
 * Controller event methods, where yyy is component's name:
 * 
 * <code>public void yyySelected()</code> executed when user clicks menu
 * <p>
 * Component checks unsaved changes before invoking yyySelected event,
 * if component property YIComponent.CHECK_CHANGES has
 * Boolean value true.
 * 
 * @author Tomi Tuomainen
 */
public class YMenuItem extends JMenuItem implements YIControllerComponent {

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
            	if (YSaveChangesHandler.changesSavedInCurrentWindow(YMenuItem.this)) {
            		String methodName = YUIToolkit.createMVCMethodName(YMenuItem.this, "Selected");
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
