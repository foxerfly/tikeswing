/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.component.label;

import java.text.NumberFormat;

import fi.mmm.yhteinen.swing.core.error.YException;


/**
 * The label for Double model values. Uses Format returned by getFormat 
 * method for String-Float conversions.
 * 
 * @author Tomi Tuomainen
 */
public class YFloatLabel extends YLabel {

	private static NumberFormat defaultFormat;
	
	public YFloatLabel() {
		super();

	}
	/**
	 * @param text
	 */
	public YFloatLabel(String text) {
		super(text);
	}
	
	/**
	 *  This method can be overridden for localization of format.
	 * 
	 * @return the default number format for this field
	 * 			(NumberFormat with default Locale and
	 * 			 two fraction digits)
	 */
	protected NumberFormat getFormat() {
		if (defaultFormat == null) {
			defaultFormat = NumberFormat.getNumberInstance();
			defaultFormat.setMinimumFractionDigits(2);
			defaultFormat.setMaximumFractionDigits(2);
		}
		return defaultFormat;
	}
	
	/**
     * Gets value of this label for YView model.
     * 
     * @return the value of the label, or null
     *         if label doesn't contain valid number
     */
    public Object getModelValue() {
    	try {
            String str = (String) super.getModelValue();
            Number number = getFormat().parse(str);
            return new Float(number.floatValue());
        } catch (Exception ex) {
        	return null;
        }
    }
    
    /**
     * Sets YView model value into this label. 
     * 
     * @param value the Float object to be presented, 
     *              if null an empty String is showed
     * 
     */
    public void setModelValue(Object value) throws YException {
        if (value == null) {
        	this.setText("");
        } else if (value instanceof Number) {
        	this.setText(getFormat().format( value));
        } else {
    	    throw new YException(
                "Model value for YFloatLabel must be instance of Number.");
        }
    }
	
	
}
