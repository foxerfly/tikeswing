/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.component.table;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JRadioButton;

import fi.mmm.yhteinen.swing.core.component.YPanel;

/**
 * Group of radio buttons to be showed in a table in one table column. 
 * Use this together with YTableButtonGroupEditor and YTableButtonGroupRenderer.
 * 
 * @author Tomi Tuomainen
 */
public class YTableButtonGroup extends YPanel {
	
	private JRadioButton[] buttons;
	private Object[] selectionIds;
	
	private YTableButtonGroupEditor editor;
	
	/**
	 * @param buttonCount how many button there will be in this group
	 */
	public YTableButtonGroup(int buttonCount) {
		super();
		addComponents(buttonCount);
	}
	
	/**
	 * Creates buttons and adds them to this panel.
	 * @param count	the button count
	 */
	private void addComponents(int count) {
        this.setLayout(new GridLayout(1, count));
        
		buttons = new JRadioButton[count];
		selectionIds = new Object[count];
		JComponent[][] comps = new JComponent[1][count];
		int[][] widths = new int[1][count];
		for (int i=0; i < count; i++) {
			final JRadioButton button = new JRadioButton();
			comps[0][i] = button;
			widths[0][i] = 1;
			buttons[i] = button;
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					buttonClicked(button);
				}
			});
            this.add(button); 
		}
	}
	
	/*
	 * Sets texts for all radio buttons. 
	 * 
	 * @param texts	radio button texts (must be the same size as button count)
	 */
	public void setTexts(String[] texts) {
		for (int i=0; i < buttons.length; i++) {
			buttons[i].setText(texts[i]);
		}
	}
	
	
	/**
	 * Sets selection ids for all radio buttons. 
	 * Selection id is the value that is copied from table
	 * to YModel when a button is selected.
	 * 
	 * @param ids the selection ids for each button 
	 * 				(must be the same size as button count)
	 */
	public void setSelectionIds(Object[] ids) {
		this.selectionIds = ids;
	}
	
	/**
	 * Sets text for a radio button.
	 * @param index the radio button index
	 * @param text	the radio button text
	 */
	
	public void setTextAt(int index, String text) {
		buttons[index].setText(text);		
	}
	
	/**
	 * Sets selection id for a radio button.
	 * Selection id is the value that is copied from table
	 * to YModel when a button is selected.
	 * 
	 * @param index the radio button index
	 * @param id	the radio button selection id
	 */
	public void setSelectionIdAt(int index, Object id) {
		selectionIds[index] = id;
	}

	/**
	 * Handles state of other buttons when a button is clicked.
	 *
	 * @param button the clicked button
	 */
	private void buttonClicked(JRadioButton button) {
		for (int i=0; i < buttons.length; i++) {
			JRadioButton b = buttons[i];
			if (b == button) {
				// do nothing 
			} else if (b.isSelected()) {
				b.setSelected(false);
			}
		}
		editor.stopCellEditing();
	}	
	
	/**
	 * @return selection id of the selected radio button
	 */
	public Object getSelectedId() {
		for (int i=0; i < buttons.length; i++) {
			JRadioButton b = buttons[i];
			if (b.isSelected()) {
				return selectionIds[i];
			}
		}
		return null;
	}
	
	/**
	 * @param id selection id of a radio button that will be selected
	 */
	public void setSelectedId(Object id) {
		for (int i=0; i < buttons.length; i++) {
			if (id == null || !id.equals(selectionIds[i])) {
				buttons[i].setSelected(false);
			} else {
				buttons[i].setSelected(true);
			}
		}
	}
	
	/**
	 * @param editor the editor using this group
	 */
	public void setEditor(YTableButtonGroupEditor editor) {
		this.editor = editor;
	}
	
}
