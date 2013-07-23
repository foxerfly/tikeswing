/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.savechanges;

import java.awt.Component;


/**
 * Interface for showing save confimation dialog. Application can 
 * implement this interface and set the customised YIChangesDialogHandler 
 * to YSaveChangesManager class.
 * 
 * @author Tomi Tuomainen
 * @see YSaveChangesHandler
 */
public interface YIChangesEnquirer {

	/**
     * Shows save confimation dialog. The dialog asks from the user,
     * if she wants to save unsaved changes. Method returns user's 
     * response, which can be YSaveChangesHandler.YES, 
     * YSaveChangesHandler.NO or YSaveChangesHandler.CANCEL.
     * 
     * @param view 	the view that is asking changes to be saved
     * @return user's choice in dialog: Yes, No or Cancel
     */
	public int showConfirmationDialog(Component view);
	
}
