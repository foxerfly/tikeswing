/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.component.field;

import java.text.NumberFormat;
import java.text.ParseException;

import fi.mmm.yhteinen.swing.core.error.YException;


/**
 * Integer formatter for YFormattedTextField. Integer field of 
 * view model should be linked to YFormattedTextField
 * using this formatter.
 * <p>
 * Customised NumberFormat for String-Integer conversions
 * can be set with constructor
 * <code>YIntegerFormatter(NumberFormat format)</code>
 * 
 * @author Tomi Tuomainen
 */
public class YIntegerFormatter extends YTextFormatter {
	
	
	/**
	 *  This method can be overridden for localization of format.
	 * 
	 * @return the default number format for this field
	 * 			(NumberFormat with default Locale)
	 */
	protected static NumberFormat getDefaultFormat() {
        NumberFormat defaultFormat = NumberFormat.getIntegerInstance();
		return defaultFormat;
	}
    
	/**
	 *  Creates a new formatter with default number format.
	 */
	public YIntegerFormatter() {
		this(getDefaultFormat());
	}
	
	/**
	 * Creates a new Formatter with given NumberFormat.
	 * @param format the number format for String-Integer conversions
	 */
	public YIntegerFormatter(NumberFormat format) {
		this.setFormat(format);
	}
	
	/*
	 * This method is overridden because we want to be sure
	 * that Integer is returned to view model.
	 * 
	 * @see javax.swing.JFormattedTextField.AbstractFormatter#stringToValue(java.lang.String)
	 *
	 */
	public Object stringToValue(String text) throws ParseException {
		// NumberFormat makes the conversion:
		Object obj = super.stringToValue(text);
		if (obj == null || obj instanceof Integer) {
			return obj; 
		} else if (obj instanceof Number) {
			Number number = (Number) obj;
			return new Integer(number.intValue());
		} else {
			throw new YException(
					"NumberFormat of YIntegerFormatter must return Number, now returned " + obj.getClass() +".");
		}
	}
	
	/*
	 * This method is overridden for checking that connected
	 * view model field is Integer.
	 * 
	 *  @see javax.swing.JFormattedTextField.AbstractFormatter#valueToString(java.lang.Object)
	 */
	public String valueToString(Object value) throws ParseException {
		if (value == null || value instanceof Number) {
			// NumberFormat makes the conversion:
			return super.valueToString(value);
		} else {
			throw new YException(
					"Value for YIntegerFormatter must be Number, now trying to set " + value.getClass());
		}
	}
}
