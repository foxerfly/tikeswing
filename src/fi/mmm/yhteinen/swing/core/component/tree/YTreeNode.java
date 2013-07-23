/*
 * Created on Jan 6, 2005
 *
 */
/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.component.tree;

import javax.swing.tree.DefaultMutableTreeNode;

import fi.mmm.yhteinen.swing.core.tools.YFormatter;

/**
 * The node for the tree.
 * 
 * @author Tomi Tuomainen
 * @see YTree
 *
 */
public class YTreeNode extends DefaultMutableTreeNode {
	
	private Object nodeModel;
	private YFormatter formatter;
    
    private boolean selected = false;
	

    /**
     * @param nodeModel     actual tree model item in YModel (JavaBean)
     * @param formatter     the formatter (may be null)
     */
    public YTreeNode(Object nodeModel, YFormatter formatter) {
        super();
        this.nodeModel = nodeModel;
        this.formatter = formatter;
    }
    /**
     * @return the formatter used by this node
     */
	public YFormatter getFormatter() {
		return formatter;
	}
    
    /**
     * 
     * @param formatter the formatter for this node
     */
	public void setFormatter(YFormatter formatter) {
		this.formatter = formatter;
	}
    
    /**
     * @return the model stored in this node
     */
	public Object getNodeModel() {
		return nodeModel;
	}
    /**
     * @param nodeModel the model to be stored in this node
     */
	public void setNodeModel(Object nodeModel) {
		this.nodeModel = nodeModel;
	}
	
    /**
     * String presentation of this node, defines how the node is showed to user.
     * If formatter is set, it defines how item node is formatted in a view. 
     * If formatter is null, node model's toString() method is used.
     * 
     */
	public String toString() {
		if (nodeModel == null) {
			return "";
		} else if (formatter == null) {
			return nodeModel.toString();
		} else {
			return formatter.format(nodeModel);
		}
	}
    
    /**
     * @return  if node is selected by a check box
     */
    public boolean isSelected() {
        return selected;
    }
    
    /**
     * 
     * @param selected  if node is selected by a check box
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
	
}
