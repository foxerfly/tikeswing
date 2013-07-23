/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.component.list;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import fi.mmm.yhteinen.swing.core.YController;
import fi.mmm.yhteinen.swing.core.YIComponent;
import fi.mmm.yhteinen.swing.core.YIModelComponent;
import fi.mmm.yhteinen.swing.core.error.YException;
import fi.mmm.yhteinen.swing.core.tools.YCoreToolkit;
import fi.mmm.yhteinen.swing.core.tools.YFormatter;
import fi.mmm.yhteinen.swing.core.tools.YProperty;

/**
 * The list.
 * <p>
 * Component's internal model should be set with one of the 
 * setListModel methods. List model is a collection of POJO's. 
 * YFormatter can be set to specify how list model is showed to user. 
 * Method setModelField can be used to to specify, which
 * list model field is mapped to a view model field. Normal equals method
 * comparison is used when mapping fields (so it is assumed that
 * equals is implemented in a class of the view model field). 
 * <p>
 * Methods getModelValue and setModelValue use setSelectedValue and
 * getSelectedValue of JList. By default only one selected
 * list item will be mapped to view model. 
 * 
 * @author Tomi Tuomainen
 */
public class YList extends JList implements YIModelComponent {

	// the collection of items for list:
	private YListItem[] items;
	
	// the field to be mapped to the model
	private String modelField;
	
	/**
	 * 
	 */
	public YList() {
		super();
	}
	
	private YProperty myProperty = new YProperty();
	
	/*
	 *  (non-Javadoc)
	 * @see fi.mmm.yhteinen.swing.core.YIComponent#getYProperty()
	 */
	public YProperty getYProperty() {
	 	return myProperty;
	}
	
	/**
     * Returns field name that is mapped to view model.
     * The field name is some field in combo model (collection 
     * of POJOs).
     * 
	 * @return the name of the field to be mapped to view model
	 */
	public String getModelField() {
		return modelField;
	}
	
	/**
     * Sets field name in that will be mapped to view model.
     * The field name is some field in list model (collection 
     * of POJOs).
     * 
     * @param modelField the name of the field to be mapped to view model
	 */
	public void setModelField(String modelField) {
		this.modelField = modelField;
	}
	
	/* (non-Javadoc)
	 * @see fi.mmm.yhteinen.swing.component.YIControllerComponent#addViewListener(fi.mmm.yhteinen.swing.YController, fi.mmm.yhteinen.swing.YModel)
	 */
	public void addViewListener(final YController controller) {
		this.addListSelectionListener(new ListSelectionListener() {
	        public void valueChanged(ListSelectionEvent ev) {
                controller.updateModelAndController(YList.this);
            }
        });
	}
	
    
	/** 
     * Sets internal model for this list. 
     * 
     * @param data     the collection of POJOs
     * @param formatter defines how a list item is showed to user
     * @param sort		if items will be sorted according to the interface String presentation
     */
	public void setListModel(Collection data, YFormatter formatter, boolean sort) {
	    int i = 0;
	    items = new YListItem[data.size()];
	    Iterator it = data.iterator();

	    while (it.hasNext()) {
	        YListItem comboItem = new YListItem();
	        if (formatter != null) {
	            comboItem.setFormatter(formatter);
	        }
	        comboItem.setItemModel(it.next());
	        items[i++] = comboItem;
	    }
	    if (sort) {
	        Arrays.sort(items, YCoreToolkit.LEXICAL_COMPARATOR);
	    }

	    super.setListData(items);
	}
    
	/**
     * Gets selected item for view model.
     * 
     * @see #setModelField(String)
     * 
     * @return the value or null if no item is selected
     */
	public Object getModelValue() {
	    Object item = super.getSelectedValue();
	    if (item == null || !(item instanceof YListItem)) {
	        return null;
	    } else {
	        YListItem listItem = (YListItem) super.getSelectedValue();
	        if (modelField == null) {
	            return listItem.getItemModel();
	        } else {
	            return YCoreToolkit.getBeanValue(listItem.getItemModel(), modelField);
	        }
	    }
	}

    /**
     * Sets model value from view model. Finds matching item 
     * from the list and sets it selected.
     * 
     * @param obj the view model value
     * @see #setModelField(String)
     */
	public void setModelValue(Object obj) throws YException {
	    if (obj == null) {
	        this.setSelectedIndex(-1);
	    } else {
	        // searching corresponding item in list:
	        if (items == null) {
	            throw new YException("List model for YList is not set.");
	        } else {
	            for (int i=0; i < this.items.length; i++) {
	                YListItem item = (YListItem)items[i];
	                if (this.modelField == null) {
	                    if (obj.equals(item.getItemModel())) {
	                        this.setSelectedValue(item, true);
	                    }
	                } else {
	                    Object value = YCoreToolkit.getBeanValue(item.getItemModel(), modelField);
	                    if (obj.equals(value)) {
	                        this.setSelectedValue(item, true);
	                    }
	                }
	            }

	        }
	    }
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






