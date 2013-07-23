/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.component.table;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.SwingConstants;

import fi.mmm.yhteinen.swing.core.component.YCheckBox;

/**
 * Table renderer for check box. The following example
 * installs check box renderer for the first table column:
 * 
 * <pre>
 *	private YCheckBox renderer = new YCheckBox();
 *	
 *	private YTable testTable = new YTable(columns) {
 *	 	public TableCellRenderer getCellRenderer(int row,
 *               int column) {
 *			if (column == 0) {
 *				return new YCheckBoxRenderer(renderer);
 *			}else  {
 *				return super.getCellRenderer(row, column);
 *			}
 *		}
 *	};
 * </pre> 
 *
 * Alternative way would be calling YTable method setEditorAndRenderer.
 * 
 * @see YTable#setEditorAndRenderer(int, int, TableCellEditor, TableCellRenderer)
 *
 * @author Tomi Tuomainen
 */
public class YCheckBoxRenderer extends YTableCellRenderer{

	  private YCheckBox checkBox;
      
      /**
       * @param checkBox the check box used by renderer
       */
      public YCheckBoxRenderer(YCheckBox checkBox) { 
          super(null); 
          this.checkBox = checkBox;
          checkBox.setHorizontalAlignment(SwingConstants.CENTER);
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
    		yTable.setColors(row, column, checkBox, isSelected, hasFocus);
    		checkBox.setModelValue(value);
             if (yTable.isCellTooltips())  {
                 checkBox.setToolTipText(checkBox.getText());
             }
	  		return checkBox;
      }

}
