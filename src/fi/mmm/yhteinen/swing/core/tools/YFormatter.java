/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.tools;

/**
 * The formatter class used by component implementations.
 * 
 * @see fi.mmm.yhteinen.swing.core.component.table.YColumn#setFormatter(YTableFormatter)
 * @see fi.mmm.yhteinen.swing.core.component.list.YComboBox#setComboModel(Collection, YFormatter, boolean)
 * @see fi.mmm.yhteinen.swing.core.component.list.YList#setListModel(Collection, YFormatter, boolean)
 * 
 * @author Tomi Tuomainen
 */
public abstract class YFormatter {

    /**
     * Defines how an object is showed in the view (to user).
     * 
     * @param item 	the item to be showed
     * @return		the appropriate String presentation
     */
	public abstract String format(Object item);
	
}
