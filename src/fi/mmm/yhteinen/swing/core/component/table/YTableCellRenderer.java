/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.component.table;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Default Renderer for YTable. Uses YTableFormatter to render data.
 * 
 * @author Tomi Tuomainen
 * @see YTable
 */
public class YTableCellRenderer extends DefaultTableCellRenderer {
    
    private YTableFormatter formatter;
    
    /**
     * @param formatter defines how the data is showed to user
     */
    public YTableCellRenderer(YTableFormatter formatter) { 
        this.formatter = formatter;
    }
    
   /*
    *  (non-Javadoc)
    * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
    */
    public Component getTableCellRendererComponent(
            JTable table, java.lang.Object value,
            boolean isSelected, boolean hasFocus, int row,
            int columnIndexOnScreen) {
    	JLabel label = (JLabel) super.getTableCellRendererComponent(table,
                value, isSelected, hasFocus, row, columnIndexOnScreen);
    	YTable yTable = (YTable) table;
    	int column = table.convertColumnIndexToModel(columnIndexOnScreen);
        yTable.setColors(row, column, label, isSelected, hasFocus);
        if (formatter != null) {
    	    label.setText(formatter.format(value, row, column));
    	}
        if (yTable.isCellTooltips())  {
            label.setToolTipText(label.getText());
        }
    	return label;
    }
    
   /**
     * @return defines how the data is showed to user
     */
    public YTableFormatter getFormatter() {
        return formatter;
    }
    /**
     * @param formatter defines how the data is showed to user
     */
    public void setFormatter(YTableFormatter formatter) {
        this.formatter = formatter;
    }
}
