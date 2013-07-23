/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import fi.mmm.yhteinen.swing.core.YIComponent;
import fi.mmm.yhteinen.swing.core.savechanges.YSaveChangesHandler;
import fi.mmm.yhteinen.swing.core.tools.YProperty;

/**
 * Action that uses YActionCommand for action execution.
 * YActionFactory returns YActions. 
 * 
 * @author Tomi Tuomainen
 **/
public class YAction extends AbstractAction implements YIComponent {

	// the command which executes action
	private YActionCommand command;
	
	private YProperty myProperty = new YProperty();
	
	/*
	 *  (non-Javadoc)
	 * @see fi.mmm.yhteinen.swing.core.YIComponent#getYProperty()
	 */
	public YProperty getYProperty() {
	 	return myProperty;
	}
	/**
	 * Calls execute method of command. If command is not set, 
	 * does nothing.
	 */
	public void actionPerformed(ActionEvent e) {
		if (YSaveChangesHandler.changesSavedInCurrentWindow(this)) {
        	if (command != null) {
        		command.execute();
        	}
		}
	}
	
	/**
	 * @return the command for execution
	 */
	public YActionCommand getCommand() {
		return command;
	}
	
	/**
	 * @param command  the command for execution
	 */
	public void setCommand(YActionCommand command) {
		this.command = command;
	}
}
