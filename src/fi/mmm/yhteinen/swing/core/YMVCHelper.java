/*
 * Created on Aug 29, 2007
 *
 */
package fi.mmm.yhteinen.swing.core;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;

import fi.mmm.yhteinen.swing.core.error.YMethodNotFoundException;
import fi.mmm.yhteinen.swing.core.error.YModelGetValueException;
import fi.mmm.yhteinen.swing.core.error.YModelSetValueException;
import fi.mmm.yhteinen.swing.core.tools.YCoreToolkit;
import fi.mmm.yhteinen.swing.core.tools.YUIToolkit;

/**
 * Helper class for creating and maintaining MVC hierarchy. 
 * <p>
 *  This class is for YController internal use, do not 
 * override or use directly. 
 * 
 * @author Tomi Tuomainen
 */
public class YMVCHelper {
   
    private YController controller;
    
    /**
     * 
     * @param controller    the controller using this helper
     */
    public YMVCHelper(YController controller) {
        this.controller = controller;
    }
    
    /**
     * Wires up the MVC structure after model and view has been set. 
     * If model or view is null, the method does not do anything.
     */
    void wireUp() {
        Object model = controller.getModel();
        YIComponent view = controller.getView();
        // if both model and view has been set, we can reset view state
        if (model != null && view != null) {
            view.getYProperty().put(YIComponent.MODEL, model);
            controller.copyToView(null); 
            controller.resetViewChanges();
        }
    }
    
    /**
    * Initializes listeners for the view.
    */
   void addComponentListeners() {
       YIComponent view =  controller.getView();
       if (view instanceof YIControllerComponent) {
           ((YIControllerComponent)view).addViewListener(controller);
       }
       ArrayList components = YUIToolkit.getViewComponents(view);
       Iterator it = components.iterator();
       while (it.hasNext()) {
           Object obj = it.next();
           if (obj instanceof YIControllerComponent) {
                ((YIControllerComponent) obj).addViewListener(controller);
           }
       }
   }
   

   /**
    * Copies fields between model and view components.
    * @param toModel           if true, component values will be copied to model; 
    *                          if false, model values will be copied to components
    * @param view              the view (not necessarily view of this controller)
    * @param model              the model (not necessarily model of this controller)                         
    * @param fieldToUpdate     the model field name to be copied
    */
   void copy(boolean toModel, String fieldToUpdate, YIComponent view, Object model) {
       ArrayList components = YUIToolkit.getViewComponents(view);
       Iterator it = components.iterator();
       while (it.hasNext()) {
           Object comp = it.next();
           // copying first extended fields since this is usually desired order (YComboBox):
           if (comp instanceof YIExtendedModelComponent) {
               YIExtendedModelComponent extComp = (YIExtendedModelComponent) comp;
               String[] fieldNames = extComp.getExtendedFields();
               for (int i=0; i < fieldNames.length; i++) {
                   String fieldName = fieldNames[i];
                   if (shouldCopy(fieldName, fieldToUpdate)) {
                       if (toModel) {
                           copyFromComponentToModel(extComp, fieldName, model);
                       } else {
                           copyFromModelToComponent(extComp, fieldName, model);
                       }
                   }
               }
           }
           // copying then model value:
           if (comp instanceof YIModelComponent) {
               YIModelComponent modelComponent = (YIModelComponent) comp;
               String mvcName = (String) modelComponent.getYProperty().get(YIComponent.MVC_NAME);
               if (shouldCopy(mvcName, fieldToUpdate)) {
                   if (toModel) {
                       copyFromComponentToModel(modelComponent, model);
                   } else {
                       copyFromModelToComponent(modelComponent, model);
                   }
               }
           }
       }
   }
   
   /**
    * Checking if fieldName matches field to be updated.
    * 
    * @param fieldName     current field name
    * @param fieldToUpdate the given field to be updated
    * @return  true if field with fieldName should be updated
    */
   private boolean shouldCopy(String fieldName, String fieldToUpdate) {
       if (fieldName != null) {
           if (fieldToUpdate != null) {
               if (fieldName.startsWith(fieldToUpdate)) {
                   return true;
               }    
               // fieldToUpdate not specified, should always copy:
           } else {
               return true;
           }
       } 
       return false;
   }
   
   /**
    * Copies value from component to view model. 
    * 
    * @param comp  the component
    * @param model  the model
    * @return      true, if value was copied
    */
   public boolean copyFromComponentToModel(YIModelComponent comp, Object model) {
       // model may be null, in which case nothing is done...
       if (model != null) {
           if (!isReadOnlyComponent(comp)) {
               try {
                   String fieldName = (String) comp.getYProperty().get(YIComponent.MVC_NAME);
                   if (fieldName != null) {
                       Object compValue = comp.getModelValue();
                       Object beanValue = null;
                       try {
                           beanValue = YCoreToolkit.getBeanValue(model, fieldName);
                       } catch (Exception ex) {
                           controller.modelGetValueFailed(new YModelGetValueException(ex, comp));
                           return false;
                       }
                       // if view has changed compared to the model...
                       if (!YCoreToolkit.equals(beanValue, compValue)) {
                           try {
                               YCoreToolkit.setBeanValue(model, fieldName, compValue);
                               return true;
                           } catch (Exception ex) {
                               controller.modelSetValueFailed(new YModelSetValueException(ex, comp, compValue));
                           }
                       }
                   }
               } catch (Exception e) {
                   // other unexpected exceptions end up here
                   controller.handleException(e);
               }
           }
       }
       return false;
   }
   
   /**
    * Copies value from component to view model. 
    * 
    * @param comp          the component
    * @param fieldName     the YIExtendedModelComponent field name to copy
    * @return              true, if value was copied
    */
   public boolean copyFromComponentToModel(YIExtendedModelComponent comp, String fieldName, Object model) {
       // model may be null, in which case nothing is done...
       if (model != null) {
           // checking is component is "read only"
           if (!isReadOnlyComponent(comp)) {
               try {
                   if (fieldName != null) {
                       Object compValue = comp.getModelValue(fieldName);
                       Object beanValue = null;
                       try {
                           beanValue = YCoreToolkit.getBeanValue(model, fieldName);
                       } catch (Exception ex) {
                           controller.modelGetValueFailed(new YModelGetValueException(ex, comp));
                           return false;
                       }
                       // if view has changed compared to the model...
                       if (!YCoreToolkit.equals(beanValue, compValue)) {
                           try {
                               YCoreToolkit.setBeanValue(model, fieldName, compValue);
                               return true;
                           } catch (Exception ex) {
                               controller.modelSetValueFailed(new YModelSetValueException(ex, comp, compValue));
                           }
                       }
                   }
               } catch (Exception e) {
                   // other unexpected exceptions end up here
                   controller.handleException(e);
               }
           }
       }
       return false;
   }
   
   
   /**
    * @param comp      the component
    * @return          if component is read only (has YIComponent.READ_ONLY property)
    */
   public boolean isReadOnlyComponent(YIComponent comp) {
       Boolean readOnly = (Boolean) comp.getYProperty().get(YIComponent.READ_ONLY);
       if (readOnly == null || !readOnly.booleanValue()) {
           return false;
       }
       return true;
   }
   
   /**
    * Copies value from view model to component. 
    * 
    * @param comp  the component
    * @return  true if value was copied
    */
   public boolean copyFromModelToComponent(YIModelComponent comp,Object model) {
       try {
           // searching model's get-method for the component...
           String fieldName = (String) comp.getYProperty().get(YIComponent.MVC_NAME);
           if (fieldName != null) {
               Object beanValue = null;
               try {
                   // model may be null, in which case null is copied to view...
                   if (model != null) {
                       beanValue = YCoreToolkit.getBeanValue(model, fieldName);
                   }
               } catch (Exception ex) {
                   controller.modelGetValueFailed(new YModelGetValueException(
                           ex, comp));
                   return false;
               }
               
               try {
                   comp.setModelValue(beanValue);
                   return true;
               } catch (Exception ex) {
                   controller.modelSetValueFailed(new YModelSetValueException(ex, comp, beanValue));
               }
           }
       } catch (Exception e) {
           // other unexpected exceptions end up here
           controller.handleException(e);
       }
       return false;
   }
   /**
    * Copies value from view model to component. 
    * 
    * @param comp  the component
    * @param fieldName     the YIExtendedModelComponent field name to copy
    * @return  true if value was copied
    */
    public boolean copyFromModelToComponent(YIExtendedModelComponent comp, String fieldName, Object model) {
        try {
            // searching model's get-method for the component...
            if (fieldName != null) {
                Object beanValue = null;
                try {
                    // model may be null, in which case null is copied to view...
                    if (model != null) {
                        beanValue = YCoreToolkit.getBeanValue(model, fieldName);
                    }
                } catch (Exception ex) {
                    controller.modelGetValueFailed(new YModelGetValueException(
                            ex, comp));
                    return false;
                }
                
                try {
                  comp.setModelValue(fieldName, beanValue);
                } catch (Exception ex) {
                    controller.modelSetValueFailed(new YModelSetValueException(ex, comp, beanValue));
                }
            }
        } catch (Exception e) {
            // other unexpected exceptions end up here
            controller.handleException(e);
        }
        return false;
    }
    
    /**
     * Updates the view model and triggers events associated
     * with the change in this controller. Components implementing
     * YIExtendedModelComponent should call this method, when it's likely 
     * that the contents of the component have been changed
     * (for example in focus lost event). 
     * 
     * All possible exceptions are passed to controller.
     * 
     * @param comp the view component that has changed
     * @param fieldName YIExtendedModelComponent field that has changed
     */
    public void updateModelAndController(YIExtendedModelComponent comp, String fieldName)  {
        try {
                boolean changed = false;
                // checking if component has truly changed:
                changed = copyFromComponentToModel(comp, fieldName, controller.getModel());
                if (changed) {
                    triggerChangeEvents(comp, fieldName);
                }
        } catch (Exception ex) {
            controller.handleException(ex);
        }
    }
    
    /**
     * Updates the view model and triggers events associated
     * with the change in this controller. Components implementing
     * YIModelComponent should call this method, when it's likely 
     * that the contents of the component have been changed
     * (for example in focus lost event). 
     * 
     * All possible exceptions are passed to controller.
     * 
     * @param comp the view component that has changed
        */
    public void updateModelAndController(YIModelComponent comp)  {
        try {
            String fieldName = (String) comp.getYProperty().get(YIComponent.MVC_NAME);
            if (comp instanceof YISharedModelComponent) {
                // handle could have been changed, so copying data anyway...
                copyFromComponentToModel(comp, controller.getModel());
               triggerChangeEvents(comp, fieldName);
            } else {
                boolean changed = false;
                // checking if component has truly changed:
                changed = copyFromComponentToModel(comp, controller.getModel());
                if (changed) {
                    triggerChangeEvents(comp, fieldName);
                }
            }
        } catch (Exception ex) {
            controller.handleException(ex);
        }
    }
    
    /**
     * Triggers necessary events when user has changed component value.
     * 
     * @param comp  the changed component
     * @param fieldName     the changed field name
     * 
     * @throws IllegalArgumentException exception in method call via reflection
     * @throws YMethodNotFoundException exception in method call via reflection
     * @throws IllegalAccessException   exception in method call via reflection
     * @throws InvocationTargetException exception in method call via reflection
     */
    void triggerChangeEvents(YIComponent comp, String fieldName) throws IllegalArgumentException, YMethodNotFoundException, IllegalAccessException, InvocationTargetException {
        controller.setViewChanged(true);
        Object model = controller.getModel();
        if (model instanceof YModel) {
            YModelChangeEvent event = new YModelChangeEvent();
            event.setFieldName (fieldName);
            event.setUserChange(true);
            ((YModel)model).notifyObservers(event);
        }
        controller.viewChanged(comp);
        try {
            this.invokeComponentChanged(comp, fieldName);
        } catch (Exception ex) { // handling exception here, so that all listeners will get a chance to receive the message
            controller.handleException(ex);
        }
        // notifying change listeners:
        controller.notifyViewChangeListeners(comp);
    }
    
    /**
     * Invokes component's change method in this controller. For example,
     * if component's mvc-name is <code>customer.name</code> method
     * <code>public void customerNameChanged()</code> is called.
     * 
     * @param comp  the component 
     * 
     * @throws YMethodNotFoundException     if suitable method is not implemented
     * @throws InvocationTargetException    exception in method call via reflection
     * @throws IllegalAccessException       exception in method call via reflection
     * @throws IllegalArgumentException     exception in method call via reflection
      */
    public void invokeComponentChanged(YIComponent comp, String fieldName) throws IllegalArgumentException, YMethodNotFoundException, IllegalAccessException, InvocationTargetException {
        //String mvcName = (String) comp.getYProperty().get(YIComponent.MVC_NAME);
        String methodName = YUIToolkit.createMVCMethodName(comp, fieldName, "Changed");
        this.invokeMethod(methodName, null, null);
    }
   
    /**
     * Invokes a method in this controller. All the exceptions are passed
     * to controller (including possible YMethodNotFoundException).
     * 
     * @param methodName    the name of the method
     * @param params        parameters for the method
     * @param paramClasses  the classes of the parameters
     */
    public void invokeMethodIfFound(String methodName, Object[] params, Class[] paramClasses) {
        try {
            invokeMethod(methodName, params, paramClasses);
        } catch (Exception ex) {
            controller.handleException(ex);
        }
    }
    
    /**
     * Invokes a method without parameters in this controller. All the 
     * exceptions are passed to controller (including possible 
     * YMethodNotFoundException).
     * 
     * @param methodName the method name
     */
    public void invokeMethodIfFound(String methodName) {
        try {
            invokeMethod(methodName, null, null);
        } catch (Exception ex) {
            controller.handleException(ex);
        }
    }
    
    /**
     * Invokes a method in this controller.
     *
     * @param methodName the method name
     * @param params        parameters for the method
     * @param paramClasses  the classes of the parameters* @param param      the parameter object for the method
     *
     * @throws YMethodNotFoundException     if suitable method is not found
     * @throws InvocationTargetException    exception in method call via reflection
     * @throws IllegalAccessException       exception in method call via reflection
     * @throws IllegalArgumentException     exception in method call via reflection
     */
    public void invokeMethod(String methodName, Object[] params, Class[] paramClasses) throws IllegalArgumentException, YMethodNotFoundException, IllegalAccessException, InvocationTargetException {
        YCoreToolkit.invokeMethod(controller, methodName, params, paramClasses);
    }
    
    
    /**
     * Sets null value to all YIModelComponents found
     * from the view of this controller (found via getters). The method
     * also updates null values to the model.
     *  
     * @param classesToIgnore the classes to ignore during clearing, for example
     *                  YLabel is usually desired to be ignored 
     */
    public void clearView(Class[] classesToIgnore) {
        YIComponent view = controller.getView();
        ArrayList comps = YUIToolkit.getViewComponents(view);
        Iterator it = comps.iterator();
        while (it.hasNext()) {
            Object obj = it.next();
            if (obj instanceof YIModelComponent) {
                YIModelComponent comp = (YIModelComponent) obj;
                boolean ignore=false;
                for (int i=0; i < classesToIgnore.length; i++) {
                    if (classesToIgnore[i].isAssignableFrom(obj.getClass())) {
                        ignore = true;
                    }
                }
                if (!ignore) {
                    try {
                        comp.setModelValue(null);
                    } catch (Exception ex) {
                        // unexpected exception, this should never happen:
                        controller.handleException(ex);
                    }
                }
            }
        }
        controller.copyToModel(null);
    }

}
