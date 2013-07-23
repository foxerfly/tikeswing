/*
 * Created on Aug 23, 2007
 *
 */
package fi.mmm.yhteinen.swing.core.component.piechart;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;

import fi.mmm.yhteinen.swing.core.YController;
import fi.mmm.yhteinen.swing.core.YIExtendedModelComponent;
import fi.mmm.yhteinen.swing.core.YIModelComponent;

import fi.mmm.yhteinen.swing.core.error.YException;
import fi.mmm.yhteinen.swing.core.tools.YProperty;

/**
 * The pie chart. 
 * <p>
 * Model for this pie chart must be List of Numbers (Double, Long etc.). 
 * Numbers present pie chart percents. 
 * <p>
 * Colors for pies may be set by calling initColors method. 
 * There should be Color initialized for each pie. Alternatively,
 * you may call setColorModelField method and add List of Colors
 * in your YModel class.
 * 
 * @author Tomi Tuomainen
 */
public class YPieChart extends JComponent implements YIModelComponent, YIExtendedModelComponent {

     private YProperty myProperty = new YProperty();
    
     private String colorModelField = null;
     
     private List originalNumbers;
     private List originalColors;
     
     private Color[] colors;
     private double[] sizes;
     
     
     /*
      * (non-Javadoc)
      * @see fi.mmm.yhteinen.swing.core.YIComponent#getYProperty()
      */
     public YProperty getYProperty() {
           return myProperty;
     }
     

     
  
     /*
      * (non-Javadoc)
      * @see fi.mmm.yhteinen.swing.core.YIModelComponent#getModelValue()
      */
    public Object getModelValue() {
        return originalNumbers;
    }

    /*
     * (non-Javadoc)
     * @see fi.mmm.yhteinen.swing.core.YIModelComponent#setModelValue(java.lang.Object)
     */
    public void setModelValue(Object obj) {
        if (!(obj instanceof List)) {
            throw new YException(
                    "YPieChart model must be instance of List.");
        } else {
            originalNumbers = (List) obj;
            sizes = new double[originalNumbers.size()];
            int i = 0;
            Iterator it = ((Collection) obj).iterator();
            while (it.hasNext()) {
                Object item = it.next();
                if (! (item instanceof Number)) {
                    throw new YException("YPieChart model must be List of Numbers");
                } else {
                    Number n = (Number) item;
                    sizes[i++] = n.doubleValue();
                }
            }
        }
    }
   
    /**
     * Initializes Colors for pie chart. It is assumed that
     * there is Color defined for each pie in the list.
     * 
     * @param colorList the Color list
     */
    public void initColors(List colorList) {
        originalColors = colorList;
        // converting Colors to array for performance
        if (originalColors == null) {
            this.colors = null;
        } else {
            colors = new Color[originalColors.size()];
            colors =(Color[]) originalColors.toArray(colors);
        }
   }

    /*
     * (non-Javadoc)
     * @see fi.mmm.yhteinen.swing.core.YIControllerComponent#addViewListener(fi.mmm.yhteinen.swing.core.YController)
     */
    public void addViewListener(YController controller) {
        // no need for listeners, this is not editable component
    }
    public String[] getExtendedFields() {
        if (colorModelField == null)return new String[0];
        return new String[] {colorModelField};
    }
    
    /*
     * (non-Javadoc)
     * @see fi.mmm.yhteinen.swing.core.YIExtendedModelComponent#getModelValue(java.lang.String)
     */
    public Object getModelValue(String field) throws Exception {
        if (field.equals(colorModelField)) {
            return originalColors;
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * @see fi.mmm.yhteinen.swing.core.YIExtendedModelComponent#setModelValue(java.lang.String, java.lang.Object)
     */
    public void setModelValue(String field, Object value) throws Exception {
        if (field.equals(colorModelField)) {
            if (value instanceof List) {
                this.initColors((List)value);
            } else {
                throw new YException("Color model field for YPieChart must be List.");
            }
        }
    }
    
    /*
     * (non-Javadoc)
     * @see javax.swing.JComponent#paint(java.awt.Graphics)
     */
    public void paint(Graphics g) {
        drawPie((Graphics2D)g, getBounds(), sizes, colors);
    }
    
    
    private Color prevColor = Color.blue;
    
    /**
     * Draws pie.
     */
    private void drawPie(Graphics2D g, Rectangle area, double[] sizes, Color[] colors) {
        // Get total value of all slices
        double total = 0.0D;
        for (int i=0; i<sizes.length; i++) {
            total += sizes[i];
        }
    
        // Draw each pie slice
        double curValue = 0.0D;
        int startAngle = 0;
        for (int i=0; i<sizes.length; i++) {
            // Compute the start and stop angles
            // Add 90 to angles to rotate the slice 90 degrees counter-clockwise
            // This makes the first slice start at 12 o'clock position (instead of 3)
            startAngle = (int)(curValue * 360 / total) + 90;
            int arcAngle = (int)(sizes[i] * 360 / total) + 90;
    
            // Ensure that rounding errors do not leave a gap between the first and last slice
            if (i == sizes.length-1) {
                arcAngle = 360 - startAngle + 90;
            }
            Color currentColor;
            // if Color is not specified darkening previous Color....
            if (colors == null || colors.length < i) {
                currentColor = prevColor.darker();
            } else {
                currentColor = colors[i];
            }
            // Set the color and draw a filled arc
            
            g.setColor(currentColor);
            prevColor = currentColor;
            g.fillArc(area.x, area.y, area.width, area.height, startAngle, arcAngle);
    
            curValue += sizes[i];
        }
    }
    
    /**
     * Sets field name that holds Colors of pie chart (in YModel). Field
     * must contain List of Colors.
     * <p>
     * @param colorModelField   YModel field name that holds Color List
     */
    public void setColorModelField(String colorModelField) {
        this.colorModelField = colorModelField;
    }


    
}
