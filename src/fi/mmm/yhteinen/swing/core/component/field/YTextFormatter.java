/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.component.field;

import java.text.ParseException;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.InternationalFormatter;

import fi.mmm.yhteinen.swing.core.error.YException;

/**
 * The default formatter of YFormattedTextField. Customised
 * formatters should extend this class, because it 
 * provides upper case conversion and max length setting
 * for YFormattedTextField.
 * <p>
 * The formatter sets up DocumentFilter which calls
 * getStringToAdd method. This method can be overridden
 * for specifying characters that can be added to text field.
 *
 * @author Tomi Tuomainen
 */
public class YTextFormatter extends InternationalFormatter {

	private Boolean upper; 
	private int maxLength = -1;
	
	public YTextFormatter() {
		super();
	}
	
	/*
	 *  (non-Javadoc)
	 * @see javax.swing.JFormattedTextField.AbstractFormatter#stringToValue(java.lang.String)
	 */
	public Object stringToValue(String text) throws ParseException {
		if (text.trim().equals("")) {
			return null;
		} else {
			// conversion is made by Format-object:
			return super.stringToValue(text);
		}
	}

	/**
	 * This method can be overridden for specifying valid characters
	 * for this text field. Just remember to call <code>
	 * super.filterStringToAdd(currentStr, strToAdd) </code>
	 * in the beginning of your implementation.
	 * 
	 * @param currentStr	the string in text field currently
	 * @param strToAdd		the string that is to be added to text field
	 * @return				the string that will be added to text field
	 */
	protected String filterStringToAdd(String currentStr, String strToAdd, int offset) {
		// checking max length...
		if (maxLength > -1) {
			String selectedText = getFormattedTextField().getSelectedText();
			int selectedLength = (selectedText == null ? 0 : selectedText.length());
			int newLength = currentStr.length()+strToAdd.length()-selectedLength;
			if (newLength > maxLength) {
				// cannot add new String...
				return null;
			} 	
    	}
		if (isUpper()) {
			return strToAdd.toUpperCase();
		} 
		return strToAdd;
	}
	
	/**
	 * Passes calls to filterStringToAdd method
	 * 
	 * @param fb			some object provided by DocumentFilter
	 * @param strToAdd 		the string that is to be added to text field
	 * @return				the string that will be added to text field
	 */
	private String getStringToAdd(DocumentFilter.FilterBypass fb, int offset, String strToAdd) {
		Document document = fb.getDocument();
		String currentStr;
		try {
			currentStr = document.getText(0, document.getLength());
			return filterStringToAdd(currentStr, strToAdd, offset);
		} catch (BadLocationException e) {
			throw new YException(e);
		}
	
	}

	private DocumentFilter filter = new DocumentFilter() {
		/*
		 *  (non-Javadoc)
		 * @see javax.swing.text.DocumentFilter#insertString(javax.swing.text.DocumentFilter.FilterBypass, int, java.lang.String, javax.swing.text.AttributeSet)
		 */
		public void insertString(DocumentFilter.FilterBypass fb,
                int offset,
                String string,
                AttributeSet attr)
         throws BadLocationException {
			String strToAdd = getStringToAdd(fb, offset, string);
			if (strToAdd != null) {
				super.insertString(fb, offset, strToAdd, attr);
			}
		}
		
		/*
		 *  (non-Javadoc)
		 * @see javax.swing.text.DocumentFilter#replace(javax.swing.text.DocumentFilter.FilterBypass, int, int, java.lang.String, javax.swing.text.AttributeSet)
		 */
		public void replace(DocumentFilter.FilterBypass fb,
                int offset,
                int length,
                String string,
                AttributeSet attrs)
         throws BadLocationException {
			String strToAdd = getStringToAdd(fb, offset, string);
			if (strToAdd != null) {
				super.replace(fb, offset, length, strToAdd, attrs);
			}
		}
	};
	
	/**
	 * Sets up document filter for this formatter. 
	 * 
	 * If this method is overridden, uppercase and max length 
	 * functionality is lost! For specifying acceptable
	 * characters override filterStringToAdd method.
	 */
	protected DocumentFilter getDocumentFilter() {
		return filter;
	};
	
	/**
	 * @return the maximum number of characters in text field
	 */
	public int getMaxLength() {
		return maxLength;
	}
	/**
	 * @param maxLength the maximum number of characters in text field
	 */
	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	/**
	 * @return if this formatter is upper case
	 */
	public boolean isUpper() {
		if (upper == null) {
			return YFormattedTextField.isStaticUpper();
		} else {
			return upper.booleanValue();
		}
		
	}
	
	/**
	 * @param upper if this formatter is upper case
	 */
	public void setUpper(boolean upper) {
		this.upper = new Boolean(upper);
	}
}
