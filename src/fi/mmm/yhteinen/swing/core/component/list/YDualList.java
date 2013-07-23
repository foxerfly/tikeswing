/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.component.list;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.lang.builder.EqualsBuilder;

import fi.mmm.yhteinen.swing.core.YController;
import fi.mmm.yhteinen.swing.core.YIComponent;
import fi.mmm.yhteinen.swing.core.YISharedModelComponent;
import fi.mmm.yhteinen.swing.core.component.YButton;
import fi.mmm.yhteinen.swing.core.component.YPanel;
import fi.mmm.yhteinen.swing.core.component.YScrollPane;
import fi.mmm.yhteinen.swing.core.error.YEqualsModelException;
import fi.mmm.yhteinen.swing.core.error.YException;
import fi.mmm.yhteinen.swing.core.tools.YFormatter;

/**
 * <p>
 * A component with that holds two YPlainLists and buttons for
 * moving rows from one list to another. 
 * <p>
 * Component's internal model (a source list model) should be set 
 * with setListModel method. A list model is a collection 
 * of POJO's. YFormatter can be set to specify how list model 
 * is showed to user. Selected values in a target list is are mapped
 * to a view model Collection. 
 * <p>
 * This component can be used with POJOs that implement Serializable
 * interface. It is also assumed, that SecurityManager allows
 * copying of private field via reflection. 
 *
 * @author Tomi Tuomainen
 */
public class YDualList extends YPanel implements YISharedModelComponent{

	private YPlainList sourceList = new YPlainList();
	private YPlainList targetList = new YPlainList();
	private YScrollPane sourcePane = new YScrollPane(sourceList);
	private YScrollPane targetPane = new YScrollPane(targetList);
	
	private Collection listModelData;
	
	private YButton buttonAdd = new YButton(">");
	private YButton buttonRemove = new YButton("<");
	private YButton buttonAddAll = new YButton(">>");
	private YButton buttonRemoveAll = new YButton("<<");

	public YDualList() {
		super();
		initButton(buttonAdd);
		initButton(buttonRemove);
		initButton(buttonAddAll);
		initButton(buttonRemoveAll);
		addComponents();
		addListeners();
		enableButtons();
	}
	
	private static final Dimension DEFAULT_BUTTON_SIZE = new Dimension(40, 25);
	/**
	 * Inits button layout.
	 * @param button	the button
	 */
	private void initButton(YButton button) {
		button.setMargin(new Insets(0, 0, 0, 0));
		button.setPreferredSize(DEFAULT_BUTTON_SIZE);
		button.setMinimumSize(DEFAULT_BUTTON_SIZE);
	}
	
	/**
	 * Creates YDualList layout.
	 */
	private void addComponents() {
		this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints(); 
        // lists:
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.gridheight = 6;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        this.add(sourcePane, gbc);
        gbc.gridx = 2;
        this.add(targetPane, gbc);
       
        // buttons:
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.gridheight = 1;
        gbc.gridx = 1;
        gbc.gridy = 1;
        this.add(buttonAddAll, gbc);
        gbc.gridy = 2;
        this.add(buttonAdd, gbc);
        gbc.gridy = 3;
        this.add(buttonRemove, gbc);
        gbc.gridy = 4;
        this.add(buttonRemoveAll, gbc);
        // dummy panels:
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        gbc.gridy = 0;
        gbc.gridx = 1;
        this.add(new JPanel(), gbc);
        gbc.gridy = 5;
        this.add(new JPanel(), gbc);
       
  }
	/**
	 * Adds listeners to components.
	 */
	private void addListeners() {
		
	}

	/**
     * Gets selected items for a view model. The target list
     * values are returned as a Collection of POJOs.
     * 
     * @return the Collection or null if no items are selected
     */
	public Object getModelValue() {
        Collection result = (Collection) targetList.getModelValue();
        if (result == null || result.isEmpty()) {
            return null;
        } else {
            return result;
        }
	}

	/**
     * Sets model value from a view model. List model must be set
     * to source list before this method is called (by the framework).
     * The method adds selected values in the view model 
     * to target list. 
     * 
     * @param obj 	the view model value (Collection)
     */
	public void setModelValue(Object obj) throws YException {
 		if (listModelData == null) {
			throw new YException(
					"List model for YDualList is not set.");
		} else if (obj != null && !(obj instanceof Collection)) {
    		throw new YException(
    			"YDualList model must be instance of Collection.");
		} else {
			Collection modelData = (Collection) obj;
			boolean modelDataEmpty = (modelData == null || modelData.size() == 0);
			ArrayList sourceData = new ArrayList();
			ArrayList targetData = new ArrayList();
			Iterator itList = listModelData.iterator();
			while (itList.hasNext()) {
				Object listValue = itList.next();
				boolean found = false;
                if (!modelDataEmpty) {
                    // searching list item from model collection...
                    Iterator itModel = modelData.iterator();
                    while (itModel.hasNext() && !found) {
                        Object modelValue = itModel.next();
					    try {
					        boolean equals = EqualsBuilder.reflectionEquals(listValue, modelValue, false, Object.class);
					        if (equals) {
					            targetData.add(modelValue);
					            found = true;
						    } 
                        } catch (Exception ex) {
                            throw new YEqualsModelException(
								"YDualList collection equals comparison couldn't be done because of SecurityManager settings."
								+ " Allow the access to private fields to be able to use YDualList.");
					    }
                    }
                }
				if (!found) {
					sourceData.add(listValue);
				}
            }
			sourceList.setModelValue(sourceData);
            if (modelData == null) {
                targetList.setModelValue(null);
            } else {
                targetList.setModelValue(targetData);
            }
			enableButtons();
		}
   	}

	/* (non-Javadoc)
	 * @see fi.mmm.yhteinen.swing.core.YIControllerComponent#addViewListener(fi.mmm.yhteinen.swing.core.YController)
	 */
	public void addViewListener(final YController controller) {
		this.buttonAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				addOne(controller);
			}
		});
		this.buttonRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				removeOne(controller);
			}
		});
		this.buttonAddAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				addAll(controller);
			}
		});
		this.buttonRemoveAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				remAll(controller);
			}
		});
		this.sourceList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent ev) {
				enableButtons();
			}
		});
		this.targetList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent ev) {
				enableButtons();
			}
		});
	}
	
	/** 
     * Sets internal model for this list.  The list model is set
     * to the source list.
     * 
     * @param data     the collection of POJOs
     * @param formatter defines how a list item is showed to user
     * @param sort		if items will be sorted according to the interface String presentation
     */
    public void setListModel(Collection data, YFormatter formatter, boolean sort) {
    	this.listModelData = data;
    	sourceList.setFormatter(formatter);
    	targetList.setFormatter(formatter);
    	sourceList.setSort(sort);
    	targetList.setSort(sort);
    }
    
    /**
     * Sets enabled state for buttons.
     */
    private void enableButtons() {
    	
    	this.buttonAdd.setEnabled(sourceList.getSelectedIndex() != -1);
    	this.buttonAddAll.setEnabled(sourceList.getModelValue() != null &&
    								 ((Collection)sourceList.getModelValue()).size() > 0);
    	this.buttonRemove.setEnabled(targetList.getSelectedIndex() != -1);
    	this.buttonRemoveAll.setEnabled(targetList.getModelValue() != null &&
    								 ((Collection)targetList.getModelValue()).size() > 0);
    }
    
    /**
     * Transfers selected value from the source list to the target list.
     * 
     * @param controller the controller of this component
     */
    private void addOne(YController controller) {
        Object[] values = sourceList.getSelectedValues();
        if (values != null && values.length > 0) {
            for (int i=0; i < values.length; i++) {
                YListItem item = (YListItem) values[i];;
                targetList.addRow(item.getItemModel());
                sourceList.removeRow(item.getItemModel());
            }
            controller.updateModelAndController(YDualList.this);
            enableButtons();
        }     
    }
    
    /**
     * Transfers selected value from the targer list to the source list.
     * 
     * @param controller the controller of this component
     */
    private void removeOne(YController controller) {
        Object[] values = targetList.getSelectedValues();
        if (values != null && values.length > 0) {
            for (int i=0; i < values.length; i++) {
                YListItem item = (YListItem) values[i];;
                sourceList.addRow(item.getItemModel());
                targetList.removeRow(item.getItemModel());
            }
    		controller.updateModelAndController(YDualList.this);
			enableButtons();
    	}
    }
    
    /**
     * Transfers all values from the source list to the target list.
     * 
     * @param controller the controller of this component
     */
    private void addAll(YController controller) {
        Collection sourceData = (Collection) sourceList.getModelValue();
        if (sourceData != null) {
            Collection targetData = (Collection) targetList.getModelValue();
            if (targetData == null) {
                targetData = new ArrayList();
            }
            Iterator it = sourceData.iterator();
            while (it.hasNext()) {
                targetData.add(it.next());
            }
            sourceList.setModelValue(null);
            targetList.setModelValue(targetData);
            controller.updateModelAndController(YDualList.this);
            enableButtons();

        }
    }
    
    /**
     * Transfers all values from the target list to the source list.
     * 
     * @param controller the controller of this component
     */
    private void remAll(YController controller) {
    	Collection targetData = (Collection) targetList.getModelValue();
    	if (targetData != null) {
        	Collection sourceData = (Collection) sourceList.getModelValue();
    		if (sourceData == null) {
    			sourceData = new ArrayList();
    		}
        	Iterator it = targetData.iterator();
    		while (it.hasNext()) {
    			sourceData.add(it.next());
    		}
    		
    			targetList.setModelValue(null);
    			sourceList.setModelValue(sourceData);
    			controller.updateModelAndController(YDualList.this);
    			enableButtons();
    		
    	}
    }
    

	/**
     * Clones Collection on the target list (selected values).
     * 
     * @see YPlainList#cloneModel
     * @see YISharedModelComponent#cloneModel()
     */
    public Object cloneModel() {
    	return targetList.cloneModel();
   }
    
   /**
     * Checks if given model value equals selected values in target list. 
     * 
     * @see YPlainList#equalsModel
     * @see YISharedModelComponent#equalsModel(java.lang.Object)
     */
    public boolean equalsModel(Object model) {
    	return this.targetList.equalsModel(model);
    }
	
    /**
     * @return the remove button
     */
    public YButton getButtonRemove() {
		return buttonRemove;
	}
	
    /**
     * @return the remove all button
     */
    public YButton getButtonRemoveAll() {
		return buttonRemoveAll;
	}
	
    /**
     * @return the list holding values that are not selected
     */
	public YPlainList getSourceList() {
		return sourceList;
	}
	
	/**
	 * @return the list holding selected values
	 */
	public YPlainList getTargetList() {
		return targetList;
	}

	/**
	 * @return the scroll pane of the source list
	 */
	public YScrollPane getSourcePane() {
		return sourcePane;
	}

	/**
	 * @return the scroll pane of the target list
	 */
	public YScrollPane getTargetPane() {
		return targetPane;
	}
	
	public void setEnabled(boolean ed) {
		super.setEnabled(ed);
		this.sourceList.setEnabled(ed);
		this.targetList.setEnabled(ed);
		this.buttonAdd.setEnabled(ed);
		this.buttonAddAll.setEnabled(ed);
		this.buttonRemove.setEnabled(ed);
		this.buttonRemoveAll.setEnabled(ed);
	}
    
    /**
     * This is a convenience method for setting MVC-name.
     * 
     * @param mvcName   the YIComponent.MVC_NAME value
     */
    public void setMvcName (String mvcName) {
        getYProperty().put(YIComponent.MVC_NAME, mvcName);
    }
}
