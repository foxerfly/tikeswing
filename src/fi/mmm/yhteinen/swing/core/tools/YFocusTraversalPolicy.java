/*
 * Created on Aug 29, 2007
 *
 */
package fi.mmm.yhteinen.swing.core.tools;

import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;

/**
 * Helper class for setting up focus order. Desired focus order is 
 * given in an array of components. 
 * <p>
 * Note that if JComboBox is editable, focusable component given to 
 * FocusTraversalPolicy should be the editor component (
 * combo.getEditor().getEditorComponent()).
 * <p>
 * Also note that FocusTraversalPolicy should be set to FocusCycleRoot 
 * component. 
 * 
 * The following example initializes focus order for YPanel (or any Container):
 * 
 * <code>
 *  this.setFocusCycleRoot(true);
 *  this.setFocusTraversalPolicy(new YFocusTraversalPolicy(
 *       new Component[] {myField1, myField2, myButton1}));
 * </code>
 * 
 * @author Tomi Tuomainen
 */
public class YFocusTraversalPolicy extends FocusTraversalPolicy {

    private Component[] comps;

    /**
     * @param comps     the focusable components with desired order
     */
    public YFocusTraversalPolicy(Component[] comps) {
        super();
        this.comps = comps;
    }

    /*
     * (non-Javadoc)
     * @see java.awt.FocusTraversalPolicy#getComponentAfter(java.awt.Container, java.awt.Component)
     */
    public Component getComponentAfter(Container focusCycleRoot, Component aComponent) {
        for (int i=0; i < comps.length; i++) {
            if (comps[i].equals(aComponent)) {
                if (i < (comps.length-1)) {
                    return comps[i+1];
                } else {
                    return comps[0];
                }
            }
        }
        return aComponent;
    }

    /*
     * (non-Javadoc)
     * @see java.awt.FocusTraversalPolicy#getComponentBefore(java.awt.Container, java.awt.Component)
     */
    public Component getComponentBefore(Container focusCycleRoot, Component aComponent) {
        for (int i=0; i < comps.length; i++) {
            if (comps[i].equals(aComponent)) {
                if (i > 0) {
                    return comps[i-1];
                } else {
                    return comps[comps.length-1];
                }
            }
        }
        return aComponent;
    }

    /*
     * (non-Javadoc)
     * @see java.awt.FocusTraversalPolicy#getDefaultComponent(java.awt.Container)
     */
    public Component getDefaultComponent(Container focusCycleRoot) {
        return comps[0];
    }

    /*
     * (non-Javadoc)
     * @see java.awt.FocusTraversalPolicy#getFirstComponent(java.awt.Container)
     */
    public Component getFirstComponent(Container focusCycleRoot) {
        return comps[0];
    }

    /*
     * (non-Javadoc)
     * @see java.awt.FocusTraversalPolicy#getLastComponent(java.awt.Container)
     */
    public Component getLastComponent(Container focusCycleRoot) {
        return comps[comps.length-1];
    }
}
