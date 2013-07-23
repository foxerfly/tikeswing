/*
 * Created on Jan 6, 2005
 *
 */
/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.component.tree;


import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.log4j.Logger;

import fi.mmm.yhteinen.swing.core.YController;
import fi.mmm.yhteinen.swing.core.YIComponent;
import fi.mmm.yhteinen.swing.core.YIExtendedModelComponent;
import fi.mmm.yhteinen.swing.core.YIModelComponent;
import fi.mmm.yhteinen.swing.core.component.YCheckBox;
import fi.mmm.yhteinen.swing.core.component.tree.YTreeModelHelper.Node;
import fi.mmm.yhteinen.swing.core.error.YException;
import fi.mmm.yhteinen.swing.core.savechanges.YSaveChangesHandler;
import fi.mmm.yhteinen.swing.core.tools.YCoreToolkit;
import fi.mmm.yhteinen.swing.core.tools.YFormatter;
import fi.mmm.yhteinen.swing.core.tools.YProperty;
import fi.mmm.yhteinen.swing.core.tools.YUIToolkit;

/**
 * The tree.
 * <p>
 * Component's internal model (the tree model) should be set with the 
 * setModelValue method. Tree model is a collection if POJO's. If POJO
 * has a method returning Collection, it is considered to create a new level
 * in the tree. 
 * <p>
 * The tree model could also be an instance of YTreeModelHelper. When using YTreeModelHelper,
 * build the tree using YTreeModelHelper methods in YModel and link YTree to the helper
 * in view class.
 *  <p>
 * YFormatter can be set to specify how a tree model item is showed to user. 
 * <p>
 * Controller event methods, where yyy is component's name:
 * 
 * <code>public void yyySelectionChanged()</code> executed when a selection 
 *                                             in the tree is changed <br>
 * <code>public void yyyWillExpand()</code> executed when a tree will expand <br>
 * <code>public void yyyWillCollapse()</code> executed when a tree will collapse <br>
 * <p>
 * Component checks unsaved changes before selecting a new tree node 
 * if component property YIComponent.CHECK_CHANGES has Boolean value 
 * true.
 * <p>
 * Tree may have check boxes to help selecting several nodes (see method enableCheckBoxes).
 * 
 * @author Tomi Tuomainen
 */
public class YTree extends JTree implements YIModelComponent, YIExtendedModelComponent {
    
    private boolean oldSelection = false;

	// formatter for nodes...
	private YFormatter formatter;
	
	// tree model in view model class...
	private Object modelValue; 
    
    
    private boolean autoSelectParentCheckBoxes = false;
    
    private boolean autoDeselectChildCheckBoxes = false;

	public YTree() {
		super();
		setShowsRootHandles(true);
		getSelectionModel().setSelectionMode
        	(TreeSelectionModel.SINGLE_TREE_SELECTION);
        this.setModelValue(null);
	}
	
	private YProperty myProperty = new YProperty();
	
	/*
	 *  (non-Javadoc)
	 * @see fi.mmm.yhteinen.swing.core.YIComponent#getYProperty()
	 */
	public YProperty getYProperty() {
	 	return myProperty;
	}
	
    private YController controller;
    
    private TreePath oldLeadSelectionPath = null;
    
	/* (non-Javadoc)
	 * @see fi.mmm.yhteinen.swing.component.YIControllerComponent#addViewListener(fi.mmm.yhteinen.swing.YController, fi.mmm.yhteinen.swing.YModel)
	 */
	public void addViewListener(final YController controller) {
        this.controller = controller;
		this.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
			    if (!oldSelection) {
                	if (YSaveChangesHandler.changesSavedInCurrentWindow(YTree.this)) {
                        oldLeadSelectionPath = e.getOldLeadSelectionPath(); // storing for external use
                		String methodName = YUIToolkit.createMVCMethodName(YTree.this, "SelectionChanged");
                		if (methodName != null) {
                			controller.invokeMethodIfFound(methodName);
                		}
                	} else {
                		// returning the old selection...
                         oldSelection = true;
			            setSelectionPath(e.getOldLeadSelectionPath());
			            oldSelection = false;
			        }
			    }
			}        
		});
	    this.addTreeWillExpandListener(new TreeWillExpandListener() {
	        public void treeWillExpand(TreeExpansionEvent event) {
	        	String methodName = YUIToolkit.createMVCMethodName(YTree.this, "WillExpand");
        		controller.invokeMethodIfFound(methodName);
            }
	        public void treeWillCollapse(TreeExpansionEvent event)
	            throws javax.swing.tree.ExpandVetoException {
	        	String methodName = YUIToolkit.createMVCMethodName(YTree.this, "WillCollapse");
        		controller.invokeMethodIfFound(methodName);
	        }
	      });
	}

	/**
     *  Gets value of this component for view model.
     * 
     * @return the collection which tree model is based on
	 */
	public Object getModelValue() {
		return modelValue;
	}
	
	

	/**
     * Sets view model value to this component. 
     * 
     * @param obj the collection which tree model is based on, 
     *            or alternatively an instance of YTreeModelHelper.
	 */
	public void setModelValue(Object obj) {
	    this.modelValue = obj;
	    if (obj == null) {
	        this.setModel(new DefaultTreeModel(new YTreeNode(null, null)));
	        this.setRootVisible(false);
	    } else if (obj instanceof YTreeModelHelper) {
	        constructTree((YTreeModelHelper) obj);
	    } else {    
	        YTreeNode root = null;
	        if (obj instanceof Collection) {
	            root = new YTreeNode(null, null);
	            this.setRootVisible(false);
	            addBranch(root, (Collection) obj);
	        } else {
	            this.setRootVisible(true);
	            root = new YTreeNode(obj, formatter);
	            constructTree(root);
	        }
	        DefaultTreeModel treeModel = new DefaultTreeModel(root);
	        this.setModel(treeModel);
	    }

	}
    
    private static Logger logger = Logger.getLogger(YTree.class);
    
    private void constructTree(YTreeModelHelper model) {
        YTreeNode root = new YTreeNode(null, null);
        this.setRootVisible(false);
        Iterator it = model.getNodes().iterator();
        while (it.hasNext()) {
           YTreeModelHelper.Node node = (Node) it.next();
           if (node.getParent() == null) {
               YTreeNode treeNode = new YTreeNode(node.getValue(), formatter);
               root.add(treeNode);
               addBranch(treeNode, model);
           }
       }
       DefaultTreeModel treeModel = new DefaultTreeModel(root);
       this.setModel(treeModel);
    }
    
    /**
     * Adds branch to tree.
     * @param model     helper containing new nodes
     * @param parent    parent node of new nodes
     */
    public void addBranch(YTreeNode parent, YTreeModelHelper model) {
        Iterator it = model.getNodes().iterator();
        while (it.hasNext()) {
           YTreeModelHelper.Node node = (Node) it.next();
           if (node.getParent() != null && node.getParent().getValue().equals(parent.getNodeModel())) {
               YTreeNode treeNode = new YTreeNode(node.getValue(), formatter);
               parent.add(treeNode);
               addBranch(treeNode, model);
           }
       }
    }
	
    /**
     * Creates a tree by iterating through model found in given node.
     * 
     * @param node the base node
     */
	private void constructTree(YTreeNode node) {
		// checking if model has get-method returning Collection...
		Object nodeModel = node.getNodeModel();
		Method[] methods = nodeModel.getClass().getDeclaredMethods();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			if (method.getName().indexOf("get") == 0 && 
				method.getParameterTypes().length == 0 &&
				method.getModifiers() == Modifier.PUBLIC &&
				Collection.class.isAssignableFrom(method.getReturnType())) {
				try {
					Collection children = (Collection) method.invoke(nodeModel, YCoreToolkit.EMPTY_PARAM);
					addBranch(node, children);
				} catch (Exception e) {
					throw new YException(e);
				}
			}
		}
	}
		
    
    /**
     * Adds a branch to this tree. 
     * 
     * @param parentNode the parent for the new branch
     * @param data       the data for the branch (POJO collection)
     */
	public void addBranch(YTreeNode parentNode, Collection data) {
		if (data != null) {
			Iterator it = data.iterator();
			while (it.hasNext()) {
				Object nodeModel = it.next();
                YTreeNode node = new YTreeNode(nodeModel, formatter);
                parentNode.add(node);
				constructTree(node);
			}
		}
	}
	
	/**
     * Returns the formatter which defines how a tree item is showed to user.
     * 
	 * @return the formatter for tree
	 */
	public YFormatter getFormatter() {
		return formatter;
	}
	/**
     * Sets the formatter which defines how a tree item is showed to user.
     * 
	 * @param formatter  the formatter for the tree 
     */
	public void setFormatter(YFormatter formatter) {
		this.formatter = formatter;
	}
    
    /**
     * @return  the currently selected tree item
     */
    public Object getSelectedNode() {
        YTreeNode node = (YTreeNode) this.getLastSelectedPathComponent();   
        if (node != null) {
            return node.getNodeModel();
        }
        return null;
    }    
    
    /**
     * This is a convenience method for setting MVC-name.
     * 
     * @param mvcName   the YIComponent.MVC_NAME value
     */
    public void setMvcName (String mvcName) {
        getYProperty().put(YIComponent.MVC_NAME, mvcName);
    }

    /**
     * Adds check boxes to all nodes in this tree. Selected nodes may be read by calling
     * getCheckBoxSelectedNodes. Selected nodes may be set by calling setCheckBoxSelectedNodes.
     * Alternatively, setCheckBoxSelectionsField may be called to set YModel field that holds
     * Collection of selected tree items (JavaBeans as in original tree model). When checkBoxSelectionsField
     * is set, selected items are linked automatically to YModel.
     * <p>
     * This is method uses YTreeCheckBoxRenderer and YTreeCheckBoxEditor (so usually
     * creating specific check box renderer/editor is not needed, default implementation handles this).
     */
    public void enableCheckBoxes() {
        YTreeCheckBoxRenderer renderer = new YTreeCheckBoxRenderer(createCheckBox());
        YTreeCheckBoxEditor editor = new YTreeCheckBoxEditor(this, renderer, createCheckBox());
        setCellRenderer(renderer);
        setCellEditor(editor);
        setEditable(true);
    }
    
    /**
     * Creates YCheckBox that is used as default editor and renderer when calling enableCheckBoxes.
     * This method may be overridden to customize checkBox layout or behaviour.
     *  
     * @return  the check box
     */
    protected YCheckBox createCheckBox() {
        YCheckBox checkBox = new YCheckBox();
        checkBox.setBackground(this.getBackground());
        return checkBox;
    }
    
    /**
     * Returns list of nodes that are currently selected by check boxes.
     * 
     * Calling this method makes sense only when tree has enabled check boxes.
     * 
     * @return      list of selected tree nodes (JavaBeans as in original tree model)
     */
    public List getCheckBoxSelectedNodes() {
        ArrayList selections = new ArrayList();
         if (this.getModel() != null) {
            YTreeNode node = (YTreeNode) this.getModel().getRoot();
            if (node != null) {
                 addSelections(node, selections);
                return selections; 
            }
        }
        return selections;
    }
    
    /**
     * Checks selected nodes and adds them to selections list.
     * 
     * @param node          the current node
     * @param selections    the list of selected nodes (JavaBeans as in original tree model)
     */
    private void addSelections(YTreeNode node, List selections) {
        if (node.isSelected() && node.getNodeModel() != null) {
            selections.add(node.getNodeModel());
        }
        TreeModel treeModel = getModel();
        int childCount = treeModel.getChildCount(node);
        for (int i=0; i < childCount; i++) {
            YTreeNode child = (YTreeNode) treeModel.getChild(node, i);
            addSelections(child, selections);
        }
        
    }
    
    
    /**
     * Sets tree nodes that should be currently selected by check boxes.
     *
     * Calling this method makes sense only when tree has enabled check boxes.
     * 
     * param selections      list of tree nodes to be selected (JavaBeans as in original tree model)
     */
    public void setCheckBoxSelectedNodes(Collection selections) {
        TreeModel treeModel = getModel();
        if (treeModel != null) {
            YTreeNode node = (YTreeNode) this.getModel().getRoot();
            if (node != null) {
                 setSelections(node, selections);
            }
        }
        
    }
    
    /**
     * Sets selected tree nodes.
     * 
     * @param node          the current node
     * @param selections    the list of nodes to be selected (JavaBeans as in original tree model)
     */
    
    private void setSelections(YTreeNode node, Collection selections) {
        node.setSelected(false);
        if (selections != null) {
            Iterator it = selections.iterator();
            while (it.hasNext() && !node.isSelected()) {
                Object obj = it.next();
                if (obj.equals(node.getNodeModel())) {
                    node.setSelected(true);
                    checkAutoSelect(node);
                }
            }
        }
        int childCount = treeModel.getChildCount(node);
        for (int i=0; i < childCount; i++) {
            YTreeNode child = (YTreeNode) treeModel.getChild(node, i);
            setSelections(child, selections);
        }
    }
    
   
    /**
     * Checks if parent/child nodes should be autoselected according to 
     * the changed node selection.
     * 
     * @param changedNode     the node that was just selected/deselected
     */
    void checkAutoSelect(YTreeNode changedNode) {
        if (changedNode.isSelected() && autoSelectParentCheckBoxes) {
            selectParentCheckBoxes(changedNode);
        } else if (!changedNode.isSelected() && autoDeselectChildCheckBoxes) {
            deselectChildCheckBoxes(changedNode);
        }
    }
    
    /**
     * Selects given node and all its parent nodes.
     * 
     * Calling this method makes sense only when tree has enabled check boxes.
     *
     * @param node  the node to be selected
     */
    public void selectParentCheckBoxes(YTreeNode node) {
        node.setSelected(true);
        YTreeNode parent = (YTreeNode) node.getParent();
        while (parent != null) {
            parent.setSelected(true);
            parent = (YTreeNode) parent.getParent();
        }
        this.repaint();
    }
    
    /**
     * Deselects given node and all its child nodes.
     * 
     * Calling this method makes sense only when tree has enabled check boxes.
     *
     * @param node  the node to be deselected
     */
    public void deselectChildCheckBoxes(YTreeNode node) {
        node.setSelected(false);
        int childCount = treeModel.getChildCount(node);
        for (int i=0; i < childCount; i++) {
            YTreeNode child = (YTreeNode) treeModel.getChild(node, i);
            deselectChildCheckBoxes(child);
        }
        this.repaint();
    }
    
    /**
    * @return autoDeselectChildCheckBoxes   if child nodes should be deselected when parent node is deselected
    */ 
    public boolean isAutoDeselectChildCheckBoxes() {
        return autoDeselectChildCheckBoxes;
    }
    
    /**
     * Child nodes may be deselected automatically when a parent is deselected.
     * By default autoDeselect is false.
     * 
     * Calling this method makes sense only when tree has enabled check boxes.
     *
     * @param autoDeselectChildCheckBoxes   if child nodes should be deselected when a parent  is deselected
     */
    public void setAutoDeselectChildCheckBoxes(boolean autoDeselectChildCheckBoxes) {
        this.autoDeselectChildCheckBoxes = autoDeselectChildCheckBoxes;
    }

    /**
     *
     * @return autoSelectParentCheckBoxes   if parent nodes should be selected when a child is selected
     */
    public boolean isAutoSelectParentCheckBoxes() {
        return autoSelectParentCheckBoxes;
    }

    /**
     * Parent nodes may be selected automatically when a child is selected.
     * By default autoSelect is false.
     * 
     * Calling this method makes sense only when tree has enabled check boxes.
     *
     * @param autoSelectParentCheckBoxes   if parent nodes should be selected when a child is selected
     */
    public void setAutoSelectParentCheckBoxes(boolean autoSelectParentCheckBoxes) {
        this.autoSelectParentCheckBoxes = autoSelectParentCheckBoxes;
    }
    
    private String checkBoxSelectionsField = null;
    
    /**
     * When tree has enabled check boxes (see enableCheckBoxes method), selected check boxes
     * may be linked directly to YModel by setting checkBoxSelectionsField. This field should
     * in a Collection in YModel, all the selected tree nodes are copied to this selections Collection.
     * <p>
     * Also, when notifyObservers is called in YModel, all selections are set to this tree according
     * to the YModel selections Collection.
     * 
     * @param field     the YModel field that holds the selections
     */
    public void setCheckBoxSelectionsField(String field) {
        checkBoxSelectionsField = field;
    }
    
    /**
     * @return field     the YModel field that holds the selections
     */
    public String getCheckBoxSelectionsField() {
        return checkBoxSelectionsField;
    }
    
    /*
     * (non-Javadoc)
     * @see fi.mmm.yhteinen.swing.core.YIExtendedModelComponent#getExtendedFields()
     */
     public String[] getExtendedFields() {
         if (checkBoxSelectionsField == null) return new String[0];
         return new String[] {checkBoxSelectionsField};
     }

     /*
      * (non-Javadoc)
      * @see fi.mmm.yhteinen.swing.core.YIExtendedModelComponent#setModelValue(java.lang.String, java.lang.Object)
      */
     public void setModelValue(String field, Object value) {
         if (field.equals(checkBoxSelectionsField)) {
             this.setCheckBoxSelectedNodes((Collection) value);
         }
     }
     
     /*
      * (non-Javadoc)
      * @see fi.mmm.yhteinen.swing.core.YIExtendedModelComponent#getModelValue(java.lang.String)
      */
     public Object getModelValue(String field) throws Exception {
        if (field.equals(checkBoxSelectionsField)) {
            return this.getCheckBoxSelectedNodes();
        }
        return null;
     }

    /**
     * This method is called from YCheckBoxEditor when the check box selection has changed.
     * 
     * @param current   just selected/deselected node
     */
    void nodeSelected(YTreeNode current) {
        checkAutoSelect(current);
        if (checkBoxSelectionsField != null) {
            controller.updateModelAndController(this, checkBoxSelectionsField);
        }
    }
    
    /**
     * Sets selected tree node.
     * 
     * @param obj   tree node to be selected (JavaBean as in original tree model);
     *              if null, tree selection is cleared
     * @return      true if node was found and selected
     */ 
    public boolean setSelectedNode(Object obj) {
        if (obj == null) {
            this.clearSelection();
            return true;
        } else {
            YTreeNode root = (YTreeNode) this.getModel().getRoot();
            boolean selected = setSelectedNode(root, obj);
            return selected;
        }
    } 
    
    /**
     * Sets the selected node (current or a child node of current).
     * 
     * @param current   the tree node
     * @param obj       tree node to be selected (JavaBean as in original tree model)
     * @return          true if node was found and selected
     */ 
    private boolean setSelectedNode(YTreeNode current, Object obj) {
        if (obj.equals(current.getNodeModel())) {
            this.setSelectionPath(new TreePath(current.getPath()));
            return true;
        } else {
            int count = current.getChildCount();
            for (int i=0; i < count; i++) {
                YTreeNode child = (YTreeNode) current.getChildAt(i);
                if (setSelectedNode(child, obj)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Refreshes tree model. This method updates tree model, but it does
     * not re-construct tree, it does not add new nodes
     * or remove nodes like setModelValue. Also, this method
     * does not collapse tree, like setModelValue.
     * 
     * This method should be called, when tree needs to be updated, 
     * but node structure remains unchanged. 
     * 
     * @param changedNodes  the changed nodes (POJOs, not YTreeNodes)
     */
    public void refreshModel(Collection changedNodes) {
        YTreeNode root = (YTreeNode) this.getModel().getRoot();
        refreshModel(root, changedNodes);
        
    }
    
    /**
     * Refreshes tree under given node.
     * 
     * @param node         the node to be refreshed, also children will be refreshed
     * @param changedNodes the changed nodes (POJOs, not YTreeNodes)
     */
    public void refreshModel(YTreeNode node, Collection changedNodes) {
        DefaultTreeModel treeModel = (DefaultTreeModel) this.getModel();
        // refreshing...
        if (node.getNodeModel() != null) {
            Iterator it = changedNodes.iterator();
            while (it.hasNext()) {
                Object obj = it.next();
                if (obj.equals(node.getNodeModel())) {
                    node.setNodeModel(obj);
                    treeModel.nodeChanged(node); // this refreshes the tree view
                }
            }
        }
        // checking children..
        Enumeration e = node.children();
        while (e.hasMoreElements()) {
            YTreeNode childNode = (YTreeNode) e.nextElement();
            refreshModel(childNode, changedNodes);
        }
    }

    /**
     * Returns the old selection path (passed in TreeSelectionEvent).
     * 
     * @return      the previous selection
     */
    public TreePath getOldLeadSelectionPath() {
        return oldLeadSelectionPath;
    }
    
    /**
     * Finds YTreeNode that holds nodeModel that equals given object.
     * @param obj       the object 
     * @return          the YTreeNode that holds the object
     */
    public YTreeNode getTreeNode(Object obj) {
        YTreeNode root = (YTreeNode) this.getModel().getRoot();
        return findNode(root, obj);
    }
    
    /**
     * Finds YTreeNode that holds nodeModel that equals given object.
     * @param node      the root node to start search
     * @param obj       the object 
     * @return          the YTreeNode that holds the object
     */
    private YTreeNode findNode(YTreeNode node, Object obj) {
        if (obj.equals(node.getNodeModel())) {
            return node;
        } else {
            Enumeration e =node.children();
            while (e.hasMoreElements()) {
                YTreeNode child = (YTreeNode) e.nextElement();
                YTreeNode found = findNode(child, obj);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }
    
    
}
