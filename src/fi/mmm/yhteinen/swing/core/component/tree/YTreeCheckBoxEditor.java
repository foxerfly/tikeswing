/*
 * Created on Feb 19, 2007
 *
 */
package fi.mmm.yhteinen.swing.core.component.tree;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.util.EventObject;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellEditor;


import fi.mmm.yhteinen.swing.core.component.YCheckBox;
import fi.mmm.yhteinen.swing.core.component.list.YComboBox;

/**
 * The tree check box editor. 
 * 
 * This editor is created automatically when calling enableCheckBoxes in YTree.
 * By default editor starts editing immediatly on every user click.
 * 
 * This editor may be extended and set to tree if 
 * the default behaviour needs to be changed for some reason.
 */
public class YTreeCheckBoxEditor extends DefaultTreeCellEditor {

    private YCheckBox checkBox;
    private YTree tree;
    private YTreeNode current;
    
    /**
     * 
     * @param tree          the tree that holds this editor
     * @param renderer      the check box renderer
     * @param checkBox      the check box to be used in editor
     */
    public YTreeCheckBoxEditor(YTree tree, YTreeCheckBoxRenderer renderer, YCheckBox checkBox) {
        super(tree, renderer);
        this.checkBox = checkBox;
        checkBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                checkBoxChanged();
            }
            
        });
        this.tree = tree;
    }
    
    /*
     * (non-Javadoc)
     * @see javax.swing.tree.DefaultTreeCellEditor#getTreeCellEditorComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int)
     */
    public Component getTreeCellEditorComponent(JTree tree,
            Object value,
            boolean isSelected,
            boolean expanded,
            boolean leaf,
            int row) {
        if (value instanceof YTreeNode) {
            current = (YTreeNode) value;
            checkBox.setText(current.toString());
            checkBox.setSelected(current.isSelected());
          
        } 
        return checkBox;
    }
    
    /*
     * (non-Javadoc)
     * @see javax.swing.tree.DefaultTreeCellEditor#canEditImmediately(java.util.EventObject)
     */
    protected boolean canEditImmediately(EventObject event) {
        return true;
    }
    
    /**
     * This is called when a check box selection is changed.
     */
    private void checkBoxChanged() {
        current.setSelected(checkBox.isSelected());
        tree.nodeSelected(current);
    }



}
