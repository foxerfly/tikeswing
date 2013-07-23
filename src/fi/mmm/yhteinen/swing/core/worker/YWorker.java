/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.worker;

/**
 * This is a helper class for executing a method in a thread (outside Swing
 * event dispatching thread).  YWorker passes YWorkerCommand to 
 * SwingWorker for execution. YWorkerListener may be set for listening 
 * command execution. 
 * <p>
 * This class aims to simplify SwingWorker usage. This might be useful 
 * for some applications (for example when needed to show a default 
 * dialog every time a remote call is executed).
 *  
 * @author Tomi Tuomainen
 */
public class YWorker {

	private boolean finished = false;
	
	// exception raised during YWorkerCommand execution
	private Exception applicationException;
	
	// default listener for execution
	private static YWorkerListener defaultListener = new YDefaultWorkerListener();
	
	private SwingWorker swingWorker;
	
	
	/**
	 * Executes a command in a thread (outside Swing event-dispatching thread.)
	 * If listener blocks method execution with modal dialog 
	 * (see YDefaultWorkerListener), this method returns the value returned by
	 * the command and throws possible command exceptions. However, if listener
	 * doesn't block the execution of this method, null is returned (listener
	 * will be anyway notified when the execution is over).
	 * @param command		the command to execute
	 * @param listener		the listener for execution
	 * 
	 * @return				the object returned by command
	 * @throws YApplicationException the exception raised the 
	 * 								command during execution
	 */
	public Object execute(final YWorkerCommand command, final YWorkerListener listener) throws Exception {
		listener.setWorker(this);
		// worker for invoking command:
		swingWorker = new SwingWorker() {
			public Object construct() {
				try {
					return command.execute();
				} catch (Exception ex) {
					applicationException = ex;
					return null;
				}
			}
			
			public void finished() {
				finished = true;
				listener.finished();				
			}
			public void interrupt() {
				super.interrupt();
				finished = true;
				listener.finished();
			}
		};
		finished = false;
		listener.beforeStart();
		swingWorker.start();
		listener.started();
		// if listener (model dialog) blocked the execution during worker execution...
		if (finished) {
		// if exception occurred in worker execution:
			if (this.applicationException != null) {
				throw applicationException;
			} else {
//				 everything ok, returning worker value:
				return swingWorker.get();
			}
		}
		return null;
	}
	
	/**
	 * Executes a command in a thread (outside Swing event-dispatching thread.)
	 * Default listener is used 
	 * @param command		the command to execute
	 * 
     * @see #setDefaultListener(YWorkerListener)
     * @see #execute(YWorkerCommand, YWorkerListener)
     * 
	 * @return				the object returned by command
	 * @throws YApplicationException the exception raised the 
	 * 								command during execution	 
	 */
	public Object execute(YWorkerCommand command) throws Exception {
		return execute(command, defaultListener);
	}
	
	/**
	 * Executes a command in a thread (outside Swing event-dispatching thread.)
	 * Default listener is used 
	 * @param command		the command to execute
	 * @param infoString	the info String for YDefaultWorkerDialog
	 * 
     * @see #setDefaultListener(YWorkerListener)
     * @see #execute(YWorkerCommand, YWorkerListener)
     * 
	 * @return				the object returned by command
	 * @throws YApplicationException the exception raised the 
	 * 								command during execution	 
	 */
	public Object execute(YWorkerCommand command, String infoString) throws Exception {
		YDefaultWorkerDialog.setInfoString(infoString);
		return execute(command, defaultListener);
	}
	
	/**
	 * Interrupts command execution.
	 *
	 */
	public void interrupt() {
		this.swingWorker.interrupt();
	}
	
	/**
	 * @return the default listener for command execution
	 */
	public static YWorkerListener getDefaultListener() {
		return defaultListener;
	}
	/**
	 * Sets default listener for command execution. If not set, the default 
	 * listener is YDefaultWorkerListener.
	 * 
	 * @param defaultListener the default listener for command execution
	 */
	public static void setDefaultListener(YWorkerListener defaultListener) {
		YWorker.defaultListener = defaultListener;
	}
	
	/**
	 * @return the possible exception that has occurred during command
	 * 		   execution
	 */
	public Exception getApplicationException() {
		return applicationException;
	}
}
