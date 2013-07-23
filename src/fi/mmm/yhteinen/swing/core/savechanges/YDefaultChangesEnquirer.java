/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.savechanges;

import java.awt.Component;

import javax.swing.JOptionPane;

import fi.mmm.yhteinen.swing.core.tools.YUIToolkit;

/**
 * Default implementation for YIChangesDialogHandler.
 * Shows an simple english dialog for user. Texts can be
 * modified via set-methods. 
 * 
 * @author Tomi Tuomainen
 */
public class YDefaultChangesEnquirer implements YIChangesEnquirer {

    // Strings used by the Save Changes dialog:
    private String saveChangesQuestion = "View has been modified. Save changes?";
    private String saveChangesTitle = "Save data";
    private String yesString = "Yes";
    private String noString = "No";
    private String cancelString = "Cancel";
    
    /*
     *  (non-Javadoc)
     * @see fi.mmm.yhteinen.swing.core.savechanges.YIChangesEnquirer#showConfirmationDialog(java.awt.Component)
     */
    public int showConfirmationDialog(Component view) {
         return JOptionPane.showOptionDialog(
                 YUIToolkit.findParentWindow(view),
                 saveChangesQuestion,
                 saveChangesTitle,
                 JOptionPane.DEFAULT_OPTION,
                 JOptionPane.QUESTION_MESSAGE,
                 null,
                 new String[] {yesString, noString, cancelString},
                 yesString);
    }
     
	
	public String getCancelString() {
		return cancelString;
	}
	
	
	public void setCancelString(String cancelString) {
		this.cancelString = cancelString;
	}
	
	public String getNoString() {
		return noString;
	}
	
	public void setNoString(String noString) {
		this.noString = noString;
	}
	
	public String getSaveChangesQuestion() {
		return saveChangesQuestion;
	}
	
	public void setSaveChangesQuestion(String saveChangesQuestion) {
		this.saveChangesQuestion = saveChangesQuestion;
	}
	
	public String getSaveChangesTitle() {
		return saveChangesTitle;
	}
	
	public void setSaveChangesTitle(String saveChangesTitle) {
		this.saveChangesTitle = saveChangesTitle;
	}
	
	public String getYesString() {
		return yesString;
	}
	
	public void setYesString(String yesString) {
		this.yesString = yesString;
	}
}
