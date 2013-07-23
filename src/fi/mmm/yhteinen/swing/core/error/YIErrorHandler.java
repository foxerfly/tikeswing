/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.error;

import java.awt.Window;



/**
 * Interface for handling exceptions. Application can implement
 * this interface and set customised YIErrorHandler to YController.
 * 
 * @author Tomi Tuomainen
 * @see fi.mmm.yhteinen.swing.core.YController#setErrorHandler(YIErrorHandler)
 */
public interface YIErrorHandler {
	
	/**
	 * Handles unexpected exceptions.
     *
	 * @param e		  the exception
     * @param window  the parent window (may be null) 
     *                  (this parameter is pretty much useless and should not be trusted in any purpose)
     *                    
	 */
	public void handleException(Throwable e, Window window);

}
