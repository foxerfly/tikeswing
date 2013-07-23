/*
 * Created on 30.12.2004
 *
 */
/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.component;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.util.ArrayList;


import javax.swing.JPanel;
import javax.swing.JComponent;

import fi.mmm.yhteinen.swing.core.YIComponent;
import fi.mmm.yhteinen.swing.core.tools.YProperty;
import fi.mmm.yhteinen.swing.core.tools.YUIToolkit;

/**
 * The panel. No controller event methods (so far).
 * 
 * @author Tomi Tuomainen
 */
public class YPanel extends JPanel implements YIComponent {

	private YProperty myProperty = new YProperty();
	
	/*
	 *  (non-Javadoc)
	 * @see fi.mmm.yhteinen.swing.core.YIComponent#getYProperty()
	 */
	public YProperty getYProperty() {
	 	return myProperty;
	}
    
    /**
     * 
     * @return   window that holds this panel, null if window cannot be resolved
     */
    private Window findWindow() {
        Window window = YUIToolkit.findParentWindow(this);
        if (window == null) {
            window = YUIToolkit.getCurrentWindow();
        }
        return window;
        
    }
    /**
     * A convenience method for setting Cursor.WAIT_CURSOR to this panel.
     */
    public void setWaitCursor() {
        Window w = findWindow();
        if (w != null) {
            w.setCursor(new Cursor(Cursor.WAIT_CURSOR));   
        }
    }
    
    /**
     * A convenience method for setting Cursor.DEFAULT_CURSOR to this panel.
     */
    public void setDefaultCursor() {
        Window w = findWindow();
        if (w != null) {
            w.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));   
        }
    }
    
    /**
     * Adds components to this panel. 
     * 
     * @param comps the components to add
     * @see #addComponents(JComponent[][], int, Insets)
     */
    public void addComponents(JComponent[][] comps) {
    	addComponents(comps, 0, null);
    }
    
    /**
     * Adds components to this panel. 
     * 
     * @param comps the components to add
     * @param columns the number of columns in this panel (if 0 maximum column count is calculated automatically)
     *
     * @see #addComponents(JComponent[][], int, Insets)
     */
    public void addComponents(JComponent[][] comps, int columns) {
    	addComponents(comps, columns, null);
    }

    /**
     * Adds components to this panel. The method creates widhts array
     * and then calls addComponents(JComponent[][], int[][], int, Insets).
     * By default, each component will have width 1. However, if a component
     * is duplicated in a row, it will have width 2. For example,
     * <code>{myField1, myField2, myField2}</code> would have width array
     * <code>{1, 2}</code>.
     * 
     * @param comps the components to add
     * @param columns	 the number of columns in this panel, if 0 maximum column count is calculated automatically
     * @param insets	 the insets used between components, if null zero Insets is used
     *
     * @see #addComponents(JComponent[][], int[][], int, Insets)
     *
     */
    public void addComponents(JComponent[][] comps, int columns, Insets insets) {
    	// creating widths array and handling duplicate components...
    	int[][] widths = new int [comps.length][];
    	for (int i=0; i < comps.length; i++) {
    		JComponent[] compRow = comps[i];
    		ArrayList widthList = new ArrayList(compRow.length);
    		ArrayList compList = new ArrayList(compRow.length);
			Object prev = new String("not any comp");
    		for (int j=0; j < compRow.length; j++) {
    			JComponent comp = compRow[j];
    			if (comp != prev) {
    				widthList.add(new Integer(1));
    				compList.add(comp);
    			} else {
    				// same component as previous in the row, increasing the width...
    				int lastIndex = widthList.size()-1;
    				Integer width = (Integer) widthList.get(lastIndex);
    				widthList.set(lastIndex, new Integer(width.intValue()+1));
    			}
				prev = comp;
    		}
    		// updating components to array...
    		compRow = new JComponent[compList.size()];
    		compRow = (JComponent[]) compList.toArray(compRow);
    		comps[i] = compRow;
    		// updating widths to array...
    		int[] widthRow = new int[widthList.size()];
    		for (int k=0; k < widthList.size(); k++) {
    			widthRow[k] = ((Integer) widthList.get(k)).intValue();
    		}
    		widths[i] = widthRow;
    	}
    	// now we can call the "original" addComponents method:
    	addComponents(comps, widths, columns, insets);
    }

    /**
     * Adds components to this panel.
     * 
     * @param components the components to add
     * @param widths 	 the relative width (number of columns) for each component
     * 
     * @see #addComponents(JComponent[][], int[][], int, Insets)
     */
    public void addComponents(JComponent[][] components, int[][] widths) {
    	addComponents(components, widths, 0);
    }
  
    /**
     * Adds components to this panel.
     * 
     * @param components the components to add
     * @param widths 	 the relative width (number of columns) for each component
     * @param columns	 the number of columns in this panel (if 0 maximum column count is calculated automatically)
     * 
     * @see #addComponents(JComponent[][], int[][], int, Insets)
     */
    public void addComponents(JComponent[][] components, 
            int[][] widths, int columns) {
        addComponents(components, widths, columns, null);
    }
    
    /**
     * Adds components to this panel. Components and their relative 
     * widhts are received in two dimensional arrays. The row in an 
     * array corresponds to a row in panel. <code>widths</code> must
     * be exactly the same size as <code>components</code>, 
     * meaning that there's a width defined for each component. 
     * 
     * The panel is divided into <code>columns</code> and every component
     * must have relative width (something between 1-<code>columns</code>).
     * This defines how many columns component takes. Null component will 
     * create a column with empty space.
     * 
     * The method uses GridBagLayout in implementation. Components are
     * resizable as screen width increases. Height of the component is
     * height of the highest preferred component in a row. Components are
     * "packed" to the upper left corner of the panel.
     * 
     * @param comps the components to add
     * @param widths 	 the relative width (number of columns) for each component
     * @param columns	 the number of columns in this panel (if 0 maximum column count is calculated automatically)
     * @param insets	 the insets used between components, if null zero Insets is used
     */
    public void addComponents(JComponent[][] comps, int[][] widths, 
            int columns, Insets insets) {
        YPanelItem[][] items = new YPanelItem[comps.length][];
        for (int i=0; i < comps.length; i++) {
            JComponent[] compRow = comps[i];
            ArrayList itemList = new ArrayList(compRow.length);
            for (int j=0; j < compRow.length; j++) {
                JComponent comp = compRow[j];
                YPanelItem item = new YPanelItem(comp);
                item.setColumns(widths[i][j]);
                itemList.add(item);
            }
            YPanelItem[] itemRow = new YPanelItem[itemList.size()];
            itemRow = (YPanelItem[]) itemList.toArray(itemRow);
            items[i] = itemRow;
        }
        this.addComponents(items, columns, true);
     }
    


    private int calculateMaxWidth(YPanelItem[][] comps) {
        int max = 1;
        for (int i=0; i < comps.length; i++) {
            YPanelItem[] row = comps[i];
            int count = 0;
            for (int j=0; j < row.length; j++) {
                count += row[j].getColumns();
            }
            if (count > max) {
                max = count;
            }
        }
        return max;
    }

    /**
     * Adds components to this panel. Component array defines the
     * overall positions of components in this panel. YPanelItem attributes
     * define layout behaviour (column width, fill, anchor, Insets).
     * 
     * The panel is divided into <code>columns</code> and every component
     * must have relative width (something between 1-<code>columns</code>).
     * This defines how many columns component takes. Null component will 
     * create a column with empty space.
     * 
     * The method uses GridBagLayout in implementation. Components are
     * resizable as screen width increases. Height of the component is
     * height of the highest preferred component in a row. Components are
     * "packed" to the upper left corner of the panel.
     * 
     * @param comps    the components to add
     * @param columns       the number of columns in this panel (if 0 maximum column count is calculated automatically)
     * @param packToNorth   when true, all additional space of panel will be below the components; also weightY of YPanelItem 
     *                      gets meaningless (so if weightY is specified this should be always false)
     */
    public void addComponents(YPanelItem[][] comps, int columns, boolean packToNorth) {
        if (columns <= 0) {
            columns = calculateMaxWidth (comps);
        }   
         this.setLayout(new GridBagLayout());
             
         GridBagConstraints gbc = new GridBagConstraints(); 
         gbc.gridy = 0;
         gbc.weightx = 1;
         gbc.gridwidth = 1;
         
         for (int i=0; i < columns; i++) {
             this.add(YPanelItem.createDivComponent(), gbc);         
         }
         for (int i=0; i < comps.length; i++) {
             YPanelItem[] row = comps[i];
             gbc.gridy++;
             for (int j=0; j < row.length; j++) {
                 YPanelItem panelItem = row[j];
                 JComponent comp = panelItem.getComponent(  );
                 gbc.insets = panelItem.getInsets();
                 gbc.fill = panelItem.getFill();
                 gbc.anchor = panelItem.getAnchor();
                 gbc.weighty = panelItem.getWeighty();
                 gbc.gridwidth = panelItem.getColumns();
                 // setting preferred width to 1:  
                  Dimension dim = new Dimension(1, (int)comp.getPreferredSize().getHeight());
                  comp.setPreferredSize(dim);
                 this.add(comp, gbc);
             }     
         }     
         if (packToNorth) {
             gbc.gridy++;
             gbc.weighty = 99999;  
             this.add(new JPanel(), gbc);     
         }
    }
    
    /**
     * This is a convenience method for setting MVC-name.
     * 
     * @param mvcName   the YIComponent.MVC_NAME value
     */
    public void setMvcName (String mvcName) {
        getYProperty().put(YIComponent.MVC_NAME, mvcName);
    }
    

}
