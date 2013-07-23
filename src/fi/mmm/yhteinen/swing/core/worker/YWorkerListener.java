/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.worker;

/**
 * A listener for YWorker. This class can be extended for listening YWorker
 * execution. 
 * 
 * @see YWorker
 * @author Tomi Tuomainen
 */
public abstract class YWorkerListener {
	
	private YWorker worker;
	
	/**
	 * This method is called when YWorker command execution has just started.
	 */
	public abstract void started();
	
	/**
	 * This method is called when YWorker command execution has just finished.
	 */
	public abstract void finished();
	
	/**
	 * This method is called just before YWorker command execution is started.
	 * The default implementation is empty. 
	 */
	public void beforeStart() {	}
	
	
	/**
	 * This method is for framework internal usage.
	 * @param worker the worker which notifies this listener
	 */
	void setWorker(YWorker worker) {
		this.worker = worker;
	}
	
	/**
	 * Interrupts worker command execution.
	 */
	public void interrupt() {
		worker.interrupt();
	}
	
	/**
	 * @return	the exception raised by worker command, null if no exception has occurred
	 */
	public Exception getApplicationException() {
		return worker.getApplicationException();
	}
	
	
}
