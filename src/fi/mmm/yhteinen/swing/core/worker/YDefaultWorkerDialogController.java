/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.worker;

import fi.mmm.yhteinen.swing.core.YController;
import fi.mmm.yhteinen.swing.core.component.YDialog;
import fi.mmm.yhteinen.swing.core.tools.YUIToolkit;

/**
 * A controller for YDefaultWorkerDialog. 
 * 
 * @author Tomi Tuomainen
 */
public class YDefaultWorkerDialogController extends YController {

	private YWorkerListener listener;
	
	private YDefaultWorkerDialog view = (YDefaultWorkerDialog)
		YDialog.createDialog(YDefaultWorkerDialog.class);
	
	/**
	 * @param listener the worker listener
	 */
	public YDefaultWorkerDialogController(YWorkerListener listener) {
		this.listener = listener;
		YUIToolkit.setUpMVC(null, view, this);		
	}
	
	/**
	 * Pops ups the worker dialog.
	 */
	public void showDialog() {
		view.setVisible(true);	
	}
	
	/**
	 * This is called when user presses Cancel.
	 * Calls interrupt method of the worker listener.
	 */
	public void buttonCancelPressed() {
		// interrupting YWorker command execution:
		listener.interrupt();	
	}
	
	/**
	 * This is called when user closes the dialog.
	 * Calls interrupt method of the worker listener.
	 */
	public void dialogClosing() {
		// interrupting YWorker command execution:
		listener.interrupt();
	}
	
	/**
	 * The listener calls this method when the execution has ended.
	 * Closes the dialog.
	 */
	public void finished() {
		if(view != null){
			view.dispose();
		}
		view = null;
	}

}
