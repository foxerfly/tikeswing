/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.component.table;

/**
 * Parameter object for YTable event handling.
 * 
 * @author Tomi Tuomainen
 */
public class YTableChangeData {

	private int column;
	private int row;
	private Object value;
	
	/**
	 * @param column 	the modified column
	 * @param row	 	the modified row
	 * @param value		the user changed value
	 */
	public YTableChangeData(int row, int column, Object value) {
		super();
		this.row = row;
		this.column = column;
		this.value = value;
	}


	public Object getChangeObject() {
		return value;
	}

	public int getColumn() {
		return column;
	}
	
	public void setColumn(int column) {
		this.column = column;
	}
	
	public int getRow() {
		return row;
	}
	
	public void setRow(int row) {
		this.row = row;
	}

	public Object getValue() {
		return value;
	}
	
	public void setValue(Object value) {
		this.value = value;
	}
}
