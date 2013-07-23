/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.component.field;

import java.text.NumberFormat;
import java.text.ParseException;

import fi.mmm.yhteinen.swing.core.error.YException;



/**
 * Double formatter for YFormattedTextField. Double field of 
 * view model should be linked to YFormattedTextField
 * using this formatter.
 * <p>
 * Customised NumberFormat for String-Double conversions
 * can be set with constructor
 * <code>YDoubleFormatter(NumberFormat format)</code>
 * 
 * @author Tomi Tuomainen
 */
public class YDoubleFormatter extends YTextFormatter {

	
	/**
	 * 
	 * @return the default number format for this field
	 * 			(NumberFormat with default Locale and
	 * 			 two fraction digits)
	 */
	protected static NumberFormat getDefaultFormat() {
        NumberFormat defaultFormat = NumberFormat.getNumberInstance();
		defaultFormat.setMinimumFractionDigits(2);
		defaultFormat.setMaximumFractionDigits(2);
		return defaultFormat;
	}
	
	/**
	 *  Creates a new formatter with default number format.
	 */
	public YDoubleFormatter() {
		this(getDefaultFormat());
	}
	/**
	 * Creates a new Formatter with given NumberFormat.
	 * @param format the number format for String-Double conversions
	 */
	public YDoubleFormatter(NumberFormat format) {
		this.setFormat(format);
	}
	
	/*
	 * This method is overridden because we want to be sure
	 * that Double is returned to view model.
	 * 
	 * @see javax.swing.JFormattedTextField.AbstractFormatter#stringToValue(java.lang.String)
	 *
	 */
	public Object stringToValue(String text) throws ParseException {
		// NumberFormat makes the conversion:
		Object obj = super.stringToValue(text);
		if (obj == null || obj instanceof Double) {
			return obj; 
		} else if (obj instanceof Number) {
			Number number = (Number) obj;
			return new Double(number.doubleValue());
		} else {
            throw new YException(
					"NumberFormat of YDoubleFormatter must return Number, now returned " + obj.getClass() +".");
		}
	}
	
	/*
	 * This method is overridden for checking that connected
	 * view model field is Double.
	 * 
	 *  @see javax.swing.JFormattedTextField.AbstractFormatter#valueToString(java.lang.Object)
	 */ 
	public String valueToString(Object value) throws ParseException {
		if (value == null || value instanceof Number) {
			// NumberFormat makes the conversion:
			return super.valueToString(value);
		} else {
            throw new YException(
					"Value for YDoubleFormatter must be Number, now trying to set " + value.getClass());
		}
	}
	

}
