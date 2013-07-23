/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.action;

import java.util.HashMap;

/**
 * Factory for actions. Each action will be created once by name.
 * 
 * @author Tomi Tuomainen
 */
public class YActionFactory {

	private static HashMap actions = new HashMap();
	
	/**
	 * Creates a new action or returns previously created
	 * action by name. 
	 * 
	 * @param name	the name of the action
	 * @return		the action
	 */
	public static YAction getAction(String name) {
		YAction action = (YAction) actions.get(name);
		if (action == null) {
			action = new YAction();
			actions.put(name, action);
		}
		return action;
	}
	
}
