/*
 * Created on Aug 29, 2007
 *
 */
package fi.mmm.yhteinen.swing.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import fi.mmm.yhteinen.swing.core.error.YComponentValidationException;
import fi.mmm.yhteinen.swing.core.error.YModelGetValueException;
import fi.mmm.yhteinen.swing.core.tools.YCoreToolkit;
import fi.mmm.yhteinen.swing.core.tools.YUIToolkit;

/**
 * Helper class for controller validation.
 * <p>
 * This class is for YController internal use, do not 
 * override or use directly. 
 * 
 * @author Tomi Tuomainen
 */
public class YValidatorHelper {

    private YController controller;

    private List validationListeners = new ArrayList();
    
    /**
     * @param controller    the controller using this helper
     */
    public YValidatorHelper(YController controller) {
        super();
        this.controller = controller;
    }
    
    /**
     * Adds component validation listener to this controller. Listener methods are invoked
     * when component validation fails or succeeds (by user action).
     * 
     * @param listener      the validation listener
     */
    public void addComponentValidationListener(YComponentValidationListener listener) {
        validationListeners.add(listener);
    }
    
    /**
     * Remvoes component validation listener.
     * 
     * @param listener      the validation listener
     */
    public void removeComponentValidationListener(YComponentValidationListener listener) {
        validationListeners.remove(listener);
    }
    
    /**
     * This method should be called from YIValidatorComponent when validation
     * has succeeded. 
     * 
      * @param comp   the validated component
      */
     public void notifyValidationListenersOnSuccess(YIComponent comp) {
         controller.componentValidationSucceeded(comp);
         Iterator it = validationListeners.iterator();
         while (it.hasNext()) {
            YComponentValidationListener listener = (YComponentValidationListener) it.next();
            listener.componentValidationSucceeded(controller, comp);
         }
     }
     
     /**
      * This method should be called from YIValidatorComponent when validation
      * has failed. 
      * 
       * @param e   the validation exception
       */
      public void notifyValidationListenersOnFail(YComponentValidationException e) {
          controller.componentValidationFailed(e);
          Iterator it = validationListeners.iterator();
          while (it.hasNext()) {
             YComponentValidationListener listener = (YComponentValidationListener) it.next();
             listener.componentValidationFailed(controller, e);
          }
      }
      
      /**
       * Returns view componenents that implement YIModelComponent interface
       * and that are synchronized with a view model.
       * A component and corresponding model field might be unsynchronized
       * because component or model may throw an exception during get/set-method
       * call. 
       * 
       * @return    the components which values equal corresponding model field values
       */
      public ArrayList getSynchronizedComponents() {
          YIComponent view = controller.getView();
          ArrayList unsynchronized = getUnsynchronizedComponents();
          ArrayList comps = YUIToolkit.getViewComponents(view);
          ArrayList result = new ArrayList(comps.size());
          Iterator it = comps.iterator();
          while (it.hasNext()) {
              Object obj = (Object) it.next();
              if (obj instanceof YIModelComponent) {
                  YIModelComponent comp = (YIModelComponent) obj;
                  if (! unsynchronized.contains(comp)) {
                      result.add(comp);
                  }
              }
          }
          return result;
      }
      
      /**
       * Returns view componenents that implement YIModelComponent interface
       * and that are not synchronized with a view model.
       * A component and corresponding model field might be unsynchronized
       * because component or model may throw an exception during get/set-method
       * call. 
       * 
       * @return    the components which values are different 
       *            from corresponding model field values
       */
      public ArrayList getUnsynchronizedComponents() {
          YIComponent view = controller.getView();
          Object model = controller.getModel();
          ArrayList result = new ArrayList();
          try {
              ArrayList comps = YUIToolkit.getViewComponents(view);
              Iterator it = comps.iterator();
              while (it.hasNext()) {
                  Object obj = (Object) it.next();
                  if (obj instanceof YIModelComponent) {
                      YIModelComponent comp = (YIModelComponent) obj;
                      Object compValue = comp.getModelValue();
                      Object beanValue = null;
                      if (model != null) {
                          String fieldName = (String) comp.getYProperty().get(YIComponent.MVC_NAME);
                          if (fieldName != null) {
                              try {
                                  beanValue = YCoreToolkit.getBeanValue(model, fieldName);
                              } catch (Exception ex) {
                                  controller.modelGetValueFailed(new YModelGetValueException(
                                          ex, comp));
                              }
                              if (!YCoreToolkit.equals(beanValue, compValue)) {
                                  result.add(comp);
                              }
                          }
                      }
                  }
              }
          } catch (Exception e) {
              controller.handleException(e);
          }
          return result;
      }
      

      
      /**
       * Returns view components that implement YIValidatorComponent 
       * interface and return false when valueValid method is called. 
       * 
       * @return     the invalid components
       */
      public List getInvalidComponents() {
          YIComponent view = controller.getView();
          ArrayList result = new ArrayList();
          ArrayList comps = YUIToolkit.getViewComponents(view);
          Iterator it = comps.iterator();
          while (it.hasNext()) {
              Object obj = (Object) it.next();
              if (obj instanceof YIValidatorComponent) {
                  YIValidatorComponent comp = (YIValidatorComponent) obj;
                  if (!comp.valueValid()) {
                      result.add(comp);
                  }
              }
          }
          return result;
      }
      
      /**
       * Returns view components that implement YIValidatorComponent 
       * interface and return true when valueValid method is called. 
       * 
       * @return     the valid components
       */
      public Collection getValidComponents() {
          YIComponent view = controller.getView();
          ArrayList result = new ArrayList();
          ArrayList comps = YUIToolkit.getViewComponents(view);
          Iterator it = comps.iterator();
          while (it.hasNext()) {
              Object obj = (Object) it.next();
              if (obj instanceof YIValidatorComponent) {
                  YIValidatorComponent comp = (YIValidatorComponent) obj;
                  if (comp.valueValid()) {
                      result.add(comp);
                  }
              }
          }
          return result;
      }
      
    
    
}
