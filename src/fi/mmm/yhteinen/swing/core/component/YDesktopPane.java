/*
 * Created on Mar 5, 2007
 *
 */
package fi.mmm.yhteinen.swing.core.component;

import javax.swing.JDesktopPane;

import fi.mmm.yhteinen.swing.core.YIComponent;
import fi.mmm.yhteinen.swing.core.tools.YProperty;

/**
 * The desktop pane. No extended TikeSwing functionality so far.
 * 
 * @author Tomi Tuomainen
 */
public class YDesktopPane extends JDesktopPane {

    private YProperty myProperty = new YProperty();
    
    /*
     *  (non-Javadoc)
     * @see fi.mmm.yhteinen.swing.core.YIComponent#getYProperty()
     */
    public YProperty getYProperty() {
        return myProperty;
    }

    public YDesktopPane() {
        super();
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
