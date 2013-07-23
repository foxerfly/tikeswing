/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.component.table;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;

import fi.mmm.yhteinen.swing.core.component.list.YComboBox;



/**
 * Table editor for YComboBox. The following example
 * installs combo editor for the first table column:
 * 
 * <pre>
 * 	private YComboBox editorCombo = new YComboBox();
 *	
 *	private YTable testTable = new YTable(columns) {
 *		public TableCellEditor getCellEditor(int row,
 *               int column) {
 *			if (column == 0) {
 *				return new YComboBoxEditor(editorCombo);
 *			} else  {
 *				return super.getCellEditor(row, column);
 *			}
 *		}
 *  }
 * </pre>
 * 
 * Alternative way would be calling YTable method setEditorAndRenderer.
 * 
 * @see YTable#setEditorAndRenderer(int, int, TableCellEditor, TableCellRenderer)
 * 
 * @author Tomi Tuomainen
 */
public class YComboBoxEditor extends DefaultCellEditor {

	/**
	 * @param combo the combo to be used in editor
	 */
	public YComboBoxEditor(YComboBox combo) {
		super(combo);
		// removing the action listener set in super constructor..
		combo.removeActionListener(this.delegate);
		// adding customized delegate as action listener...
		this.delegate = new ComboEditorDelegate();
		combo.addActionListener(this.delegate);
        this.setClickCountToStart(1);
	}
	
	  /*
     *  (non-Javadoc)
     * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
     */
	public Component getTableCellEditorComponent(JTable table,
	        Object value, boolean isSelected,
	        int row, int column) {
	    YComboBox combo = (YComboBox) 
	    super.getTableCellEditorComponent(
	            table, value, isSelected, row, column);
	    combo.setModelValue(value);
	    // if not selected value, setting first item selected (this will fix problem when editing with arrows when initial value is null)
	    if (value == null && combo.getItemCount() > 0) {
	        combo.setSelectedIndex(0);
	    }
	    column = table.convertColumnIndexToModel(column);
	    ((YTable)table).setColors(row, column, combo, isSelected, true);
	    return combo;
	}
    
    /*
     *  (non-Javadoc)
     * @see javax.swing.CellEditor#stopCellEditing()
     */
    public boolean stopCellEditing() {
        // moving caret to the beginning of editor when editing is stopped...
        YComboBox combo = (YComboBox)getComponent();
        JTextField field = (JTextField) combo.getEditor().getEditorComponent();
        field.setCaretPosition(0);
        return super.stopCellEditing();
    }
    
    /*
     *  (non-Javadoc)
     * @see javax.swing.CellEditor#getCellEditorValue()
     */
    public Object getCellEditorValue() {
    	YComboBox combo = (YComboBox)getComponent();
    	Object obj = combo.getModelValue();
    	return obj;
    }

    /**
     * Delegate for handling editor events.
     * 
     */
    class ComboEditorDelegate extends DefaultCellEditor.EditorDelegate implements ActionListener {
    	
    	public void actionPerformed(ActionEvent e) {
    		// doing nothing... (default implementation exits cell editing)
    	}
    }
}
