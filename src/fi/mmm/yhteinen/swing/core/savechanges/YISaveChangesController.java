/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.savechanges;


/**
 * Interface YController classes for implementing unsaved changes
 * checking. YController must implement save method so that
 * the framework can automatically save data in a view (if user decides
 * to do so). 
 * 
 * @author Tomi Tuomainen
 * @see YSaveChangesHandler
 */
public interface YISaveChangesController {

	/**
	 * Performs necessary validating and saves view data.
	 * 
	 * @return true if saving was succesful, otherwise false
	 */
	public boolean save();
	
}
