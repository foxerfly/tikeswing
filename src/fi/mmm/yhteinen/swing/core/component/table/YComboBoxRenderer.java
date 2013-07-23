/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.component.table;

import java.awt.Component;

import javax.swing.JTable;


import fi.mmm.yhteinen.swing.core.component.list.YComboBox;


/**
 * Table renderer for YComboBox. The following example
 * installs combo renderer for the first table column:
 * 
 * <pre>
 *	private YComboBox rendererCombo = new YComboBox();
 *	
 *	private YTable testTable = new YTable(columns) {
 *	 	public TableCellRenderer getCellRenderer(int row,
 *               int column) {
 *			if (column == 0) {
 *				return new YComboBoxRenderer(rendererCombo);
 *			}else  {
 *				return super.getCellRenderer(row, column);
 *			}
 *		}
 *	};
 * </pre> 
 * Alternative way would be calling YTable method setEditorAndRenderer.
 * 
 * @see YTable#setEditorAndRenderer(int, int, TableCellEditor, TableCellRenderer)
 * @author Tomi Tuomainen
 */
public class YComboBoxRenderer extends YTableCellRenderer {

	  private YComboBox combo;
      
      /**
       * @param combo the combo used by renderer
       */
      public YComboBoxRenderer(YComboBox combo) { 
          super(null); 
          this.combo = combo;
      }
      
      /*
       *  (non-Javadoc)
       * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
       */
      public Component getTableCellRendererComponent(
            JTable table, java.lang.Object value,
            boolean isSelected, boolean hasFocus, int row,
            int columnIndexOnScreen) {
      		YTable yTable = (YTable) table;
      		int column = table.convertColumnIndexToModel(columnIndexOnScreen);
    		yTable.setColors(row, column, combo, isSelected, hasFocus);
    		
    		// the following two lines are just for the layout:
      		boolean cellEditable = table.isCellEditable(row, column);
      		combo.setEditable(cellEditable);
      		combo.setModelValue(value);
            if (yTable.isCellTooltips())  {
                combo.setToolTipText(combo.getEditorText());
            }
    		return combo;
      }
      
}
