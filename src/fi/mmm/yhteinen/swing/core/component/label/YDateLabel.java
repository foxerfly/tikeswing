/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.component.label;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import fi.mmm.yhteinen.swing.core.error.YException;

/**
 * The label for java.util.Date model values. Uses Format 
 * returned by getFormat method for String-Date conversions.
 * 
 * @author Tomi Tuomainen
 */
public class YDateLabel extends YLabel {

	private static DateFormat defaultFormat;
	
	public YDateLabel() {
		super();

	}
	/**
	 * @param text
	 */
	public YDateLabel(String text) {
		super(text);
	}
	
	/**
	 * This method can be overridden for localization of format.
	 * 
	 * @return the default date format for this field
	 * 			(SimpleDateFormat with default Locale and
	 * 			 lenient as false)
	 */
	protected DateFormat getFormat() {
		if (defaultFormat == null) {
			defaultFormat = new SimpleDateFormat();
			defaultFormat.setLenient(false);
		}
		return defaultFormat;
	}
	
	/**
     * Gets value of this label for YView model.
     * 
     * @return obj the value of the label, or null
     *         if label doesn't contain valid number
     */
    public Object getModelValue() {
    	try {
            String str = (String) super.getModelValue();
            Date date = getFormat().parse(str);
            return date;
        } catch (Exception ex) {
        	return null;
        }
    }
    
    /**
     * Sets YView model value into this label. 
     * 
     * @param value the Date object to be presented, 
     *              if null an empty String is showed
     * 
     */
    public void setModelValue(Object value) throws YException {
        if (value == null) {
        	setText("");
        } else if (value instanceof Date) {
        	setText(getFormat().format(value));
        } else {
        	throw new YException(
                "Model value for YDateLabel must be instance of Date.");
        }
    }
	
	
}
