/*
 * Created on Oct 12, 2006
 *
 */
package fi.mmm.yhteinen.swing.core.component;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * Defines the component and layout properties.
 * 
 * @see YPanel#addComponents(YPanelItem[][], int, boolean)
 */
public class YPanelItem {

    private JComponent component;
    private int columns;
    private Insets insets;
    private int anchor;
    private int fill;
    private int weighty = 0;
    
    public static final Insets DEFAULT_INSETS = new Insets(1, 5, 0, 0);
    
    /**
     * @param component     the component that should be added to panel
     */
    public YPanelItem(JComponent component) {
        this(component, -1);
    }
    
    /**
     * @param component     the component that should be added to panel
     */
    public YPanelItem(JComponent component, int columns) {
        this(component, columns, null);
    }


    /**
     * @param component     the component that should be added to panel
     * @param columns       the relative width of component (how many columns it should reserve space)
     * @param insets        the minimum amount of space between the component and the edges of its display area
     */
    public YPanelItem(JComponent component, int columns, Insets insets) {
        this(component, columns, insets, -1);
    }


    /**
     * @param component     the component that should be added to panel
     * @param columns       the relative width of component (how many columns it should reserve space)
     * @param insets        the minimum amount of space between the component and the edges of its display area
     * @param anchor        where, within the display area, to place the component, @see java.awt.GridBagConstraints#anchor
     */
    public YPanelItem(JComponent component, int columns, Insets insets, int anchor) {
        this (component, columns, insets, anchor, -1);
    }

    /**
     * @param component     the component that should be added to panel
     * @param columns       the relative width of component (how many columns it should reserve space)
     * @param insets        the minimum amount of space between the component and the edges of its display area
     * @param anchor        where, within the display area, to place the component - @see java.awt.GridBagConstraints#anchor
     * @param fill          determines whether to resize the component, and if so, how - @see java.awt.GridBagConstraints#fill
     */
    public YPanelItem(JComponent component, int columns, Insets insets, int anchor, int fill) {
        this(component, columns, insets, anchor, fill, -1);
    }
    
    /**
     * @param component     the component that should be added to panel
     * @param columns       the relative width of component (how many columns it should reserve space)
     * @param insets        the minimum amount of space between the component and the edges of its display area
     * @param anchor        where, within the display area, to place the component - @see java.awt.GridBagConstraints#anchor
     * @param fill          determines whether to resize the component, and if so, how - @see java.awt.GridBagConstraints#fill
     * @param weighty       specifies how to distribute extra vertical space - @see java.awt.GridBagConstraints#weighty
     */
    public YPanelItem(JComponent component, int columns, Insets insets, int anchor, int fill, int weighty) {
        super();
        if (component == null) {
            this.component = createDivComponent();
        } else {
            this.component = component;
        }
        if (columns <= 0) {
            this.columns = 1; 
        } else {
            this.columns = columns;
        }
        if (insets == null) {
            this.insets = DEFAULT_INSETS;
        } else {
            this.insets = insets;
        }
        if (anchor <= 0) {
            this.anchor = GridBagConstraints.NORTHWEST; 
        } else {
            this.anchor = anchor;
        }
        if (fill <= 0) {
            this.fill = GridBagConstraints.HORIZONTAL;
        } else {
            this.fill = fill;
        }
        if (weighty <= 0) {
            this.weighty = 0;
        } else {
            this.weighty = weighty;
        }
    }
    
    /**
     * Creates a component for empty cells.
     */
    static JComponent createDivComponent() {
        return new JPanel();
    }
    
    
    public int getAnchor() {
        return anchor;
    }
    public void setAnchor(int anchor) {
        this.anchor = anchor;
    }
   
    public JComponent getComponent() {
        return component;
    }
    public void setComponent(JComponent component) {
        this.component = component;
    }
    public int getFill() {
        return fill;
    }
    public void setFill(int fill) {
        this.fill = fill;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public Insets getInsets() {
        return insets;
    }

    public void setInsets(Insets insets) {
        this.insets = insets;
    }

    public int getWeighty() {
        return weighty;
    }

    public void setWeighty(int weighty) {
        this.weighty = weighty;
    }
    
    
    
    
}
