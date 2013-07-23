/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.component.table;

import java.util.Comparator;

import fi.mmm.yhteinen.swing.core.YComponent;
import fi.mmm.yhteinen.swing.core.YIComponent;

/**
 * The column meta data for the table. Defines column external attributes
 * and maps the column to table model POJO.
 * 
 * @author Tomi Tuomainen
 * @see YTable
 */
public class YColumn extends YComponent {

	private static final int DEFAULT_WIDTH = 100;
	private String title = "";
    private int preferredWidth = DEFAULT_WIDTH;
    private boolean editable = false;
    private YTableFormatter formatter = null;
    private Comparator comparator = null;
    private Class editorClass = null;
    
    /**
     * @param mvcName the name of the field on table model POJO
     */
    public YColumn(String mvcName) {
        super();
        this.getYProperty().put(YIComponent.MVC_NAME, mvcName);
    }
    
    /**
     * @param mvcName  the name of the field on table model POJO
     * @param title the name of the column showed to user
     */
    public YColumn(String mvcName, String title) {
        this(mvcName);
        this.title = title;
    }
    
    /**
     * @param mvcName  	the name of the field on table model POJO
     * @param title 	the name of the column showed to user
     * @param preferredWidth the preferred width of the column
     */
    public YColumn(String mvcName, String title, int preferredWidth) {
       this(mvcName, title);
        this.preferredWidth = preferredWidth;
    }
        
    /**
     * @param mvcName  the name of the field on table model POJO
     * @param title the name of the column showed to user
     * @param preferredWidth the preferred width of the column
     * @param editable      is the column editable
     */
    public YColumn(String mvcName, String title, int preferredWidth,
            boolean editable) {
        this(mvcName, title, preferredWidth);
        this.editable = editable;
    }
    
    /**
     * @param mvcName  the name of the field on table model POJO
     * @param title the name of the column showed to user
     * @param preferredWidth the preferred width of the column
     * @param editable      is the column editable
     * @param formatter     defines how the data in the column is showed to user
     */
    public YColumn(String mvcName, String title, int preferredWidth,
            boolean editable, YTableFormatter formatter) {
        this(mvcName, title, preferredWidth, editable);
        this.formatter = formatter;
    }
    
    /**
     * @param mvcName  the name of the field on table model POJO
     * @param title the name of the column showed to user
     * @param preferredWidth the preferred width of the column
     * @param editable      is the column editable
     * @param formatter     defines how the data in the column is showed to user
     * @param comparator	the comparator used for sorting the column
     */
    public YColumn(String mvcName, String title, int preferredWidth,
            boolean editable, YTableFormatter formatter, Comparator comparator) {
        this(mvcName, title, preferredWidth, editable);
        this.formatter = formatter;
        this.comparator = comparator;
    }
    
    /**
     * @param mvcName  the name of the field on table model POJO
     * @param title the name of the column showed to user
     * @param preferredWidth the preferred width of the column
     * @param editable      is the column editable
     * @param formatter     defines how the data in the column is showed to user
     * @param comparator	the comparator used for sorting the column
     * @param editorClass	the class that specifies which editor is instantiated
     */
    public YColumn(String mvcName, String title, int preferredWidth,
            boolean editable, YTableFormatter formatter, Comparator comparator,
			Class editorClass) {
        this(mvcName, title, preferredWidth, editable);
        this.formatter = formatter;
        this.comparator = comparator;
        this.editorClass = editorClass;
    }
    
    
    /**
     * @param mvcName  the name of the field on table model POJO
     * @param title the name of the column showed to user
     * @param preferredWidth the preferred width of the column
     * @param editable      is the column editable
     * @param editorClass	the class that specifies which editor is instantiated
     */
    public YColumn(String mvcName, String title, int preferredWidth,
            boolean editable, Class editorClass) {
        this(mvcName, title, preferredWidth, editable);
        this.editorClass = editorClass;
    }
    
    /**
     * @return the name of the column showed to user
     */
    public String getTitle() {
        return title;
    }
    /**
     * @param title the name of the column showed to user
     */
    public void setTitle(String title) {
        this.title = title;
    }
    /**
     * @return is this column editable
     */
    public boolean isEditable() {
        return editable;
    }
    /**
     * @param editable is this column editable
     */
    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    /**
     * @return the preferred width of the column
     */
    public int getPreferredWidth() {
        return preferredWidth;
    }
    /**
     * @param preferredWidth the preferred width of the column
     */
    public void setPreferredWidth(int preferredWidth) {
        this.preferredWidth = preferredWidth;
    }

    /**
     * @return defines how the data in the column is showed to user
     */
    public YTableFormatter getFormatter() {
        return formatter;
    }
    /**
     * @param formatter defines how the data in the column is showed to user
     */
    public void setFormatter(YTableFormatter formatter) {
        this.formatter = formatter;
    }
	/**
	 * @return the comparator used for sorting the column
	 */
	public Comparator getComparator() {
		return comparator;
	}
	/**
	 * Comparable can be set for table column for customised sorting.
	 * This overrides default sorting with Comparable table contents. 
	 * 
	 * @param comparator the comparator used for sorting table
	 */
	public void setComparator(Comparator comparator) {
		this.comparator = comparator;
	}
	/**
	 * @return the class that specifies which editor is instantiated
	 */
	public Class getEditorClass() {
		return editorClass;
	}
	
	/**
	 * @param editorClass the class that specifies which editor is instantiated
	 */
	public void setEditorClass(Class editorClass) {
		this.editorClass = editorClass;
	}
}
