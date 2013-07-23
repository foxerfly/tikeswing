/*
 * Created on Aug 29, 2007
 *
 */
package fi.mmm.yhteinen.swing.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import fi.mmm.yhteinen.swing.core.error.YComponentGetValueException;
import fi.mmm.yhteinen.swing.core.error.YModelGetValueException;
import fi.mmm.yhteinen.swing.core.error.YModelSetValueException;
import fi.mmm.yhteinen.swing.core.tools.YCoreToolkit;
import fi.mmm.yhteinen.swing.core.tools.YUIToolkit;

/**
 * Helper class for handling view changes.
 * <p>
 * This class is for YController internal use, do not 
 * override or use directly. 
 * 
 * @author Tomi Tuomainen
 */
public class YChangesHelper {
    
    private static final String ORIGINAL = "originalModelValue";
    private static final String ORIGINAL_EXTENDED = "originalExtendedModelValue";
  
    private List changeListeners = new ArrayList();
    

    private static Logger logger = Logger.getLogger(YChangesHelper.class);
    
    private YController controller;
   
    /**
     * @param c controller using this helper
     */
    public YChangesHelper (YController c) {
        this.controller = c;
    }
    
    /**
     * Notifies all YViewChangeListeners of controller.
     */
    private void notifyChangeListenersOfReset() {
        Iterator it = changeListeners.iterator();
        while (it.hasNext()) {
           YViewChangeListener listener = (YViewChangeListener) it.next();
           listener.viewChangesReset(controller);
        }
    }
    
    void notifyViewChangeListenersOfChange(YIComponent comp) {
        Iterator it = changeListeners.iterator();
        while (it.hasNext()) {
            YViewChangeListener listener = (YViewChangeListener) it.next();
            try {
                listener.viewChanged(controller, comp);
            } catch (Exception ex) {
               logger.error(ex);// handling exception here, so that all listeners will get a chance to receive the message
            }
        }
    }


    /**
     * Resets user changes tracking.  
     * 
     */
    public void resetViewChanges() {
        controller.setViewChanged(false);
        YIComponent view = controller.getView();
        if (view != null) {
            ArrayList components = YUIToolkit.getViewComponents(view);
            Iterator it = components.iterator();
            while (it.hasNext()) {
                Object comp = it.next();
                if (comp instanceof YIModelComponent) {
                    resetChanges((YIModelComponent) comp);
                }
                if (comp instanceof YIExtendedModelComponent) {
                    resetChanges((YIExtendedModelComponent) comp);
                }
            }
            notifyChangeListenersOfReset();
        }
    }
    
    
    /**
     * Returns components that have changes (after the last
     * call to resetViewChanges). 
     * 
     * @return list of changed components
     */
    public List getChangedComponents() {
        ArrayList components = YUIToolkit.getViewComponents(controller.getView());
        Iterator it = components.iterator();
        ArrayList result = new ArrayList();
        while (it.hasNext()) {
            Object comp = it.next();
            boolean changes = false;
            if (comp instanceof YIModelComponent) {
                YIModelComponent modelComp = (YIModelComponent) comp;
                if (hasChanges(modelComp)) {
                    logger.debug("changes in "  + modelComp.getYProperty().get(YIComponent.MVC_NAME)); 
                    result.add(modelComp);
                    changes = true;
                }
            }
            if (!changes) {
                // investigating extended field...
                if (comp instanceof YIExtendedModelComponent) {
                    YIExtendedModelComponent extComp = (YIExtendedModelComponent) comp;
                    if (hasChanges(extComp)) {
                        result.add(extComp);
                        logger.debug("extended changes in "  + extComp.getYProperty().get(YIComponent.MVC_NAME)); 
                    }
                }
            }
        }
        return result;
   }
    
    /**
     * Cancels user changes in view (and view model) to the state set in
     * resetViewChanges. 
     * 
     * @see #resetViewChanges() 
     * 
     */ 
    public void cancelViewChanges() {
        ArrayList components = YUIToolkit.getViewComponents(controller.getView());
        Iterator it = components.iterator();
        while (it.hasNext()) {
            Object comp = it.next();
            if (comp instanceof YIModelComponent) {
                cancelChanges((YIModelComponent)comp);
            }
            if (comp instanceof YIExtendedModelComponent) {
                cancelChanges((YIExtendedModelComponent) comp);
            }
        }
        notifyChangeListenersOfReset();
    }
    

    /**
     * Checks if component's value hasChanged after the last 
     * resetChanges call. 
     *
     * @param comp  the component to be checked
     * @return true if the component's value has changed
     */
    public boolean hasChanges(YIComponent comp) {
        boolean changes = false;
        if (comp instanceof YIModelComponent) {
            changes = hasChanges((YIModelComponent) comp);
        } 
        if (!changes) {
            if (comp instanceof YIExtendedModelComponent) {
                changes = hasChanges((YIExtendedModelComponent) comp); 
            }
        }
        return changes;
    }     
    
    /**
     * Checks if component is YIValidatorComponent with invalid value.
     * @param   comp    current component
     * @return          if component has invalid validator value
     */
    private boolean validatorInvalid(YIComponent comp) {
        if (comp instanceof YIValidatorComponent) {
            // validator component may have invalid value that is not yet copied to the model
            YIValidatorComponent validator = (YIValidatorComponent) comp;
            if (!validator.valueValid()) {
                return true;
            }
        }
        return false;
    }
        
    /**
     * Checks if component's value hasChanged after the last 
     * resetChanges call.
     * 
     * @param comp the component to be checked
     * @return true if the component's value has changed
     */
    public boolean hasChanges(YIModelComponent comp) {
        if (validatorInvalid(comp)) {
            return true;    
        } 
        // comparing model value to component value
        if (comp instanceof YISharedModelComponent) {
            // YISharedModelComponent must implement equals comparison
            boolean same = ((YISharedModelComponent) comp).equalsModel(
                    comp.getYProperty().get(ORIGINAL));
            return ! same;
        } else {   
            try {
                Object currentValue = comp.getModelValue();
                Object oldValue = comp.getYProperty().get(ORIGINAL);
                boolean same = YCoreToolkit.equals(
                        oldValue,
                        currentValue); 
                return !same;
            } catch (Exception ex) {
                controller.componentGetValueFailed(new YComponentGetValueException(
                        ex, comp));
                return true;
            }
        }
    }
    /**
     * Checks if component's value hasChanged after the last 
     * resetChanges call.
     * 
     * @param comp the component to be checked
     * @return true if the component's value has changed
     */
    public boolean hasChanges(YIExtendedModelComponent comp) {
        if (validatorInvalid(comp)) {
            return true;    
        } 
        String[] fields = comp.getExtendedFields();
        HashMap map = (HashMap) comp.getYProperty().get(ORIGINAL_EXTENDED);
        for (int i=0; i < fields.length; i++) {
            try {
                 Object currentValue = comp.getModelValue(fields[i]);
                 Object oldValue = map.get(fields[i]);
                boolean same = YCoreToolkit.equals(
                        oldValue,
                        currentValue); 
                return !same;
            } catch (Exception ex) {
                controller.componentGetValueFailed(new YComponentGetValueException(
                        ex, comp));
                return true;
            }
        }
        return false;
    }
    
   
    /**
     * Cancel's user changes in a component (sets back original value
     * that was read in resetChanges method).
     * 
     * @param comp the component which changes should be cancelled
     * @return true if user changes was cancelled
     */
    public boolean cancelChanges(YIModelComponent comp) {
        if (hasChanges(comp)) {
          // Object valueToCancel = comp.getModelValue();
           Object originalValue = comp.getYProperty().get(ORIGINAL);
           // setting back original value
           try {
                comp.setModelValue(originalValue);
                controller.copyFromComponentToModel(comp);
                resetChanges(comp);    
                return true;
           } catch (Exception ex) {
               controller.modelSetValueFailed(new YModelSetValueException(
                        ex, comp, originalValue));
           }
        }
        return false;
    }
    
    /**
     * Cancel's user changes in a component (sets back original value
     * that was read in resetChanges method).
     * 
     * @param comp the component which changes should be cancelled
     * @return true if user changes was cancelled
     */
    public boolean cancelChanges(YIExtendedModelComponent comp) {
        if (hasChanges(comp)) {
            HashMap map = (HashMap) comp.getYProperty().get(ORIGINAL_EXTENDED);
            String[] fields = comp.getExtendedFields();
            for (int i=0; i < fields.length; i++) {
                Object originalValue = map.get(fields[i]);
                
                try {
                    comp.setModelValue(fields[i], originalValue);
                    controller.copyFromComponentToModel(comp, fields[i]);
                    resetChanges(comp); 
                } catch (Exception ex) {
                    controller.modelSetValueFailed(new YModelSetValueException(
                        ex, comp, originalValue));
                }
            }
            return true;            
        }
        return false;
    }
    
    /**
     * Reads component's value: stores the state for cancelChanges
     * and hasChanges methods.
     * 
     * @param comp the component that holds the value to be stored
     */
    public void resetChanges(YIModelComponent comp) {
        if (comp instanceof YISharedModelComponent) {
            // YISharedModelComponent must implement cloning of 
            // it's internal model...
            comp.getYProperty().put(ORIGINAL, 
                    ((YISharedModelComponent)comp).cloneModel());
        } else {   
            try {
                comp.getYProperty().put(ORIGINAL, comp.getModelValue());
            } catch (Exception ex) {
                controller.modelGetValueFailed(new YModelGetValueException(ex, comp));
            }
        }
    }
    
    /**
     * Reads component's value: stores the state for cancelChanges
     * and hasChanges methods.
     * 
     * @param comp the component that holds the value to be stored
     */
    public void resetChanges(YIExtendedModelComponent comp) {
        String[] fields = comp.getExtendedFields();
        HashMap map = new HashMap(fields.length);
        comp.getYProperty().put(ORIGINAL_EXTENDED, map);
        for (int i=0; i < fields.length; i++) {
            try {
                map.put(fields[i], comp.getModelValue(fields[i]));
            } catch (Exception ex) {
                controller.modelGetValueFailed(new YModelGetValueException(ex, comp));
            }
        }
    }

    /**
     * Adds change listener to this controller. Listener methods are invoked
     * when view changes (by user action) or resetViewChanges or cancelViewChanges is called.
     * 
     * @param listener      the view change listener
     */
    public void addViewChangeListener(YViewChangeListener listener) {
        changeListeners.add(listener);
    }
    
    /**
     * Removes view change listener from this controller.
     * 
     * @param listener      the listener to be removed
     */
    public void removeViewChangeListener(YViewChangeListener listener) {
       changeListeners.remove(listener);
    }

}
