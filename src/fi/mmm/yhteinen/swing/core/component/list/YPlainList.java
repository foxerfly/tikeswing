/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.component.list;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JList;

import org.apache.commons.lang.SerializationException;
import org.apache.commons.lang.SerializationUtils;

import fi.mmm.yhteinen.swing.core.YController;
import fi.mmm.yhteinen.swing.core.YIComponent;
import fi.mmm.yhteinen.swing.core.YIModelComponent;
import fi.mmm.yhteinen.swing.core.YISharedModelComponent;
import fi.mmm.yhteinen.swing.core.error.YCloneModelException;
import fi.mmm.yhteinen.swing.core.error.YEqualsModelException;
import fi.mmm.yhteinen.swing.core.error.YException;
import fi.mmm.yhteinen.swing.core.tools.YCoreToolkit;
import fi.mmm.yhteinen.swing.core.tools.YFormatter;
import fi.mmm.yhteinen.swing.core.tools.YProperty;

/**
 * A simple list which SELECTION is NOT mapped to view model field (like
 * in YList and YComboBox). The view model Collection will be the 
 * model of the list (like in YTable). Updates of the list should
 * be made in view model class, not by using methods of JList
 * (setListData etc.)
 * 
 * @author Tomi Tuomainen
 */
public class YPlainList extends JList implements YIModelComponent {

	private boolean sort = false;
	public static final YListItem[] EMPTY_MODEL = new YListItem[0];
	private YFormatter formatter;
	
	// list data in view model class:
	private Collection modelCollection; 
	
	// the "real" data for this YList:
	private YListItem[] items;
	
	public YPlainList() {
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
     * Gets items for view model.
     * 
     * @return the value or null if no item is in the list
     */
    public Object getModelValue() {
    	return this.modelCollection;
    }

    /**
     * Sets model value to this component.
     * 
     * @param obj the collection which is used as table model
     */
    public void setModelValue(Object obj) throws YException {
        if (obj == null) {
            items = EMPTY_MODEL;
            this.modelCollection = null;
        } else {
            if (!(obj instanceof Collection)) {
                throw new YException("YPlainList model must be instance of Collection.");
            } else {
                this.modelCollection = (Collection) obj;
                // creating the real list model...
                items = new YListItem[modelCollection.size()];
                Iterator it = modelCollection.iterator();

                int i = 0;
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

            }
        }
        super.setListData(items);
    }

	/**
	 * @return formatter that defines how the data in the list 
	 * 				is showed to user
	 */
	public YFormatter getFormatter() {
		return formatter;
	}
	
	/**
	 * @param formatter defines how the data in the list is showed to user
	 */
	public void setFormatter(YFormatter formatter) {
		this.formatter = formatter;
	}

	/* (non-Javadoc)
	 * @see fi.mmm.yhteinen.swing.core.YIControllerComponent#addViewListener(fi.mmm.yhteinen.swing.core.YController)
	 */
	public void addViewListener(YController controller) {
		// nothing is done here, since model is not updated in this component
	}
	
	/**
	 * @return true if items in this list are sorted with
	 * 				lexical comparator
	 */
	public boolean isSort() {
		return sort;
	}
	/**
	 * @param sort if true items in this list are sorted with
	 * 				lexical comparator
	 */
	public void setSort(boolean sort) {
		this.sort = sort;
	}
	 /**
     * Adds a new row in this list.
     * 
     * @param obj the object (POJO) to be added to list model
     */
	public void addRow(Object obj) {
	    if (modelCollection == null) {
	        modelCollection = new ArrayList();
	    }
	    modelCollection.add(obj);
	    this.setModelValue(modelCollection);
	}
    
    /**
     * Removes a row from the list.
     * 
     * @param obj   the row object (POJO in list model) to be removed
     * @return true if row was found and removed
     */
	public boolean removeRow (Object obj) {
	    boolean removed = false;
	    if (modelCollection != null) {
	        removed =  modelCollection.remove(obj);
	        if (removed) {
	            setModelValue(modelCollection);
	        }
	    } 
	    return removed;
	}
    
	/**
     * Clones the list model Collection. Default implementation 
     * uses serialization in cloning, assuming that all objects 
     * in the list model are serializable. If this method throws 
     * an exception, override the method and implement 
     * customised copying of your dual list Collection.
     * 
     * @see YISharedModelComponent#cloneModel()
     */
    public Object cloneModel() {
    	Collection selection = (Collection) this.getModelValue();
		try {
    		Object clone = SerializationUtils.clone((Serializable) selection);
    		return clone;
    	} catch (SerializationException ex) {
    		throw new YCloneModelException(
    				"YList collection " + selection + " " +
					" is not Serializable, so cloneModel() cannot be executed." +
    				" Please implement the Serializable for all Collection objects or override cloneModel in YTable.");
    	}
    }
    
   /**
     * Checks if given model value equals list model. 
     * The default implementation uses reflection, which will 
     * access all the fields, also private. It is assumed that 
     * SecurityManager allows this access.  
     * 
     * If this method throws an exception, override the method 
     * and implement customised equals comparison for your
     * table model Collection.
     * 
     * @see YCoreToolkit#equalsCollection(Collection, Collection)
     * @see YISharedModelComponent#equalsModel(java.lang.Object)
     */
    public boolean equalsModel(Object model) {
    	Collection selection = (Collection) this.getModelValue();
		try {
    		boolean equals = YCoreToolkit.equalsCollection(
    				selection, 
    				(Collection) model);
    		return equals;
    	} catch (Exception ex) {
    		throw new YEqualsModelException(
    				"YPlainList collection " + selection 
					+ " equals comparison couldn't be done because of SecurityManager settings."
					+ " Allow the access to private fields or override equalsModel in YTable.");
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
