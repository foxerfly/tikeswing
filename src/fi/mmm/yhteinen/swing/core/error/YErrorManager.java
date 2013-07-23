/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.error;

import java.awt.Window;

/**
 * ErrorManager has been deprecated. It is strongly recommended
 * not to throw any exception to YErrorManager. Instead, set
 * YIErrorHandler to YController.
 * @author Tomi Tuomainen
 */
public class YErrorManager {

    private static YIErrorHandler handler = new YDefaultErrorHandler();
    
    /**
     * Passes exception to error handler.
     * @param e      the exception
     * @param window  the parent window (may be null)  
     */
    public static void handleException(Throwable e, Window window) {
        handler.handleException(e, window);
    }
    
    /**
     * Passes exception to error handler.
     * @param e      the exception
     */
    public static void handleException(Throwable e) {
        handler.handleException(e, null);
    }
    
    /**
     * Creates a new YException and passes it to
     * error handler.
     * 
     * @param message        the exception message
     * @param window  the parent window (may be null)  
     */
    public static void handleException(String message, Window window) {
        handler.handleException(new YException(message), window);
    }

    /**
     * Creates a new YException and passes it to
     * error handler.
     * 
     * @param message        the exception message
     */
    public static void handleException(String message) {
        handler.handleException(new YException(message), null);
    }

    /**
     * Returns the exception handler. The default handler is
     * an instance of YDefaultErrorHandler.
     * 
     * @return the handler for exceptions
     */
    public static YIErrorHandler getHandler() {
        return handler;
    }
    /**
     * @param handler the handler for exceptions
     */
    public static void setHandler(YIErrorHandler handler) {
        YErrorManager.handler = handler;
    }
}
