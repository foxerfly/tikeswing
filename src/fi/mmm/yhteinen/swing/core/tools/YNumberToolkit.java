/*
 * Created on 30.12.2004
 *
 */
package fi.mmm.yhteinen.swing.core.tools;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

/**
 * Helper class for numeric conversions. Framework numeric
 * components use this class (YDecimalNumberField, YDoubleField,
 * YFloatfield,  YWholeNumberField, YIntegerField, YLongField)
 * (for framework components). 
 * 
 * The  class uses machine default Locale. 
 * 
 * @author Tomi Tuomainen
 *
 */
public class YNumberToolkit {

    /**
    * the number formatter
    */
    private static NumberFormat numberFormat =
        NumberFormat.getNumberInstance(Locale.getDefault());
    {
    	numberFormat.setParseIntegerOnly(false);
    }
    
    
    /**
     * Creates Number object from a String representing a whole number.
     *
     * @param	s 	the whole number as String object
     * @return  the Number or null if String was not valid number
     */
    public static Number parseWholeNumber(String s)  {
    	if (s == null || s.equals("")) return null;
    	try {
    		
    		YNumberToolkit.numberFormat.setMaximumFractionDigits(0);
    		YNumberToolkit.numberFormat.setMinimumFractionDigits(0);
    		return numberFormat.parse(s);
    	} catch (ParseException e) {
    		return null;
    	}
    }
    
    /**
     * Creates a String presentation of a whole number. 
     *
     * @param number		the Number to convert
     * @param useGrouping	is grouping used in formatting
     * @return  			the whole number as String
     */
    public static String formatWholeNumber(Number number, boolean useGrouping) {
    	if (number == null) return "";
        numberFormat.setMaximumFractionDigits(0);
        numberFormat.setMinimumFractionDigits(0);
        numberFormat.setGroupingUsed(useGrouping);
        return numberFormat.format(number.longValue());
    }
    
    
    /**
     * Creates a String presentation of a decimal number. 
     *
     * @param number			the Number to convert
     * @param fractionDigits	the number of fraction digits in result String
     * @param useGrouping		is grouping used in formatting
     * @return  				the decimal number as String
     */ 
    public static String formatDecimalNumber(Number number, int fractionDigits, boolean useGrouping) {
    	if (number == null) return "";
        numberFormat.setMaximumFractionDigits(fractionDigits);
        numberFormat.setMinimumFractionDigits(fractionDigits);
        numberFormat.setGroupingUsed(useGrouping);
        return numberFormat.format(number.doubleValue());
    }
    
    
    /**
     * Creates Number object from a String representing a decimal number.
     *
     * @param s					the decimal number as String
     * @param fractionDigits	the number of decimals in the String 
     * 							(the rest of the decimals will be ignored)
     * @return  the Number or null if String was not valid number
     */
    public static Number parseDecimalNumber(String s, int fractionDigits) {
		if (s == null || s.equals("")) return null;
		// transferring commas to periods, since numberFormat doesn't seem to handle it:
	//	s = s.replaceAll("[,]",".");
		try {
			numberFormat.setParseIntegerOnly(false);
			numberFormat.setMaximumFractionDigits(fractionDigits);
	        numberFormat.setMinimumFractionDigits(fractionDigits);
	        Number number = numberFormat.parse(s);
			return number;
		} catch (Exception ex) {
			return null;
		}
	}

}
