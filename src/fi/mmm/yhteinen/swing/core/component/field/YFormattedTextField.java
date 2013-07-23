/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.component.field;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.Format;
import java.text.ParseException;
import java.util.Iterator;

import javax.swing.JFormattedTextField;

import fi.mmm.yhteinen.swing.core.YController;
import fi.mmm.yhteinen.swing.core.YIComponent;
import fi.mmm.yhteinen.swing.core.YIModelComponent;
import fi.mmm.yhteinen.swing.core.YIValidatorComponent;
import fi.mmm.yhteinen.swing.core.error.YComponentValidationException;

import fi.mmm.yhteinen.swing.core.error.YException;
import fi.mmm.yhteinen.swing.core.tools.YProperty;
import fi.mmm.yhteinen.swing.core.tools.YUIToolkit;

/**
 * The text field. 
 * <p>
 * Formatter for this field should be set by calling constructor
 * <code>YFormattedTextField(AbstractFormatter formatter)</code>.
 * The default constructor (with no parameters) sets YTextFormatter
 * for this field. When YTextFormatter is used, maximum length 
 * and upper case settings are available (see setMaxLength and 
 * setThisUpper methods). 
 * <p>
 * Formatters of this package should be used, if this field
 * is connected to other than String field in view model. For example,
 * if view model has Double field, the corresponding field
 * is set up with constructor
 * <code>new YFormattedTextField(new YDoubleFormatter())</code>.
 * <p>
 * The default input verifier used is YFormattedTextFieldVerifier.
 * The default verifier keeps the focus in the field until
 * valid value is given. The invalid value is marked with
 * red bold Font (see setInvalidLayout method). Verifier can
 * be removed or changed by calling setInputVerifier method.
 * <p>
 * Although the field is designed to be used with framework
 * formatters, the behaviour can be customised as "usual"
 * (see Javadoc of JFormattedTextField). Framework methods 
 * getModelValue and setModelValue use JFormattedTextField
 * methods getValue and setValue for linking the field
 * to view model.
 * <p>
 * Controller event methods, where yyy is component's name:
 * 
 * <code>public void yyyKeyReleased()</code> executed when 
 * a key is released, key events must be enabled with method 
 * setKeyEventsEnabled.
 *  
 * @author Tomi Tuomainen
 */
public class YFormattedTextField extends JFormattedTextField 
	implements YIModelComponent, YIValidatorComponent {

	
	private boolean keyEventsEnabled = false;
	 
	private static boolean staticUpper = false;
	
	private YProperty myProperty = new YProperty();
	public static final YFormattedTextFieldVerifier DEFAULT_VERIFIER = 
			new YFormattedTextFieldVerifier();
	
	/**
	 * Creates a new text field with YTextFormatter.
	 */
	public YFormattedTextField() {
		super(new YTextFormatter());
		init();
	}
	
	/** 
	 * Creates a new text field by calling super
	 * constructor. YTextFormatter is not set.
	 */
	public YFormattedTextField(Object value) {
		super(value);
		init();
	}

	/** 
	 * Creates a new text field by calling super
	 * constructor. YTextFormatter is not set.
	 */
	public YFormattedTextField(Format format) {
		super(format); 
		init();
	}
	/** 
	 * Creates a new text field just by calling super
	 * constructor. 
	 */
	public YFormattedTextField(AbstractFormatter formatter) {
		super(formatter);
		init();
	}

	/** 
	 * Creates a new text field by calling super
	 * constructor. 
	 */
	public YFormattedTextField(AbstractFormatterFactory factory) {
		super(factory);
		init();
		
	}
	
	/**
	 * Sets up default settings for the field.
	 */
	private void init() {
		this.setInputVerifier(new YFormattedTextFieldVerifier());
	}
	/** 
	 * Creates a new text field just by calling super
	 * constructor. YTextFormatter is not set.
	 */
	public YFormattedTextField(AbstractFormatterFactory factory,
			Object currentValue) {
		super(factory, currentValue);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see fi.mmm.yhteinen.swing.core.YIComponent#getYProperty()
	 */
	public YProperty getYProperty() {
	 	return myProperty;
	}
	

	/**
	 * Gets value of this field for view model.
	 * 
	 * If text in this field is an empty String, null is returned.
	 * Otherwise object returned by getValue() passed by this method.
	 * 
	 * @return the value for view model
	 */
	public Object getModelValue()  {
        try {
            commitEdit();
        } catch (ParseException ex) {
            throw new YException(ex);
        }
		if (getText().trim().equals("")) {
			return null;
		} else {
			Object obj = getValue();
			return obj;
		}
	}

	/**
	 * Sets view model value into this field. Calls setValue method
	 * to update the field.
	 * 
	 * @param obj the view model value
	 */
	public void setModelValue(Object obj) {
		this.setValue(obj);
	}
    


	/* (non-Javadoc)
	 * @see fi.mmm.yhteinen.swing.core.YIControllerComponent#addViewListener(fi.mmm.yhteinen.swing.core.YController)
	 */
	public void addViewListener(final YController controller) {
        this.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent ev) {
				if (valueValid()) {
                    controller.notifyValidationListenersOnSuccess(YFormattedTextField.this);
              	controller.updateModelAndController(YFormattedTextField.this);
				} else {
                    controller.notifyValidationListenersOnFail(
							new YComponentValidationException(
									"Invalid value in YFormattedTextField.",
									YFormattedTextField.this,
									YFormattedTextField.this.getText()));
				}
				if (YFormattedTextField.this.getFocusLostBehavior() != JFormattedTextField.COMMIT) {
                    // if not COMMIT, the invalid value will be reverted, setting back valid layout:
				    setValidLayout();
                }
			}
		});
        this.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (keyEventsEnabled) {
					String methodName = YUIToolkit.createMVCMethodName(YFormattedTextField.this, "KeyReleased");
					if (methodName != null) {
						controller.invokeMethodIfFound(methodName);
					}
				}
			}
		});
	}
	
	private Font validFont;
	private Color validColor;
	
	/**
	 * YFormattedTextFieldVerifier uses this method to set the
	 * layout of field containing invalid value. By default
	 * red bold Font is used. This method can be overridden
	 * for setting customised layout. 
	 *
	 * @see YFormattedTextField#setValidLayout()
	 */
	public void setInvalidLayout() {
		if (validFont == null) {
			this.validColor = this.getForeground();
			this.validFont = this.getFont();
		}
		this.setForeground(Color.red);
		Font font = validFont.deriveFont(Font.BOLD);
		this.setFont(font);
	}
	
	/**
	 * YFormattedTextFieldVerifier uses this method to return
	 * the valid layout for this field. This method should 
	 * be overridden too, if setInvalidLayout is overridden. 
	 */
	public void setValidLayout() {
		if (validFont != null) {
			this.setForeground(validColor);
			this.setFont(validFont);
			validFont = null;
		}
	}
	
	/**
	 * @return is upper case used for all the fields
	 */
	public static boolean isStaticUpper() {
		return staticUpper;
	}
	
	/**
	 * Sets all YFormattedTextFields in the application
	 * to upper case. This setting will affect if 
	 * setThisUpper is not set for the field.
	 * 
	 * YTextFormatter must be used to make the functionality work.
	 * 
	 * @param staticUpper is upper case used for all the field instances
	 */
	public static void setStaticUpper(boolean staticUpper) {
		YFormattedTextField.staticUpper = staticUpper;
	}
	/**
	 * YTextFormatter must be used if this method is called!
	 * 
	 * @return is upper case used for this field
	 */
	public boolean isThisUpper() {
		AbstractFormatter f = this.getFormatter();
		if (! (f instanceof YTextFormatter)) {
			throw new YException(
					f.getClass() + " is not YTextFormatter, YFormattedTextField.isThisUpper cannot be used.");
		} else {
			YTextFormatter formatter = (YTextFormatter) f;
			return formatter.isUpper();
		}
	}
	/**
	 * Sets this text field to upper case mode. This method
	 * will override possible global setting set by setStaticUpper 
	 * method.
	 * 
	 * YTextFormatter must be used if this method is called!
	 * 
	 * @param thisUpper is this text field upper case
	 */
	public void setThisUpper(boolean thisUpper) {
		AbstractFormatter f = this.getFormatter();
		if (! (f instanceof YTextFormatter)) {
			throw new YException(
					f.getClass() + " is not YTextFormatter, YFormattedTextField.setThisUpper cannot be used.");
		} else {
			YTextFormatter formatter = (YTextFormatter) f;
			formatter.setUpper(thisUpper);
		}
	}
	/**
	 * Sets maximum length for this field. User cannot 
	 * type more characters than maxLength. 
	 * 
	 * YTextFormatter must be used if this method is called!
	 * 
	 * @param maxLength the maximum length of this field
	 */
	public void setMaxLength(int maxLength) {
		AbstractFormatter f = this.getFormatter();
		if (f == null || ! (f instanceof YTextFormatter)) {
			throw new YException(
					f + " is not YTextFormatter, YFormattedTextField.setMaxLength cannot be used.");
		} else {
			YTextFormatter formatter = (YTextFormatter) f;
			formatter.setMaxLength(maxLength);
		}
	}
	
	/**
	 * Returns the maximum length set for this field. If 
	 * maxLength is not set, -1 is returned.
	 * 
	 * YTextFormatter must be used if this method is called!
	 * 
	 * @param maxLength the maximum length of this field
	 */
	public int getMaxLength(int maxLength) {
		AbstractFormatter f = this.getFormatter();
		if (f == null || ! (f instanceof YTextFormatter)) {
			throw new YException(
					f + " is not YTextFormatter, YFormattedTextField.setMaxLength cannot be used.");
		} else {
			YTextFormatter formatter = (YTextFormatter) f;
			return formatter.getMaxLength();
		}
		
	}
	
	/**
	 * @return if controller key events are enabled
	 */
	public boolean isKeyEventsEnabled() {
		return keyEventsEnabled;
	}
	/**
	 * Enables key released event for YController.
	 * 
	 * @param keyEventsEnabled if controller key events are enabled
	 */
	public void setKeyEventsEnabled(boolean keyEventsEnabled) {
		this.keyEventsEnabled = keyEventsEnabled;
	}
	
	/**
	 * This method selects text (in Windows style) when
	 * focus has moved to this field. This method
	 * can be overridden to change this behaviour.
	 */
	protected void focusGained() {
		if (getText().length() > 0) {
	 			this.selectAll();
	 	}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.awt.Component#processFocusEvent(java.awt.event.FocusEvent)
	 */
	 protected void processFocusEvent(FocusEvent e) {
	 	super.processFocusEvent(e);
	 	if (e.getID() == FocusEvent.FOCUS_GAINED) {
	 		focusGained();
	 	}
      }
	 
	/**
	 * If input verifier is set to this component, the method
	 * uses verify method of the verifier.    
	 * 
	 * 
     * @return boolean value returned by verify method of input verifier
	 * @see fi.mmm.yhteinen.swing.core.YIValidatorComponent#valueValid()
	 */
	public boolean valueValid() {
		if (this.getInputVerifier() != null) {
			return this.getInputVerifier().verify(this);
		} else {
			try {
				this.commitEdit();
			} catch (ParseException e) {
					return false;
			}
			return true;
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
