/*
 * Created on 3.1.2005
 *
 */
/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.component.table;

import java.awt.Component;
import java.text.ParseException;
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import fi.mmm.yhteinen.swing.core.component.field.YFormattedTextField;
import fi.mmm.yhteinen.swing.core.error.YException;

/**
 * A table editor for YFormattedTextField. The following example
 * installs editor for all Long objects it a table:
 * <pre>
 * YTable table = new YTable(columns);
 * YFormattedTextField field = new YFormattedTextField(longFormatter);
 * YFormattedFieldEditor editor = new YFormattedFieldEditor(field);
 * table.setDefaultEditor(Long.class, editor);
 * </pre>
 * The editor class should be also set to YColumn for specifying which
 * editor should be started.
 * <p>
 * Editor can be set also by overriding getCellEditor method of YTable (or 
 * just by calling setEditorAndRenderer method of YTable).
 * 
 * @author Tomi Tuomainen
 * @see YTable
 * @see YColumn#setEditorClass(Class)
 * @see YTable#setEditorAndRenderer(int, int, TableCellEditor, TableCellRenderer)

 */
public class YFormattedFieldEditor extends DefaultCellEditor {

    /**
     * @param field the field used by editor
     */
    public YFormattedFieldEditor(YFormattedTextField field) {
        super(field);
        this.setClickCountToStart(1);
    }

    /*
     *  (non-Javadoc)
     * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
     */
    public Component getTableCellEditorComponent(JTable table,
            Object value, boolean isSelected,
            int row, int column) {
    	YFormattedTextField field = (YFormattedTextField) 
            super.getTableCellEditorComponent(
                table, value, isSelected, row, column);
    	field.setModelValue(value);
    	column = table.convertColumnIndexToModel(column);
    	((YTable)table).setColors(row, column, field, isSelected, true);
    	return field;
    }

    /*
     *  (non-Javadoc)
     * @see javax.swing.CellEditor#getCellEditorValue()
     */
    public Object getCellEditorValue() {
    	YFormattedTextField field = (YFormattedTextField)getComponent();
    	Object obj = field.getModelValue();
		return obj;
    }
    
    /*
     *  (non-Javadoc)
     * @see javax.swing.CellEditor#stopCellEditing()
     */
    public boolean stopCellEditing() {
    	YFormattedTextField field = (YFormattedTextField)getComponent();
    	if (field.valueValid()) {
    		try {
    			field.commitEdit();
    		} catch (ParseException ex) {
    			// unexpected exception
    			throw new YException(ex);
    		}
   		} else {
   			return false;
   		}
   		if (!field.isValid()) {
   			return false;
   		}
        return super.stopCellEditing();
    }

}
