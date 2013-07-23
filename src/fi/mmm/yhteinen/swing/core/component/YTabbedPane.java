/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.component;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import fi.mmm.yhteinen.swing.core.YComponentValidationListener;
import fi.mmm.yhteinen.swing.core.YController;
import fi.mmm.yhteinen.swing.core.YIComponent;
import fi.mmm.yhteinen.swing.core.YIControllerComponent;
import fi.mmm.yhteinen.swing.core.YViewChangeListener;
import fi.mmm.yhteinen.swing.core.error.YComponentValidationException;

import fi.mmm.yhteinen.swing.core.savechanges.YSaveChangesHandler;
import fi.mmm.yhteinen.swing.core.tools.YCoreToolkit;
import fi.mmm.yhteinen.swing.core.tools.YProperty;
import fi.mmm.yhteinen.swing.core.tools.YUIToolkit;


/**
 * The tabbed pane.
 * <p>
 * Controller event methods, where yyy is component's name:
 * 
 * <code>public void yyySelectionChanged()</code> executed 
 * when a tab is selected
 * <p>
 * Component checks unsaved changes before selecting a new tab,  
 * if component property YIComponent.CHECK_CHANGES has Boolean 
 * value true. Changes are checked only below currently selected tab
 * (not in whole Window).
 * <p>
 * Tabbed pane uses setDirty/refresh mechanism specified in 
 * YRefreshManager. When a tab is selected, a call to 
 * YRefreshManager.spreadRefresh with selected tab is performed.
 * 
 * @author Tomi Tuomainen
 *
 */
public class YTabbedPane extends JTabbedPane implements YIControllerComponent {

    private static Logger logger = Logger.getLogger(YTabbedPane.class);
    
    /**
     * YProperty for each tab.
     */
    private HashMap tabProperties = new HashMap();
    
	private YProperty myProperty = new YProperty();
    
    private HashMap viewChangeListeners = new HashMap();
    
    private HashMap viewChangeErrorListeners = new HashMap();
    
    private HashMap viewValidatorListeners = new HashMap();
	
    /**
     * Map holding controllers of each tab (Integer key - List value)
     */
    private HashMap controllerMap = new HashMap();
    
    public YTabbedPane() {
        super();
       addListeners();
    }
    

	/*
	 *  (non-Javadoc)
	 * @see fi.mmm.yhteinen.swing.core.YIComponent#getYProperty()
	 */
	public YProperty getYProperty() {
	 	return myProperty;
	}

    /**
     * Adds internal listeners.
     *
     */
    private void addListeners() {
        this.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                refreshActiveTab();
            }
        });
    }
   
 
    /* (non-Javadoc)
     * @see fi.mmm.yhteinen.swing.component.YIComponent#addViewListener(fi.mmm.yhteinen.swing.YController, fi.mmm.yhteinen.swing.YModel)
     */
    public void addViewListener(final YController controller) {
        this.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
            	String methodName = YUIToolkit.createMVCMethodName(YTabbedPane.this, "SelectionChanged");
            	if (methodName != null) {
            		controller.invokeMethodIfFound(methodName);
            	}
            }
        });
    }
    
    /**
     * Finds the first controller in given component.
     * 
     * @param   the component to be investigated
     * @return  the YController of the component (or a controller in 
     *          child component of the given component)
     */
    private YController findController(Component comp) {
        if (comp instanceof YIComponent) {
            YController controller = YUIToolkit.getController((YIComponent)comp);
            if (controller != null) {
                return controller;
            } else {
                if (comp instanceof Container) {
                    Component[] comps = ((Container) comp).getComponents();
                    for (int i=0; i < comps.length; i++) {
                        comp = comps[i];
                        controller = findController(comp);
                        if (controller != null) {
                            return controller;
                        }
                    }
                }
            }
        }
        return null;
    }
    
    /**
     * 
     * Refreshes selected tab.
     * 
     * @see YController#startViewRefresh(boolean)
     *
     */
    private void refreshActiveTab() {
        YController controller = findController(this.getSelectedComponent());
        if (controller != null) {
            controller.startViewRefresh(false);
        }
    }
    
    /**
     * This method is overridden for checking unsaved changes 
     * before selecting a tab.
     */
    public void setSelectedIndex(int index) {
        Component comp = this.getSelectedComponent();
        if (comp != null && comp instanceof Container) {
            if (YSaveChangesHandler.changesSaved(this)) {
                super.setSelectedIndex(index);
            }
        } else {
            super.setSelectedIndex(index);
        }
    }

    /**
	 * Adds property values for tabbed pane tabs with given property name.
	 * The parameter propertyValue holds values for each tab.
	 * 
	 * @param propertyName 	the name of the property to set
	 * @param propertyValue	the array of property values for each tab
	 */
    public void addTabYProperties(String propertyName, Object[] propertyValue) {
    		for (int i=0; i < propertyValue.length; i++) {
    			addTabYProperty(i, propertyName, propertyValue[i]);
    		}
    }
    
    /**
	 * Adds property value for a tabbed pane tab with given property name.
	 * 
	 * @param tabIndex		the tab index
	 * @param propertyName 	the name of the property to set
	 * @param propertyValue	the value of the property values to set
	 */
    public void addTabYProperty(int tabIndex, String propertyName, Object propertyValue) {
    	Integer key = new Integer(tabIndex);
    	YProperty property = (YProperty) tabProperties.get(key);
    	if (property == null) {
    		property = new YProperty(propertyName, propertyValue);
    		tabProperties.put(key, property);
    	} else {
    		property.put(propertyName, propertyValue);
    	}
    }
    
    /**
     * Gets YProperty for given tab. 
     * 
     * @param tabIndex the tab index
     * @return		the YProperty of the tab
     */
    public YProperty getTabYProperty(int tabIndex) {
    	Integer key = new Integer(tabIndex);
    	YProperty property = (YProperty)tabProperties.get(key);
    	if (property == null)  {
    		// property didn't exist, creating a new one...
    		property = new YProperty();
    		tabProperties.put(key, property);
    	}
    	return property;
    }
    
    /**
     * This is a convenience method for setting MVC-name.
     * 
     * @param mvcName   the YIComponent.MVC_NAME value
     */
    public void setMvcName (String mvcName) {
        getYProperty().put(YIComponent.MVC_NAME, mvcName);
    }
    
    
    public static final String DEFAULT_CHANGED_LABEL = "*";
    
    private Set changedTabs = new HashSet(); 
    private Set faultyTabs = new HashSet();
    
    /**
     * @param index tab index
     * @return  true if tab has labeled as changed
     */
    public boolean tabHasChanges(int index) {
        return changedTabs.contains(new Integer(index));
    }
    
    /**
     * @param index tab index
     * @return  true if tab has labeled as faulty
     */
    public boolean tabHasErrors(int index) {
       return faultyTabs.contains(new Integer(index));
    }
    
    /**
     * Labels tab changed in given index. 
     * This method may be overridden to customize change labeling.
     * 
     * @param index the tab index
     */
    public void labelAsChanged(int index) {
        String title = this.getTitleAt(index);
        if (!title.startsWith(DEFAULT_CHANGED_LABEL)) {
            title = DEFAULT_CHANGED_LABEL + title;
            this.setTitleAt(index, title);
        }
    }

    /**
     * Labels tab unchanged in given index. 
     * This method may be overridden to customize change labeling.
     * 
     * @param index the tab index
     */
    public void labelAsUnChanged(int index) {
        String title = this.getTitleAt(index);
        if (title.startsWith(DEFAULT_CHANGED_LABEL)) {
            title = title.substring(DEFAULT_CHANGED_LABEL.length());
            this.setTitleAt(index, title);
        }
    }
    
    /**
     * Labels tab as faulty in given index. 
     * This method may be overridden to customize error labeling.
     *  
     * @param index the tab index
     */
    protected void labelAsFaulty(int index) {
        this.setForegroundAt(index, Color.RED);
    }

    /**
     * Labels tab as not faulty in given index.  
     * This method may be overridden to customize error labeling.
     * 
     * @param index the tab index
     */
    public void labelAsNotFaulty(int index) {
        this.setForegroundAt(index, this.getForeground());
    }
    
    /**
     * This method enables view changes labeling in this tabbed pane. Methods labelAsChanged and 
     * labelAsUnChanged may be overridden to specify tab labeling. 
     * <p>
     * This method proposes that controllers are found in view hierarchy UNDER this
     * tabbed pane. Override getTabControllers to changes this assumption. 
     * <p>
     * When adding new tabs, call this method to start listening to new controllers. 
     * 
     * @param enabled   if view change labeling is enabled
     */
    public void enableViewChangesLabeling(boolean enabled) {
        this.clearViewChangeListeners(viewChangeListeners);
        if (enabled) {
            initListeners(viewChangeListeners,null, null);
        }
    }
    
    
    /**
     * This method enables view errors labeling in this tabbed pane. Methods labelAsFaulty and 
     * labelAsUnFaulty may be overridden to specify erroneous tab labeling. 
     * <p>
     * Tab is considered to be faulty on if its subviews (found in view hierarchy) holds controller, 
     * that has invalid components. YTabbedPane method hasError(YController) may be overridden
     * to implement custom error checking. 
     * <p>
     * This method proposes that controllers are found in view hierarchy UNDER this
     * tabbed pane. Override getTabControllers ato change this assumption.
     * <p>
     * When adding new tabs, call this method to start listening to new controllers. 
     *
     * @param enabled   if view errors lableing is enabled
     */
     public void enableViewErrorsLabeling(boolean enabled) {
         this.clearViewChangeListeners(viewChangeErrorListeners);
         this.clearComponentValidationListeners(viewValidatorListeners);
         if (enabled) {
             initListeners(null, viewChangeErrorListeners, viewValidatorListeners);
         }
     }

    
    /**
     * Inits listeners for each tab.
     * 
     * @param viewChangeListeners           created YTabbedPaneViewChangeListeners are added to this map (if parameter is not null)
     * @param viewChangeErrorListeners      created YTabbedPaneViewChangeErrorListeners are added to this map (if parameter is not null)
     * @param viewValidatorListeners       created YTabbedPaneValidationListeners are added to this map (if parameter is not null)
     */
    private void initListeners(HashMap viewChangeListeners, HashMap viewChangeErrorListeners, HashMap viewValidatorListeners) {
        
        controllerMap.clear(); //tab-controller map is initialized too
        List tabs = this.getTabs();
        for (int i=0; i< tabs.size(); i++) {
            Component tab = (Component) tabs.get(i);
            List controllers = getTabControllers(tab);
            controllerMap.put(new Integer(i), controllers);
            Iterator it = controllers.iterator();
            // adding listeners (if not added):
            while (it.hasNext()) {
                YController controller = (YController) it.next();
                if (viewChangeListeners != null) {
                    YViewChangeListener listener = new YTabbedPaneViewChangeListener();
                    viewChangeListeners.put(controller, listener);
                    controller.addViewChangeListener(listener);
                }
                if (viewChangeErrorListeners != null) {
                    YTabbedPaneViewChangeErrorListener listener = new YTabbedPaneViewChangeErrorListener();
                    viewChangeErrorListeners.put(controller, listener);
                    controller.addViewChangeListener(listener);
                }
                if (viewValidatorListeners != null) {
                    YTabbedPaneValidationListener listener = new YTabbedPaneValidationListener();
                    viewValidatorListeners.put(controller, listener);
                    controller.addComponentValidationListener(listener);
                }
            }
        }
    }
    
    /**
     * Returns tabs of this tabbedpane.
     * 
     * @return  list of tabs (components that have tab index)
     */
    public List getTabs() {
        Object[] tabs = new Object[this.getTabCount()];
        Component[] comps = YTabbedPane.this.getComponents();
        for (int i=0; i< comps.length; i++) {
            Component comp = comps[i];
            int tabIndex = YTabbedPane.this.indexOfComponent(comp);
            if (tabIndex > -1) {
                tabs[tabIndex] = comp;
            }
        }
        return Arrays.asList(tabs);
    }
    
    /**
     * Helper class to shorten code.
     */
    private abstract class YTabCommand {
        public abstract void perform(Component tab, int index);
    }
    
    private void performForAllTabs(YTabCommand command) {
        List tabs = YTabbedPane.this.getTabs();
        for (int i=0; i< tabs.size(); i++) {
            Component comp = (Component) tabs.get(i);
            command.perform(comp, i);
        }
    }
    

    
    /**
     * Removes change listeners from YController and clears also listeners map.
     * 
     * @param changeListeners   the listener map
     */
    private void clearViewChangeListeners(HashMap changeListeners) {
        Iterator it = changeListeners.keySet().iterator();
        while (it.hasNext()) {
            YController controller = (YController) it.next();
            YViewChangeListener listener = (YViewChangeListener) changeListeners.get(controller);
            controller.removeViewChangeListener(listener);
            it.remove();
        }
    }
    
    /**
     * Removes valdition listeners from YController and clears also listeners map.
     * 
     * @param changeListeners   the listener map
     */
    private void clearComponentValidationListeners(HashMap listeners) {
        Iterator it = listeners.keySet().iterator();
        while (it.hasNext()) {
            YController controller = (YController) it.next();
            YComponentValidationListener listener = (YComponentValidationListener) listeners.get(controller);
            controller.removeComponentValidationListener(listener);
            it.remove();
        }
    }
        
    /**
     * Gets controllers for given tab. The default implementation uses
     * YUIToolkit.getControllersByViewHierarchy
     * method. If controller is not found from tab view hierarchy (for example
     * YController is in parent view that holds the controller), this method should be
     * overridden to fix view errors and view changes labeling. 
     * 
     * @param tab   the tab component
     * @return      controllers of the tab
     */
    protected List getTabControllers(Component tab) {
        List controllers = YUIToolkit.getControllersByViewHierarchy(tab);
        return controllers;
    }
    
    /**
     * Gets tab components. The default implementation searches components
     * in view hierarchy by calling Container getComponents. If this approach
     * does not work correctly, this method may be overridden to fix view errors 
     * and view changes labeling. 
     * 
     * @param tab   the tab component
     * @return      all components that the tab contains
     */
    protected List getTabComponents(Component tab) {
        ArrayList result = new ArrayList();
        findComponents(tab, result);
        return result;
    }
    
    /**
     * Finds components in given tab
     * 
     * @param comp      the component to be investigated
     * @param result    the result list (filled recursively)
     */
    private void findComponents(Component comp, ArrayList result) {
        result.add(comp);
         comp = YUIToolkit.getContentPane(comp);
        if (comp instanceof Container) {
            Component[] comps = ((Container)comp).getComponents();
            for (int i = 0; i < comps.length; i++) {
                Component child = comps[i];
                findComponents(child, result);
            }
        }
    }
    
    /**
     * Checks if given controller has error. Default implementation checks this with YController 
     * viewValuesValid (works with YIValidatorComponents). In general, applications
     * should override this method to implement custom checks. 
     * 
     * @param controller    the controller
     * @return              true controller has errors (tab should be labeled erroneous)
     */
    protected boolean hasError(YController controller) {
        return !controller.viewValuesValid();
    }

    /**
     * Refreshes changed labels for all tabs.
     *
     */
    public void refreshTabChanges() {
        List tabs = this.getTabs();
        for (int i=0; i < tabs.size(); i++) {
            boolean changed = false;
            Component tab = (Component) tabs.get(i);
            List controllers = this.getTabControllers(tab);
            for (int j=0; j < controllers.size() && !changed; j++) {
                YController c = (YController) controllers.get(j);
                if (c.hasViewChanges()) {
                    changed = true;
                }
            }
            this.setTabChanged(i, changed);
        }
    }
    
    
    /**
     * Refreshes all tabs errors labels. 
     * 
     */
    public void refreshTabErrors() {
        List tabs = this.getTabs();
        for (int i=0; i < tabs.size(); i++) {
            boolean faulty = false;
            Component tab = (Component) tabs.get(i);
            List controllers = this.getTabControllers(tab);
            for (int j=0; j < controllers.size() && !faulty; j++) {
                YController c = (YController) controllers.get(j);
                if (hasError(c)) {
                    faulty = true;
                }
            }
            this.setTabFaulty(i, faulty);
        }
    }
    
    
    /**
     * Sets tab changed. Usually it is not necessary to call this method directly, 
     * call enableViewChangesLabeling to enable automatic view changes labeling 
     * (which uses this method).
     * 
     * @param index tab index
     * @param changed   if changed
     */
    public void setTabChanged(int index, boolean changed) {
        if (changed) {
            this.changedTabs.add(new Integer(index));
            this.labelAsChanged(index);
        } else {
            this.changedTabs.remove(new Integer(index));
            this.labelAsUnChanged(index);
        }
    }
    
    /**
     * Sets tab faulty. Usually it is not necessary to call this method directly, 
     * call enableViewErrorsLabeling to enable automatic view errors labeling 
     * (which uses this method).
     * 
     * @param index tab index
     * @param faulty   if errors
     */
    public void setTabFaulty(int index, boolean faulty) {
        if (faulty) {
            this.faultyTabs.add(new Integer(index));
            this.labelAsFaulty(index);
        } else {
            this.faultyTabs.remove(new Integer(index));
            this.labelAsNotFaulty(index);
        }
    }
    
   
   

   
   /**
    * Solving in which tab controller is located.
    * 
    * @param controller
    * @return   tab of controller, null if not found
    */
   private Integer getTab(YController controller) {
       for (int i=0; i < this.getTabCount(); i++) {
           Integer index = new Integer(i);
           List controllers = (List)  this.controllerMap.get(index);
           for (int j=0; j < controllers.size(); j++) {
               YController c = (YController) controllers.get(j);
               if (c == controller) {
                   return index;
               }
           }
       }
       return null;
   }
   /**
    * Refreshes tab errors concerning given controller. If controller is null,
    * all tabs are refreshed.
    *  
    * @param controller the changed controller
    */
   public void refreshTabErrors(final YController controller) {
       Integer i = getTab(controller);
       if (i == null) {
           logger.warn("tab not found for controller" + controller.getClass());
       } else {
           if (hasError(controller)) { 
               if (!tabHasErrors(i.intValue())) { // not already labeled with errors
                   setTabFaulty(i.intValue(), true);
               }
           } else {  // no errors
               if (tabHasErrors(i.intValue())) {
                   // checking if other controllers in tab have errors
                   List controllers= (List) controllerMap.get(i);
                   boolean stillErrors = false;
                   for (int j=0; j< controllers.size() && !stillErrors; j++) {
                       YController c = (YController)  controllers.get(j);
                       if (hasError(c)) {
                           stillErrors = true;
                       }
                   }
                   if (!stillErrors) { // all errors have been cleared?
                       setTabFaulty(i.intValue(), false);
                   }
               }
           }
       }
   }
   
   /**
    * View change listener.
    */
   class YTabbedPaneViewChangeListener extends YViewChangeListener {
       
       /*
        * (non-Javadoc)
        * @see fi.mmm.yhteinen.swing.core.YViewChangeListener#viewChanged(fi.mmm.yhteinen.swing.core.YController, fi.mmm.yhteinen.swing.core.YIComponent)
        */   
       public void viewChanged(YController controller, YIComponent comp) {
           // labeling the current tab (user can change only that):
           int index = getSelectedIndex();
           YTabbedPane.this.setTabChanged(index, true);
       }
      
       /*
        * (non-Javadoc)
        * @see fi.mmm.yhteinen.swing.core.YViewChangeListener#viewChangesReset(fi.mmm.yhteinen.swing.core.YController)
        */
       public void viewChangesReset(final YController controller) {
           Integer i = getTab(controller);
           if (i == null) {
               logger.warn("tab not found for controller" + controller.getClass());
           } else if (tabHasChanges(i.intValue())){ 
               // checking if all changes in this tab are resetted...
               List controllers= (List) controllerMap.get(i);
               boolean stillChanges = false;
               for (int j=0; j< controllers.size() && !stillChanges; j++) {
                   YController c = (YController)  controllers.get(j);
                   if (c.hasViewChanges()) {
                       stillChanges = true;
                   }
               }
               if (!stillChanges) {
                   setTabChanged(i.intValue(), false);
               }
           }
       }
   }
  

   
   
   
   /**
    * View change error listener.
    */
   class YTabbedPaneViewChangeErrorListener extends YViewChangeListener {
       
       /*
        * (non-Javadoc)
        * @see fi.mmm.yhteinen.swing.core.YViewChangeListener#viewChanged(fi.mmm.yhteinen.swing.core.YController, fi.mmm.yhteinen.swing.core.YIComponent)
        */   
       public void viewChanged(YController controller, YIComponent comp) {
           // labeling the current tab (user can change only that):
           if (hasError(controller)) {
               // changed comopnent must be in the selected index
               YTabbedPane.this.setTabFaulty(getSelectedIndex(), true);
           } else { 
               YTabbedPane.this.refreshTabErrors(controller); // checking all tabs for this controller for clearing errors
           }
       }
       
       /*
        * (non-Javadoc)
        * @see fi.mmm.yhteinen.swing.core.YViewChangeListener#viewChangesReset(fi.mmm.yhteinen.swing.core.YController)
        */
       public void viewChangesReset(YController controller) {
           YTabbedPane.this.refreshTabErrors(controller); // checking all tabs for this controller for clearing errors
       }
       
   }
   
   /**
    * View validation listener.
    */
   class YTabbedPaneValidationListener extends YComponentValidationListener {

       public void componentValidationFailed(YController controller, YComponentValidationException e) {
//         changed comopnent must be in the selected index
           YTabbedPane.this.setTabFaulty(getSelectedIndex(), true);       
        }

       public void componentValidationSucceeded(YController controller, YIComponent comp) {
           YTabbedPane.this.refreshTabErrors(controller); // checking all tabs for this controller for clearing errors
       }
   }
    
    
   

   
    
 
	
}
