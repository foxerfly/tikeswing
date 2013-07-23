
/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core;

import java.lang.reflect.InvocationTargetException;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;


import fi.mmm.yhteinen.swing.core.error.YComponentGetValueException;
import fi.mmm.yhteinen.swing.core.error.YComponentSetValueException;
import fi.mmm.yhteinen.swing.core.error.YComponentValidationException;
import fi.mmm.yhteinen.swing.core.error.YDefaultErrorHandler;
import fi.mmm.yhteinen.swing.core.error.YErrorManager;
import fi.mmm.yhteinen.swing.core.error.YException;
import fi.mmm.yhteinen.swing.core.error.YIErrorHandler;
import fi.mmm.yhteinen.swing.core.error.YMethodNotFoundException;
import fi.mmm.yhteinen.swing.core.error.YModelGetValueException;
import fi.mmm.yhteinen.swing.core.error.YModelSetValueException;
import fi.mmm.yhteinen.swing.core.savechanges.YIChangesEnquirer;
import fi.mmm.yhteinen.swing.core.tools.YUIToolkit;

/**
 * The controller class of the framework. Extend this class to 
 * implement a controller. 
 * 
 * @author Tomi Tuomainen
 */
public class YController extends YComponent implements Observer {
	 
    private static Logger logger = Logger.getLogger(YController.class);
        
	// the parent controller
	private YController parent;
	// the child controllers
	private ArrayList children = new ArrayList();
	   
	private YIComponent view;
    private Object model;
    
    private static final Map wholeAppProps = createMap();
    
    private YMVCHelper mvcHelper = new YMVCHelper(this);
    private YChangesHelper changesHelper = new YChangesHelper(this);
    private YRefreshHelper refreshHelper = new YRefreshHelper(this);
    private YValidatorHelper validatorHelper =new YValidatorHelper(this);
    private YEventHelper eventHelper = new YEventHelper(this);
    
    private boolean viewChanges = false;

    
    
    /**
     * Creates a new controller and initializes model and view
     * (calling setUpMVC or setView or setModel is not necessary if
     * this constructor is used).
     * 
     * @param model
     * @param view
     */
	public YController(YModel model, YIComponent view) {
        setModel(model);
        setView(view);
    }
    
    /**
     * Creates a new controller without a model and a view.
     */
    public YController() {
        
    }
    
  /**
   * Sets view for this controller and adds listeners for 
   * view components (for updating model and 
   * invoking controller methods). 
   * 
   * Applications shouldn't use this directly! MVC structure should
   * be set with YUIToolkit.setUpMVC instead!
   *
   * @see	fi.mmm.yhteinen.swing.core.tools.YUIToolkit#setUpMVC(Object, YIComponent, YController)
   * 
   * @param view 		the view for this controller
   */
  public void setView(YIComponent view) {
  	  	this.view = view;
  	  	mvcHelper.addComponentListeners();
  	  	view.getYProperty().put(YIComponent.CONTROLLER, this);
        mvcHelper.wireUp();
  }
  	
   /**
    * @return the view model.
    */
   public YIComponent getView() {
       return view;
   }
    
   
    /**
     * Sets model for this controller. 
       * 
     * @param model the model for this controller.
     */
    public void setModel(Object model) {
    	this.model = model;
        if (model instanceof YModel) {
            ((YModel)model).addObserver(this);
        }
        mvcHelper.wireUp();
    }
    
    /**
     * @return the model of this controller
     */
    public Object getModel() {
        return model;
    }
    
    
    /**
     * Sets up MVC structure. This is just convenience method
     * for calling setModel and setView separately.
     * 
     * @param model     the view model (any Object), may be null
     * @param view      the view, may not be null
     */
    public void setUpMVC(Object model, YIComponent view) {
        this.setModel(model);
        this.setView(view);
    }
    
    /**
     * Sets child controller for this controller. This method
     * should be used when creating hierarcical MVC structure, 
     * where controllers are connected to each other
     * (MVC++ or HMVC pattern).
     * 
     * The method also sets parent for the child.
     * 
     * @param child     the child of this controller
     */
    public void addChild(YController child) {
        this.children.add(child);
        child.setParent(this);
    }

    /**
     * Removes all children from this controller. Also sets
     * parent null for each child
     */
    public void removeChildren() {
        Iterator it = this.children.iterator();
        while (it.hasNext()) {
            YController c = (YController) it.next();
            c.setParent(null);
            it.remove();
        }
    }
    
    /**
     * Removes a child from this controller. Also sets
     * parent null for the child. If the child is not
     * a child controller of this controller, method won't
     * do anything. 
     */
    public void removeChild(YController child) {
        if (this.children.remove(child)) {
            child.setParent(null);
        }
    }
    
    /**
     * @return the parent of this controller
     */
    public YController getParent() {
        return parent;
    }
    /**
     * Sets parent controller for this controller.
     * Usually you should use addChild instead of this one
     * (Method addChild also sets parent for child controller).
     * 
     * @param parent the parent of this controller
     * @see #addChild(YController)
     */
    public void setParent(YController parent) {
        this.parent = parent;
    }
    
    /**
     * @return the child controllers of this controller
     */
    public ArrayList getChildren() {
        return children;
    }
    
    /**
     * This is called when view model is changed (when model extends
     * YModel and calls notifyObservers method). Updates view 
     * components based on changed model values.
     * 
     * Also framework  uses this for notifying other controllers 
     * about model changes. 
     * 
     * DO NOT OVERRIDE THIS METHOD.
     * 
     * @param o		the view model
     * @param arg	the name of the model field that has changed,
     * 				if null all the fields will be updated
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
    	// framework internal event for notifying controllers...
        if (arg instanceof YModelChangeEvent) {
         	this.modelChanged((YModelChangeEvent)arg);
        } else {
        	String fieldName = null;
        	if (arg instanceof String) {
        		fieldName = (String) arg;
        	}
        	copyToView(fieldName);
        	YModelChangeEvent event = new YModelChangeEvent();
        	event.setFieldName(fieldName);
        	event.setUserChange(false);
        	this.modelChanged(event);
        	
        }
   }
    
    /**
     * This is helper method for applications that neeed to copy data from
     * any view to any model (not connected to this controller). This controller
     * only handles possible errors. 
     * 
     * @param fieldToUpdate     field name to be copied, if null all view valu
     * @param view              the view 
     * @param model             the model
     */
    public void copyToModel(String fieldToUpdate, YIComponent view, Object model) {
        mvcHelper.copy(true, fieldToUpdate, view, model);
    }
    
    /**
     * This is helper method for applications that neeed to copy data from
     * any model to any view (not connected to this controller). This controller
     * only handles possible errors. 
     * 
     * @param fieldToUpdate     field name to be copied, if null all view values are copied
     * @param model             the model
     * @param view              the view 
     */
    public void copyToView(String fieldToUpdate, Object model, YIComponent view) {
        mvcHelper.copy(false, fieldToUpdate, view, model);
    }
    
    /**
     * Copies model values to view components.
     * 
     * @param fieldToUpdate the model field name to be copied, 
     *                  if null the whole model will be copied to 
     *                  corresponding view components
     */
    public void copyToView(String fieldToUpdate) {
        mvcHelper.copy(false, fieldToUpdate, this.getView(), this.getModel() );
    }
    
    /**
     * Copies view component values to model.
     * 
     * @param fieldToUpdate the model field name to be copied, 
     *                      if null all view component values will be copied
     *                      to corresponding model fields
     */
    public void copyToModel(String fieldToUpdate) {
        mvcHelper.copy(true, fieldToUpdate, this.getView(), this.getModel());
    }

    
  
    
    /**
     * @param comp      the component
     * @return          if component is read only (has YIComponent.READ_ONLY property)
     */
    public boolean isReadOnlyComponent(YIComponent comp) {
        return mvcHelper.isReadOnlyComponent(comp);
    }
    
    /**
     * Copies value from component to view model. 
     * 
     * @param comp  the component
     * @return      true, if value was copied
     */
    public boolean copyFromComponentToModel(YIModelComponent comp) {
        return mvcHelper.copyFromComponentToModel(comp, this.getModel());
    }
    
    /**
     * Copies value from component to view model. 
     * 
     * @param comp          the component
     * @param fieldName     the YIExtendedModelComponent field name to copy
     * @return              true, if value was copied
     */
    public boolean copyFromComponentToModel(YIExtendedModelComponent comp, String fieldName) {
        return mvcHelper.copyFromComponentToModel(comp, fieldName, this.getModel());
    }
    
	
    /**
     * Copies value from view model to component. 
     * 
     * @param comp  the component
     * @return  true if value was copied
     */
    public boolean copyFromModelToComponent(YIModelComponent comp) {
        return mvcHelper.copyFromModelToComponent(comp, this.getModel());
    }
    
    /**
     * Copies value from view model to component. 
     * 
     * @param comp  the component
     * @param fieldName     the YIExtendedModelComponent field name to copy
     * @return  true if value was copied
     */
	public boolean copyFromModelToComponent(YIExtendedModelComponent comp, String fieldName) {
        return  mvcHelper.copyFromModelToComponent(comp, fieldName, this.getModel());
	}
    

    /**
     * Adds change listener to this controller. Listener methods are invoked
     * when view changes (by user action) or resetViewChanges or cancelViewChanges is called.
     * 
     * @param listener      the view change listener
     */
    public void addViewChangeListener(YViewChangeListener listener) {
        changesHelper.addViewChangeListener(listener);
    }
    
    /**
     * Adds component validation listener to this controller. Listener methods are invoked
     * when component validation fails or succeeds (by user action).
     * 
     * @param listener      the validation listener
     */
    public void addComponentValidationListener(YComponentValidationListener listener) {
        validatorHelper.addComponentValidationListener(listener);
    }
    
    /**
     * Remvoes component validation listener.
     * 
     * @param listener      the validation listener
     */
    public void removeComponentValidationListener(YComponentValidationListener listener) {
        validatorHelper.removeComponentValidationListener(listener);
    }

    
    /**
     * Removes view change listener from this controller.
     * 
     * @param listener      the listener to be removed
     */
    public void removeViewChangeListener(YViewChangeListener listener) {
       changesHelper.removeViewChangeListener(listener);
    }



    /**
     * Resets user changes tracking. After calling this method
     * getChangedComponents() returns empty list and
     * hasViewChanges returns false. 
     * 
     * @see #hasViewChanges()
     * @see #cancelViewChanges()
     */
    public void resetViewChanges() {
        changesHelper.resetViewChanges();
    }
    
    /**
     * Returns components that have changes (after the last
     * call to resetViewChanges). 
     * 
     * @return list of changed components
     * @see #resetViewChanges() 
     */
    public List getChangedComponents() {
        return changesHelper.getChangedComponents();
    }
    
  
    
    
    /**
     * Checks if user has changed a view component after the last
     * call to resetViewChanges. Note that this method does not call
     * getChangedComponents, that investigates each component changes individually. 
     * This method just uses flagging
     * to check if viewChanged has happened since resetViewChanges().
     * 
     * @return true if user has changed the view 
     * @see #resetViewChanges() 
     */
    public boolean hasViewChanges() {
        return this.viewChanges;
     }
    

    
    /**
     * Cancels user changes in view (and view model) to the state set in
     * resetViewChanges. 
     * 
     * @see #resetViewChanges() 
     * 
     */    
    public void cancelViewChanges() {
        changesHelper.cancelViewChanges();
    }

    /**
     * Checks if component's value hasChanged after the last 
     * resetChanges call. 
     *
     * @param comp  the component to be checked
     * @return true if the component's value has changed
     */
    public boolean hasChanges(YIComponent comp) {
        return changesHelper.hasChanges(comp);
    }     
        
    /**
     * Checks if component's value hasChanged after the last 
     * resetChanges call.
     * 
     * @param comp the component to be checked
     * @return true if the component's value has changed
     */
    public boolean hasChanges(YIModelComponent comp) {
        return changesHelper.hasChanges(comp);
    }
    /**
     * Checks if component's value hasChanged after the last 
     * resetChanges call.
     * 
     * @param comp the component to be checked
     * @return true if the component's value has changed
     */
    public boolean hasChanges(YIExtendedModelComponent comp) {
        return changesHelper.hasChanges(comp);
    }
    
   
    /**
     * Cancel's user changes in a component (sets back original value
     * that was read in resetChanges method).
     * 
     * @param comp the component which changes should be cancelled
     * @return true if user changes was cancelled
     */
    public boolean cancelChanges(YIModelComponent comp) {
        return changesHelper.cancelChanges(comp);
    }
    
    /**
     * Cancel's user changes in a component (sets back original value
     * that was read in resetChanges method).
     * 
     * @param comp the component which changes should be cancelled
     * @return true if user changes was cancelled
     */
    public boolean cancelChanges(YIExtendedModelComponent comp) {
        return changesHelper.cancelChanges(comp);
    }
    
    /**
     * Reads component's value: stores the state for cancelChanges
     * and hasChanges methods.
     * 
     * @param comp the component that holds the value to be stored
     */
    public void resetChanges(YIModelComponent comp) {
        changesHelper.resetChanges(comp);
    }
    
    /**
     * Reads component's value: stores the state for cancelChanges
     * and hasChanges methods.
     * 
     * @param comp the component that holds the value to be stored
     */
    public void resetChanges(YIExtendedModelComponent comp) {
        changesHelper.resetChanges(comp);
    }
    
    
    /**
     * Sets view of this controller to "dirty" state. Spreads also the 
     * dirty state to all child components of the view. After this,
     * the refreshView method of this controller (and also refreshView 
     * methods of child component controllers) will be called. However, 
     * if the view is in a tabbed pane tab, that is not currently selected, 
     * the refreshView is not called until the tab will be selected. 
     * 
     * @param refreshData   some object to be passed to refreshView method
     */
    public void setViewDirty(Object refreshData) {
        refreshHelper.setViewDirty(refreshData);
    }
    
    /**
     * Calls refreshView  (clearing the dirty state) for this 
     * controller and all the controllers of the child views. 
     * 
     * @param forceRefresh if true, refreshView is called always, if false,
     *                     refreshView is called only if view is "dirty"
     */
    public void startViewRefresh(boolean forceRefresh) {
        refreshHelper.startViewRefresh(forceRefresh);
    }

    
    /**
     * Updates view as the result of setViewDirty call. Extending
     * class should override this method to implement functionality 
     * needed for updating the view based on refreshObject
     * 
     * @see #setViewDirty(Object)
     * 
     * @param refreshObject some object passed by setViewDirty
     */
    public void refreshView(Object refreshObject) {
    }
    
    /**
     * This method is for framework internal use.
     * @return  the refresh helper of this controller
     */
    YRefreshHelper getRefreshHelper() {
       return this.refreshHelper;
    }


     
    /**
     * Invokes a method in this controller. All the exceptions are passed
     * to controller error handler (including YMethodNotFoundException).
     * 
     * @param methodName	the name of the method
     * @param params		parameters for the method
     * @param paramClasses	the classes of the parameters
     */
    public void invokeMethodIfFound(String methodName, Object[] params, Class[] paramClasses) {
    	mvcHelper.invokeMethodIfFound(methodName, params, paramClasses);
    }
    
    /**
     * Invokes a method without parameters in this controller. All the 
     * exceptions are passed to controller error handler (including possible 
     * YMethodNotFoundException).
     * 
     * @param methodName the method name
     */
    public void invokeMethodIfFound(String methodName) {
        mvcHelper.invokeMethodIfFound(methodName);
    }
    
    /**
     * Invokes a method in this controller.
     *
     * @param methodName the method name
     * @param params		parameters for the method
     * @param paramClasses	the classes of the parameters* @param param		 the parameter object for the method
     *
     * @throws YMethodNotFoundException 	if suitable method is not found
     * @throws InvocationTargetException	exception in method call via reflection
     * @throws IllegalAccessException		exception in method call via reflection
     * @throws IllegalArgumentException		exception in method call via reflection
     */
    public void invokeMethod(String methodName, Object[] params, Class[] paramClasses) throws IllegalArgumentException, YMethodNotFoundException, IllegalAccessException, InvocationTargetException {
    	mvcHelper.invokeMethodIfFound(methodName, params, paramClasses);
    }

    /**
     * Invokes component's change method in this controller. For example,
     * if component's mvc-name is <code>customer.name</code> method
     * <code>public void customerNameChanged()</code> is called.
     * 
     * @param comp	the component 
     * 
     * @throws YMethodNotFoundException 	if suitable method is not implemented
     * @throws InvocationTargetException	exception in method call via reflection
     * @throws IllegalAccessException		exception in method call via reflection
     * @throws IllegalArgumentException		exception in method call via reflection
      */
    public void invokeComponentChanged(YIComponent comp, String fieldName) throws IllegalArgumentException, YMethodNotFoundException, IllegalAccessException, InvocationTargetException {
    	mvcHelper.invokeComponentChanged(comp, fieldName);
    }
    
    /**
     * Updates the view model and triggers events associated
     * with the change in this controller. Components implementing
     * YIModelComponent should call this method, when it's likely 
     * that the contents of the component have been changed
     * (for example in focus lost event). 
     * 
     * All possible exceptions are passed to controller error handler.
     * 
     * @param comp the view component that has changed
        */
    public void updateModelAndController(YIModelComponent comp)  {
        mvcHelper.updateModelAndController(comp);
    }
    
    
    /**
     * Updates the view model and triggers events associated
     * with the change in this controller. Components implementing
     * YIExtendedModelComponent should call this method, when it's likely 
     * that the contents of the component have been changed
     * (for example in focus lost event). 
     * 
     * All possible exceptions are passed to controller error handler.
     * 
     * @param comp the view component that has changed
     * @param fieldName YIExtendedModelComponent field that has changed
     */
    public void updateModelAndController(YIExtendedModelComponent comp, String fieldName)  {
        mvcHelper.updateModelAndController(comp, fieldName);
    }

    
    /**
     * This method is executed when a component's setModelValue method
     * throws an exception. This may happen when the framework
     * copies from a model to a view component. 
     * 
     * This method may be overridden for customized handling of 
     * the exception. The default implementation passes exception 
     * to controller error handler.
     * 
     * @param e		the exception which holds the 
     * 				<code>originalException</code> thrown by a component
     */
    public void componentSetValueFailed(YComponentSetValueException e) {
    	this.handleException(e);
    }
    
    /**
     * This method is executed when a component's getModelValue method
     * throws an exception. This may happen when the framework
     * copies data from a view component to a model. 
     * 
     * This method may be overridden for customized handling of 
     * the exception. The default implementation passes exception 
     * to controller error handler.
     * 
     * @param ex		the exception which holds the 
     * 					<code>originalException</code> thrown by a component
     */
	public void componentGetValueFailed(YComponentGetValueException ex) {
		this.handleException(ex);
	}
    /**
     * This method is executed if getting data from a view model
     * class ends up in an exception. This may happen when the 
     * framework copies data from a model to a view component.
     * 
     * Usually this method shouldn't be overridden: to handle
     * an exception thrown in a model getter method, override
     * method <code>modelGetterException</code> instead. 
     * This method just passes <code>originalException</code>
     * InvocationTargetException to <code>modelGetterException</code> 
     * method. All other exceptions are passed to controller error handler.
     * 
     * @param ex	the exception which holds the 
     * 				<code>originalException</code> that occurred
     * 				when setting data into a model
     */
	public void modelGetValueFailed(YModelGetValueException ex) {
		if (ex.getOriginalException() instanceof InvocationTargetException) {
			this.modelGetterException(
					ex.getOriginalException().getCause(), ex );
		} else {
			this.handleException(ex);
		}	
	 }
    
    /**
     * This method is executed if setting data into a view model
     * class ends up in an exception. This may happen when the 
     * framework copies data from a view component to a model. 
     * 
     * Usually this method shouldn't be overridden: to handle
     * an exception thrown in a model setter method, override
     * method <code>modelSetterException</code> instead. 
     * This method just passes <code>originalException</code>
     * InvocationTargetException to <code>modelSetterException</code> 
     * method. All other exceptions are passed to controller error handler.
     * 
     * @param ex	the exception which holds the 
     * 				<code>originalException</code> that occurred
     * 				when setting data into a model
     */
    public void modelSetValueFailed(YModelSetValueException ex) {
    	if (ex.getOriginalException() instanceof InvocationTargetException) {
			this.modelSetterException(
					ex.getOriginalException().getCause(), ex );
		} else {
			this.handleException(ex);
		}	
    }
    
    
    /**
     * This is a convenience method for handling exceptions
     * thrown by a getter method in a model class. If a model
     * for some reason may throw an exception, this
     * method should be overridden to handle that exception.
     * 
     * The default implementation passes the 
     * <code>detailedException</code> to to controller error handler.
     * 
     * @param modelException	the exception thrown by a model getter
     * @param detailedException	the detailed information about the exception
     */
	public void modelGetterException(Throwable modelException, 
			YModelGetValueException detailedException) {
		this.handleException(detailedException);			
	}
    
    /**
     * This is a convenience method for handling exceptions
     * thrown by a setter method in a model class. If a model
     * validates data and may throw an exception, this
     * method should be overridden to handle that exception.
     * 
     * The default implementation passes the 
     * <code>detailedException</code> to controller error handler.
     * 
     * @param modelException	the exception thrown by a model setter
     * @param detailedException	the detailed information about the exception
     */
	public void modelSetterException(Throwable modelException, 
			YModelSetValueException detailedException) {
		this.handleException(detailedException);			
	}
    
    
     /**
     * This method is called after failed component validation. 
     * <p>
     * When implementing YIValidatorComponent, call notifyValidationListenersOnFail,
     * do not call this method directly. This method is meant only for overriding and
     * implementing application code after validation. The default implementation passes exception 
     * to controller error handler.
     * 
     * @param e		the exception data 
    */
    public void componentValidationFailed(YComponentValidationException e) {
    	this.handleException(e);
    }
    
    /**
     * This method is called after successful component validation. 
     * <p>
     * When implementing YIValidatorComponent, call notifyValidationListenersOnSuccess,
     * do not call this method directly. This method is meant only for overriding and
     * implementing application code after validation. The default implementation is empty.
     * 
     * @param comp  the validated component
     */
    public void componentValidationSucceeded(YIComponent comp) {
    
    }
    
    /**
    * This method should be called from YIValidatorComponent when validation
    * has succeeded. 
    * 
     * @param comp   the validated component
     */
    public void notifyValidationListenersOnSuccess(YIComponent comp) {
        validatorHelper.notifyValidationListenersOnSuccess(comp);
    }
    
    /**
     * This method should be called from YIValidatorComponent when validation
     * has failed. 
     * 
      * @param e   the validation exception
      */
     public void notifyValidationListenersOnFail(YComponentValidationException e) {
         validatorHelper.notifyValidationListenersOnFail(e);
     }
    
    /**
     * Checks if view component values equals model values.
     * 
     * @return	if view component values match model values
     */
    public boolean viewEqualsModel() {
    	return (getUnsynchronizedComponents().size() == 0);
    }
    
    /**
     * Returns view componenents that implement YIModelComponent interface
     * and that are synchronized with a view model.
     * A component and corresponding model field might be unsynchronized
     * because component or model may throw an exception during get/set-method
     * call. 
     * 
     * @return	the components which values equal corresponding model field values
     */
    public ArrayList getSynchronizedComponents() {
        return validatorHelper.getSynchronizedComponents();
    }
    
    /**
     * Returns view componenents that implement YIModelComponent interface
     * and that are not synchronized with a view model.
     * A component and corresponding model field might be unsynchronized
     * because component or model may throw an exception during get/set-method
     * call. 
     * 
     * @return	the components which values are different 
     * 			from corresponding model field values
     */
    public ArrayList getUnsynchronizedComponents() {
        return validatorHelper.getUnsynchronizedComponents();
    }
    
    /**
     * Checks if view components that implement YIValidatorComponent
     * are all in valid state.
     * 
     * @return true if all valueValid method of all components return true
     */
    public boolean viewValuesValid() {
    	return (getInvalidComponents().size() == 0);
    }
    
    /**
     * Returns view components that implement YIValidatorComponent 
     * interface and return false when valueValid method is called. 
     * 
     * @return	 the invalid components
     */
    public List getInvalidComponents() {
        return validatorHelper.getInvalidComponents();
    }
    
    /**
     * Returns view components that implement YIValidatorComponent 
     * interface and return true when valueValid method is called. 
     * 
     * @return	 the valid components
     */
    public Collection getValidComponents() {
        return validatorHelper.getValidComponents();
    }
    
    /**
     * Sets view changed. Usually it is not necessary to call this method, 
     * since YController tracks changes automatically. (when component
     * value changes, view is set changed). If this method is called 
     * manually with true, hasViewChanges() returns true, but getChangedComponents
     * might still return empty list.
     *
     * @param b     if view must be in changed state
     */
    public void setViewChanged(boolean b) {
        this.viewChanges = b;
    }
    

    
    /**
     * This method is executed when the view has changed by 
     * a user action. This method may be overridden for
     * tracking user actions.
     * 
     * @param comp the component that changed
     */  
    public void viewChanged(YIComponent comp) {
        
    }
    
    /**
     * This method is executed when the model has changed by
     * the application and notifyObservers is called by the 
     * model object (extending Observable class). Also when
     * user changes some field connected to the model,
     * this will be called. Parameter event tells us
     * what kind of change happened.
     * 
     * @param event	 the change event
     */
    public void modelChanged(YModelChangeEvent event) {
        
    }
    
   
    
    /**
     * View should pass an event to be handled in controller via
     * this method. Controller should handle the method or pass
     * it to parent controller. The default implementation
     * passes event to parent. 
     * 
     * For application event broadcasting use sendApplicationEvent and
     * receivedApplicationEvent methods.
     * 
     * @param event		the event
     * 
     */
    public void handleApplicationEvent(YApplicationEvent event) {
    	if (getParent() != null) {
    		getParent().handleApplicationEvent(event);
    	}
    }

    /**
     * View should pass an event to be handled in controller via
     * this method. Controller should handle the method or pass
     * it to its child controllers. The default implementation
     * passes event to all child controllers. 
     * 
     * For application event broadcasting use sendApplicationEvent and
     * receivedApplicationEvent methods.
     * 
     * @param event		the event
     * 
     */
    public void handleApplicationEventDown(YApplicationEvent event) {
    	if (getChildren() != null){
    	    for (int i=0; i < getChildren().size(); i++) {
                YController child = (YController) children.get(i);
                child.handleApplicationEventDown(event);
                
            }
    	}
    }    
    
    /**
     * Received an event sent via sendApplicationEvent. Inheriting 
     * class should implement this method for handling broadcasted
     * events. 
     * 
     * @param event	 the event
     */
    public void receivedApplicationEvent(YApplicationEvent event) {
    	
    }
       
   
    /**
     * Registers controller for listening an application event. To receive the
     * event, the listener must implement method
     * <code>receivedApplicationEvent(YApplicationEvent)</code>
     * 
     * IMPORTANT: This method sets up STATIC listening. If this
     * controller is used in a window, unregister must be called to free resources
     * (otherwise there will be memory leak!). Calling unregisterRecursively
     * for highest parent is usually sufficient.
     * 
     * @param eventName  	the event name
     */
    public void register(String eventName) {
        eventHelper.register(eventName);
    }
    
    /**
     * Unregisters controller for listening specified application event.
     * (sent via sendApplicationEvent).
     * 
     * @param event 	the event to unregister
     */
    public void unregister(String event) {
        eventHelper.unregister(event);
    }
    
    /**
     * Unregisters this controller for listenening application events 
     * sent via sendApplicationEvent. 
     */
    public void unregister() {
        eventHelper.unregister();
    }
    
    /**
     * Unregisters this controller and all the child controllers for
     * listenening application events sent via sendApplicationEvent. 
     */
    public void unregisterRecursively() {
    	eventHelper.unregisterRecursively();
    }

    
    /**
     * Sends an event to all registered listeners.
     * 
     * @param event 	the event 
     */
    public static void sendApplicationEvent(YApplicationEvent event) {
        YEventHelper.sendApplicationEvent(event);
    }

    
	
    /**
     * Sets null value to all YIModelComponents found
     * from the given view (found via getters). The method
     * also updates null values to the model.
     *  
     */
	public void clearView() {
		clearView (new Class[0]);
	}
	
    /**
     * Sets null value to all YIModelComponents found
     * from the view of this controller (found via getters). The method
     * also updates null values to the model.
     *  
     * @param classesToIgnore the classes to ignore during clearing, for example
     * 					YLabel is usually desired to be ignored 
     */
	public void clearView(Class[] classesToIgnore) {
        mvcHelper.clearView(classesToIgnore);
	}
    
    private static Map createMap() {
        return Collections.synchronizedMap(new HashMap());
    }
    
    /**
     * Adds property into context of the application.
     * (This method is a just a wrapper for static Hashtable.)
     * 
     * @param key   the property key
     * @param value the property value
     */
    public static void putIntoAppContext(Object key, Object value) {
        wholeAppProps.put(key, value);
    }
    
    /**
     * Gets property from context of the application.
     * (This method is a just a wrapper for static Hashtable.)
     * 
     * @param key   the property key
     * @return       the property value
     */
    public static Object getFromAppContext(Object key) {
       return wholeAppProps.get(key);
    }
    
    /**
     * Puts given property into controller's context. 
     * The method stores property into YProperty of the controller.
     * 
     * @param key   the property key
     * @param value the property value
     */
    public void putIntoControllerContext(Object key, Object value) {
        getYProperty().put(key, value);
    }
    
    /**
     * Gets property from the controller's context. If property key
     * is not found from the context of the given controller, method
     * will try to find it from parent controllers. If the key is not found 
     * from any controller in the current controller tree,
     * the method will try to find it from the application context. 
     * 
     * @param key       the property key
     * @return value    the property value, null if key is not found (or the value is null)
     */
    public Object getFromControllerContext(Object key) {
        YController controller = this;
        while (controller != null) {
            if (controller.getYProperty().containsKey(key)) {
                return controller.getYProperty().get(key);
            } else {
                controller = controller.getParent();
            }
        }    
        // if not found from any controller, trying to find from application context:
        return getFromAppContext(key);
    }

    /**
     * Check if controller is in "dirty" state
     * 
     * @return true if controller is in "dirty" state, otherwise false.
     */
    public boolean isDirty(){
    	return refreshHelper.isDirty();
    }
    
    /**
     * Notifies ViewChangeListeners. Usually it is not needed to call this directly,
     * since every view change event will call this method. 
     * 
     * @param comp  the changed component
     */
    public void notifyViewChangeListeners(YIComponent comp) {
        changesHelper.notifyViewChangeListenersOfChange(comp);
    }
     //
    
     
    /**
     * Sets error handler for this controller. Handler is added put into 
     * conroller context so that all child controller will use the same handler
     * (if not specified otherwise). 
     * 
     * @param errorHandler  the error handler
     */
    public void setErrorHandler(YIErrorHandler errorHandler) {
        this.putIntoControllerContext(YIErrorHandler.class, errorHandler);
    }
    
    /**
     * Returns error handler used in YController exception handling 
     * (see handleException method). If error handler is not set to 
     * this controller, error handler of YErrorManager is used for 
     * backward compatibility. 
     * 
     * @return the error handler used in this controller
     */
    public YIErrorHandler getErrorHandler() {
        YIErrorHandler handler = (YIErrorHandler) this.getFromControllerContext(YIErrorHandler.class);
        if (handler != null) {
            return handler;
        } else {
            logger.warn("YIErrorHandler not set in controller context. Using error handler of YErrorManager. Error handler should be set in YController since YErrorManager is deprecated.");
            return YErrorManager.getHandler();
        }
    }
    
    /**
     * Exceptions during reflection based framework operations are passed
     * in this method. These operations are:
     * <ul>
     * <li>copying data from model to view</li>
     * <li>copying data from view to model</li>
     * <li>invoking controller event methods (if not found)</li>
     * </ul>
     * Exception classes to be handled here can be found in 
     * fi.mmm.yhteinen.swing.core.error package.
     * <p>
     * This method passes on exceptions to error handler
     * of this controller.
     * 
     * @see #setErrorHandler(YIErrorHandler)
     * 
     * 
     * @param ex    exception in data copy
     */
    public void handleException(Exception ex) {
        this.getErrorHandler().handleException(ex, YUIToolkit.getCurrentWindow());
    }
    
    /**
     * Sets save changes enquirer for this controller. Enquirer is put into 
     * conroller context so that all child controller will use the same enquirer
     * (if not specified otherwise). 
     * <p>
     * YSaveChangesHandler uses primarily enquirer of assiociated controller.
     * 
     * @param enq  the enquirer
     */
    public void setSaveChangesEnquirer(YIChangesEnquirer enq) {
        this.putIntoControllerContext(YIChangesEnquirer.class, enq);
    }
    
    /**
     * Returns save changes enquirer in this controller context.
     * If not found, null is returned
     * @return  the enquirer
     */
    public YIChangesEnquirer getSaveChangesEnquirer() {
        YIChangesEnquirer enq = (YIChangesEnquirer) this.getFromControllerContext(YIChangesEnquirer.class);
       return enq;
    }
    
    /**
     * Sets view reference to null.
     */
    public void nullifyView() {
        this.view = null;
    }
    
    /**
     * Sets model reference to null.
     */
    public void nullifyModel() {
        this.model = null;
    }
    
    /**
     * Calling this method is not usually recommended, but it may be used,
     * to trigger change events. For example, if
     * field contents is modified directly in code, this method may be called to
     * trigger viewChanged events.
     * 
     * @param comp  the changed component
     */
    public void triggerChangeEvents(YIComponent comp) {
        try {
            String name = (String) comp.getYProperty().get(YIComponent.MVC_NAME);
            this.mvcHelper.triggerChangeEvents(comp, name);
        } catch (Exception ex) {
            this.handleException(ex);
        }
    }

}
