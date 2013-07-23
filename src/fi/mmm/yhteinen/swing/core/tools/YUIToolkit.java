/* Copyright (C) 2005 Ministry of Agriculture and Forestry of Finland */
package fi.mmm.yhteinen.swing.core.tools;

import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.text.JTextComponent;

import org.apache.log4j.Logger;

import fi.mmm.yhteinen.swing.core.YController;
import fi.mmm.yhteinen.swing.core.YIComponent;
import fi.mmm.yhteinen.swing.core.YIControllerComponent;
import fi.mmm.yhteinen.swing.core.YIModelComponent;
import fi.mmm.yhteinen.swing.core.YISharedModelComponent;
import fi.mmm.yhteinen.swing.core.YModel;
import fi.mmm.yhteinen.swing.core.component.YTabbedPane;
import fi.mmm.yhteinen.swing.core.error.YException;
import fi.mmm.yhteinen.swing.core.error.YInvalidMVCNameException;

/**
 * Helper class with common user interface related methods. 
 * 
 * @author Tomi Tuomainen
 */
public class YUIToolkit {
	
	private static Window currentWindow;
	private static Logger logger = Logger.getLogger(YUIToolkit.class);
	
    /**
     * An empty parameter for any method invoked via reflection.
     */
    private static final Object[] EMPTY_PARAM = new Object[0];

    
    /**
     * Finds the parent window of given Component.
     * 
     * @param comp the component
     * @return the parent window or null if the component is not in a Window
     */
    public static Window findParentWindow (Component comp) {
        Container parent = comp.getParent();
        while (parent != null && !(parent instanceof Window)) {
            parent = parent.getParent();
        }
        if (parent != null) {
            return (Window) parent;
        } else {
            return null;
        }
    }


	/**
	 * Checks if given Component is JFrame or JDialog and
	 * returns the content pane of Window. If Component is not
	 * JDialog nor JFrame, the Component itself is returned. 
	 * 
	 * @param comp the Component 
	 * @return			the component's content pane, or component itself 
	 * 					(if content pane doesn't exist)
	 */
	public static Component getContentPane(Component comp) {
		if (comp instanceof JFrame) {
			return ((JFrame)comp).getContentPane();
		} else if (comp instanceof JDialog) {
			return ((JDialog)comp).getContentPane();
		}
		return comp;
	}

	/**
	 * Gets YProperty of given YIComponent with given property name
	 * and checks if the property value is a Boolean value "true".
	 * If property is null or not a Boolean, false is returned.
	 * 
	 * @param comp	the component
	 * @param propertyName	the name of the assumed Boolean property
	 * @return				if property is Boolean true
	 */
	public static boolean isPropertyTrue(YIComponent comp, 
				String propertyName) {
		Object property =  comp.getYProperty().get(propertyName);
		if (property == null || ! (property instanceof Boolean)) {
			return false;
		} else {
			return ((Boolean)property).booleanValue();
		}
	}

	/**
	 * Sets YProperty for given Component, if Component is instanceof YIComponent.
	 * Method sets given YProperty recursively also for children of given 
	 * component.
	 * 
	 * @param parent	the parent component to spread YProperty
	 * @param property	the property
	 */
	public static void spreadYProperty(Component parent, YProperty property) {
		if (parent instanceof YIComponent) {
			((YIComponent)parent).getYProperty().putAll(property);
		}
		if (parent instanceof Container) {
			parent = getContentPane(parent);
			Component[] children = ((Container) parent).getComponents();
			for (int i=0; i < children.length; i++) {
				Component child = children[i];
				spreadYProperty(child, property);
			}
		}
	}
	
	/**
	 * Sets YProperty for given controller and recursively to all child
	 * controllers. 
	 * 
	 * @param controller the controller
	 * @param propertyName	the property name
	 * @param propertyValue	the property value
	 */
	public static void spreadYProperty(YController controller, 
			String propertyName, Object propertyValue) {
		controller.getYProperty().put(propertyName, propertyValue);
		Collection children = controller.getChildren();
		Iterator it = children.iterator();
		while (it.hasNext()) {
			spreadYProperty((YController) it.next(), propertyName, propertyValue);
		}
	}
	
	/**
	 * Checks that a model has appropriate get-methods
	 * corresponding to the MVC names of view components. All 
	 * the YIModelComponents in given view are checked. 
	 * This methods handles only MVC names with dots, other
	 * bean notations (name[index], name("key")) are not checked 
     *
	 * 
	 * @param model the view model
	 * @param view	the view
	 * @throws YInvalidMVCNameException
	 * 				if YIModelComponent of the view has MVC-name
	 * 				that doesn't match any getter in view model
	 */
	public static void checkGetMethodNames(Object model, YIComponent view) throws YInvalidMVCNameException {
		ArrayList comps = YUIToolkit.getViewComponents(view);
		Iterator it = comps.iterator();
		String falseNames = "";
		while (it.hasNext()) {
			Object comp = (Object) it.next();
			// checking only components which are connected to the model:
			if (comp instanceof YIModelComponent) {
				String mvcName = (String) ((YIComponent) comp).getYProperty().get(YIComponent.MVC_NAME);
				if (mvcName != null) {
					try {
						String nameToCheck = null;
						int i = mvcName.indexOf('(');
						if (i == -1) {
							i = mvcName.indexOf('[');
						}
						if (i != -1) {
							// not checking indexed or mapped parts of mvc name...
							nameToCheck = mvcName.substring(0, i);
						} else {
							nameToCheck = mvcName;
						}
						
						Class modelClass = model.getClass();
						int pos = nameToCheck.indexOf('.');
						// checking nested getters for parent objects:
						while (pos > -1) {
							String parentName = nameToCheck.substring(0, pos);
							nameToCheck = nameToCheck.substring(pos+1);
							String methodName = "get" + YCoreToolkit.firstUpper(parentName);
							Method method = modelClass.getMethod(methodName, null);
							modelClass = method.getReturnType();
							pos = nameToCheck.indexOf('.');
						}
					} catch (Exception e) {
						falseNames += mvcName + " ";
						
					}
				}
			}
		}
		if (falseNames.length() > 0) {
			throw new YInvalidMVCNameException(falseNames,  view.getClass()+"");
		}
	}
	
	/**
	 * Sets up MVC structure. Sets object references that YFramework
	 * requires between model, view and controller. In other words,
	 * this method takes care that controller.getModel(), 
	 * controller.getView(), view.getModel() and 
	 * view.getController() work correctly. 
	 * 
	 * The method checks getters in the model if <code>checkGetMethodNames</code>
	 * is true. Possible <code>YInvalidMVCNameException</code> is logged as error.
     * 
     *  @deprecated use YController setUpMVC-method
     *  
	 * @param model 			  the view model (any Object), may be null
	 * @param view				  the view, may not be null
	 * @param controller 		  the YController, may not be null
	 * @param checkGetMethodNames if true, getters of model are checked for each YIModelComponent
	 */
	public static void setUpMVC(Object model, YIComponent view, YController controller, 
				boolean checkGetMethodNames) {
		if (checkGetMethodNames && model != null) {
			try {
				checkGetMethodNames(model, view);
			}  catch (YInvalidMVCNameException e) {
				logger.error(e);
			}
		}
		controller.setView(view);
		controller.setModel(model);
	}
	
	/**
	 * Sets up MVC structure. Checks also getters in the given model.
	 * @deprecated use YController setUpMVC-method
     * 
	 * @see #setUpMVC(Object, YIComponent, YController, boolean)
	 * 
	 * @param model 	the view model (any Object), may be null
	 * @param view		the view, may not be null
	 * @param controller the YController, may not be null
	 */
	public static void setUpMVC(Object model, YIComponent view, YController controller) {
		setUpMVC(model, view, controller, true);
	}
	
	/**
	 * When MVC structure has been set up, this method can be used
	 * to update model object in the triangle. 
	 * 
	 * @param model 	the view model (any Object), may be null
	 * @param view		the view, may not be null
	 * @param controller the YController, may not be null
	 */
	public static void updateModel(Object model, YIComponent view, YController controller) {
		view.getYProperty().put(YIComponent.MODEL, model);
		controller.setModel(model);
	}
    
    /**
     * Sets enabled state for all Components found in given view.
     * 
     * @param view              the view
     * @param enabled  if view should be enabled
     */
    public static void setViewEnabled(YIComponent view, boolean enabled) {
        setViewEnabled(view, enabled, null);
    }

    /**
     * Sets enabled state for all Components found in given view.
     * 
     * @param view                  the view
     * @param enabled              if view should be enabled
     * @param compClassesToIgnore   classes of components that are ignored in this method
     */
    public static void setViewEnabled(YIComponent view, boolean enabled, Class[] compClassesToIgnore) {
        setViewEnabled(view, enabled, compClassesToIgnore, null);
    }
    /**
     * Sets editable state for all JTextComponents found in given view.
     * 
     * @param view                 the view
     * @param enabled             if view should be enabled
     * @param compClassesToIgnore classes of components that are ignored in this method
     * @param compsToIgnore        components that are ignored in this method
     */
    public static void setViewEnabled(YIComponent view, boolean enabled, 
                Class[] compClassesToIgnore, JTextComponent[] compsToIgnore) {
        ArrayList comps = getViewComponents(view);
        Iterator it = comps.iterator();
        while (it.hasNext()) {
            Object obj = it.next();
            if (obj instanceof Component) {
                boolean ignore=false;
                if (compsToIgnore != null) {
                    // checking components to ignore...
                    for (int i=0; i < compsToIgnore.length && !ignore; i++) {
                        if (compsToIgnore[i] == obj) {
                            ignore = true;
                        } 
                    }
                } 
                if (compClassesToIgnore != null) {
                    // checking component classes to ignore..
                    for (int i=0; i < compClassesToIgnore.length && !ignore; i++) {
                        if (compClassesToIgnore[i].isAssignableFrom(obj.getClass())) {
                            ignore = true;
                        } 
                    }
                }
                if (!ignore) {
                    ((Component) obj).setEnabled(enabled);
                }
            }
        }
    }
	/**
	 * Sets editable state for all JTextComponents found in given view.
     * 
	 * @param view		the view
	 * @param editable	if view should be editable
	 */
	public static void setViewEditable(YIComponent view, boolean editable) {
		setViewEditable(view, editable, null);
	}

    /**
     * Sets editable state for all JTextComponents found in given view.
     * 
     * @param view                  the view
     * @param editable              if view should be editable
     * @param compClassesToIgnore   classes of components that are ignored in this method
     */
    public static void setViewEditable(YIComponent view, boolean editable, Class[] compClassesToIgnore) {
         setViewEditable(view, editable, compClassesToIgnore, null);
    }
	/**
	 * Sets editable state for all JTextComponents found in given view.
	 * 
	 * @param view		           the view
	 * @param editable	           if view should be editable
	 * @param compClassesToIgnore classes of components that are ignored in this method
	 * @param compsToIgnore        components that are ignored in this method
	 */
	public static void setViewEditable(YIComponent view, boolean editable, 
                Class[] compClassesToIgnore, JTextComponent[] compsToIgnore) {
		ArrayList comps = getViewComponents(view);
		Iterator it = comps.iterator();
		while (it.hasNext()) {
			Object obj = it.next();
			if (obj instanceof JTextComponent) {
				boolean ignore=false;
				if (compsToIgnore != null) {
                    // checking components to ignore...
                    for (int i=0; i < compsToIgnore.length && !ignore; i++) {
                        if (compsToIgnore[i] == obj) {
                            ignore = true;
                        } 
                    }
                } 
                if (compClassesToIgnore != null) {
                    // checking component classes to ignore..
                    for (int i=0; i < compClassesToIgnore.length && !ignore; i++) {
			            if (compClassesToIgnore[i].isAssignableFrom(obj.getClass())) {
							ignore = true;
						} 
					}
				}
                if (!ignore) {
					((JTextComponent) obj).setEditable(editable);
				}
			}
		}
	}
	

	/**
	 * Adds property values into components with given property name.
	 * The mapping parameter is two dimensional array, each row holding
	 * an array of component-value pair. So the second element of this
	 * row array holds the property value which is set to the component
	 * in first element. 
	 * 
	 * @param propertyName the name of the property to set
	 * @param mapping	   the array of {Component, Object}
	 */
	public static void addYProperties(String propertyName, Object [][] mapping) {
		for (int i=0; i < mapping.length; i++) {
			YIComponent comp = (YIComponent) mapping[i][0];
			Object propertyValue  = mapping[i][1];
			comp.getYProperty().put(propertyName, propertyValue);
		}	
	}
	
	/**
	 * Convenience method for getting reference to the controller
	 * of the view.
	 * 
	 * @param view	the view component
	 * @return	the YProperty CONTROLLER of given component
	 */
	public static YController getController(YIComponent view) {
		return (YController) view.getYProperty().
			get(YIComponent.CONTROLLER);
    	
	}
	
	/**
	 * Convenience method for getting reference to the model
	 * of the view.
	 * 
	 * @param view	the view component
	 * @return	the YProperty MODEL of given component
	 */
	public static YModel getModel(YIComponent view) {
		return (YModel) view.getYProperty().
			get(YIComponent.MODEL);
    	
	}
	
     /**
     * Creates a method name for controller event method.
     * The name is based on components MVC name, however only
     * letters and digits are accepted in the method name. 
     * This means special characters nested, indexed and mapped 
     * notations are ignored. However, a letter after
     * a special character is converted to upper case. 
     * <p>
     * 
     * Example <br>
     *  MVC_NAME: <code>company.customer[1].address("home")</code><br>
     *  suffix: <code>Changed</code><br>
     *  method returns: <code>companyCustomer1AddressHomeChanged</code><br>
     * 
     * @param comp           the component
     * @param suffix         the suffix for the method
     * @return               the method name
     */ 
      public static String createMVCMethodName(YIComponent comp, String suffix) {
          String mvcName = (String) comp.getYProperty().get(YIComponent.MVC_NAME);
          return createMVCMethodName (comp, mvcName, suffix);
      }
   
      /**
       * Creates a method name for controller event method.
       * The name is based on given fieldName, however only
       * letters and digits are accepted in the method name. 
       * This means special characters nested, indexed and mapped 
       * notations are ignored. However, a letter after
       * a special character is converted to upper case. 
       * <p>
       * 
       * Example <br>
       *    MVC_NAME: <code>company.customer[1].address("home")</code><br>
       *  suffix: <code>Changed</code><br>
       *    method returns: <code>companyCustomer1AddressHomeChanged</code><br>
       * 
       * @param comp             the component
       * @param suffix           the suffix for the method
       * @param fieldName        MVC-name or YIExtendedModelComponent field name
       * @return                 the method name
       */ 
    public static String createMVCMethodName(YIComponent comp, String fieldName, String suffix) {
    	if (fieldName == null)  {
    		return null;
    	} else {
    		char[] name = fieldName.toCharArray();
    		StringBuffer result = new StringBuffer();
    		boolean nextUpper = false;
    		for (int i=0; i < name.length; i++) {
    			char ch = name[i];
    			// if character is accepted in the method name...
    			if (Character.isLetterOrDigit(name[i])) {
    				if (nextUpper) {
    					ch = Character.toUpperCase(ch);
    					nextUpper = false;
    				}
    				result.append(ch);
    			} else {
    				// not accepted character...
    				nextUpper = true;
    			}
    		}
    		String s = (result + suffix);
    		return s;
    	}
    }
    
    /**
     * Checks that given Component is NOT a tabbed pane tab 
     * that is not currently selected.
     *
     * @param comp	 the component to be checked
     * @return false if Component is in a tab that is not selected; 
     * 					otherwise true 
     * 				  
     */
    public static boolean currentlyVisible (Component comp) {
    	Component parent = comp.getParent();
    	Component current = comp;
    	while (parent != null) {
    		if (parent instanceof YTabbedPane) {
    			YTabbedPane tabbedPane = (YTabbedPane) parent;
    			if (tabbedPane.getSelectedComponent() != current) {
    				return false;
    			} 
    		}
    		current = parent;
    		parent = parent.getParent();
    	}
    	return true;
    }
    
    /**
     * Checks if given class is YFramework core component 
     * (YPanel, YFrame, YTextfield etc). If the class is 
     * extending core component, false is returned. If the class is
     * core component itself, true is returned.
     * 
     * @param aClass	the class to be investigated
     * @return			is class YFramework core component
     */
    public static boolean isCoreComponent (Class aClass) {
    	Class[] interfaces = aClass.getInterfaces();
		for (int i=0; i < interfaces.length; i++) {
				Class iface = interfaces[i];
				if (iface.equals(YIComponent.class) || 
						iface.equals(YIModelComponent.class) ||
						iface.equals(YIControllerComponent.class) 
						|| iface.equals(YISharedModelComponent.class)) {
					return true;
				}
		}
		return false;
    }
    
    /**
     * Returns of components of a view class. If given view
     * has COMPONENT_LIST property that holds a Collection 
     * or YIComponent array, that list is returned. Otherwise,
     * method checks view and it's super classes and returns all
     * YIComponents found via get-methods. 
     *  
     * @param view	the view holding the components
     * @return		components of the view
     */
    public static ArrayList getViewComponents(YIComponent view) {
    	ArrayList result = new ArrayList();
    	// checking if view is using YProperty COMPONENT_LIST...
    	Object componentList = view.getYProperty().get(YIComponent.COMPONENT_LIST);
    	if (componentList != null) {
    		if (componentList instanceof Collection) {
    			return new ArrayList((Collection)componentList);
    		} else if (componentList instanceof YIComponent[]) {
    			YIComponent[] comps = (YIComponent[]) componentList;
    			for (int i = 0; i < comps.length; i++) {
    				result.add(comps[i]);
    			}
    		} else {
    			throw new YException("View " + view.getClass() + " has COMPONENT_LIST " + componentList.getClass() + ". " 
    					+ "COMPONENT_LIST must be Collection or array of YIComponents. ");
    		}
    	} else {
    		// no COMPONENT_LIST specified, investigating getters via reflection...
    		Class viewClass = view.getClass();
    		// while class object is instanceof YIComponent...
    		while (YIComponent.class.isAssignableFrom(viewClass)) {
    			
    			Method[] methods = viewClass.getDeclaredMethods();
    			for (int i = 0; i < methods.length; i++) {
    				Method method = methods[i];
    				if (method.getName().indexOf("get") == 0 && 
    						method.getParameterTypes().length == 0 &&
							method.getModifiers() == Modifier.PUBLIC &&
                            YIComponent.class.isAssignableFrom(method.getReturnType())) {
    					try {
    						Object obj  = method.invoke(view, EMPTY_PARAM);
    						if (obj instanceof YIComponent) {
    							result.add(obj);
    						}
    					} catch (Exception e) {
    						throw new YException(e);
    					}
    				}
    			}
				viewClass = viewClass.getSuperclass();
    		}
    	}
    	return result;
    }
	
	/**
	 * @param window	currently active window
	 */
	public static void setCurrentWindow(Window window) {
		currentWindow = window;
	}
	
	/**
	 * Returns currently active window. This method works with
	 * applications using YFrame and YDialog, which update
	 * currently active window of YUIToolkit.
	 *
	 * @return currently active window
	 */
	public static Window getCurrentWindow() {
		return currentWindow;
	}

	/**
	 * Adds COMPONENT_LIST property to a view. This method does not handle 
	 * upper classes of given view. 
	 * 
	 * @param view			the view to be initialized
	 * @see #guessViewComponents(YIComponent, Class)
	 */
	public static void guessViewComponents(YIComponent view) {
		guessViewComponents(view, null);
	}
	
	/**
	 * Adds COMPONENT_LIST property to a view. When using this method, it is not necessary
	 * to code get-methods for each view component. 
	 * <p>
	 * The method uses reflection to dig out all YIComponent fields of the given view. 
	 * Therefore, SecurityManager must allow accessing private fields
	 * via reflection when using this method! Use log4j info level to check out, which 
	 * view components are actually added to the COMPONENT_LIST.
	 * <p>
	 * If upperClass parameter is null, only fields of the given view class are handled.
	 * If upperClass parameter is given, also upper classes are handled (until given upperClass is handled).
	 * 
	 * @param view			the view to be initialized
	 * @param upperClass	the highest upper class to be handled, may be null
	 */
	public static void guessViewComponents(YIComponent view, Class upperClass) {
		ArrayList comps = new ArrayList();
		Class viewClass = view.getClass();
		boolean search = true;
		while (search) {
			Field[] fields = viewClass.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				Field field = fields[i];
				if (YIComponent.class.isAssignableFrom(field.getType())) {
					try {
						field.setAccessible(true);
						YIComponent comp = (YIComponent) field.get(view);
                        if (comp != null) {
                            logger.info("Guessing component: view= " + viewClass + ", component=" + field.getName());
                            comps.add(comp);
                        }
					} catch (Exception e) {
						logger.warn("View component could not be guessed for " + view.getClass() + 
								", component " + field.getName() + ". ");
					}
				}
			}
			search = (upperClass != null && !upperClass.equals(viewClass));
			viewClass = viewClass.getSuperclass();
		} 
		view.getYProperty().put(YIComponent.COMPONENT_LIST, comps);
	}
	
	
	/**
	 * Handles conversion of a field name into MVC_NAME. This method
	 * implements "nested field names guessing" 
	 * (upper case characters will be converted to nested bean notations 
	 * and digits will be converted to array notations)
	 * 
	 * @param mvcName	the name to be converted
	 * @return			the name with nested notations
	 */
	private static String guessNested(String mvcName) {
		StringBuffer result = new StringBuffer(mvcName.length());
		char[] source = mvcName.toCharArray();
		for (int i =0; i < source.length; i++) {
			char ch = source[i];
			if (Character.isUpperCase(ch)) {
				// customerName -> customer.name
				result.append("." + Character.toLowerCase(ch));
			} else if (Character.isDigit(ch)) {
				// customers12Name -> customers[12].name
				result.append('[');
				result.append(ch);
				i++;
				boolean found = false;
				// adding other digits until letter is found...
				for ( ; !found && i < source.length; i++) {
					ch = source[i];
					if (Character.isDigit(ch)) {
						result.append(ch);
					} else {
						// closing the digit part...
						result.append(']'); // closing the digit part
						result.append('.'); // adding the separator
						result.append(Character.toLowerCase(ch)); // adding the letter
						found = true; // exiting to outer loop...
						i--; // moving back for the outer loop...
					}
				}
				// adding ']' if the end was reached..
				if (i==source.length) {
					result.append(']');
				}
			} else {
				result.append(ch);
			}
		}
		return result.toString();
	}

	/**
	 * Sets MVC_NAME property for each YICOMPONENT of a view. This method does not handle 
	 * upper classes of the given view class.
	 * 
	 * @param view			the view to be initialized
	 * @param guessNested	if nested components guessing is used (upper case characters
	 * 						will be converted to nested bean notations and digits will be 
	 * 						converted to array notations)
	 * @param formatter		the formatter for application specific rules, may be null
	 * 
	 * @see #guessMVCNames(YIComponent, boolean, YFormatter, Class)
	 */ 
	public static void guessMVCNames(YIComponent view, boolean guessNested, YFormatter formatter) {
		guessMVCNames(view, guessNested, formatter, null);
	}
	
	
	/**
	 * Sets MVC_NAME property for each YICOMPONENT of a view. The method 
	 * guesses MVC_NAMEs using field names of the given view class. 
	 * By default, MVC_NAME will be exactly the name of a field. 
	 * <p>
	 * If parameter guessNested is true, the method sets MVC_NAMES as follows: 
	 * <ul>
	 * <li>letters in upper case will be converted to nested bean notations, for example
	 * field named <code>customerName</code> will have MVC_NAME <code>customer.name</code></li>
	 * <li>digits will be converted to array notations, for example field named <code>customers12Name</code>
	 * will have MVC_NAME <code>customers[12].name</code></li>
	 * </ul>
	 * <p>
	 * Formatter parameter may be specified for application specific conversion rules 
	 * (how field name is converted to MVC_NAME). For example the following formatter 
	 * would remove five characters from a field name: <br>
	 * <code>
	 * 	private YFormatter formatter = new YFormatter() {
	 * 		public String format(Object o) {
	 *			return o.toString().substring(5);
	 *		}
	 * };
	 * </code>
	 * <p>
	 * The method uses reflection to dig out all YIComponent fields of the given view. 
	 * Therefore, SecurityManager must allow accessing private fields
	 * via reflection when using this method! Use log4j info level to check out, what 
	 * MVC_NAMEs are set.
	 * <p>
	 * If upperClass parameter is null, only fields of the given view class are handled.
	 * If upperClass parameter is given, also upper classes are handled (until 
	 * given upperClass is handled).
	 * 
	 * @param view			the view to be initialized
	 * @param guessNested	if nested components guessing is used (upper case characters
	 * 						will be converted to nested bean notations and digits will be 
	 * 						converted to array notations)
	 * @param formatter		the formatter for application specific rules, may be null
	 * @param upperClass	the highest upper class to be handled, may be null
	 */
	public static void guessMVCNames(YIComponent view, boolean guessNested, YFormatter formatter, Class upperClass) {
        guessYPropertyValues(view, guessNested, formatter, upperClass, YIComponent.MVC_NAME);
	}
    
	private static void guessYProperty(YIComponent comp, String fieldName, boolean guessNested, YFormatter formatter, Object property) {
        String name = fieldName;
        if (formatter != null) {
            name = formatter.format(name);
        }
        if (name != null && guessNested) {
            name = guessNested(name);
        }
        logger.info("Guessing property " + property + ", field=" + fieldName + ",  name=" + name);
        comp.getYProperty().put(property, name);
    }
    
	/**
	 * A convenience method for setting YProperty to components of a view. The property value
	 * will be field names in given view class. 
	 * 
	 * @param view			the view to be initialized
	 * @param guessNested	if nested components guessing is used (upper case characters
	 * 						will be converted to nested bean notations and digits will be 
	 * 						converted to array notations)
	 * @param formatter		the formatter for application specific rules, may be null
	 * @param upperClass	the highest upper class to be handled, may be null
	 * @param property		the property to be set (for example YIComponent.MVC_NAME)
	 */
    public static void guessYPropertyValues(YIComponent view, boolean guessNested, YFormatter formatter, Class upperClass, Object property) {
        Class viewClass = view.getClass();
        String viewName = viewClass.getName();
        int pos = viewName.lastIndexOf('.');
        if (pos > -1) {
            viewName = viewName.substring(pos+1);            
        }
        
        viewName = viewName.substring(0,1).toLowerCase() + viewName.substring(1);
        guessYProperty(view, viewName, guessNested, formatter, property);
        boolean search = true;
        while (search) {
            Field[] fields = viewClass.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                if (YIComponent.class.isAssignableFrom(field.getType())) {
                    try {
                        field.setAccessible(true);
                        YIComponent comp = (YIComponent) field.get(view);
                        if (comp != null) {
                            guessYProperty(comp, field.getName(), guessNested, formatter, property);
                        }
                    } catch (Exception e) {
                        logger.warn("Property " + property + " could not be guessed for " + view.getClass() + 
                                ", field " + field.getName() + ". ");
                    }
                }
            }
            search = (upperClass != null && !upperClass.equals(viewClass));
            viewClass = viewClass.getSuperclass();
        }
    }
    
    /**
     * Finds YControllers in view hierarchy. All child components of given 
     * components are investigated. If component is YIComponent view that
     * is connected to YController, the controller is returned.
     * 
     * @param comp  the root component
     * @return  list of YControllers, empty list if no controllers is found
     */
    public static List getControllersByViewHierarchy(Component comp) {
        ArrayList result = new ArrayList();
        getControllersByViewHierarchy(comp, result);
        return result;
    }
    
    /**
     * Finds YControllers in view hierarchy.
     * 
     * @param comp  the root component
     * @param   result  the result list of YControllers
      */
    private static void getControllersByViewHierarchy(Component comp, ArrayList result) {
        if (comp instanceof YIComponent) {
            YController controller = (YController) 
            ((YIComponent)comp).getYProperty().get(YIComponent.CONTROLLER);
            if (controller != null) {
                result.add(controller);
            }
        }
        comp = YUIToolkit.getContentPane(comp);
        if (comp instanceof Container) {
            Component[] comps = ((Container)comp).getComponents();
            for (int i = 0; i < comps.length; i++) {
                Component child = comps[i];
                getControllersByViewHierarchy(child, result);
            }
        }
    }
	

}