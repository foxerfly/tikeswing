/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.error;


import java.awt.Window;

import org.apache.log4j.Logger;



/**
 * Default handler for exceptions. 
 * <p>
 * This handler logs exceptions with the following levels:
 * <ul>
 * <li>trace - YMethodNotFoundException</li>
 * <li>warn - YInvalidMVCNameException, YCloneModelException, 
 * 		 	YEqualsModelException, YComponentValidationException</li>
 * <li>error - all other exceptions</li>
 * </ul>
 * 
 * A simple error dialog is shown for errors. 
 * 
 * @author Tomi Tuomainen
 */
public class YDefaultErrorHandler implements YIErrorHandler {
	
	private static Logger logger = Logger.getLogger(YDefaultErrorHandler.class);
	
	/*
	 *  (non-Javadoc)
	 * @see fi.mmm.yhteinen.swing.core.error.YIErrorHandler#handleException(java.lang.Object, java.lang.Throwable)
	 */
	public void handleException(Throwable ex, Window window) {
		if (ex instanceof YMethodNotFoundException) {
			logger.info(ex.getMessage());
		} else if (ex instanceof YInvalidMVCNameException ||
					ex instanceof YCloneModelException ||
					ex instanceof YEqualsModelException ||
					ex instanceof YComponentValidationException) {
			logger.warn(ex.getMessage());
		} else {
			if (ex instanceof YComponentModelCopyException) {
				YComponentModelCopyException copyEx = (YComponentModelCopyException) ex;
				logger.error(ex.getMessage());
				logger.error("Original exception: ", copyEx.getOriginalException());
			} else {
				logger.error("Unknown error", ex);
			}
			// kommentoitu pois, koska esim. YTextFormatterin
			// nostaman poikkeuksen näyttäminen virheikkunassa saa aikaan ikisilmukan...
//			Window window = YUIToolkit.getCurrentWindow();
//			JOptionPane.showOptionDialog(
//				window,
//				"Unknown application error.",
//                "Error",
//                JOptionPane.DEFAULT_OPTION,
//                JOptionPane.ERROR_MESSAGE,
//                null,
//                new String[] {"Ok"},
//                "Ok");
		}
	}

}
