/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.component.table;

import java.awt.Component;

import javax.swing.JTable;



/**
 * Table renderer for YTableButtonGroup. The following example
 * installs button group renderer for the first table column:
 * 
 * <pre>
 * 	private YTableButtonGroup createTableButtonGroup() {
 *		YTableButtonGroup group = new YTableButtonGroup(3);
 *		group.setTexts(new String[] {"Age 10", "Age 20", "Age 30"});
 *		group.setSelectionIds(new Object[] {new Integer(10), new Integer(20), new Integer(30)});
 *		return group;
 *	}
 *
 *	private final YTableButtonGroup renderer = createTableButtonGroup();
 *
 *	private YTable table = new YTable (columns) {
 *		public TableCellRenderer getCellRenderer(int row,
 *               int column) {
 *			if (column == 1) {
 *				return new YTableButtonGroupRenderer(renderer);
 *			} else {
 *				return super.getCellRenderer(row, column);
 *			}
 *		}
 *	}
 * </pre> 
 * 
 *  Alternative way would be calling YTable method setEditorAndRenderer.
 * 
 * @see YTable#setEditorAndRenderer(int, int, TableCellEditor, TableCellRenderer)
 *
 * @see YTableButtonGroupEditor
 * @see YTableButtonGroup
 * 
 * @author Tomi Tuomainen
 */
public class YTableButtonGroupRenderer extends YTableCellRenderer {

	  private YTableButtonGroup buttonGroup;
      
      /**
       * @param buttonGroup the button group to be used in this renderer
       */
      public YTableButtonGroupRenderer(YTableButtonGroup buttonGroup) { 
          super(null); 
          this.buttonGroup = buttonGroup;
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
      		yTable.setColors(row, column, buttonGroup, isSelected, hasFocus);
      		buttonGroup.setSelectedId(value);
            return buttonGroup;
      }

}
