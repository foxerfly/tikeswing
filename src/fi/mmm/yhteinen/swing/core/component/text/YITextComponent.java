/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.component.text;

/**
 * Common interface for text components. A component must implement
 * this interface if YTextDocument is used.
 * 
 * @author Tomi Tuomainen
 * @see YTextDocument
 */
public interface YITextComponent {

	/**
	 * @return is field upper case
	 */
	public boolean isUpper();
	
	/**
	 * @return the maximum length of this field
	 */
	public int getMaxLength();
	
}
