/*
 * Created on Aug 29, 2007
 *
 */
package fi.mmm.yhteinen.swing.core;

import java.awt.Component;
import java.awt.Container;

import fi.mmm.yhteinen.swing.core.tools.YUIToolkit;

/**
 * Helper class for handling dirty/refresh of tabs.
 * <p>
 * This class is for YController internal use, do not 
 * override or use directly. 
 * 
 * @author Tomi Tuomainen
 */
public class YRefreshHelper {

    private YController controller;
    
    private boolean dirty = false;
    
    private Object refreshData = null;
    

    /**
     * @param controller    the controller using this helper.
     */
    public YRefreshHelper(YController controller) {
        this.controller = controller;
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
        YIComponent view = controller.getView();
        setDirtyRecursively(view, refreshData);
        if (view instanceof Component && 
                YUIToolkit.currentlyVisible((Component)view)) {
            startViewRefresh(false);
        }
    }
    
    /**
     * Sets controller of the component to "dirty" state. 
     * Also all controllers of the child components will be 
     * set to "dirty" state recursively (by spreading refreshData to children).
     * 
     * @param comp          the component which controller is set to dirty
     * @param refreshData   some object to be passed to refreshView method
     */
    private void setDirtyRecursively(Object comp, Object refreshData) {
        if (comp instanceof YIComponent) {
            YController controller = YUIToolkit.getController((YIComponent) comp);
            if (controller != null) {
                controller.getRefreshHelper().refreshData = refreshData;
                controller.getRefreshHelper().dirty = true;
            }
        }
        if (comp instanceof Container) {
            comp = YUIToolkit.getContentPane((Container)comp); // for JDialog or JFrame
            Component comps[] = ((Container)comp).getComponents();
            for (int i=0; i < comps.length; i++) {
                Component child = comps[i];
                setDirtyRecursively(child, refreshData);
            }
        }
    }
    
    /**
     * Calls refreshView  (clearing the dirty state) for this 
     * controller and all the controllers of the child views. 
     * 
     * @param forceRefresh if true, refreshView is called always, if false,
     *                     refreshView is called only if view is "dirty"
     */
    public void startViewRefresh(boolean forceRefresh) {
        refreshRecursively(controller.getView(), forceRefresh);
    }
    
    /**
     * Calls refreshView of the component (clearing the "dirty" state).
     * Also all the controllers of the child components will be refreshed.
     * 
     * @param comp          the component to be refreshed
     * @param refreshData   some object to be passed to refreshView method
     */
    private void refreshRecursively(Object comp, boolean forceRefresh) {
        if (comp instanceof YIComponent) {
            YController controller = YUIToolkit.getController((YIComponent) comp);
            if (controller != null) {
                if (forceRefresh || 
                    (controller.getRefreshHelper().dirty && YUIToolkit.currentlyVisible((Component)comp))) {
                    controller.refreshView(controller.getRefreshHelper().refreshData);
                    controller.getRefreshHelper().dirty = false;
                }
            }
        }
        if (comp instanceof Container) {
            comp = YUIToolkit.getContentPane((Container)comp); // for JDialog or JFrame
            Component comps[] = ((Container) comp).getComponents();
            for (int i=0; i < comps.length; i++) {
                Component child = comps[i];
                refreshRecursively(child, forceRefresh);
            }
        }
   }

    /**
     * 
     * @return  if controller of this helper is dirty and needs to be refreshed
     */
    public boolean isDirty() {
        return dirty;
    }
    
       
}

