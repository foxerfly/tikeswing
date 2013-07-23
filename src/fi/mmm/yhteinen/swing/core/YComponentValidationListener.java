package fi.mmm.yhteinen.swing.core;

import fi.mmm.yhteinen.swing.core.error.YComponentValidationException;

/**
 * Validator listener for YController.
 * 
 * 
 * @author Tomi Tuomainen
 */
public abstract class YComponentValidationListener {

    /**
     * This method is called after componentValidationFailed of YController is performed.
     * 
     * @param controller    the controller of view with changes
     * @param e           the validation exception
     */
    public abstract void componentValidationFailed(YController controller, YComponentValidationException e);
    
    /**
     * This method is called after componentValidationSucceeded of YController is performed.
     * 
     * @param controller    the controller of view with changes
     * @param comp          the validated component
     */
    public abstract void componentValidationSucceeded(YController controller, YIComponent comp);
    
}
