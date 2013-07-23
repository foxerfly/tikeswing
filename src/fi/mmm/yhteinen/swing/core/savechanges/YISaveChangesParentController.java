/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.savechanges;

/**
 * Interface for YController classes for implementing unsaved changes
 * checking. This interface is used to mark that controller saves
 * data of child controllers. When the framework checks unsaved
 * changes, controller's child controllers are checked too if this
 * interface is implemented.
 * 
 * @author Tomi Tuomainen
 * @see YSaveChangesHandler
 */
public interface YISaveChangesParentController extends YISaveChangesController {

}
