/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.worker;

import java.awt.Dialog;
import java.awt.Frame;
import fi.mmm.yhteinen.swing.core.YIComponent;
import fi.mmm.yhteinen.swing.core.component.YButton;
import fi.mmm.yhteinen.swing.core.component.YDialog;
import fi.mmm.yhteinen.swing.core.component.YPanel;
import fi.mmm.yhteinen.swing.core.component.label.YLabel;

/**
 * A dialog for YDefaultWorkerListener. If YDefaultWorkerListener is used,
 * dialog texts can be set via static methods of this class.
 * However, probably an application wants to customise its own dialog, which is
 * done via implementing a new YWorkerListener (-> this class will be useless).
 * 
 * @author Tomi Tuomainen
 */
public class YDefaultWorkerDialog extends YDialog  {

	private static String titleString = "";
	private static String cancelString = "Cancel";
	private static String infoString = "Executing worker...";
	
	private YButton buttonCancel = new YButton(cancelString);
	
	public YDefaultWorkerDialog() {
		super();
		init();
	}
	/**
	 * @param owner	the owner of this dialog
	 */
	public YDefaultWorkerDialog(Dialog owner) {
		super(owner);
		init();
	}

	/**
	 * @param owner the owner of this dialog
	 */
	public YDefaultWorkerDialog(Frame owner) {
		super(owner);
		init();
	}
	
	/**
	 * Initialized the dialog UI.
	 */
	private void init() {
		addMVCNames();
		addComponents();
		this.setSize(170, 100);
		setLocationRelativeTo(getOwner());
		this.setTitle(titleString);	
		this.setModal(true);
	}
	
	
	/**
	 * Setting names for the components for YController 
	 * (implemented inside YDefaultWorkerListener).
	 */
	private void addMVCNames() {
		buttonCancel.getYProperty().put(YIComponent.MVC_NAME, "buttonCancel");
		getYProperty().put(YIComponent.MVC_NAME, "dialog");
	}
	
	/**
	 * Adds components to dialog.
	 *
	 */
	private void addComponents() {
		YPanel basePanel = new YPanel();
		basePanel.add(new YLabel(infoString));
		basePanel.add(buttonCancel);
		this.setContentPane(basePanel);
	}
	
	public YButton getButtonCancel() {
		return buttonCancel;
	}

	public static String getCancelString() {
		return cancelString;
	}
	
	public static void setCancelString(String cancelString) {
		YDefaultWorkerDialog.cancelString = cancelString;
	}
	
	public static String getInfoString() {
		return infoString;
	}

	public static void setInfoString(String infoString) {
		YDefaultWorkerDialog.infoString = infoString;
	}

	public static String getTitleString() {
		return titleString;
	}
	
	public static void setTitleString(String titleString) {
		YDefaultWorkerDialog.titleString = titleString;
	}
}
