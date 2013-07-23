/*
 * Created on Aug 29, 2007
 *
 */
package fi.mmm.yhteinen.swing.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Helper class for handling events.
 * <p>
 * This class is for YController internal use, do not 
 * override or use directly. 
 * 
 * @author Tomi Tuomainen
 */
public class YEventHelper {

    private YController controller;

    /** static map for application event broadcasting between controllers */
    private static HashMap eventListeners = new HashMap();
    
    /**
     * @param controller    the controller using this helper
     */
    public YEventHelper(YController controller) {
        super();
        this.controller = controller;
    }
    
    /**
     * Registers controller for listening an application event. To receive the
     * event, the listener must implement method
     * <code>receivedApplicationEvent(YApplicationEvent)</code>
     * 
     * @param eventName     the event name
     */
    public void register(String eventName) {
        ArrayList listeners = (ArrayList) eventListeners.get(eventName);
        if (listeners == null) {
            listeners = new ArrayList(1);
        }
        listeners.add(controller);
        eventListeners.put(eventName, listeners);
    }
    
    /**
     * Unregisters controller for listening specified application event.
     * (sent via sendApplicationEvent).
     * 
     * @param event     the event to unregister
     */
    public void unregister(String event) {
         ArrayList listeners = (ArrayList) eventListeners.get(event);
         if (listeners != null) {
            listeners.remove(controller);
         }
    }
    
    /**
     * Unregisters this controller for listenening application events 
     * sent via sendApplicationEvent. 
     */
    public void unregister() {
        Collection values = eventListeners.values();
        Iterator it = values.iterator();
        while (it.hasNext()) {
            ArrayList listeners = (ArrayList) it.next();
            if (listeners.contains(controller)) {
                listeners.remove(controller);
            }
        }
    }
    
    /**
     * Unregisters this controller and all the child controllers for
     * listenening application events sent via sendApplicationEvent. 
     */
    public void unregisterRecursively() {
        unregisterRecursively(controller);
    }
    
    /**
     * Unregisters controller and all the child controllers for
     * listenening application events.
     * 
     * @param controller the controller to unregister 
     */
    private void unregisterRecursively(YController controller) {
        controller.unregister();
        Iterator it = controller.getChildren().iterator();
        while (it.hasNext()) {
            YController child = (YController) it.next();
            unregisterRecursively(child);
        }
    }
    
    /**
     * Sends an event to all registered listeners.
     * 
     * @param event     the event 
     */
    public static void sendApplicationEvent(YApplicationEvent event) {
        ArrayList listeners = (ArrayList) eventListeners.get(event.getName());
        if (listeners != null) {
            Iterator it = listeners.iterator();
            while (it.hasNext()) {
                YController listener = (YController) it.next();
                listener.receivedApplicationEvent(event);
               
            }
        }
    }

    
    
}
