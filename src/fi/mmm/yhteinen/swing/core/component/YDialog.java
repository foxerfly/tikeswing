/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.component;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Constructor;

import javax.swing.JDialog;
import javax.swing.JFrame;

import fi.mmm.yhteinen.swing.core.YController;
import fi.mmm.yhteinen.swing.core.YIComponent;
import fi.mmm.yhteinen.swing.core.YIControllerComponent;
import fi.mmm.yhteinen.swing.core.error.YException;
import fi.mmm.yhteinen.swing.core.savechanges.YSaveChangesHandler;
import fi.mmm.yhteinen.swing.core.tools.YProperty;
import fi.mmm.yhteinen.swing.core.tools.YUIToolkit;

/**
 * The dialog. 
 * <p>
 * Controller event methods, where yyy is component's name:
 * 
 * <code>public void yyyOpened()</code> executed when the dialog is opened
 * <code>public void yyyClosing()</code> executed when the dialog is closing
 * <p>
 * Component checks unsaved changes before invoking yyyClosing event,
 * if component property YIComponent.CHECK_CHANGES has
 * Boolean value true.
 *<p>
 * Default close operation is JDialog.DO_NOTHING_ON_CLOSE which
 * means closing should be implemented in closing event method.
 * 
 * @author Tomi Tuomainen
 */
public class YDialog extends JDialog implements YIControllerComponent {

	/**
	 * Convenience factory method for creating a new dialog.
	 * Uses YUIToolkit.getCurrentWindow() to find out currently
	 * open Window (which will be the parent of created dialog).
	 * 
	 * The given dialog class should have constructors for both 
	 * Dialog ja Frame classes. 
	 * 
	 * @param dialogClass	the class of the dialog to be instantiated
	 * @return				the new dialog, or null if dialog could not be instantiated
	
	 */
	public static YDialog createDialog(Class dialogClass) {
		try {
			Window owner = YUIToolkit.getCurrentWindow();
			if (owner == null) {
				return (YDialog) dialogClass.newInstance();
			} else {
				Class [] parameters = null;
				if (owner instanceof Dialog) {
					parameters = new Class[] {Dialog.class};
				} else if (owner instanceof Frame) {
					parameters = new Class[] {Frame.class};
				}	 
				Constructor constructor = dialogClass.getConstructor(parameters);
				return (YDialog) constructor.newInstance(new Object[] {owner});
			}
		} catch (Exception ex) {
			throw new YException(ex);
			
		}
	}
    
	/**
	 * 
	 */
	public YDialog()  {
		super();
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
	}
	
	/**
	 * @param owner the parent dialog of this dialog
	 */
	public YDialog(Dialog owner)  {
		super(owner);
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
	}
	
	
    /**
     * @param owner 	the parent frame of this dialog
     */
    public YDialog(Frame owner) {
        super(owner);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }

    /**
     * @param owner 	the parent frame of this dialog
     * @param title		the title for this dialog
     */
    public YDialog(Frame owner, String title)  {
        super(owner, title);
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
            	String methodName = YUIToolkit.createMVCMethodName(YDialog.this, "Opened");
            	if (methodName != null) {
            		controller.invokeMethodIfFound(methodName);
            	}
            }
            public void windowClosing(WindowEvent e) {
            	// checking unsaved changes before closing...
            	if (YSaveChangesHandler.changesSaved(YDialog.this)) {
            		String methodName = YUIToolkit.createMVCMethodName(YDialog.this, "Closing");
            		if (methodName != null) {
            			controller.invokeMethodIfFound(methodName);
            		}
            	}
            }
            
            public void windowActivated(WindowEvent e) {
            	YUIToolkit.setCurrentWindow(YDialog.this);
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
