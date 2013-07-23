/*
 * Created on Feb 19, 2007
 *
 */
package fi.mmm.yhteinen.swing.core.component.tree;

import java.awt.Component;


import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;


import fi.mmm.yhteinen.swing.core.component.YCheckBox;


/**
 * The tree check box renderer. 
 * 
 * This renderer is created automatically when calling enableCheckBoxes in YTree.
 * 
 * This renderer may be extended and set to tree if 
 * the default behaviour needs to be changed for some reason.
 */
public class YTreeCheckBoxRenderer extends DefaultTreeCellRenderer {
    
    private YCheckBox checkBox;
    
    /**
     * 
     * @param checkBox  the check box used in rendering 
     */
    public YTreeCheckBoxRenderer(YCheckBox checkBox) {
        this.checkBox = checkBox;
    }
    
    /*
     * (non-Javadoc)
     * @see javax.swing.tree.DefaultTreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int, boolean)
     */
    public Component getTreeCellRendererComponent(JTree tree,
            Object value,
            boolean sel,
            boolean expanded,
            boolean leaf,
            int row,
            boolean hasFocus) {
        if (value instanceof YTreeNode) {
            YTreeNode node = (YTreeNode) value;
            checkBox.setText(node.toString());
            checkBox.setSelected(node.isSelected());
            checkBox.setEnabled(tree.isEditable());
        } 
        return checkBox;
    }
    
}
