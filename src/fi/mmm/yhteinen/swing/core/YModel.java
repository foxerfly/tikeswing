/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core;

import java.util.Observable;

/**
 * The model class of the framework. Extend this class to 
 * implement a model.
 * 
 * @author Tomi Tuomainen
 */
public class YModel extends Observable {

	public YModel() {
		super();
	}
	
	/**
	 * Notifies connectected controllers and models about model changes.
	 * All the fields of the model are considered to have changed.
	 */
	 public void notifyObservers() {
	 	setChanged();
	 	super.notifyObservers();
	 }
	 
	/**
	 * Notifies connectected controllers and models about model changes.
	 * If changedField is null, all the field are considered to have changed.
	 * 
	 * @param changedField	the changed field
	 */
	 public void notifyObservers(String changedField) {
	 	setChanged();
		super.notifyObservers(changedField);
	 }
	 
	 /**
	  * For framework internal use. Notifies views about model changes.
	  * 
	  * @param event the change event
	  */
	 void notifyObservers(YModelChangeEvent event) {
	 	setChanged();
		super.notifyObservers(event);
	 }

}
