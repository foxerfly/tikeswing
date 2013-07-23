/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.worker;


/**
 * The default listener for YWorker. Uses 
 * YDefaultWorkerDialogController to show info dialog 
 * during YWorker command execution.
 * 
 * @author Tomi Tuomainen
 */
public class YDefaultWorkerListener extends YWorkerListener {
	
	private YDefaultWorkerDialogController controller;
	
	/**
	 * This method waits one second before showing YDefaultWorkerDialog.
	 * 
	 * @see fi.mmm.yhteinen.swing.core.worker.YWorkerListener#started()
	 */
	public void started() {
		// waiting 1 second before showing dialog:
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		controller = new YDefaultWorkerDialogController (this);
		controller.showDialog();
	}
	
	/* (non-Javadoc)
	 * @see fi.mmm.yhteinen.swing.core.worker.YWorkerListener#finished()
	 */
	public void finished() {
		if (controller != null) {
			controller.finished();
		}
	}
}
