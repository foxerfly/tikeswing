/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.savechanges;

import java.awt.Component;
import java.awt.Container;
import java.util.Iterator;

import fi.mmm.yhteinen.swing.core.YController;
import fi.mmm.yhteinen.swing.core.YIComponent;
import fi.mmm.yhteinen.swing.core.tools.YUIToolkit;

/**
 * Static helper class that for framework's unsaved changes checking. 
 * <p>
 * If unsaved changes exist, it is  asked from the user, if those 
 * changes should be saved. Return value true indicates that event 
 * that is happening should go on. False indicates that event 
 * shouldn't happen and the view state should stay as it is. 
 * <p>
 * Controller of the view must implement YISaveChangesController interface
 * to make the automatic checking work. If controller saves data of the child
 * controllers (shares the same model), controller should implement 
 * YISaveChangesParentController. 
 * <p>
 * Customised dialog for asking user action (Yes, No, Cancel) may be 
 * set in YController setSaveChangesEnquirer method. If controller-specific
 * enquirer is not set static enquirer of this class is used.
 * 
 * @author Tomi Tuomainen
 * 
 * @see YISaveChangesController
 * @see YISaveChangesParentController
 */
public class YSaveChangesHandler {
    
	private static YIChangesEnquirer changesDialogHandler = new YDefaultChangesEnquirer();
	
    /**
     * Save Changes -dialog result.
     */
    public static final int YES = 0;
    /**
     * Save Changes -dialog result.
     */
    public static final int NO = 1;
    /**
     * Save Changes -dialog result.
     */
    public static final int CANCEL = 2;
        
    /**
     * Checks if current window (in which given component belongs to)
     * has unsaved changes. 
     *  
     * Checking is done only if component's YIComponent.CHECK_CHANGES
     * property has Boolean value true. 
     * 
     * @param comp	the component that triggers unsaved changes checking
     * @return		true, if changes were saved
     */
    public static boolean changesSavedInCurrentWindow(YIComponent comp) {
    	if (YUIToolkit.isPropertyTrue(comp, YIComponent.CHECK_CHANGES)) {
    		Component compToCheck = YUIToolkit.getCurrentWindow();
    		return saveChangesRecursively(compToCheck);
    	}
    	return true;
    }
    
  
    /**
     * Checks if current component (and it's child components)
     * have unsaved changes. 
     * 
     * Checking is done only if component's YIComponent.CHECK_CHANGES
     * property has Boolean value true. 
     * 
     *  
     * @param comp	the component to be checked
     * @return		true, if changes were saved
     */
    public static boolean changesSaved(YIComponent comp) {
    	if (YUIToolkit.isPropertyTrue(comp, YIComponent.CHECK_CHANGES) &&
    			comp instanceof Component) {
    		return saveChangesRecursively((Component)comp);
    	}
    	return true;
    }
    
    /**
     * Checks unsaved changes recursively for Component and it's child
     * Components. 
     * 
     * @param comp 		the Component to checked
     * @return false 	if component or any child component returns false
     */
    private static boolean saveChangesRecursively(Component comp) {
    	if (comp instanceof YIComponent) {
        	YController controller = (YController) 
				((YIComponent)comp).getYProperty().get(YIComponent.CONTROLLER);
        	if (controller != null) {
        		if (! changesSaved(controller)) {
        			return false;
        		}
        	}
        }
	    comp = YUIToolkit.getContentPane(comp);
        if (comp instanceof Container) {
        	Component[] comps = ((Container)comp).getComponents();
        	for (int i = 0; i < comps.length; i++) {
        		Component child = comps[i];
        		if (! saveChangesRecursively(child)) {
                     return false;
                 }
             }
         }
         return true;
    }
    
    /**
	 * @return the changes dialog handler
	 */
	public static YIChangesEnquirer getChangesDialogHandler() {
		return changesDialogHandler;
	}
	
	/**
	 * Customised dialog that shows confirmation dialog
	 * of saving changes can be set via this method.
     * 
     * @deprecated Set changedDialogHandler to YController of each Window instead
	 * 
	 * @param changesDialogHandler  the changes dialog handler
	 */
	public static void setChangesDialogHandler(
			YIChangesEnquirer changesDialogHandler) {
		YSaveChangesHandler.changesDialogHandler = changesDialogHandler;
	}
	
    /**
     * Checks if user has changed view model after the last
     * call to resetViewChanges. Checks given controller and all
     * the child controllers.
     * 
     * @return true if user has changed the view 
     * @see YController#hasViewChanges()
     */
    public static boolean hasViewChangesRecursively(YController controller) {
    	if (controller.hasViewChanges()) {
    		return true;
    	} else {
    		Iterator it = controller.getChildren().iterator();
    		while (it.hasNext()) {
    			if (hasViewChangesRecursively((YController)it.next())) {
    				return true;
    			}
    		}
    	}
    	return false;
    }

    
    /**
     * Resets user changes tracking.  Resets given controller and all
     * the child controllers.
     * 
     * @see YController#resetViewChanges()
     */
    public static void resetViewChangesRecursively(YController controller) {
    	controller.resetViewChanges();
   		Iterator it = controller.getChildren().iterator();
   		while (it.hasNext()) {
   			resetViewChangesRecursively((YController)it.next());
    	}
    }
    
    /**
     * Cancels user changes in view (and view model) to the state set in
     * resetViewChanges. Cancels given controller and all the child controllers.
     * 
     * @see YController#cancelViewChanges()
     */    
    public static void cancelViewChangesRecursively(YController controller) {
    	controller.cancelViewChanges();
   		Iterator it = controller.getChildren().iterator();
   		while (it.hasNext()) {
   			cancelViewChangesRecursively((YController)it.next());
    	}
    }
    	
	/**
	 * Checks if controller (or actually it's view model) 
	 * has unsaved changes. If changes exist, it is asked from 
	 * user, if those changes should be saved. 
	 * 
	 * Controller must implement YISaveChangesController
	 * so that saving can be automatically done by this method. 
	 * 
	 * @param controller the controller to be checked
	 * @return	true if changes didn't exist, saving was done successfully, 
	 * 				or user choosed not to save changes;
	 * 			false if user choosed Cancel or saving was not succesful 
	 * 
	 */
	public static boolean changesSaved(YController controller) {
        // controller must implement YISaveChangesController...
       if (controller instanceof YISaveChangesController) {
       	   boolean savesChildren = controller instanceof YISaveChangesParentController;
           if (controller.hasViewChanges() ||
           		(savesChildren && hasViewChangesRecursively(controller))) {
               // using changes enquirer of YController...
               YIChangesEnquirer enq = controller.getSaveChangesEnquirer();
               if (enq == null) {
                   // if not set, using static enquirer of this class
                   enq = getChangesDialogHandler();
               }
               
           	   int result = enq.showConfirmationDialog((Component)controller.getView());
               if (result == YSaveChangesHandler.YES) {
               	  boolean succeeded = ((YISaveChangesController) controller).save();
               	  if (! succeeded) {
               	  	return false;
               	  } else {
               	  	if (savesChildren) {
               	  		resetViewChangesRecursively(controller);
               	  	} else {
               	  		controller.resetViewChanges(); 
               	  	}
               	  }
               } else if (result == YSaveChangesHandler.NO) {
               		if (savesChildren) {
               			cancelViewChangesRecursively(controller);
               		} else {
               			controller.cancelViewChanges();
               		}
               } else if (result == YSaveChangesHandler.CANCEL) {
                   return false;
               }
           }
      }
      return true;
  }
}
