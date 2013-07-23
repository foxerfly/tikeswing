/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.worker;

/**
 * The command for YWorker. Application should extends this class 
 * for providing YWorker a command to execute. Method execute()
 * should be overridden to execute something outside
 * Swing event dispatching thread. This means GUI handling 
 * should NOT be implemented directly in execute method!  However, 
 * you can use SwingUtilities to update GUI component 
 * (for example a progress bar) in the execute method. 
 * 
 * @see javax.swing.SwingUtilities#invokeAndWait(java.lang.Runnable)
 * @see YWorker
 * 
 * @author Tomi Tuomainen
 */
public abstract class YWorkerCommand {

	private YWorkerListener listener;
	
	/**
	 * This method executes some code outside event dispatching thread.
	 * 
	 * @return				some value may be returned by the command 
	 * @throws Exception	some exception during command execution
	 */
	public abstract Object execute() throws Exception;
	
	/**
	 * Returns listener of this command. This can be used to call listener
	 * inside execute method. However, it should be noted that an application
	 * can not update GUI normally during execute method! However, you can use
	 * SwingUtilities to update GUI (for example a progress bar)
	 * during execute method. 
	 * 
	 * @see javax.swing.SwingUtilities#invokeAndWait(java.lang.Runnable)
	 * @return the listener of this command
	 */
	public YWorkerListener getListener() {
		return listener;
	}
}
