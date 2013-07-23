/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.error;

/**
 * This exception is thrown if some method that framework tried to 
 * invoke via reflection is not found. In YController this is
 * not considered as an error, since YFramework by default
 * does not require you to implement event methods in controllers. 
 * However, this exception can be caught in YController error handler to decide,
 * what to do with it.
 * 
 * @author Tomi Tuomainen
 */
public class YMethodNotFoundException extends YException {

	/**
	 * @param source			the source object
	 * @param methodName 		the method that is not found
	 * @param paramClasses		the method parameter classes
	 */
	public YMethodNotFoundException(Object source, String methodName, Class[] paramClasses) {
		super(createMessage(source, methodName, paramClasses));
	}
	
	private static String createMessage(Object source, String methodName, Class[] paramClasses) {
		String params = "(";
		if (paramClasses != null) {
			for (int i=0; i < paramClasses.length; i++) {
				if (i>0) params+= ",";
				params += paramClasses[i].getName();
			}
		}
		params += ")";
		return "Method " +methodName + params + " not found in " + source.getClass();
	}
	
}
