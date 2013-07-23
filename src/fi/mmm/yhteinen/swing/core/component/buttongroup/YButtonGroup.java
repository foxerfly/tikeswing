/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.component.buttongroup;


import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import fi.mmm.yhteinen.swing.core.YController;
import fi.mmm.yhteinen.swing.core.YIComponent;
import fi.mmm.yhteinen.swing.core.YIModelComponent;
import fi.mmm.yhteinen.swing.core.tools.YProperty;

/**
 * The button group for YRadioButtons. Button group is connected to view model with
 * with MVC_NAME YProperty. View model field values are set to YRadioButtons
 * with setSelectionId method. 
 * <p>
 * For example, if view model contains field named
 * <code>selection</code> with value <code>foo</code>, the MVC-name of this
 * button group should be <code>selection</code>. The radio button with
 * selection id <code>foo</code> will be selected.
 * 
 * @author Tomi Tuomainen
 */
public class YButtonGroup extends ButtonGroup implements YIModelComponent {

	private YRadioButton noSelection = new YRadioButton();
	
	private YProperty myProperty = new YProperty();
	
	
	public YButtonGroup() {
		super();
		this.add(noSelection);
		noSelection.setVisible(false);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see fi.mmm.yhteinen.swing.core.YIComponent#getYProperty()
	 */
	public YProperty getYProperty() {
	 	return myProperty;
	}
	
	
	private YController controller;

	/* (non-Javadoc)
	 * @see fi.mmm.yhteinen.swing.core.YIControllerComponent#addViewListener(fi.mmm.yhteinen.swing.core.YController)
	 */
	public void addViewListener(YController controller) {
		// storing the controller of this component for later use
		this.controller = controller;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see javax.swing.ButtonGroup#add(javax.swing.AbstractButton)
	 *
	 */public void add(AbstractButton button) {
		super.add(button);
		// adding listener for each button for updating the framework model and controller
		button.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				AbstractButton button = (AbstractButton) e.getSource();
				if (button.isSelected()) {
					controller.updateModelAndController(YButtonGroup.this);
				}
			}
		});
	}
	
	

	/**
	 * Returns selectionId of currently selected radio button (for view model). 
	 * 
	 * Null is returned if no radio button is selected.
	 * 
	 * @return the selectionId of selected YRadioButton
	 */
	public Object getModelValue() {
		if (noSelection.isSelected()) {
			return null;
		} else {
			Enumeration e = this.getElements();
			while (e.hasMoreElements()) {
				Object elem = e.nextElement();
				if (elem instanceof YRadioButton) {
					YRadioButton button = (YRadioButton) elem;
					if (button.isSelected()) {
						return button.getSelectionId();
					}
				}
			}
			return null;
		}
	}

	/**
	 * Sets selected radio button based on given selectionId (obj parameter
	 * given by view model)
	 * 
	 * @param obj the selection to be set
	 */
	public void setModelValue(Object obj) {
		if (obj == null) {
			noSelection.setSelected(true);
		} else {
			Enumeration e = this.getElements();
			while (e.hasMoreElements()) {
				Object elem = e.nextElement();
				if (elem instanceof YRadioButton && elem != noSelection) {
					YRadioButton button = (YRadioButton) elem;
					if (obj.equals(button.getSelectionId())) {
						button.setSelected(true);
					}
				}
			}
		}
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
