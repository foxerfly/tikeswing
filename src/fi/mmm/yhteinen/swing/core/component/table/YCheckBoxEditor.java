/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.component.table;


import java.awt.Component;


import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import fi.mmm.yhteinen.swing.core.component.YCheckBox;

/**
 * Table editor for YCheckBox. The following example
 * installs check box editor for the first table column:
 * <pre>
 * 	private YCheckBox editor = new YCheckBox();
 *	
 *	private YTable testTable = new YTable(columns) {
 *		public TableCellEditor getCellEditor(int row,
 *               int column) {
 *			if (column == 0) {
 *				return new YCheckBoxEditor(editor);
 *			} else  {
 *				return super.getCellEditor(row, column);
 *			}
 *		}
 * }
 * </pre>
 * 
 * Alternative way would be calling YTable method setEditorAndRenderer.
 * 
 * @see YTable#setEditorAndRenderer(int, int, TableCellEditor, TableCellRenderer)
 * 
 * @author Tomi Tuomainen
 */
public class YCheckBoxEditor extends DefaultCellEditor {

	/**
	 * @param checkBox the check box to be used in editor
	 */
	public YCheckBoxEditor(YCheckBox checkBox) {
		super(checkBox);
		checkBox.setHorizontalAlignment(SwingConstants.CENTER);
	}
	
	  /*
     *  (non-Javadoc)
     * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
     */
	public Component getTableCellEditorComponent(JTable table,
			Object value, boolean isSelected,
			int row, int column) {
		YCheckBox checkBox = (YCheckBox) 
		
		super.getTableCellEditorComponent(
				table, value, isSelected, row, column);
		checkBox.setModelValue(value);
		column = table.convertColumnIndexToModel(column);
		((YTable)table).setColors(row, column, checkBox, isSelected, true);
		
		return checkBox;
	}

    /*
     *  (non-Javadoc)
     * @see javax.swing.CellEditor#getCellEditorValue()
     */
    public Object getCellEditorValue() {
    	YCheckBox checkBox = (YCheckBox)getComponent();
    	Object obj = checkBox.getModelValue();
    	return obj;
    }
    
}
