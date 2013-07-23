/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.component.label;

import java.text.NumberFormat;

import fi.mmm.yhteinen.swing.core.error.YException;

/**
 * The label for Long model values. Uses Format returned by getFormat 
 * method for String-Long conversions.
 * 
 * @author Tomi Tuomainen
 */
public class YLongLabel extends YLabel {

	private static NumberFormat defaultFormat;
	
	public YLongLabel() {
		super();

	}
	/**
	 * @param text
	 */
	public YLongLabel(String text) {
		super(text);
	}
	
	/**
	 *  This method can be overridden for localization of format.
	 * 
	 * @return the default number format for this label
	 * 			(NumberFormat with default Locale)
	 */
	protected NumberFormat getFormat() {
		if (defaultFormat == null) {
			defaultFormat = NumberFormat.getIntegerInstance();
		}
		return defaultFormat;
	}
	
	/**
     * Gets value of this label for view model.
     * 
     * @return the value of the label, or null
     *         if label doesn't contain valid number
     */
    public Object getModelValue() {
    	try {
            String str = (String) super.getModelValue();
            Number number = getFormat().parse(str);
            return new Long(number.longValue());
        } catch (Exception ex) {
        	return null;
        }
    }

    /**
     * Sets YView model value into this label. 
     * 
     * @param value the Long object to be presented, 
     *              if null an empty String is showed
     * 
     */
    public void setModelValue(Object value) throws YException {
        if (value == null) {
        	this.setText("");
        } else if (value instanceof Number) {
        	setText(getFormat().format( value));
        } else {
        	throw new YException(
    	        "Model value for YLongLabel must be instance of Number");
        }
    }
}
