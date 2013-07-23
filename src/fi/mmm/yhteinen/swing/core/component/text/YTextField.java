
/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.component.text;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JTextField;
import fi.mmm.yhteinen.swing.core.YController;
import fi.mmm.yhteinen.swing.core.YIComponent;
import fi.mmm.yhteinen.swing.core.YIModelComponent;
import fi.mmm.yhteinen.swing.core.tools.YProperty;
import fi.mmm.yhteinen.swing.core.tools.YUIToolkit;

/**
 * The text field. Field in connected view model must be String.
 * <p>
 * Controller event methods, where yyy is component's name:
 * 
 * <code>public void yyyKeyReleased()</code> executed when 
 * a key is released, key events must be enabled (isKeyEventsEnabled())
 * 
 * @author Tomi Tuomainen
 */
public class YTextField extends JTextField implements YIModelComponent,
	YITextComponent {
    
    private boolean keyEventsEnabled = false;
	
	private static boolean staticUpper = false;
	private Boolean thisUpper; 
	
	private Integer maxLength;
	
	public YTextField() {
		this.setDocument(new YTextDocument(this));
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
	 * Sets all instances of this class upper case. 
	 * 
	 * @param staticUpper if true, all the characters will be converted
	 * 					  to upper case
	 */
	public static void setStaticUpper(boolean staticUpper) {
		YTextField.staticUpper = staticUpper;
	}
	
	/**
	 * Sets upper case for this field. Overrides static upper setting.
	 * 
	 * @param upper if true, all the characters will be converted
	 * 					  to upper case
	 */
	public void setUpper(boolean upper) {
		thisUpper = new Boolean(upper);
	}
	
	/**
	 * @return is this instance upper case, default value is false
	 */
	public boolean isUpper() {
		if (thisUpper != null) {
			return thisUpper.booleanValue();
		} else {
			return staticUpper;
		}
	}
	
	/**
	 * @return the maximum length of this field. Default value is null,
	 * no limit is set.
	 */
	public int getMaxLength() {
		if (maxLength == null) {
			return -1;
		} else {
			return maxLength.intValue();
		}
	}
	/**
	 * Sets maximum length for user input. 
	 * 
	 * @param maxLength the maximum length for this field
	 */
	public void setMaxLength(int maxLength) {
		this.maxLength = new Integer(maxLength);
	}
	
    /**
     * Defines if key events will be delegated to controller object.
     * Default value is false.
     * 
     * @return if key controller events are enabled
     */
    public boolean isKeyEventsEnabled() {
        return keyEventsEnabled;
    }
    /**
     * @param keyEventsEnabled The keyEventsEnabled to set.
     */
    public void setKeyEventsEnabled(boolean keyEventsEnabled) {
        this.keyEventsEnabled = keyEventsEnabled;
    }
	/**
	 * Gets value of this field for view model.
	 * 
	 * If text in this field is an empty String, null is returned.
	 * @return the text in this field
	 */
	public Object getModelValue() {
		String text = getText();
        if (text.trim().equals("")) {
            return null;
        } else {
            return text;
        }
	}

	/**
	 * Sets view model value into this field. 
	 * 
	 * If obj is null, an empty String is set. 
	 * 
	 * @param obj the object which String presentation is showed in this field
	 */
	public void setModelValue(Object obj) {
        if (obj == null) {
            this.setText("");
        } else {
            this.setText(obj.toString());
        }
    }
	

	
	
	/*
	 *  (non-Javadoc)
	 * @see fi.mmm.yhteinen.swing.core.YIControllerComponent#addViewListener(fi.mmm.yhteinen.swing.core.YController)
	 */
	public void addViewListener(final YController controller) {
        this.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent ev) {
            	controller.updateModelAndController(YTextField.this);
            }
        });
 		this.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (keyEventsEnabled) {
					String methodName = YUIToolkit.createMVCMethodName(YTextField.this, "KeyReleased");
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
