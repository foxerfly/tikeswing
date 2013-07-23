/*
 * Created on 3.1.2005
 *
 */
/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.component.table;


import java.awt.Component;

import javax.swing.JTable;

import fi.mmm.yhteinen.swing.core.component.field.YFormattedTextField;

/**
 * A table renderer for YFormattedTextField.
 * <p>
 * The following example installs renderer for all Long objects 
 * in a table:
 * <pre>
 * YTable table = new YTable(columns);
 * YFormattedTextField field = new YFormattedTextField(longFormatter);
 * YFormattedFieldRenderer renderer = new YFormattedFieldRenderer(field);
 * table.setDefaultRenderer(Long.class, renderer);
 * </pre>
 * 
 * Renderer can be set also by overriding getCellRenderer method of YTable
 * (or just by calling setEditorAndRenderer method of YTable).
 * 
 * 
 * @author Tomi Tuomainen
 * @see YTable
 * @see YTable#setEditorAndRenderer(int, int, TableCellEditor, TableCellRenderer)
 */
public class YFormattedFieldRenderer extends YTableCellRenderer {
    
        private YFormattedTextField field;
        
        /**
         * @param field the field used by renderer
         */
        public YFormattedFieldRenderer(YFormattedTextField field) { 
            super(null); 
            this.field = field;
            this.setHorizontalAlignment(field.getHorizontalAlignment());
        }
        
        public Component getTableCellRendererComponent(
                JTable table, java.lang.Object value,
                boolean isSelected, boolean hasFocus, int row,
                int columnIndexOnScreen) {
        		Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, columnIndexOnScreen);
        		int column = table.convertColumnIndexToModel(columnIndexOnScreen);
        		YTable yTable = (YTable) table;
        		yTable.setColors(row, column, comp, isSelected, hasFocus);
        		return comp;
        }
        
        /*
         *  (non-Javadoc)
         * @see javax.swing.table.DefaultTableCellRenderer#setValue(java.lang.Object)
         */
        public void setValue(Object value) {
            field.setModelValue(value); 
            setText(field.getText());
        }
       
}
