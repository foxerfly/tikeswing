/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.component.field;

import java.text.ParseException;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField.AbstractFormatter;

/**
 * Verifier for YFormattedTextField. Checks if focus can
 * leave the text field by using formatter stringToValue method.
 * If value is not valid (according to the formatter set to the
 * field) setInvalidLayout of the field is called. If value
 * is valid, setValidLayout of the field is called.
 * 
 * @author Tomi Tuomainen
 */
public class YFormattedTextFieldVerifier extends InputVerifier {
	
    private boolean alwaysYieldFocus = false;
    
	public YFormattedTextFieldVerifier() {
		super();
	}
	    
	/*
	 *  (non-Javadoc)
	 * @see javax.swing.InputVerifier#verify(javax.swing.JComponent)
	 */
	public boolean verify(JComponent input) {
	         if (input instanceof YFormattedTextField) {
	             YFormattedTextField field = (YFormattedTextField)input;
	             AbstractFormatter formatter = field.getFormatter();
	             if (formatter != null) {
	                 String text = field.getText();
	                 if (!text.trim().equals("")) {
	                 	try {
	                      formatter.stringToValue(text);
	                 	} catch (ParseException pe) {
	                 		field.setInvalidLayout();
	                 		return false;
	                 	}
	                 }
	             }
             	field.setValidLayout();
	          }
	          return true;
	      }
	
	      /*
	       *  (non-Javadoc)
	       * @see javax.swing.InputVerifier#shouldYieldFocus(javax.swing.JComponent)
	       */
	      public boolean shouldYieldFocus(JComponent input) {
	        if (alwaysYieldFocus) {
             return true;   
            } else {
              return verify(input);
            }
	      }
	      
        /**
         * @return if focus should be always yielded 
         */
        public boolean isAlwaysYieldFocus() {
            return alwaysYieldFocus;
        }
        
        /**
         * Setting alwaysYieldFocus to true means that focus can be transferred to the next
         * component, even if verify method of this formatter returns false. In other words, 
         * if you want that invalid values won't prevent focus actions, call this method
         * with true.
         * 
         * @param alwaysYieldFocus  if focus should be always yielded 
         */
        public void setAlwaysYieldFocus(boolean alwaysYieldFocus) {
            this.alwaysYieldFocus = alwaysYieldFocus;
        }
	
          
}
