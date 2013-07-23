/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.component.field;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import fi.mmm.yhteinen.swing.core.error.YException;


/**
 * Date formatter for YFormattedTextField. Date field of view model 
 * should be linked to YFormattedTextField using this formatter.
 * <p>
 * Customised DateFormat for String-java.util.Date conversions
 * can be set with constructor
 * <code>YDateFormatter(DateFormat format)</code>.
 * 
 * @author Tomi Tuomainen
 */
public class YDateFormatter extends YTextFormatter {

	/**
	 * This method can be overridden for localization of format.
	 * 
	 * @return the default date format for this field
	 * 			(SimpleDateFormat with default Locale and
	 * 			 lenient as false)
	 */
	protected static DateFormat getDefaultFormat() {
		DateFormat defaultFormat = new SimpleDateFormat();
		defaultFormat.setLenient(false);
		return defaultFormat;
	}
	
	/**
	 * Creates a new formatter with default date format.
	 */
	public YDateFormatter() {
		this(getDefaultFormat());
	}
	
	/**
	 * Creates a new Formatter with given DateFormat.
	 * @param format the date format for String-Date conversions
	 */
	public YDateFormatter(DateFormat format) {
		this.setFormat(format);
	}
	
	/*
	 * This method is overridden because we want to be sure
	 * that java.util.Date is returned to view model.
	 *
	 * @see javax.swing.JFormattedTextField.AbstractFormatter#stringToValue(java.lang.String)
	 */
	public Object stringToValue(String text) throws ParseException {
		// Format makes the conversion:
		Object obj = super.stringToValue(text);
		if (obj == null || obj instanceof Date) {
			return obj; 
		} else {
            throw new YException(
					"DateFormat of YDateFormatter must return Date, now returned " + obj.getClass() +".");

		}
	}
	
	/*
	 * This method is overridden for checking that connected
	 * view model field is java.util.Date.
	 * 
	 *  @see javax.swing.JFormattedTextField.AbstractFormatter#valueToString(java.lang.Object)
	 */
	public String valueToString(Object value) throws ParseException {
		if (value == null || value instanceof Date) {
			// Format makes the conversion:
			return super.valueToString(value);
		} else {
			throw new YException(
					"Value for YDateFormatter must be Date, now trying to set " + value.getClass());

		}
	}
}
