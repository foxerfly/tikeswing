package fi.mmm.yhteinen.swing.core;



/**
 * The interface for components that need to be connected several YModel fields.
 * If a component is "standard" YIModelComponent, the MVC_NAME of the component is
 * the link to YModel. In addition, component may be YIExtendedModelComponent, 
 * when several extended fields are linked to YModel fields. These additional links
 * are specified via component's getExtendedFields method. 
 * <p> 
 * YIExtendedModelComponent must implement also setModelValue and getModelValue
 * that handle field copying for each extended field (passed as parameter).
 * <p>
 * YComboBox is an example of YIExtendedModelComponent. If <code>comboModelField</code>
 * is set to YComboBox, it will get combo model Collection directly from YModel 
 * (via notifyObservers).
 * 
 * @author Tomi Tuomainen
 */
public interface YIExtendedModelComponent extends YIComponent {

    /**
     * @return  the field names in YModel that are linked to this component
     */
    public String[] getExtendedFields();
    
    /**
     * Sets view model value into this component, like in YIModelComponent.
     * Field is an extra parameter to notify the component, which extended
     * field has changed.
     * 
     * @param field     the extended field
     * @param value     the changed value
     * @throws Exception  may be thrown if value cannot be set to this component
     */
    public void setModelValue(String field, Object value) throws Exception;
    
    
    /**
     * Gets value of this component for view model, like in YIModelComponent.
     * Field is an extra parameter to specify, which extended field value should be returned.
     * 
     * @param field     the extended field
     * @return          the changed value
     * @throws Exception   may be thrown if value cannot be read from this component
     */
    public Object getModelValue(String field) throws Exception;
    
}
