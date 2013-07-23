/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core;

/**
 * Event class for messaging between controllers.
 * 
 * @see YController#handleApplicationEvent(YApplicationEvent)
 * @see YController#sendApplicationEvent(YApplicationEvent)
 *
 * @author Tomi Tuomainen
 */
public class YApplicationEvent {

	private Object source;
	private String name;
	private Object value;
	
	
	/**
	 * @param source the source of the event
	 * @param name	the name for this event
	 * @param value the value for this event
	 */
	public YApplicationEvent(String name, Object value, Object source) {
		super();
		this.source = source;
		this.name = name;
		this.value = value;
	}
	
	/**
	 * @param name	the name for this event
	 */
	public YApplicationEvent(String name) {
		this(name, null, null);
	}
	
	/**
	 * @param name	the name for this event
	 * @param value	the value for this event
	 */
	public YApplicationEvent(String name, Object value) {
		this (name, value, null);
	}
	
	/**
	 * @return the event name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the event name 
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the event value
	 */
	public Object getValue() {
		return value;
	}
	/**
	 * @param value the event value
	 */
	public void setValue(Object value) {
		this.value = value;
	}
	/**
	 * @return the source of the event
	 */
	public Object getSource() {
		return source;
	}
	/**
	 * @param source the source of the event
	 */
	public void setSource(Object source) {
		this.source = source;
	}
}
