/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.component.list;

import java.awt.Font;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import javax.swing.plaf.metal.MetalComboBoxEditor;

import fi.mmm.yhteinen.swing.core.YController;
import fi.mmm.yhteinen.swing.core.YIComponent;
import fi.mmm.yhteinen.swing.core.YIExtendedModelComponent;
import fi.mmm.yhteinen.swing.core.YIModelComponent;

import fi.mmm.yhteinen.swing.core.error.YException;
import fi.mmm.yhteinen.swing.core.tools.YCoreToolkit;
import fi.mmm.yhteinen.swing.core.tools.YFormatter;
import fi.mmm.yhteinen.swing.core.tools.YProperty;

/**
 * The combo box. 
 * <p>
 * Combo model is a collection of POJO's. Model may be set
 * by calling setComboModelField method (model Collection
 * is read directly from YModel), or alternatively setComboModel method
 * may be called manually.
 * <p>
 * YFormatter can be set to specify how list model 
 * is showed to user. Selection in the combo is mapped to view
 * model field. Method setModelField can be used to to specify, which
 * combo model field is mapped to a view model field. Normal equals method
 * comparison is used, when mapping fields (so it is assumed that
 * equals is implemented in a class of the view model field). 
 * <p>
 * Default behaviour of YComboBox is a little different from Swing 
 * JComboBox. When YComboBox is editable it accepts only input 
 * that is found from the combo model). When user 
 * edits combo's field, the combo is searching the closest 
 * match in it's model (for given text) and shows the selection in 
 * it's list. 
 * <p>
 * Alternative (normal Swing) behaviour for combo can be set 
 * with setForceListItem method. If forceListItem is set 
 * to false, user can type whatever text into combo 
 * (not just list items). In this case, the value 
 * mapped to view model will be the text in combo's editor field.
 * <p>
 * By default, this combo box uses always MetalComboBoxEditor. 
 * Other combo box editors may not work with YComboBox. 
 * 
 * @author Tomi Tuomainen
 */
public class YComboBox extends JComboBox implements YIModelComponent, YIExtendedModelComponent {

	private final YListItem emptyItem = new YListItem() {
		public String toString() {
			return "";
		}
	};
	 
	// the field to be mapped to the model
	private String modelField;
	
	// if list item must always be selected:
	private boolean forceListItem = true;
	
	// the collection of items for combo list:
	private YListItem[] items;
	
	private JTextField editorField;
	
    // this is used in returning the old value if component is in not editable state
    private Object validValue;
    
    //  if setModelValue method is currently being executed
    private boolean settingModelValue = false;
    
    private Collection originalComboModel;
    
    private boolean updateModelOnlyWhenFocusLost = false;
    
    private String comboModelField;
    
    private YFormatter formatter;
    private boolean addEmpty;
    private boolean sort;
    
    
	public YComboBox() {
        this.setEditor(new MetalComboBoxEditor());
        editorField = (JTextField) this.getEditor().getEditorComponent();
       
        this.setEditable(true);
        // adding internal listeners...
		this.editorField.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				editorKeyReleased(e);
			}
		});
		editorField.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				editorFocusLost(e);
			}
		});
		
        this.addPopupMenuListener(new PopupMenuListener() {
            public void popupMenuCanceled(PopupMenuEvent e) {}
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) { }

            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                if (!isEditable()) {
                    // if pop up is opened in not editable mode, we store the current selection
                    // to be returned when pop up is closed...
                   validValue = getModelValue();
                }
            }
         
        });
        this.setFont(this.getFont().deriveFont(Font.PLAIN));
        
	}
	
	/**
	 * @param forceListItem true, if text in combo must always be selected list item,
	 * 						false, if user can type any text in combo's text field
	 */
	public YComboBox(boolean forceListItem) {
		this();
		this.forceListItem = forceListItem;
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
     * Returns field name that is mapped to view model.
     * The field name is some field in combo model (collection 
     * of POJOs).
     * 
	 * @return the name of the field to be mapped to view model
	 */
	public String getModelField() {
		return modelField;
	}
	/**
     * Sets field name in that will be mapped to view model.
     * The field name is some field in combo model (collection 
     * of POJOs).
     * 
     * @param modelField the name of the field to be mapped to view model
	 */
	public void setModelField(String modelField) {
		this.modelField = modelField;
	}
	
	/* (non-Javadoc)
	 * @see fi.mmm.yhteinen.swing.component.YIControllerComponent#addViewListener(fi.mmm.yhteinen.swing.YController, fi.mmm.yhteinen.swing.YModel)
	 */
	public void addViewListener(final YController controller) {
        this.addItemListener(new ItemListener() {
	        public void itemStateChanged(ItemEvent ev) {
	            if (ev.getStateChange() == ItemEvent.SELECTED) {
//	                checking if event is really triggered by user...
	                if (!settingModelValue) {
	                    // if combo is in "forceListItem" mode and the field is not editable, 
	                    // we return the last valid value so that the selection is cancelled:
	                    if (!isEditable() && forceListItem) {
	                        try {
	                            setModelValue(validValue);
	                        } catch (YException ex) {
	                            controller.handleException(ex);
	                        }
	                    } else {
                            // updating model only if editing has truly stopped (focus is lost from editor):
                            if (!updateModelOnlyWhenFocusLost) {
                                controller.updateModelAndController(YComboBox.this);
                                if (moveCaretInSelection) editorField.setCaretPosition(0);
                            }    
	                    }
	                }
	            }
	        }
	    });
        this.editorField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                if (updateModelOnlyWhenFocusLost) {
                    controller.updateModelAndController(YComboBox.this);
                }
            }
            
        });
	}
    
    /** 
     * Sets internal model for this combo. 
     * If comboModelField is set, this method is useless (combo model is automatically read from YModel).
     * 
     * @param items     the collection of POJOs
     * @param addEmpty  if an empty item is added in the beginning of combo list
     */
	public void setComboModel(Collection items, boolean addEmpty) {
		setComboModel(items, null, addEmpty, true);
	}
	
	/** 
     * Sets internal model for this combo. 
      * If comboModelField is set, this method is useless (combo model is automatically read from YModel).
    * 
     * @param items     the collection of POJOs
     * @param formatter defines how a list item is showed to user
     * @param addEmpty  if an empty item is added in the beginning of combo list
     */
	public void setComboModel(Collection items, YFormatter formatter, boolean addEmpty) {
		setComboModel(items, formatter, addEmpty, true);
	}
	

    /** 
     * Sets internal model for this combo. 
     * If comboModelField is set, this method is useless (combo model is automatically read from YModel).
     * 
     * @param data     the collection of POJOs
     * @param formatter defines how a list item is showed to user
     * @param addEmpty  if an empty item is added in the beginning of combo list
     * @param sort		if items will be sorted (ascending order)
     */
    public void setComboModel(Collection data, YFormatter formatter, boolean addEmpty, boolean sort) {
        this.formatter = formatter;
        this.addEmpty = addEmpty;
        this.sort = sort;
        setComboModel(data);
    }
    
    /** 
     * Sets internal model for this combo. 
     * If comboModelField is set, this method is useless (combo model is automatically read from YModel).
     * 
     * @param data     the collection of POJOs
     */ 
    public void setComboModel(Collection data) {
        this.originalComboModel = data;
        int i = 0;
        if (addEmpty) {
            items = new YListItem[data.size()+1];
            items[0] = emptyItem;
            i = 1;
        } else {
            items = new YListItem[data.size()];
        }
        Iterator it = data.iterator();
            while (it.hasNext()) {
                YListItem comboItem = new YListItem();
                if (formatter != null) {
                    comboItem.setFormatter(formatter);
                }
                comboItem.setItemModel(it.next());
                items[i++] = comboItem;
            }
            if (sort) {
                Arrays.sort(items, YCoreToolkit.LEXICAL_COMPARATOR);
            }
       
        super.setModel(new DefaultComboBoxModel(items));
        
    }
	
	/**
     * Gets selected item for view model.
     * 
     * @see #setModelField(String)
     * 
     * @return the value or null if no item is selected
     */
    public Object getModelValue() {
    	if (!forceListItem) {
    		return this.editorField.getText();
    	} else {
    		Object item = super.getSelectedItem();
    		if (item == null || item == emptyItem ||
    			!(item instanceof YListItem)) {
    			return null;
    		} else {
    			YListItem comboItem = (YListItem) super.getSelectedItem();
        		if (modelField == null) {
    				return comboItem.getItemModel();
    			} else {
    				return YCoreToolkit.getBeanValue(comboItem.getItemModel(), modelField);
    			}
    		}
    	}
    }
    
    

    
    /**
     * Sets model value from view model. Finds matching item 
     * from the combo box and sets it selected.
     * 
     * @param obj 	the view model value
     * @see #setModelField(String)
     */
    public void setModelValue(Object obj) throws YException {
        this.settingModelValue = true;
        try {
            if (obj == null) {
                this.setSelectedIndex(-1);
            } else {
                if (!forceListItem) {
                    this.editorField.setText(obj.toString());
                } else {
                    // searching corresponding item in list:
                    if (items == null) {
                        throw new YException("Combo model for YComboBox is not set.");
                    } else {
                             for (int i=0; i < this.items.length; i++) {
                                YListItem item = (YListItem)items[i];
                                if (!(item == emptyItem)) {
                                    if (this.modelField == null) {
                                        if (obj.equals(item.getItemModel())) {
                                            this.setSelectedItem(item);
                                        }
                                    } else {
                                        Object value = YCoreToolkit.getBeanValue(item.getItemModel(), modelField);
                                        if (obj.equals(value)) {
                                            this.setSelectedItem(item);
                                        }
                                    }
                                }
                            }
                       
                    }
                }
            }
            if (forceListItem && getSelectedItem() != null) {
                // this fixes caret position in the editor...
                this.editorField.setText(getSelectedItem().toString());
                this.editorField.setCaretPosition(0);
            }
        } finally {
            settingModelValue = false;
        }
    }
    
	private boolean moveCaretInSelection = true;
    
	/**
	 * Sets list index based on editor's text.
	 * 
	 * @return true if matching item was found on the list
	 */
	private boolean setSelectedByEditor() {
		String editorText = editorField.getText().toUpperCase();
		if (editorText.length() == 0) {
			return false;
		}
		int pos = editorField.getCaretPosition();
		boolean found = false;
		for (int i = 0; i < getItemCount() && !found; i++) {
			String item = getItemAt(i).toString();
			if (item.toUpperCase().startsWith(editorText)) {
				editorField.setText(item);
				editorField.setCaretPosition(item.length());
				editorField.moveCaretPosition(pos);
                moveCaretInSelection = false;
				setSelectedIndex(i);
                moveCaretInSelection = true;
				found = true;
			}
		}
		return found;
	}
	
	/**
	 * Sets selected index based on the text user has typed into editor.
	 * 
	 * @param e the key event
	 */
	private void editorKeyReleased(KeyEvent e) {
      if (forceListItem) {
			char ch = e.getKeyChar();
			//Quit if typed in char is non-alphabetical.
			if (ch == KeyEvent.CHAR_UNDEFINED ||
					Character.isISOControl(ch)) {
				return;
			}
			boolean found = setSelectedByEditor();
			if (!found) {
				// setting back the text and selection before key event...
				String editorText = editorField.getText();
				int index = editorText.lastIndexOf(ch);
				if (index > -1) {
					editorField.setText(editorText.substring(0, index));
				}
				setSelectedByEditor();
			}
			showPopup();
		}
	}
	
	/**
	 * Checks the value of the editor when exiting field.
	 * @param e
	 */
	private void editorFocusLost(FocusEvent e) {
        // t‰m‰ on kommentoitu pois, koska se aiheuttaa virheen, jossa valitaan aakkosj‰rjestyksessa "l‰hin"
        // kun combosta poistutaa, eik‰ hiirell‰ valittu, mahdollisesti taas aiheuttaa jotain sivuvaikutuksia johonkin
        // tapaukseen, mutta kun en nyt tied‰, mik‰ menee rikki, niin pidet‰‰n toistaiseksi kommentoituna, 
        // jotta tuo yksi tapaus toimii
//       if (forceListItem && !setSelectedByEditor()) {
//			setSelectedIndex(-1);
//		}
	}
    
   

	/**
	 * @return true, if text in combo must always be selected list item,
	 * 			false, if user can type any text in combo's text field
	 */
	public boolean isForceListItem() {
		return forceListItem;
	}
	
	/**
	 * Defines if user can edit any text in combo. The default value
	 * is false, which means that, when user edits combos text field,
	 * combo searches corresponding element in its list. Therfore, user 
	 * cannot type text, that is not in the list.
	 * 
	 * @param forceListItem true, if text in combo must always be selected list item,
	 * 						false, if user can type any text in combo's text field
	 */
	public void setForceListItem(boolean forceListItem) {
		this.forceListItem = forceListItem;
	}
	/**
	 * @return the item representing an empty item in this combo
	 */
	public YListItem getEmptyItem() {
		return emptyItem;
	}

    /**
     * @return Returns combo model that was set with setComboModel 
     */
    public Collection getComboModel() {
        return originalComboModel;
    }

    /**
     * @return the combo model that was set via setComboModel method
     */
    public Collection getOriginalComboModel() {
        return originalComboModel;
    }
    
    /**
     * This is a convenience method for setting MVC-name.
     * 
     * @param mvcName   the YIComponent.MVC_NAME value
     */
    public void setMvcName (String mvcName) {
        getYProperty().put(YIComponent.MVC_NAME, mvcName);
    }
    
    /**
     * @return if YModel is updated only when focus is lost (not immediatly when selection is changed)
     */
    public boolean isUpdateModelOnlyWhenFocusLost() {
        return updateModelOnlyWhenFocusLost;
    }

    /**
     * @param doUpdate if YModel is updated only when focus is lost (not immediatly when selection is changed)
     */
    public void setUpdateModelOnlyWhenFocusLost(boolean doUpdate) {
        this.updateModelOnlyWhenFocusLost = doUpdate;
    }

    
   /*
    * (non-Javadoc)
    * @see fi.mmm.yhteinen.swing.core.YIExtendedModelComponent#getExtendedFields()
    */
    public String[] getExtendedFields() {
        if (comboModelField == null)return new String[0];
        return new String[] {comboModelField};
    }

    /*
     * (non-Javadoc)
     * @see fi.mmm.yhteinen.swing.core.YIExtendedModelComponent#setModelValue(java.lang.String, java.lang.Object)
     */
    public void setModelValue(String field, Object value) throws Exception {
        if (field.equals(comboModelField)) {
            Object obj = getModelValue();
            setComboModel((Collection)value);
            setModelValue(obj);
        }
    }
    
    /*
     * (non-Javadoc)
     * @see fi.mmm.yhteinen.swing.core.YIExtendedModelComponent#getModelValue(java.lang.String)
     */
    public Object getModelValue(String field) throws Exception {
       if (field.equals(comboModelField)) {
           return originalComboModel;
       }
       return null;
    }

    
    /**
     * @return  if an empty item is added as the first element of combo model
     */
    public boolean isAddEmpty() {
        return addEmpty;
    }

    /**
     * @param addEmpty  if an empty item is added as the first element of combo model
     */
    public void setAddEmpty(boolean addEmpty) {
        this.addEmpty = addEmpty;
    }



    /**
     * @return formatter defines how a list item is showed to user
     */

    public YFormatter getFormatter() {
        return formatter;
    }
    /**
     * Sets formatter for combo model.
     * @param formatter defines how a list item is showed to user
     */
    public void setFormatter(YFormatter formatter) {
        this.formatter = formatter;
    }

    /**
     * @return sort   if combo model items will be sorted (ascending order)
     */
    public boolean isSort() {
        return sort;
    }
    
    /**
     * @param sort if combo model items will be sorted (ascending order)
     */
    public void setSort(boolean sort) {
        this.sort = sort;
    }

    /**
     * @return  YModel field name that holds combo model data
     */
    public String getComboModelField() {
        return comboModelField;
    }

    /**
     * Sets field name that holds combo model data (in YModel). 
     * <p>
     * The combo selection is linked to YModel via MVC-name. The combo model
     * may be linked by calling this method (as alternative to calling
     * setComboModel(Collection) method manually). 
     * <p>
     * @param comboModelField   YModel field name that holds combo model data
     */
    public void setComboModelField(String comboModelField) {
        this.comboModelField = comboModelField;
    }
    
    /**
    *
    * @return text current showing in editor
    */
   public String getEditorText() {
       return this.editorField.getText();
   }
    
}
