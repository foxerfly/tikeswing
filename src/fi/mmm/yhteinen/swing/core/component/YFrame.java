/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.component;


import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import fi.mmm.yhteinen.swing.core.YController;
import fi.mmm.yhteinen.swing.core.YIComponent;
import fi.mmm.yhteinen.swing.core.YIControllerComponent;
import fi.mmm.yhteinen.swing.core.savechanges.YSaveChangesHandler;
import fi.mmm.yhteinen.swing.core.tools.YProperty;
import fi.mmm.yhteinen.swing.core.tools.YUIToolkit;

/**
 * The frame. 
 * <p>
 * Controller event methods, where yyy is component's name:
 * 
 * <code>public void yyyOpened()</code> executed when the frame is opened
 * <code>public void yyyClosing()</code> executed when the frame is closing
 * <p>
 * Component checks unsaved changes before invoking yyyClosing event,
 * if component property YIComponent.CHECK_CHANGES has
 * Boolean value true.
 * <p>
 * Default close operation is JFrame.DO_NOTHING_ON_CLOSE which
 * means closing should be implemented in closing event method.
 *
 * @author Tomi Tuomainen
 */
public class YFrame extends JFrame implements YIControllerComponent {

    
    public YFrame() {
        super();
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }

    public YFrame(String title)  {
        super(title);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);        
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
        this.addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent e) {
            	String methodName = YUIToolkit.createMVCMethodName(YFrame.this, "Opened");
            	if (methodName != null) {
            		controller.invokeMethodIfFound(methodName);
            	}
            }
            public void windowClosing(WindowEvent e) {
            	// checking unsaved changes before closing...
            	if (YSaveChangesHandler.changesSaved(YFrame.this)) {
            		String methodName = YUIToolkit.createMVCMethodName(YFrame.this, "Closing");
            		if (methodName != null) {
            			controller.invokeMethodIfFound(methodName);
            		}
            	}   
            }
            /**
             * Invoked when a window is activated.
             */
            public void windowActivated(WindowEvent e) {
            	YUIToolkit.setCurrentWindow(YFrame.this);
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
