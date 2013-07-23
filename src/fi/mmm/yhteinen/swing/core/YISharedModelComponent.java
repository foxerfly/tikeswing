/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core;

/**
 * The interface for a model component that shares it's internal
 * model with the a model. If a component wants to share object
 * references with a view model, this interface must be implemented
 * (if not, YController methods <code>hasViewChanges</code>,
 * <code>resetViewChanges</code> and <code>cancelViewChanges</code> won't work).
 *
 * @author Tomi Tuomainen
 * 
 */
public interface YISharedModelComponent extends YIModelComponent {

    /**
     * Returns a clone of component's model (which is also
     * a reference to value of model object).
     * 
     * @return the cloned model
     */
    public Object cloneModel();
    
    /**
     * Checks if the given model equals component's own model
     * (returned  by getModelValue() method).
     * 
     * @param model the model to check
     * @return true if the model equals component's model
     */
    public boolean equalsModel(Object model);
    
}
