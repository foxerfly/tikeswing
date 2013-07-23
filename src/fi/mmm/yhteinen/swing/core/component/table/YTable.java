
/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.component.table;


import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;

import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import javax.swing.ListSelectionModel;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.SerializationException;
import org.apache.commons.lang.SerializationUtils;
import org.apache.log4j.Logger;

import fi.mmm.yhteinen.swing.core.YController;
import fi.mmm.yhteinen.swing.core.YIComponent;
import fi.mmm.yhteinen.swing.core.YISharedModelComponent;
import fi.mmm.yhteinen.swing.core.YIValidatorComponent;

import fi.mmm.yhteinen.swing.core.error.YCloneModelException;
import fi.mmm.yhteinen.swing.core.error.YEqualsModelException;
import fi.mmm.yhteinen.swing.core.error.YException;
import fi.mmm.yhteinen.swing.core.error.YModelGetValueException;
import fi.mmm.yhteinen.swing.core.error.YModelSetValueException;

import fi.mmm.yhteinen.swing.core.savechanges.YSaveChangesHandler;
import fi.mmm.yhteinen.swing.core.tools.YCoreToolkit;
import fi.mmm.yhteinen.swing.core.tools.YProperty;
import fi.mmm.yhteinen.swing.core.tools.YUIToolkit;

/**
 * The table.
 * <p>
 * Component's internal model (table model) should be set with the 
 * setModelValue method. Table model is a collection if POJOs.  Column
 * attributes should set in a constructor as YColumn objects. YColumn
 * defines which POJO field is mapped to which column.
 * <p>
 * When adding/deleting rows use addRow/removeRow methods. 
 * Alternatively you can modify table model directly in view model
 * class, but remember to notify table model! 
 * (<code>getModel().fireTableDataChanged()</code>)
 * <p>
 * YFormatter can be set to specify how a table model item is showed to user. 
 * The formatter setting for a column overrides table renderer set for
 * specified classes (for example via setDefaultEditorAndRenderer-method).
 * <p>
 * Table has column sorter which uses YColumn comparator if set. If comparator
 * is not specified, sorter uses Comparable table objects. If table data objects
 * are not instances of Comparable, sorting is made based on String presentation.
 * <p>
 * Controller event methods, where yyy is component's name:
 * <br>
 * <code>public void yyySelectionChanged(Object)</code> 
 * executed when a selection in the table is changed, parameter object is current row selection
 * <br>
 * <code>public void yyyChanged(YTableChangeData)()</code> 
 * executed when a user has edited table 
 * <br>
 * <code>public void yyyDoubleClicked(Object)</code> 
 * executed when user has double clicked table cell, parameter object is current row selection
 * <br>
 * <code>public void yyyRowInsert()</code>
 * executed when a new row should be created in table model
 * @see #setAutoInsert(boolean)
 * <p>
 * Component checks unsaved changes before selecting a new row in 
 * the table, if component property YIComponent.CHECK_CHANGES has 
 * Boolean value true. If your application doesn't "see" data changes 
 * data in the table, or cancelling table changes doesn't work, 
 * you probably should override methods equalsModel and cloneModel 
 * to get them work with your table Collection.
 * <p>
 *  
 * @see YColumn
 * @author Tomi Tuomainen
 */
public class YTable extends JTable implements YISharedModelComponent, 
    YIValidatorComponent {
    
    private boolean cellTooltips = false;
    
    private int lastEditedRow = -1;
    private int lastEditedColumn = -1;
    
    public static final ArrayList EMPTY_MODEL = new ArrayList(0);
    private static Logger logger = Logger.getLogger(YTable.class);
    
    private YController controller;    
    
    private YTableModel tableModel = new YTableModel();
    private YTableSorter sorter = new YTableSorter(this, tableModel);
    
    private YColumn[] columns;
    
    /** Sorting is not possible in this table */
    public static final int SORTING_DISABLED = 0;
    
    /** Sorting is done lexically. */
    public static final int SORTING_LEXICAL = 1;

    /**
     * Sorting is based on Comparable table objects. Comparator
     * in YColumn is used; if not set default Comparator
     * in YTableSorter is used. 
     */
    public static final int SORTING_COMPARABLE = 2;
        
    private boolean ignoreSelection = false;
    private boolean editable = true; 
    
    // table data in view model class:
    private Collection modelCollection = null;
    
    // the "real" data of table model:s
    private ArrayList data = EMPTY_MODEL;
    
    private HashMap editors = new HashMap(); // editors specified for individual cells
    private HashMap renderers = new HashMap(); // renderers specified for individual cells
    private HashMap editableCells = new HashMap(); // editable states specified for individual cells

    private HashMap foregroundColors = new HashMap(); // cell colors specified for individual cells
    private HashMap selectedForegroundColors = new HashMap(); // cell colors specified for individual cells
    private HashMap backgroundColors = new HashMap(); // cell colors specified for individual cells
    private HashMap selectedBackgroundColors = new HashMap(); // cell colors specified for individual cells

    private boolean sortable = true;
    private Comparator sortingComparator = null;
    
    private boolean autoInsert = false;
    private boolean autoInsertCancelWhenNotModified = true;
    
    // this is for internal use
    private boolean autoInsertCancel = false;
    
    private int currentRowIndex = -1;
    private int currentColumnIndex = -1;
    private int prevRowIndex = -1;
    private int prevColumnIndex = -1;
    
    private boolean startEditingOnSelection = true;
    private boolean skipNotEditableCells = false;
    
    private KeyEvent lastKeyPressedEvent = null;
    
    private YTableCellRenderer DEFAULT_RENDERER = new YTableCellRenderer(null);
    
    // this is true by default so that coloring stuff will work:
    private boolean alwaysUseYTableCellRenderer = true;
    


    /**
     * Creates a new table. If this constructor is used
     * table columns must be specified by calling setColumns method.
     */
    public YTable () {
        super();
        this.setModel(sorter); 
        sorter.setTableHeader(getTableHeader());
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.setSurrendersFocusOnKeystroke(true);
       // initializing default colors... 
        this.setBackgroundColor(-1, -1, this.getBackground(), this.getSelectionBackground());
        this.setForegroundColor(-1, -1, this.getForeground(), this.getSelectionForeground());
    }
    
 
    /**
     * Creates a new table with specified columns.
     * 
     * @param columns the column meta data.
     */
    public YTable (YColumn[] columns) {
        this();
        this.setColumns(columns);
    }
            
    private YProperty myProperty = new YProperty();
    
    /*
     *  (non-Javadoc)
     * @see fi.mmm.yhteinen.swing.core.YIComponent#getYProperty()
     */
    public YProperty getYProperty() {
        return myProperty;
    }
    /**
     * 
     *  Gets value of this field for view model.
     * 
     * @return the collection which is the table model
     */
    public Object getModelValue() {
        return modelCollection;
    }
    
    /**
     * Sets model value to this component.
     * 
     * @param obj the collection which is used as table model
     */
    public void setModelValue(Object obj) {
       
            if (autoInsertCancel) {
                this.autoInsertCancel = false;
            }
            // closing the editor if for some reason it is still active:
            if (this.getCellEditor() != null) {
                this.getCellEditor().stopCellEditing();
            }
            if (obj == null) {
                data = EMPTY_MODEL;
            } else {
                if (!(obj instanceof Collection)) {
                    throw new YException(
                            "Table model must be instance of Collection.");
                } else {
                    this.modelCollection = (Collection) obj;
                    // creating the real table model...
                    this.data = new ArrayList(modelCollection.size());
                    Iterator it = modelCollection.iterator();
                    while (it.hasNext()) {
                        Object rowObj = it.next();
                        if (isRendered(rowObj)) {
                            data.add(rowObj);
                        }
                    }
                }
            }
            tableModel.fireTableDataChanged();
       
    }
    
    /**
     * This method should be overridden for specifying which row objects are showed in the table.
     * The default implementation returns true, so all objects in table model are visible.
     * 
     * @param rowObject the object to be rendered
     * @return          true, if given object will be visible; otherwise false
     */
    public boolean isRendered(Object rowObject) {
        return true;
    }
    
    /**
     * Returns POJO (in a table model) in specified row. 
     * 
     * @param row the row
     * @return the row object
     */
    public Object getObject(int row) {
        return data.get(sorter.modelIndex(row));
    }
    
    /**
     * @return  POJO int the selected row, null if row is not selected
     */
    public Object getSelectedObject() {
        if (getSelectedRow() < 0) {
            return null;
        } else {
            return getObject(getSelectedRow());
        } 
    }

    /**
     * Adds controller listeners. 
     * 
     * @param controller            the controller of this table
     * @param masterDetailTable     masterdetailtable to be notified (may be null)
     */
    void addViewListener(final YController controller, final YMasterDetailTable masterDetailTable) {
        this.controller = controller; 

        this.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting())  { // && !sorter.isSortingHappening()) {
                    if (!ignoreSelection) {
                        if (YSaveChangesHandler.changesSavedInCurrentWindow(YTable.this)) {
                            String methodName = YUIToolkit.createMVCMethodName(YTable.this, "SelectionChanged");
                            if (methodName != null) {
                                Object param[] = {YTable.this.getSelectedObject()};
                                Class paramClass[] = {Object.class};
                                controller.invokeMethodIfFound(methodName, param, paramClass);
                                if (masterDetailTable != null) {
                                    masterDetailTable.tableSelectionChanged(YTable.this);
                                }
                            }
                        } else {
                            // returning the old selection...
                            ignoreSelection = true;
                            setSelectedRow(prevRowIndex);
                            ignoreSelection = false;
                        }
                    }   
                }
            }
        });
        this.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { 
                if (e.getClickCount() == 2) {
                    String methodName = YUIToolkit.createMVCMethodName (
                            YTable.this, "DoubleClicked");
                    if (methodName != null) {
                        Object param[] = {YTable.this.getSelectedObject()};
                        Class paramClass[] = {Object.class};
                        controller.invokeMethodIfFound(methodName, param, paramClass);
                    }
                }
            }
        });
    }
    
    /* (non-Javadoc)
     * @see fi.mmm.yhteinen.swing.component.YIComponent#addViewListener(fi.mmm.yhteinen.swing.YController, fi.mmm.yhteinen.swing.YModel)
     */
    public void addViewListener(final YController controller ) {
        addViewListener(controller, null);
    }
    
    /**
     * @return the columns meta data
     */
    public YColumn[] getColumns() {
        return columns;
    }
    
    /**
     * @param columns the column meta data 
     */
    public void setColumns(YColumn[] columns) {
        this.columns = columns;
        DefaultTableColumnModel columnModel = new DefaultTableColumnModel();
        this.setColumnModel(columnModel);
        for (int i=0; i < columns.length; i++) {
            YColumn column = columns[i];
            TableColumn tableColumn = new TableColumn(i);
            tableColumn.setHeaderValue(column.getTitle());
            tableColumn.setPreferredWidth(column.getPreferredWidth());
            // if column has formatter, setting a new renderer, note that this overrides other renderers!
            if (column.getFormatter() != null) {
                tableColumn.setCellRenderer(
                        new YTableCellRenderer(column.getFormatter()));
            } 
            this.addColumn(tableColumn);
        }
    }
   
    /**
     * Returns if this table could be editable (if columns are editable).
     * @return if this table is editable
     */
    public boolean isEditable() {
        return editable;
    }
    
    /**
     * Sets this table editable. If not editable, none of the cells
     * will be editable. 
     *  
     * @param editable 
     * @see #setEditable(int, int, boolean)
     */
    public void setEditable(boolean editable) {
        this.editable = editable;
    }
    
    /**
     * Sets specified cell or cells editable. If given row is -1, the setting
     * affects the whole column. If given column is -1, the setting affects
     * the whole row. 
     * 
     * If editable state set via this method, it overrides the editable state
     * set to YColumn. However, the whole table must be set editable 
     * (via setEditable(boolen) method) before any cell is editable.
     * 
     * @param row       the row to be editable
     * @param column    the column to be editable
     * @param editable  if the given row/column is editable
     */
    public void setEditable(int row, int column, boolean editable) {
        String key = getKey(row, column);
        editableCells.put(key, new Boolean(editable));
    }
    
    /**
     * Sets foreground cell color. If given row is -1, the setting affects the whole column.
     * If given column is -1, the setting affects the whole row.
     * 
     * Note that you must use YTableCellRenderer in cells to make YTable coloring work.
     * 
     * @param row       the table row 
     * @param column    the table column
     * @param defaultColor  the color for the row/column
     * @param selectedColor the color for the row/column when a cell is selected 
     *                      (if null, defaultColor is used)
     */
    public void setForegroundColor(int row, int column, Color defaultColor, Color selectedColor) {
        String key = getKey(row, column);
        foregroundColors.put(key, defaultColor);
        if (selectedColor != null) {
            selectedForegroundColors.put(key, selectedColor);
        } else {
            selectedForegroundColors.put(key, defaultColor);
        }
    }
    
    /**
     * Sets background cell color. If given row is -1, the setting affects the whole column.
     * If given column is -1, the setting affects the whole row.
     * 
     * Note that you must use YTableCellRenderer in cells to make YTable coloring work.
     * 
     * @param row       the table row 
     * @param column    the table column
     * @param defaultColor  the color for the row/column
     * @param selectedColor the color for the row/column when a cell is selected 
     *                      (if null, defaultColor is used)
     */
    public void setBackgroundColor(int row, int column, Color defaultColor, Color selectedColor) {
        String key = getKey(row, column);
        backgroundColors.put(key, defaultColor);
        if (selectedColor != null) {
            selectedBackgroundColors.put(key, selectedColor);
        } else {
            selectedBackgroundColors.put(key, defaultColor);
        }
    }
    
    /**
     * Helper method for HashMaps used in the table implementation.
     * If method doesn't find value for given row and column,
     * it will try to find it with just given column. If value still is
     * not found, the method will try to find it with just given row.
     * 
     * @param row (that holds value)
     * @param col (that holds value)
     * @param map the hash map that has row/column specific values
     * @return value (if not found null is returned)
     */
    private Object getValue(int row, int col, HashMap map) {
        Object obj = map.get(getKey(row, col));
        if (obj == null) {
            obj = map.get(getKey(-1, col));
            if (obj == null) {
                obj = map.get(getKey(row, -1));
            }
            if (obj == null) {
                obj = map.get(getKey(-1, -1));
            }
        }
        return obj;
    }
    /**
     * Returns foreground cell color. 
     * 
     * @param row       the table row
     * @param column    the table column
     * @param isSelected    if cell is currently selected
     * @return the color of given cell
     */
    public Color getForegroundColor(int row, int column, boolean isSelected) {
        if (!isSelected) {
            return (Color) getValue(row, column, foregroundColors);
        } else {
            return (Color) getValue(row, column, selectedForegroundColors);
        }
    }
    
    /**
     * Returns background cell color. 
     * 
     * @param row       the table row
     * @param column    the table column
     * @param isSelected    if cell is currently selected
     * @return the color of given cell
     */
    public Color getBackgroundColor(int row, int column, boolean isSelected) {
        if (!isSelected) {
            return (Color) getValue(row, column, backgroundColors);
        } else {
            return (Color) getValue(row, column, selectedBackgroundColors);
        }
    }

    

    /**
     * This method sets table colors according to settings of setForegroundColor 
     * and setBackgroundColor. This method should be called from YTable renderer 
     * and editor classes (getTableCellRendererComponent and getTableCellEditorComponent methods)
     * to make sure that YTable color settings work. 
     * 
     * This method assumes that the component has focus (call from editor).
     * 
     * This method may be overrridden to customize color settings. 
     * 
     * @param row           the table row 
     * @param column        the table column
     * @param comp          the editor or renderer component
     * @param isSelected    if the comp is currently selected by user
     * @param hasFocus      if the component (cell) is currently focused
     */
    public void setColors(int row, int column, Component comp, boolean isSelected, boolean hasFocus) {
        Color foregroundColor = getForegroundColor(row, column, isSelected);
        if (foregroundColor != null) {
            comp.setForeground(foregroundColor);
        }
        Color backgroundColor = getBackgroundColor(row, column, isSelected);
        if (backgroundColor != null) {
            comp.setBackground(backgroundColor);
        }
    }
   
   
    
   /*
    *  (non-Javadoc)
    * @see javax.swing.JTable#isCellEditable(int, int)
    */
    public boolean isCellEditable(int row, int col) {
        // the table must be editable before any cell is editable...
        if (!this.isEditable()) {
            return false;
        }
        col = this.convertColumnIndexToModel(col);
        // checking editable state for specific cell from editableCells
        Boolean editable = (Boolean) this.getValue(row, col, editableCells);
        if (editable == null) {
            // no editable state specified in editableCells, using YColumn editable...
            return columns[col].isEditable();
        } else {
                return editable.booleanValue();     
        }
    }
    
    /**
     * Clears editable cell settings of this table. This method will clear settings 
     * that have been set by calling method setEditable(int, int, boolean). 
     * Editable setting for the whole table or YColumn is not cleared. 
     */
    public void clearEditableCells() {
        this.editableCells = new HashMap();
    }
    
    /**
     * Adds a new row in this table
     * 
     * @param obj the object (POJO) to be added to table model
     * @param setSelected if the new row should be set selected
     */
    public void addRow(Object obj, boolean setSelected) {
        addRow(obj, setSelected, -1);
    }
    
    /**
     * Adds a new row in this table
     * 
     * @param obj         the object (POJO) to be added to table model
     * @param setSelected if the new row should be set selected
     * @param row         the specified position where the new row is inserted
     */
    public void addRow(Object obj, boolean setSelected, int row) {
        if (row < 0) {
            this.data.add(obj);
            this.modelCollection.add(obj);
            row = data.size()-1;
        } else {
            this.data.add(row, obj);
            if (modelCollection instanceof List) {
                ((List) modelCollection).add(row, obj);
            } else {
                // we cannot add row object into specific location...
                modelCollection.add(obj);
            }
        }
        tableModel.fireTableRowsInserted(row, row);
        if (setSelected) {
            this.setSelectedRow(row);
        }
    }
    
    /**
     * Removes a row from the table.
     * 
     * @param row the row index
     * @return the removed object
     */
    public Object removeRow(int row) {
        Object obj = this.data.remove(row);
        modelCollection.remove(obj);
        tableModel.fireTableRowsDeleted(row, row);
        return obj;
    }
    
    /**
     * Removes a row from the table.
     * 
     * @param obj   the row object (POJO in table model) to be removed
     * @return true if row was found and removed
     */
    public boolean removeRow(Object obj) {
        if (this.data.remove(obj)) {
            this.modelCollection.remove(obj);
            tableModel.fireTableDataChanged();
            return true;
        }
        return false;
    }
    
    /**
     * Sets selected row.
     * 
     * @param row     the row to be selected
     */
    public void setSelectedRow(int row) {
        if (row < 0) {
            clearSelection();
        } else {
            setRowSelectionInterval(row, row);
            this.ensureRowIsVisible(row);
        }
    }
    
    /**
     * Sets selected row. It is assumed that row POJOs
     * implement <code>equals</code> so that the row can be 
     * identified by given object.
     * 
     * @param obj   the object to be selected
     * @return      true, if row was identified and selected
     */
    public boolean setSelectedRow(Object obj) {
        for (int i=0; i < this.getRowCount(); i++) {
            Object rowObject = this.getObject(i);
            if (rowObject.equals(obj)) {
                this.setSelectedRow(i);
                return true;
            }
        }
        return false;
    }
    
   /**
     * Clones table model. The default implementation uses 
     * serialization in cloning, assuming that all objects
     * in the table model are serializable. If this method
     * throws an exception, override the method and implement 
     * customised copying of your table model Collection.
     * 
     * @see YISharedModelComponent#cloneModel()
     */
    public Object cloneModel() {
        try {
            Object clone = SerializationUtils.clone((Serializable) modelCollection);
            return clone;
        } catch (SerializationException ex) {
            throw new YCloneModelException(
                    "YTable collection " + modelCollection + " " +
                    " is not Serializable, so cloneModel() cannot be executed." +
                    " Please implement the Serializable for all Collection objects or override cloneModel in YTable.");
    
        }
    }
    
   /**
     * Checks if given model equals table model. The default
     * implementation uses reflection, which will access all the
     * fields, also private. It is assumed that SecurityManager 
     * allows this access.  
     * 
     * If this method throws an exception, override the method 
     * and implement customised equals comparison for your
     * table model Collection.
     * 
     * @see YCoreToolkit#equalsCollection(Collection, Collection)
     * @see YISharedModelComponent#equalsModel(java.lang.Object)
     */
    public boolean equalsModel(Object model) {
        try {
            if (!editable) {
                // if this table is not editable, table model cannot have changed...
                // (returning directly true for performance reasons)
                return true;                
            }
            boolean equals = YCoreToolkit.equalsCollection(modelCollection, (Collection) model);
            return equals;
        } catch (Exception ex) {
            throw new YEqualsModelException(
                    "YTable collection " + modelCollection 
                    + " equals comparison couldn't be done because of SecurityManager settings."
                    + " Allow the access to private fields or override equalsModel in YTable.");
        }
    }

    /**
     * Adds property values for table columns with given property name.
     * The parameter propertyValue holds values for column.
     * 
     * @param propertyName  the name of the property to set
     * @param propertyValue the array of property values for each tab
     */
    public void addColumnYProperties(String propertyName, Object[] propertyValue) {
        if (propertyValue.length != columns.length) {
            throw new YException("The length of property value array is not the same as column count.");
        } else {
            for (int i=0; i < propertyValue.length; i++) {
                addColumnYProperty(i, propertyName, propertyValue[i]);
            }
        }
    }
    
    /**
     * Adds property value for table column with given property name.
     * 
     * @param propertyName  the name of the property to set
     * @param propertyValue the value of the property values to set
     */
    public void addColumnYProperty(int columnIndex, String propertyName, Object propertyValue) {
        YColumn col = columns[columnIndex];
        col.getYProperty().put(propertyName, propertyValue);
    }
    
    /**
     * Gets YProperty for given column. 
     * 
     * @param columnIndex the tab index
     * @return          the YProperty of the tab
     */
    public YProperty getColumnYProperty(int columnIndex) {
        YColumn col = columns[columnIndex];
        return col.getYProperty();
    }

    
    /**
     * Calls rowInsert method of YController.
     */
    private void invokeRowInsert() {
        String methodName = YUIToolkit.createMVCMethodName (
                YTable.this, "RowInsert");
        if (methodName != null) {
            controller.invokeMethodIfFound(methodName);
            autoInsertCancel = true;
        }
    }
   
 

   /**
    * This method is overridden in YTable to to catch last key pressed event.
    * 
    * @see javax.swing.JTable#processKeyBinding(javax.swing.KeyStroke, java.awt.event.KeyEvent, int, boolean)
    */
   protected boolean processKeyBinding(KeyStroke ks, KeyEvent e,
           int condition, boolean pressed) {
       if (pressed) {
           lastKeyPressedEvent = e;
       }
       return super.processKeyBinding(ks, e, condition, pressed);
   }
   
   protected void processKeyEvent(KeyEvent e) {
       super.processKeyEvent(e);
   }
    
   /**
    * Transfers focus to next cell below the selected cell.
    *
    */
   public void setNextCellSelectedBelow() {
       int nextColumn = currentColumnIndex;
       int nextRow = currentRowIndex+1;
       if (nextRow > (this.getRowCount()-1)) {
           nextRow = 0; // lastrow -> moving to first
       }
       changeSelection(nextRow, nextColumn, false, false);
   }
   
   /**
    * Transfers focus to next cell above the selected cell.
    *
    */
   public void setNextCellSelectedAbove() {
       int nextColumn = currentColumnIndex;
       int nextRow = currentRowIndex-1;
       if (nextRow < 0) {
           nextRow = this.getRowCount()-1; // lastrow -> moving to first
       }
       changeSelection(nextRow, nextColumn, false, false);       
   }
   
   /**
    * Transfers focus to next cell (on the right from the selected cell)
    *
    */
   public void setNextCellSelected() {
       int nextColumn = -1;
       int nextRow = -1;
       if (currentColumnIndex == this.getColumnCount()-1) { 
           nextColumn = 0; // the last column -> moving to first
           if (currentRowIndex == this.getRowCount() -1) { 
               nextRow = 0; // the last row -> moving to first
           } else {
               nextRow = currentRowIndex+1;
           }
       } else {
           nextColumn = currentColumnIndex+1;
           nextRow = currentRowIndex;
       }
       changeSelection(nextRow, nextColumn, false, false);       
   }
   
   /**
    * Transfers focus to previous cell (on the left from the selected cell).
    *
    */
   public void setPreviousCellSelected() {
       int nextColumn = -1;
       int nextRow = -1;
       if (currentColumnIndex == 0) { 
           nextColumn = this.getColumnCount()-1; // the first column -> moving to last
           if (currentRowIndex == 0) {
               nextRow = this.getRowCount()-1; // the first row -> moving to last
           } else {
               nextRow = currentRowIndex-1;
           }
       } else {
           nextColumn = currentColumnIndex-1;
           nextRow = currentRowIndex;
       }
       changeSelection(nextRow, nextColumn, false, false);
   }
    
   /**
    * Checks what cell is next in the table and setting focus. 
    * 
    */
   private void moveToNextCell() {
       if (lastKeyPressedEvent != null) {
           if (lastKeyPressedEvent.getKeyCode() == KeyEvent.VK_DOWN) {
               setNextCellSelectedBelow();
           } else if (lastKeyPressedEvent.getKeyCode() == KeyEvent.VK_UP) {
               setNextCellSelectedAbove();
           } else if (lastKeyPressedEvent.getKeyCode() == KeyEvent.VK_TAB 
                   && !lastKeyPressedEvent.isShiftDown()) {
               setNextCellSelected();
           } else if (lastKeyPressedEvent.getKeyCode() == KeyEvent.VK_TAB && lastKeyPressedEvent.isShiftDown()){ // skipping backwards
               setPreviousCellSelected();
           } else if (lastKeyPressedEvent.getKeyCode() == KeyEvent.VK_ENTER ) { //&& this.enterFocusesVertically) {       
               setNextCellSelected();
           }
       }
   }
   
   /**
    * Checking if auto-inserted row should be cancelled.
       
    * @return   true, if auto-inserted row was deleted
    */
   private boolean checkAutoInsertCancel() {
       if (autoInsertCancelWhenNotModified && autoInsertCancel && prevRowIndex > currentRowIndex && prevRowIndex < this.getRowCount()) {
           autoInsertCancel = false;
           removeRow(prevRowIndex);
           return true;
       } 
       return false;
   }
   
   /**
    * Checking if row should be auto-inserted.
    * 
    * @return true, if row was inserted
    */
   private boolean checkAutoInsert()  {
       if (autoInsert && lastKeyPressedEvent != null) {
           // down in the last row triggers auto-insert
           if (prevRowIndex == (this.getRowCount()-1)  && (lastKeyPressedEvent.getKeyCode() == KeyEvent.VK_DOWN)) {
               this.invokeRowInsert();
               return true;
           // tab or enter in the last cell triggers auto-insert
           } else if (prevRowIndex == (this.getRowCount()-1) && (prevColumnIndex == this.getColumnCount()-1)
                   && (lastKeyPressedEvent.getKeyCode() == KeyEvent.VK_TAB ||
                       lastKeyPressedEvent.getKeyCode() == KeyEvent.VK_ENTER)){
               this.invokeRowInsert();
               return true;
           }
       }
       return false;
   }
   
    /*
     *  (non-Javadoc)
     * @see javax.swing.JTable#changeSelection(int, int, boolean, boolean)
     */
   public void changeSelection(int rowIndex, int columnIndex, boolean toggle,
           boolean extend) {

       prevRowIndex = currentRowIndex;
       prevColumnIndex = currentColumnIndex;
       currentRowIndex = rowIndex;
       currentColumnIndex = columnIndex;
//       logger.debug("prevRowIndex=" + prevRowIndex);
//       logger.debug("prevColumnIndex=" + prevColumnIndex);
//       logger.debug("currentRowIndex=" + currentRowIndex);
//       logger.debug("currentColumnIndex=" + currentColumnIndex);

       super.changeSelection(rowIndex, columnIndex, toggle, extend);
       boolean cancelled = this.checkAutoInsertCancel();
       boolean inserted = false;
       if (!cancelled) {
           inserted = this.checkAutoInsert();
       }
       // possible changes to cell selection...
       if (getModel() != null && getModel().getRowCount() > 0) {
           if (inserted) {
               changeSelection((this.getRowCount()-1), 0, false, false); // first cell in new row to be selected
           } else {
               if (this.isCellEditable(currentRowIndex, currentColumnIndex)) {
                   if (this.startEditingOnSelection) {
                       this.editCellAt(currentRowIndex, currentColumnIndex);   
                       // forcing focus to the selected component:
                       this.transferFocus(); 
                       if (this.getCellEditor() instanceof DefaultCellEditor) {
                           DefaultCellEditor editor = (DefaultCellEditor) this.getCellEditor();
                           editor.getComponent().requestFocus();
                       }
                    }
                } else {
                    if (skipNotEditableCells) {
                        moveToNextCell();
                    }
                }
           }
       }
       this.lastKeyPressedEvent = null;
   }

    

    
    /**
     * @return if table is in auto-insert mode
     */
    public boolean isAutoInsert() {
        return autoInsert;
    }

    /**
     * Enables automatic row insert when user moves to nonexisting row. This happens
     * when: 
     * <ul>
     * <li>user presses down arrow when the last row is focused</li>
     * <li>user presses tab when the last row and the last column is focused</li>
     * </ul>
     * 
     * When auto-insert is enabled, controller must implement method
     * <code>public void yyyRowInsert()</code>. The impementation should
     * create a new row for Collection (table data). For example, controller
     * could call YModel method:
     * <code>
     * public void newAccount() {
     *      accounts.add(new Account());
     *       notifyObservers("accounts");
     * }
     * </code>
     * 
     * @param autoInsert    if table is in auto-insert mode, default is false
     */
    public void setAutoInsert(boolean autoInsert) {
        this.autoInsert = autoInsert;
    }
    
    /**
     * @return if auto-inserted row should be deleted if it is not modified when focus is lost from the row
     */
    public boolean isAutoInsertCancelWhenNotModified() {
        return autoInsertCancelWhenNotModified;
    }

    /**
     * When set to true, new row is automatically deleted, if user 
     * exists row without modifying any row cells.
     * This method has no meaning if table is not in auto-insert-mode. 
     * 
     * @param autoInsertCancelWhenNotModified 
     *           if auto-inserted row should be deleted if it is not modified when focus is lost from the row
     */
    public void setAutoInsertCancelWhenNotModified(
            boolean autoInsertCancelWhenNotModified) {
        this.autoInsertCancelWhenNotModified = autoInsertCancelWhenNotModified;
    }


    /**
     * @return if not editable cells should be skipped 
     *          when cell focus is changing (default false)
     */
    public boolean isSkipNotEditableCells() {
        return skipNotEditableCells;
    }

    /**
     * @param skipNotEditableCells if not editable cells should be skipped 
     *                              when cell focus is changing (default false)
     */
    public void setSkipNotEditableCells(boolean skipNotEditableCells) {
        this.skipNotEditableCells = skipNotEditableCells;
    }

    /**
     * @return if cell editor should be activated when focusing editable cell (default true)
     */
    public boolean isStartEditingOnSelection() {
        return startEditingOnSelection;
    }

    /**
     * @param startEditingOnSelection if cell editor should be activated when focusing editable cell (default true)
     */
    public void setStartEditingOnSelection(boolean startEditingOnSelection) {
        this.startEditingOnSelection = startEditingOnSelection;
    }


    /**
     * @return  if user can sort this table by clicking column headers
     */
    public boolean isSortable() {
        return sortable;
    }

    /**
     * Defines if this table can be sorted. The default value is true.
     * 
     * @param sortable if user can sort this table by clicking column headers
     */
    public void setSortable(boolean sortable) {
        this.sortable = sortable;
    }

    /**
     * Sets Comparator used in sorting colums for this table. 
     * If null is set, the table default sorting logic is used
     * (Comparable sorting, except for String objects case-insensitive sorting)
     *
     * It should be noted that if Comparator is set to YColumn,
     * it is used instead (calling this method has no effect).
     * 
     * @param comparator the comparator for sorting
     */
    public void setSortingComparator(Comparator comparator) {
        this.sortingComparator = comparator;
    }
    
    /**
     * @return  the comparator used for sorting (set via setSortingComparator method)
     */
    public Comparator getSortingComparator() {
        return this.sortingComparator;
    }   
    
    
    /**
     * This method checks if current editor component is YIValidatorComponent
     * and returns YIValidatorComponent.valueValid as result. This method
     * checks only current editor, not any other table cells.
     * 
     * This method may be overridden for customized validation.
     * 
     * @see fi.mmm.yhteinen.swing.core.YIValidatorComponent#valueValid()
     */
    public boolean valueValid() {
        if (this.getCellEditor() instanceof DefaultCellEditor) {
            DefaultCellEditor dce = (DefaultCellEditor) this.getCellEditor();
            if (dce.getComponent() instanceof YIValidatorComponent) {
                YIValidatorComponent validator = (YIValidatorComponent) dce.getComponent();
                return validator.valueValid();
            }
        }
        return true;
    }
    
    private String getKey(int row, int column) {
        return row+","+column;
    }
    
    /**
     * Method tries to ensure that given row is visible. 
     * This method may be used when table is embedded into a scroll pane.
     *
     * @param row   the row that should be visible
     */
    public void ensureRowIsVisible(int row) {
        Rectangle rect = getCellRect(row, 0, true);
        this.scrollRectToVisible(rect);
    }
    
    
    /**
     * Sets editor and renderer for given cell. If given row is -1, editor and renderer
     * are specified for the whole column. If given column is -1, editor and renderer
     * are specified for the whole row. 
     * 
     * (This is a convenience method to be used instead of overriding getCellEditor and
     * getCellRenderer methods.)
     * 
     * @param row       the row index
     * @param column    the column index
     * @param editor    the editor
     * @param renderer  the renderer
     */
    public void setEditorAndRenderer(int row, int column, TableCellEditor editor, TableCellRenderer renderer) {
        String key = getKey(row, column);
        editors.put(key, editor);
        renderers.put(key, renderer);
    }
    
    /*
     *  (non-Javadoc)
     * @see javax.swing.JTable#getCellEditor(int, int)
     */
    public TableCellEditor getCellEditor(int row, int column) {
        TableCellEditor editor = (TableCellEditor) editors.get(getKey(row, column));
        if (editor == null) {
            // no editor specified for the cell, checking if specified for the column...
            editor = (TableCellEditor) editors.get(getKey(-1, column));
            if (editor == null) {
                // checking if editor specified just for the row ...
                editor = (TableCellEditor) editors.get(getKey(row, -1));
                if (editor == null){
                	// checking if editor specified the whole table
                	editor = (TableCellEditor) editors.get(getKey(-1, -1));
                }
            }
        }
        if (editor != null) {
            return editor;
        } else {
            return super.getCellEditor(row, column);        
        }
    }
    
    /*
     *  (non-Javadoc)
     * @see javax.swing.JTable#getCellRenderer(int, int)
     */
    public TableCellRenderer getCellRenderer(int row, int column) {
        TableCellRenderer renderer = (TableCellRenderer) renderers.get(getKey(row, column));
        if (renderer == null) {
            // no renderer specified for the cell, checking if specified for the column...
            renderer = (TableCellRenderer) renderers.get(getKey(-1, column));
            if (renderer == null) {
                // checking if renderer is specified just for the row ...
                renderer = (TableCellRenderer) renderers.get(getKey(row, -1));
                if (renderer == null){
                	// checking if renderer specified the whole table
                	renderer = (TableCellRenderer) renderers.get(getKey(-1, -1));
                }
            }
        }
        if (renderer != null) {
            return renderer;
        } else {
            TableCellRenderer superRenderer = super.getCellRenderer(row, column);   
            if (alwaysUseYTableCellRenderer && ! (superRenderer instanceof YTableCellRenderer)) {
                // by default we will force using som YTableCellRenderer so that coloring stuff will work
                return DEFAULT_RENDERER;
            }  else {
                return superRenderer;
            }
        }
    }
    
    

    
     
    /**
     * This method is for framework internal use. 
     * @param lastKeyPressedEvent
     */
    public void setLastKeyPressedEvent(KeyEvent lastKeyPressedEvent) {
        this.lastKeyPressedEvent = lastKeyPressedEvent;
    }

    

    
    /**
     * The inner table model.
     */
    private class YTableModel extends AbstractTableModel {
        
        /*
         *  (non-Javadoc)
         * @see javax.swing.table.TableModel#getColumnClass(int)
         */
        public Class getColumnClass(int columnIndex) {
            if (columns != null) {
                // if editor class is specified, using that...
                Class editorClass = columns[columnIndex].getEditorClass();
                if (editorClass != null) {
                    return editorClass;
                }
            }
            // otherwise searching for an object in the column
            Object columnObj = null;
            if (data != null) {
                String fieldName = (String) columns[columnIndex].
                getYProperty().get(YIComponent.MVC_NAME);
                // iterating rows until a not-null object in the column is found...
                for (int i=0; i < data.size() && columnObj == null; i++) {
                    Object row = data.get(i);
                    if (row != null) {
                        try {
                            columnObj = YCoreToolkit.getBeanValue(row, fieldName);
                        } catch (Exception ignored) {
                            // don't care about exceptions, the row may hold null values... 
                            // we just want to dig out some Object from any row 
                        }
                    }
                }
            }
            if (columnObj != null) {
                return columnObj.getClass();
            } else {
                return Object.class;
            }
        }
        
         /*
          *  (non-Javadoc)
          * @see javax.swing.table.TableModel#isCellEditable(int, int)
          */
         public boolean isCellEditable(int row, int col) { 
            return YTable.this.isCellEditable(row, col);    
         }
        
        /* (non-Javadoc)
         * @see javax.swing.table.TableModel#getColumnCount()
         */
        public int getColumnCount() {
            if (columns == null) {
                return 0;
            } else {
                return columns.length;
            }
        }

        /* (non-Javadoc)
         * @see javax.swing.table.TableModel#getRowCount()
         */
        public int getRowCount() {
            if (data == null) return 0;
            return data.size();
        }

        /* (non-Javadoc)
         * @see javax.swing.table.TableModel#getValueAt(int, int)
         */
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (data == null) return null;
            // using reflection to get value from table model collection...
            Object obj = data.get(rowIndex);
            String fieldName = (String) columns[columnIndex].
                getYProperty().get(YIComponent.MVC_NAME);
            if( fieldName == null ) {
                // column is not connected to any POJO field, returning the whole POJO:
                return obj;
            } else { 
                // finding data for the column from a row POJO
                try {
                    return YCoreToolkit.getBeanValue(obj, fieldName);
                } catch (Exception e) {
                    controller.modelGetValueFailed(new YModelGetValueException(e,
                            YTable.this));
                } 
            }
            return null;
        }
        
        /*
         *  (non-Javadoc)
         * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
         */
        public void setValueAt(Object value, int row, int col) {
            if (data != null) {
                Boolean readOnly = (Boolean) getYProperty().get(YIComponent.READ_ONLY);
                if (readOnly == null || !readOnly.booleanValue()) {
                    Object rowObject = data.get(row);
                    String fieldName = (String) columns[col].
                        getYProperty().get(YIComponent.MVC_NAME);
                    
                    if (fieldName == null) {
                        // the column is not connected to any special POJO field, updating the whole row POJO:
                        // checking if value has truly changed:
                        if (ObjectUtils.equals(rowObject, value)) {
                            return;
                        }
                        data.set(row, value);
                        YTable.this.modelCollection.remove(rowObject);
                        YTable.this.modelCollection.add(value);
                    } else {
                        // setting value into the row POJO
                        // checking if value has truly changed:
                        try {
                            if (ObjectUtils.equals(value, 
                                    YCoreToolkit.getBeanValue(rowObject, fieldName))) {
                                return;
                            }
                        } catch (Exception ex) {
                            controller.modelGetValueFailed(new YModelGetValueException(ex, YTable.this));
                        }

                        try {
                            YCoreToolkit.setBeanValue(rowObject, fieldName, value);
                        } catch (Exception ex) {
                            controller.modelSetValueFailed(new YModelSetValueException(ex,
                                    YTable.this, value));
                        }
                    }
                    YTable.this.lastEditedRow = row;
                    YTable.this.lastEditedColumn = col;
                    autoInsertCancel = false; // cell has been changed, not cancelling auto-insert row
                    // triggering ContentChanged event:
                    String methodName = YUIToolkit.createMVCMethodName(YTable.this, "Changed");
                    Object[] params = {new YTableChangeData(row, col, value)};
                    
                    Class[] paramClasses = {YTableChangeData.class};
                    controller.invokeMethodIfFound(methodName, params, paramClasses);
                    
                    controller.updateModelAndController(YTable.this);
                }
                fireTableCellUpdated(row, col);
            }
        }
    } // class YTableModel
    
    /**
     * This is a convenience method for setting MVC-name.
     * 
     * @param mvcName   the YIComponent.MVC_NAME value
     */
    public void setMvcName (String mvcName) {
        getYProperty().put(YIComponent.MVC_NAME, mvcName);
    }

    /**
     * @return  the row that was edited, -1 if no edited rows
     */
    public int getLastEditedRow() {
        return lastEditedRow;
    }

    /**
     * @return  the column that was edited, -1 if no edited columns
     */
    public int getLastEditedColumn() {
        return lastEditedColumn;
    }
    
   

    /**
     *
     * @return  is cell tool tips enabled
     */
    public boolean isCellTooltips() {
        return cellTooltips;
    }

    /**
     * Enables/disables cell tool tips. If enabled, each table cell has tool tip
     * same as table cell renderer text.
     *
     * (When implementing a new renderer, please take this into account:  
     * see YTableCellRenderer for example).
     *
     * @param cellTooltips  if cell tool tips are enabled
     */
    public void setCellTooltips(boolean cellTooltips) {
        this.cellTooltips = cellTooltips;
    }

    /**
     * @return  if YTableCellRenderer is always used
     */
    public boolean isAlwaysUseYTableCellRenderer() {
        return alwaysUseYTableCellRenderer;
    }

    /**
     * By default YTableCellRenderer is always used to ensure that coloring stuff in YTable works.
     * If you want to use your own renderer, that is NOT YTableCellRenderer, call this
     * method with false parameter. Also, take care of coloring stuff in your own renderer if needed
     * (call YTable.setColors method).
     * 
     * @param alwaysUseYTableCellRenderer if YTableCellRenderer is always used
     */
    public void setAlwaysUseYTableCellRenderer(boolean alwaysUseYTableCellRenderer) {
        this.alwaysUseYTableCellRenderer = alwaysUseYTableCellRenderer;
    }


}   



  

