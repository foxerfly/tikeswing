/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core;

import fi.mmm.yhteinen.swing.core.tools.YProperty;

/**
 * This interface must be implemented by all YFramework view components.
 * The implementation of this class should be the following (use
 * copy - paste when implementing a new component): 
 * <pre>
 * private YProperty myProperty = new YProperty();
 *
 * public YProperty getYProperty() {
 *		return myProperty;
 * }
 * </pre>
 * 
 * @author Tomi Tuomainen
 */
public interface YIComponent  {
	
	/**
	 * The name used in connection a view component to the model (and 
	 * the controller). The property should hold String value.
	 */
	public static final String MVC_NAME = "mvcName";

	/**
	 * If this property is set to a component with Boolean value true,
	 * a value of the component is never copied to the model.
	 * (use this if model does not contain setter method).
	 */
	public static final String READ_ONLY = "readOnly";
	
	/**
	 * The list of components. View class may use this property instead
	 * of implementing get methods for every view component. The property
	 * may hold a Collection or an array of YIComponents.
	 */
	public static final String COMPONENT_LIST = "componentList";

	/**
	 * If a component should check unsaved user changes before
	 * execution of some event. The property should hold Boolean value.
	 */
	public static final String CHECK_CHANGES = "checkChanges";
	
	/**
	 * The controller of the view. The property should hold YController.
	 */
	public static final Object CONTROLLER = "controller";

	/**
	 * The model of the view. The property should hold YModel.
	 */
	public static final Object MODEL = "model";
	


	
	/**
	 * @return	the YProperty of component
	 */
	public YProperty getYProperty();
	
}
