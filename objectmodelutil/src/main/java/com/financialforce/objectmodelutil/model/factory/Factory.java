/**
 * Copyright (c) 2014, FinancialForce.com, inc
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, 
 *   are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice, 
 *      this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice, 
 *      this list of conditions and the following disclaimer in the documentation 
 *      and/or other materials provided with the distribution.
 * - Neither the name of the FinancialForce.com, inc nor the names of its contributors 
 *      may be used to endorse or promote products derived from this software without 
 *      specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES 
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL 
 *  THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 *  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 *  OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 *  OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 *  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
**/

//package
package com.financialforce.objectmodelutil.model.factory;
//imports
import java.lang.reflect.Constructor;
import java.util.HashMap;

import com.financialforce.objectmodelutil.model.exception.SpecificException;
import com.financialforce.objectmodelutil.model.exception.misc.MiscFactoryException;

/**
 * A factory abstract class that contains methods to 
 * build new instances of any class and pass perameters
 * to the class' constructor where required.
 * <p>
 * A child class should expose these methods with
 * public static methods that cast the output to the required
 * object type. 
 * 
 * @author Brad Slater (DevOps) financialforce.com
 * @version 1.0.0
 */
public abstract class Factory {
	
	/**
	 * Factory method to create a new instance of an object with args
	 * 
	 * @param type				Class of object
	 * @param args				Arguments to pass to constructor
	 * @return Object	
	 * @throws MiscFactoryException
	 */
	protected static Object getNewInstance(String className, Object[] args) throws MiscFactoryException{
		
		try {
			Class<?> type = Class.forName(className); 
			//if there are no args try return the empty constructor instance
			if (args == null || args.length == 0){
				return getNewInstance(className);
			}
			
			//create var to hold matching constructor if one is found.
			Constructor<?> matchingConstructor = null;
			
			//get constructors
			Constructor<?>[] constructors = type.getConstructors();
			for (Constructor<?> c : constructors) {
				//get perams
				Class<?>[] peramTypes  = c.getParameterTypes();
				
				boolean typeMatch = true;
				for (int i = 0; i < peramTypes.length; i++){
					//for each peram
					if(peramTypes[i]== args[i].getClass()){
						//types match do nothing
					} else {
						//types do not match but can Object[] args be unboxed for typeMatch?
						if (peramTypes[i] == returnPrimitiveType(args[i].getClass())){
							//type matches unboxed arg so call will work, do nothing
						}
						else {
							//types do not match.
							typeMatch = false;
						}
					}
				}
				//if all types matched set the matching constructor and break.
				if(typeMatch){
					matchingConstructor = c;
					break;
				}
			}
			
			return matchingConstructor.newInstance(args);
			

		} catch (Exception e){
			throw new MiscFactoryException(new SpecificException(className));
		}
		
	}
		
	/**
	 * Factory method to create a new instance of an object. 
	 * 
	 * @param type				Class of object
	 * @return Object	
	 * @throws MiscFactoryException
	 */
	protected static Object getNewInstance(String className) throws MiscFactoryException {		
		try {
			Class<?> type = Class.forName(className); 
			return type.newInstance();
		} catch (Throwable e){
			throw new MiscFactoryException(new SpecificException(className));
		}
	}
	
	/**
	 * Internal method to return the primitive type of a wrapper 
	 * or null if the type is not a wrapper.
	 * 
	 * @param 	type		the wrapper type
	 * @return	type		the corresponding primitive type
	 */
	private static Class<?> returnPrimitiveType(Class<?> type){		
		
		HashMap<Class<?>,Class<?>> ret = new HashMap<Class<?>,Class<?>>();
		ret.put(Boolean.class, 		boolean.class);
		ret.put(Character.class,	char.class);
		ret.put(Byte.class,			byte.class);
		ret.put(Short.class, 		short.class);
		ret.put(Integer.class, 		int.class);
		ret.put(Long.class,			long.class);
		ret.put(Float.class,		float.class);
		ret.put(Double.class,		double.class);
		ret.put(Void.class,			void.class);
		
		try{
			return ret.get(type);
		} catch (Throwable e){
			return null;
		}
	}
	
}
