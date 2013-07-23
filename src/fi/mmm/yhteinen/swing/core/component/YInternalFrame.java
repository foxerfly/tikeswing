package fi.mmm.yhteinen.swing.core.component;

import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import fi.mmm.yhteinen.swing.core.YController;
import fi.mmm.yhteinen.swing.core.YIControllerComponent;
import fi.mmm.yhteinen.swing.core.savechanges.YSaveChangesHandler;
import fi.mmm.yhteinen.swing.core.tools.YProperty;
import fi.mmm.yhteinen.swing.core.tools.YUIToolkit;

/**
 * 
 * The internal frame. 
 * <p>
 * Controller event methods, where yyy is component's name:
 * 
 * <code>public void yyyOpened()</code> executed when the frame is opened
 * <code>public void yyyClosing()</code> executed when the frame is closing
 * <p>
 * Component checks unsaved changes before invoking yyyClosing event,
 * if component property YIComponent.CHECK_CHANGES has
 * Boolean value true.
 * <p>
 * Default close operation is JFrame.DO_NOTHING_ON_CLOSE which
 * means closing should be implemented in closing event method.
 * 
 * @author Tomi Tuomainen
 */
public class YInternalFrame extends JInternalFrame implements YIControllerComponent {

  private YProperty myProperty = new YProperty();

  //----------------------------------------------------------------------------

  /**
   *
   */
  public YInternalFrame() {
    super();
    this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
  }

  //----------------------------------------------------------------------------

  /**
   *
   * @param title String
   */
  public YInternalFrame(String title) {
    super(title);
    this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
  }

  //----------------------------------------------------------------------------

  /**
   *
   * @return YProperty
   */
  public YProperty getYProperty() {
    return myProperty;
  }

  //----------------------------------------------------------------------------

  /**
   *
   * @param controller YController
   */
  public void addViewListener(final YController controller) {

    this.addInternalFrameListener(new InternalFrameListener() {

      public void internalFrameOpened(InternalFrameEvent e) {
        String methodName = YUIToolkit.createMVCMethodName(YInternalFrame.this, "Opened");
        if (methodName != null) {
          controller.invokeMethodIfFound(methodName);
        }

      }

      public void internalFrameClosing(InternalFrameEvent e) {
        // checking unsaved changes before closing...
        if (YSaveChangesHandler.changesSaved(YInternalFrame.this)) {
          String methodName = YUIToolkit.createMVCMethodName(YInternalFrame.this, "Closing");
          if (methodName != null) {
            controller.invokeMethodIfFound(methodName);
          }
        }
        
      }

      public void internalFrameClosed(InternalFrameEvent e) {}

      public void internalFrameIconified(InternalFrameEvent e) {}

      public void internalFrameDeiconified(InternalFrameEvent e) {}

      public void internalFrameActivated(InternalFrameEvent e) {
      }

      public void internalFrameDeactivated(InternalFrameEvent e) {}
      }
    
    );
  }

}
