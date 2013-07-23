/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.component.table;

import java.awt.GridLayout;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import fi.mmm.yhteinen.swing.core.YController;
import fi.mmm.yhteinen.swing.core.YIComponent;
import fi.mmm.yhteinen.swing.core.YIModelComponent;
import fi.mmm.yhteinen.swing.core.component.YPanel;
import fi.mmm.yhteinen.swing.core.component.YScrollPane;
import fi.mmm.yhteinen.swing.core.error.YException;
import fi.mmm.yhteinen.swing.core.tools.YCoreToolkit;
import fi.mmm.yhteinen.swing.core.tools.YUIToolkit;

/**
 * This component is for showing master-detail data in several YTable components.
 * Model object of this component should be hierarchical POJO with nested List collections.
 * <p>
 * MVC_NAME must be set only to this component, that will be the MVC_NAME of the
 * first table. When the first table selection has been changed, the component
 * updates detail table by finding a sub collection in selected row object. 
 * <p>
 * YProperty DETAIL_LIST_NAME may be set to specify, which sub collection
 * is set to the table. If this is not set, the component will update the
 * first Collection found. 
 * 
 * <p>
 * Controller event methods, where yyy is component's name:
 * 
 * <code>public void yyySelectionChanged(YTable, Object)</code> executed when a selection ín a table has been changed                                          in the table is changed <br>
 * </p> 
 * 
 * All standard functionality should be available in YTables. This component just
 * wraps those tables and updates contents based on master table selection. However,
 * this means that also MVC_NAMEs of tables are created runtime.
 *
 * @author Tomi Tuomainen 
 */
public class YMasterDetailTable extends YPanel implements YIModelComponent {
    
    // YProperty that may be set for YTables (specify sub collection name)
    public String DETAIL_LIST_NAME = "DETAIL_LIST_NAME";
    
    //  data of YModel
    private Object modelCollection; 
    
    public static final ArrayList EMPTY_MODEL = new ArrayList(0);
    
    // master/detal tables
    private YTable[] tables;
    
    private YController controller;
    
    /**
     * Creates component with given tables. The table order must correspond to 
     * hierarchical data (first table will show data of the first collection)
     * 
     * @param tables    the master/detail tables to show hierarchical data
     */
    public YMasterDetailTable(YTable[] tables) {
            this.tables = tables;
            addTables(tables);
    }
    
    
    /**
     * Adds tables to into this component (YPanel). By default each
     * table will be equal size and is added with YScrollPane. 
     * 
     * This method may be overridden to customize
     * table sizes (set suitable layout and add tables). 
     * 
     * @param tables    the tables to add to this panel
     */
    protected void addTables(YTable[] tables) {
        this.setLayout(new GridLayout(tables.length, 1));
        for (int i=0; i < tables.length; i++) {
            YScrollPane pane = new YScrollPane(tables[i]);
            this.add(pane);
        }
    }

    /*
     *  (non-Javadoc)
     * @see fi.mmm.yhteinen.swing.core.YIModelComponent#getModelValue()
     */    
    public Object getModelValue() {
        return modelCollection;
    }
    
    /*
     *  (non-Javadoc)
     * @see fi.mmm.yhteinen.swing.core.YIModelComponent#setModelValue(java.lang.Object)
     */
    public void setModelValue(Object obj) {
        if (obj == null) {
            modelCollection = EMPTY_MODEL;
        } else {
            if (!(obj instanceof Collection)) {
                throw new YException(
                        "Table model must be instance of Collection.");
            } else {
                this.modelCollection = (Collection) obj;
            }
        }
        // setting collection to the first table
        String mvcName = (String) this.getYProperty().get(YIComponent.MVC_NAME);
        tables[0].getYProperty().put(YIComponent.MVC_NAME, mvcName);
        tables[0].setModelValue(modelCollection);
    }
    
    /*
     *  (non-Javadoc)
     * @see fi.mmm.yhteinen.swing.core.YIControllerComponent#addViewListener(fi.mmm.yhteinen.swing.core.YController)
     */
    public void addViewListener(YController controller) {
        this.controller = controller;
        for (int i=0; i < tables.length; i++) {
            tables[i].addViewListener(controller, this);
        }
    } 
    
    /**
     * Triggers controller events when table selection has been changed.
     * 
     * @param table  the table with changed selection
     */
    private void triggerControllerEvent(YTable table) {
        String methodName = YUIToolkit.createMVCMethodName(this, "SelectionChanged");
        if (methodName != null) {
            Object param[] = {table, table.getSelectedObject()};
            Class paramClass[] = {YTable.class, Object.class};
            controller.invokeMethodIfFound(methodName, param, paramClass);
        }
        
    }

    /**
     * Updates data to the detail table when table selection has been changed.
     * 
     * @param table  the table with changed selection
     */
    private void updateNextTable(YTable table) {
        YTable nextTable = null;
        for (int i=0; i < (tables.length-1) && nextTable == null; i++) {
            if (table == tables[i]) {
                nextTable = tables[i+1];
            }
        }
        if (nextTable != null) { // detail-table found
            Object obj = table.getSelectedObject();
            if (obj == null) {
                // no selection
                 nextTable.setModelValue(null);
            } else {
               
                Method[] methods = obj.getClass().getMethods();
                for (int i = 0; i < methods.length; i++) {
                    Method method = methods[i];
                    // checking if method is public getter for Collection
                    if (method.getName().indexOf("get") == 0 && 
                            method.getParameterTypes().length == 0 &&
                            method.getModifiers() == Modifier.PUBLIC &&
                            Collection.class.isAssignableFrom(method.getReturnType())) {
                        String collectionName = method.getName().substring(3);
                        String listName = (String) nextTable.getYProperty().get(DETAIL_LIST_NAME);
                        // list name must match (if defined):
                        if (listName == null || listName.equals(collectionName)) {
                            try {
                                // setting mvc name
                                String mvcName = (String) table.getYProperty().get(YIComponent.MVC_NAME);
                                int index = table.getSelectedRow();
                                String nextMvcName = mvcName+"[" + index+ "]." + collectionName;
                                nextTable.getYProperty().put(YIComponent.MVC_NAME, nextMvcName);
                                // setting collection    
                                Collection children = (Collection) method.invoke(obj, YCoreToolkit.EMPTY_PARAM);
                                nextTable.setModelValue(children);
                            } catch (Exception ex) {
                                throw new YException(ex);
                            }
                        }
                    }
                }      
            }
        }
    }
    
    /**
     * This method updates detail table when row selection in master table has been changed.
     * 
     * This method is called from YTable after a row has been selected.
     * 
     * @param table     the table of changed selection
     */
    void tableSelectionChanged(YTable table) {
        triggerControllerEvent(table);
        updateNextTable(table);
    }
    
    /**
     * This is a convenience method for setting MVC-name.
     * 
     * @param mvcName   the YIComponent.MVC_NAME value
     */
    public void setMvcName (String mvcName) {
        getYProperty().put(YIComponent.MVC_NAME, mvcName);
    }



}
