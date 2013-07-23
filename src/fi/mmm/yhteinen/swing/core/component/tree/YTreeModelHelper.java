/*
 * Created on Feb 21, 2007
 *
 */
package fi.mmm.yhteinen.swing.core.component.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Helper class for constructing YTree. Use this in YModel to build the tree and the instance of
 * YTreeModel to YTree in view class.
 * 
 * @author Tomi Tuomainen
 */
public class YTreeModelHelper {

    private List nodes = new ArrayList();
    

    public YTreeModelHelper() {
    }
    
    
    /**
     * The internal tree node (not to be used outside).
     */
    class Node {
        private Object value;
        private Node parent;
        
        public Node(Object value, Node parent) {
            super();
            this.value = value;
            this.parent = parent;
        }
        public Node getParent() {
            return parent;
        }
        public void setParent(Node parent) {
            this.parent = parent;
        }
        public Object getValue() {
            return value;
        }
        public void setValue(Object value) {
            this.value = value;
        }
    }
    
    /**
     * Finds Node with given value in this tree model.
     * 
     * @param value    the node value
     * @return         the node with given value
     */
    private Node getNode(Object value) {
        for (int i=0; i < nodes.size(); i++) {
            Node node = (Node) nodes.get(i);
            if (node.getValue().equals(value)) {
                return node;
            }
        }
        return null;
    }
        
    /**
     * Call this to add node without parent (the first level of tree).
     * 
     * @param obj   the tree node to be added (JavaBean)
     */
    public void addNode(Object obj) {
        Node node = new Node(obj, null);
        node.setValue(obj);
        nodes.add(node);
    }
    
    /**
     * Call this to add child node.
     * 
     * @param parent    the parent node for the new node (JavaBean)
     * @param obj       the tree node to be added (JavaBean)
     */
    public void addNode(Object parent, Object obj) {
        Node parentNode = getNode(parent);
        Node node = new Node(obj, parentNode);
        nodes.add(node);
    }

    /**
     * This method is for internal use. 
     * 
     * @return  the list of nodes
     */
    List getNodes() {
        return nodes;
    }
    
}
