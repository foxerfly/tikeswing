/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core;

/**
 * The interface for components that may trigger YController
 * events.
 *
 * @author Tomi Tuomainen
 */
public interface YIControllerComponent extends YIComponent {
	
    /**
     * Adds a listener for component events for notifying
     * view and controller (and updating also model
     * if implementing class is instance of YIModelComponent).
     * 
     * @param controller the controller of the view that holds this component
     */
    public void addViewListener(YController controller);
    

 
}
