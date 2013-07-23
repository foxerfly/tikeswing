/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.component.table;

import java.util.Comparator;

/**
 * The formatter class used by YTable.
 * <p>
 * Note that YTable creates a new renderer when formatter is set to YColumn 
 * (overriding any other renderer set previously).
 * 
 * @author Tomi Tuomainen
 * @see YTable
 */
public abstract class YTableFormatter {

	private boolean sortByFormatter = true;
	
    /**
     * Defines how an object is showed in the table (to user).
     * 
     * @param item  the item to be showed
     * @param row   the row of the item
     * @param column the column of the item
     * @return      the appropriate String presentation
     */
    public abstract String format(Object item, int row, int column);

    /**
     * @return 	if formatter value is passed to Comparable when sorting YTable
     */
	public boolean isSortByFormatter() {
		return sortByFormatter;
	}

	/**
	 * The parameter of this method defines, if YTable sorting 
	 * is based on formatter value or model value in table. The default
	 * value is true, meaning that String that is formatted by this YFormatter is passed
	 * to Comparable when YTable is sorted. 
	 * 
	 * @param sortByFormatter 	if formatter value is passed to Comparable when sorting YTable
	 */
	public void setSortByFormatter(boolean sortByFormatter) {
		this.sortByFormatter = sortByFormatter;
	}

    
}
