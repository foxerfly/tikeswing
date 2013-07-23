/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.component.label;

import java.text.NumberFormat;

import fi.mmm.yhteinen.swing.core.error.YException;


/**
 * The label for Integer model values. Uses Format returned by getFormat 
 * method for String-Integer conversions.
 * 
 * @author Tomi Tuomainen
 */
public class YIntegerLabel extends YLabel {

	private static NumberFormat defaultFormat;
	
	public YIntegerLabel() {
		super();

	}
	/**
	 * @param text
	 */
	public YIntegerLabel(String text) {
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
     * @return the value of the label (Integer), or null
     *         if label doesn't contain valid number
     */
    public Object getModelValue() {
    	try {
            String str = (String) super.getModelValue();
            Number number = getFormat().parse(str);
            return new Integer(number.intValue());
        } catch (Exception ex) {
        	return null;
        }
    }

    /**
     * Sets YView model value into this label. 
     * 
     * @param value the Integer object to be presented, 
     *              if null an empty String is showed
     * 
     */
    public void setModelValue(Object value) throws YException {
        if (value == null) {
        	this.setText("");
        } else if (value instanceof Number) {
        	setText(getFormat().format(value));
        } else {
        	throw new YException(
    	        "Model value for YIntegerLabel must be instance of Number.");
        }
    }

}
