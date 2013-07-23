/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.tools;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessControlException;
import java.text.Collator;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;

import fi.mmm.yhteinen.swing.core.error.YException;
import fi.mmm.yhteinen.swing.core.error.YMethodNotFoundException;


/**
 * Static helper class with common miscellaneous methods. 
 * This class is meant primarily for framework internal use.
 * However, when implementing new components for the framework,
 * this class might be useful.
 * 
 * @author Tomi Tuomainen
 */
public class YCoreToolkit {
	
	/**
	 * Null-safe Comparable comparator. Null (and not-Comparable object) 
	 * is considered to be the lowest value.
	 */
	public static final Comparator COMPARABLE_COMPARATOR= new Comparator() {
        public int compare(Object o1, Object o2) {
        	if (!(o1 instanceof Comparable)) {
        		o1 = null;
        	}
        	if (!(o2 instanceof Comparable)) {
        		o2 = null;
        	}
        	if (o1 == null && o2 == null) {
        		return 0;
        	} else if (o1 == null) {
        		return -1;
        	} else if (o2 == null) {
        		return 1;
        	} else {
        		return ((Comparable) o1).compareTo(o2);
        	}
        }
    };
    
	/**
	 * Null-safe local-sensitive comparator, comparing obj.toString() values. 
	 * 
	 * Null is considered to be an empty string.
	 */
    public static final Comparator LEXICAL_COMPARATOR = new Comparator() {
        public int compare(Object o1, Object o2) {
            String s1 = (o1 == null ? "" : o1.toString());
            String s2 = (o2 == null ? "" : o2.toString());
            if (s1 == null) s1 = "";
            if (s2 == null) s2 = "";
            // using locale-specific Collator:
            Collator c = Collator.getInstance();
            return c.compare(s1, s2);
        }
    };
    
    /**
     * An empty parameter for a method invoked via reflection.
     */
    public static final Object[] EMPTY_PARAM = new Object[0];
   
    /**
     * An empty parameter for a method invoked via reflection.
     */
    public static final Class[] EMPTY_CLASS_PARAM = new Class[0];
    
    /**
     * Removes parameters in methodName: in other words, removes
     * all characters after '(' including '('.
     * 
     * @param methodName
     * @return the method name with no parameters
     */
    private static String removeParameters(String methodName) {
    	int index = methodName.indexOf('(');
		if (index > -1) {
			methodName = methodName.substring(0, index);
		}
		return methodName;
    }
   
	/**
	 * Converts first character of given String to upper case.
	 * 
	 * @param s the String to handle
	 * @return	the String with first character as upper case
	 */
	public static String firstUpper(String s) {
		if (s.length() == 1) {
			return s.toUpperCase();
		} else {
			return s.substring(0, 1).toUpperCase() + s.substring(1);
		}
	}
	
	/**
	 * Reads value of the object via get-method. Object must
	 * have get-method in standard Java bean style for the field.
	 * 
	 * NestedNullException is never raised: if a nested object is null, 
	 * null will be returned. 
	 * 
	 * @param obj 		the object
	 * @param fieldName the field name in the object
	 * @return 			the field value
	 * 
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * 
	 */
	public static Object getBeanValue(Object obj, String fieldName){
		try {
			return PropertyUtils.getProperty(obj, fieldName);
		} catch (NestedNullException nex) {
			return null; // this is ok, parent of required field may be null
		} catch (Exception ex) {
            throw new YException(ex);
        }
	}
	
	/**
	 * Sets value to the object via set-method. Object must
	 * have set- and get-methods in standard Java bean style for the field.
	 * 
	 * @param obj 		the object
	 * @param fieldName the field name in the object
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public static void setBeanValue(Object obj, String fieldName, 
			Object value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		PropertyUtils.setProperty(obj, fieldName, value);
	}	
	
	/**
	 * Compares Collections of Java beans  If both collections contain 
	 * the same objects in the same order, collections are considered equal.
     *
	 * Objects in the collection are the same if the fields are the same 
	 * (equals-method is not used). Method uses EqualsBuilder.reflectionEquals 
	 * to check the fields. 
	 * 
	 * @param c1 the Collection to be compared
	 * @param c2 the Collection to be compared
	 * @return true if Collections are considered equals
	 * 
	 * @see org.apache.commons.lang.builder.EqualsBuilder#reflectionEquals(java.lang.Object, java.lang.Object, boolean, java.lang.Class)
	 */
	public static boolean equalsCollection(Collection c1, Collection c2) {
		if (c1 == null && c2 == null) {
			return true;
		} else if ((c1 == null && c2 != null)
			|| (c1 != null && c2 == null)) {
			return false;
		} else if (c1.size() != c2.size()){
			return false;
		} else {
			Iterator it = c1.iterator();
			Iterator it2 = c2.iterator();
			while (it.hasNext()) {
				Object obj1 = it.next();
				Object obj2 = it2.next();
				if (!EqualsBuilder.reflectionEquals(obj1, obj2, false, Object.class)) {
					return false;
				}
			}
			return true;
        }
	}
	
	/**
	 * Compares Java beans using equals and public get-methods. If equals
	 * implementation is found in the class of currently investigated object,
	 * it is used. If equals is not implemented in the class, all the fields
	 * of the object are investigated using public get-methods. Objects are 
	 * considered equals, if all the values returned by get-methods are equal. 
	 * 
	 * Get-method values are investigated recursively so that nested Java beans
	 * are investigated too (if they don't implement equals-method by themself). 
	 * 
	 * If currently investigated object is a Collection, 
	 * equalsCollection-method is called.
	 * 
	 * @param obj1 the object to be compared
	 * @param obj2 the object to be compared
	 * @return true if objects are considered equal
	 */	
	public static boolean equalsByGetters(Object obj1, Object obj2) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		if (obj1 == null && obj2 == null) {
			return true;
		} else if ((obj1 == null && obj2 != null)
			|| (obj1 != null && obj2 == null)) {
			return false;
		} else if (!obj1.getClass().equals(obj2.getClass())) {
			return false;
		} else {
			// using equals-method if found in class...
			Method equalsMethod = null;
			try {
				equalsMethod = obj1.getClass().getDeclaredMethod("equals", new Class[] {Object.class});
            } catch (Exception ignored) { }
			if (equalsMethod != null) {
				return obj1.equals(obj2);
			} else {
				if (obj1 instanceof Collection) {
					return equalsCollection((Collection) obj1, (Collection) obj2);	
				} else {
					Class class1 = obj1.getClass();
					while (! class1.equals(Object.class)) {
						Method[] methods = class1.getDeclaredMethods();
						for (int i = 0; i < methods.length; i++) {
							Method method = methods[i];
							// if method is a public getter...
							if (method.getName().indexOf("get") == 0 && 
								method.getParameterTypes().length == 0 &&
								method.getModifiers() == Modifier.PUBLIC) {
							
								Object value1 = method.invoke(obj1, EMPTY_PARAM);
								Object value2 = method.invoke(obj2, EMPTY_PARAM);
								
								if (!equalsByGetters(value1, value2)) {
									return false;
								}
							}
						}
						class1 = class1.getSuperclass();
					}    
				}
			}
			return true;
		}
	}


	/**
	 * Compares two objects. If both objects are null, 
	 * true is returned. If the other is null, false is returned.
	 * Otherwise <code>obj1.equals()</code> method is used.
	 * 
	 * @param obj1 the object to be compared
	 * @param obj2 the object to be compared
	 * @return true if objects are considered equals
	 */
	public static boolean equals(Object obj1, Object obj2) {
		return ObjectUtils.equals(obj1, obj2);
	}
	

	/**
	 * Finds a public method of object.
	 * 
	 * @param obj			the object
	 * @param methodName	the name of the method
	 * @param paramClasses	the parameter types of method
	 * @return				the method or null if not found
	 */
	public static Method findMethod(Object obj, String methodName, Class[] paramClasses) {
    	return findMethod(obj, methodName, paramClasses, true);
    }
	
	/**
	 * Finds a method of object.
	 * 
	 * @param obj			the object
	 * @param methodName	the name of the method
	 * @param paramClasses	the parameter types of method
	 * @param publicOnly	if only public methods should be returned
	 * @return				the method or null if not found
	 */
	public static Method findMethod(Object obj, String methodName, Class[] paramClasses, boolean publicOnly) {
    	if (paramClasses == null) paramClasses = EMPTY_CLASS_PARAM;
		Class aClass = obj.getClass();
		// might end up in Object class if any getter doesn't match...
		while (! aClass.equals(Object.class)) {
			try {
			Method[] methods = aClass.getDeclaredMethods();
			for (int i = 0; i < methods.length; i++) {
				Method method = methods[i];
				String currentName = YCoreToolkit.removeParameters(method.getName());
				if (currentName.equals(methodName) && 
					(!publicOnly ||	method.getModifiers() == Modifier.PUBLIC) &&
					method.getParameterTypes().length == paramClasses.length) {
					boolean match = true;
					for (int j=0; j < paramClasses.length && match; j++) {
						if (!method.getParameterTypes()[j].isAssignableFrom(
								paramClasses[j])) {
							match = false;
						}
					}
					if (match) {
						return method;
					}
				}
			}
			aClass = aClass.getSuperclass();
			} catch (AccessControlException ex) {
				// if SecurityManager prevents access, exiting the loop...
				aClass = Object.class;
			}
		}	
		return null;
    }
   
	
	/**
	 * Invokes a method in an object.
	 * 
	 * @param obj 		 	the object which method is invoked
	 * @param methodName 	the name of the method
	 * @param params	 	the parameters for the method
	 * @param paramClasses	the parameter classes
	 * @param publicOnly	if only public methods should be invoked
	 * 
	 * @return			 the return value of the method, 
	 * 
	 * @throws YMethodNotFoundException	it method was not found
	 * @throws InvocationTargetException exception in method call via reflection
	 * @throws IllegalAccessException	 exception in method call via reflection
	 * @throws IllegalArgumentException  exception in method call via reflection
	 */
    public static Object invokeMethod(
    		Object obj, String methodName, Object[] params, Class[] paramClasses, boolean publicOnly) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, YMethodNotFoundException {
    	if (params == null) params = EMPTY_PARAM;
    	Method method = findMethod(obj,methodName, paramClasses, publicOnly);
    	if (method == null) {
    		throw new YMethodNotFoundException(obj, methodName, paramClasses );
    	} else {
    		YCoreToolkit.tryAccessible(method);
    		return method.invoke(obj, params);
    	}
    }
    
	/**
	 * Invokes a public method in an object.
	 * 
	 * @param obj 		 	the object which method is invoked
	 * @param methodName 	the name of the method
	 * @param params	 	the parameters for the method
	 * @param paramClasses	the parameter classes
	 * 
	 * @return			 the return value of the method, 
	 * 
	 * @throws YMethodNotFoundException	it method was not found
	 * @throws InvocationTargetException exception in method call via reflection
	 * @throws IllegalAccessException	 exception in method call via reflection
	 * @throws IllegalArgumentException  exception in method call via reflection
	 */
    public static Object invokeMethod(
    		Object obj, String methodName, Object[] params, Class[] paramClasses) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, YMethodNotFoundException {
    	return invokeMethod(obj, methodName, params, paramClasses, true);
    }
   
  
    /**
     * Copies an object by iterating fields recursively via
     * get- and set-methods. Object may contain also Collections.
     * 
     * @param obj the object to copy
     * @return the copy of the object
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws IllegalArgumentException
     * @throws YMethodNotFoundException
     */
    public static Object deepCopyObject(Object obj) throws YMethodNotFoundException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException  {
        if (obj instanceof Collection) {
            return copyCollection ((Collection) obj);
        } else {
            return copyContents(obj);
        }
    }
    
    /**
      * Copies contents of Collection recursively by using
      * get- and set-methods.
      * 
      * @param source the collection to copy
      * @return the copy
      */
    private static Collection copyCollection(Collection source) {
        try {
            Collection copy = (Collection) source.getClass().newInstance();
            Iterator it = source.iterator();
            while (it.hasNext()) {
                Object obj = it.next();
                Object copyObj = copyContents(obj);
                copy.add(copyObj);
            }
            return copy;
        } catch (Exception ex) {
        	throw new YException(ex);
        }
    }
    
    /**
     * Copies contents of object by using get- and set-methods.
     * 
     * @param  source 	the object to copy
     * @return the copy
     */
    private static Object copyContents(Object source) throws InstantiationException, YMethodNotFoundException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Object copy = source.getClass().newInstance();
        Method[] methods = source.getClass().getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            if (method.getName().indexOf("get") == 0 && 
                method.getParameterTypes().length == 0 &&
				method.getModifiers() == Modifier.PUBLIC) {
            	Object value = method.invoke(source, EMPTY_PARAM);
    			Class valueClass = method.getReturnType();
                String fieldName = method.getName().substring(3);
                if (valueClass.equals(Collection.class)) {
                    Collection copiedCollection = copyCollection((Collection) value);
                    setBeanValue(copy, fieldName, copiedCollection);
                } else {
                    setBeanValue(copy, fieldName, value);
                }
            }
        }
        return copy;
    }
    
    /**
     * Sets method accessible for reflection execution, if 
     * SecurityManager allows it. If accessible cannot be
     * set, no exception is thrown.
     * 
     * @param method the method to be set accessible
     */
    public static void tryAccessible(Method method) {
    	try {
    		method.setAccessible(true);
    	} catch (SecurityException ignored) {
    		
    	}
    }

	
}