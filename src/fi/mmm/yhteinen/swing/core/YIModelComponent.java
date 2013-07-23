/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core;


/**
 * The interface for components that are connected to a
 * view model object.
 * 
 * @author Tomi Tuomainen
 */
public interface YIModelComponent extends YIControllerComponent {

    /**
     * Gets value of this component for view model.
     * Used when copying values from this component to a model object.
     * 
     * If component does not hold a valid value, null should
     * be returned from this method. Validation itself should
     * be taken care of by implementing YIValidatorComponent interface.
     *
     * @return the value of the component which should 
     *          be set to model object
     */
    public Object getModelValue();
    
    /**
     * Sets view model value into this component. 
     * Used when copying values from a model object to this component.
     *  
     * Component should check if the data type of the parameter 
     * object is suitable. If value cannot be copied to the component 
     * YModelValueException should be raised. This exception
     * can be handled in YController.componentSetValueFailed method. 
     * 
     * @param obj the model object value which should be set to component
      */
    public void setModelValue(Object obj);
   
}