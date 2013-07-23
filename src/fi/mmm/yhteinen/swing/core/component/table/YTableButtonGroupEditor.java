/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.component.table;

import java.awt.Component;
import javax.swing.AbstractCellEditor;

import javax.swing.JTable;
import javax.swing.table.TableCellEditor;



/**
 * Table editor for YTableButtonGroup. The following example
 * installs button group editor for the first table column:
 * <pre>
 *  
 * 
 * 	private YTableButtonGroup createTableButtonGroup() {
 *		YTableButtonGroup group = new YTableButtonGroup(3);
 *		group.setTexts(new String[] {"Age 10", "Age 20", "Age 30"});
 *		group.setSelectionIds(new Object[] {new Integer(10), new Integer(20), new Integer(30)});
 *		return group;
 *	}
 *
 *	private final YTableButtonGroup editor = createTableButtonGroup();
 *
 *	private YTable table = new YTable (columns) {
 *		public TableCellEditor getCellEditor(int row,
 *              int column) {
 *			if (column == 1) {
 *				return new YTableButtonGroupEditor(editor);
 *			} else {
 *				return super.getCellEditor(row, column);
 *			}
 *		}
 *	}
 * </pre>
 * 
 *  Alternative way would be calling YTable method setEditorAndRenderer.
 * 
 * @see YTable#setEditorAndRenderer(int, int, TableCellEditor, TableCellRenderer)
 *
 * @see YTableButtonGroupRenderer
 * @see YTableButtonGroup
 * 
 * @author Tomi Tuomainen
 */
public class YTableButtonGroupEditor extends AbstractCellEditor implements TableCellEditor {

	private YTableButtonGroup buttonGroup;
	/**
	 * @param buttonGroup	the button group to be used in this editor
	 */
	public YTableButtonGroupEditor(YTableButtonGroup buttonGroup) {
		super();
		this.buttonGroup = buttonGroup;
		buttonGroup.setEditor(this);
	}
	
	  /*
     *  (non-Javadoc)
     * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
     */
    public Component getTableCellEditorComponent(JTable table,
            Object value, boolean isSelected,
            int row, int column) {
    	buttonGroup.setSelectedId(value);
    	column = table.convertColumnIndexToModel(column);
    	((YTable)table).setColors(row, column, buttonGroup, isSelected, true);
    	return buttonGroup;
    }

    /*
     *  (non-Javadoc)
     * @see javax.swing.CellEditor#getCellEditorValue()
     */
    public Object getCellEditorValue() {
    	return buttonGroup.getSelectedId();
    }
    
}
