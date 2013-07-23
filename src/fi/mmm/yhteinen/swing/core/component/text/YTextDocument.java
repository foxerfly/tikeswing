/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.component.text;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * A document that..
 * <ul>
 * <li>checks max length of the text to be inserted</li>
 * <li>converts text to upper case if YITextDocument method <code>isUpper</code> returns true</li>
 * </ul>
 * 
 * @author Tomi Tuomainen
 */
public class YTextDocument extends PlainDocument {
    
	private YITextComponent textComponent;
	
	public YTextDocument(YITextComponent comp) {
		textComponent = comp;
	}
	
    /**
     * Checks if text can be added to text component of this
     * document.
     * 
     * @param currentString	the current string in text component
     * @param strToAdd the string to be inserted in text component
     * @return true if the new string wouldn't be longer than max length
     */
    private boolean newMaxLengthValid(String currentString, String stringToAdd) {
    	int max = textComponent.getMaxLength();
    	if (max > -1) {
    		int newLength = currentString.length()+stringToAdd.length();
			if (newLength > max) {
				return false;
			} 	
    	}
    	return true;
    }
    /*
     *  (non-Javadoc)
     * @see javax.swing.text.Document#insertString(int, java.lang.String, javax.swing.text.AttributeSet)
     */
	public void insertString(int offs, String strToAdd, AttributeSet a)
		throws BadLocationException {
		if (strToAdd == null) return;
        StringBuffer source = new StringBuffer(strToAdd);
		String currentString = getText(0, this.getLength());
        for (int i = source.length(); i > 0; i--) {
            strToAdd = source.substring(0, i);
            if (newMaxLengthValid(currentString, strToAdd)) {
                if (textComponent.isUpper()) {
                    strToAdd = strToAdd.toUpperCase();
                } 
                super.insertString(offs, strToAdd, a);
                break;
            }
        }
	}

} // class YTextDocument


