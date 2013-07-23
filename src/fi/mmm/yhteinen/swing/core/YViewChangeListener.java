/*
 * Created on May 20, 2007
 *
 */
package fi.mmm.yhteinen.swing.core;

/**
 * Change listener for YController.
 * 
 * @see YController#addViewChangeListener(YViewChangeListener)
 * @see YController#removeViewChangeListener(YViewChangeListener)
 * 
 * @author Tomi Tuomainen
 */
public abstract class YViewChangeListener {

    /**
     * This method is called after viewChanged of YController is performed.
     * 
     * @param controller    the controller of view with changes
     * @param comp          the changed component
     */
    public abstract void viewChanged(YController controller, YIComponent comp);
    
    /**
     * This method is called after view changes have been reset by YController method call 
     * resetViewChanges or cancelViewChanges.
     * 
     * @param controller    the controller of view with changes
     */
    public abstract void viewChangesReset(YController controller);
  
}
