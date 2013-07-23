/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.component.list;
import fi.mmm.yhteinen.swing.core.tools.YFormatter;

/**
 * The list item for YComboBox and YListItem.
 * Formatter specifies how the item is showed to user. 
 * The itemModel is some POJO to be stored in list.
 * 
 * @author Tomi Tuomainen
 */
public class YListItem {
	
	private Object itemModel;
	private YFormatter formatter;
    
	/**
	 * @return the formatter used by this item
	 */
	public YFormatter getFormatter() {
		return formatter;
	}
	/**
     * 
	 * @param formatter the formatter for this item
	 */
	public void setFormatter(YFormatter formatter) {
		this.formatter = formatter;
	}
	/**
	 * @return the model stored in this item
	 */
	public Object getItemModel() {
		return itemModel;
	}
	/**
	 * @param item the model to be stored in this item
	 */
	public void setItemModel(Object item) {
		this.itemModel = item;
	}
	
    /**
     * String presentation of this item, defines how the item is showed to user.
     * If formatter is set, it defines how item model is formatted in a view. 
     * If formatter is null, item model's toString() method is used.
     * 
     */
	public String toString() {
		if (formatter == null) {
			return itemModel.toString();
		} else {
			return formatter.format(itemModel);
		}
	}
}
