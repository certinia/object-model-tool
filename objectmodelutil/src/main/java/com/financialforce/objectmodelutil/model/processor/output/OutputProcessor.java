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
package com.financialforce.objectmodelutil.model.processor.output;
//imports
import java.util.ArrayList;
import java.util.HashMap;
import com.financialforce.objectmodelutil.model.data.FunctionalArea;
import com.financialforce.objectmodelutil.model.exception.misc.MiscFactoryException;
import com.financialforce.objectmodelutil.model.exception.output.OutputException;
import com.financialforce.objectmodelutil.model.processor.Processor;
import com.sforce.soap.metadata.CustomObject;

/**
 * An abstract class to abstract the output processing functionality
 * from the main application logic.
 * <p>
 * Extends Processor for instance creation methods and validation methods.
 * 
 * @author Brad Slater (DevOps) financialforce.com
 * @version 1.0.0
 */
public abstract class OutputProcessor extends Processor {

			
	/**
	 * Method to implement for processing data.
	 *
	 * @param objects
	 * @peram areas
	 * @throws OutputException 
	 */
	public abstract void processOutput(ArrayList<CustomObject> objects, ArrayList<FunctionalArea> areas, HashMap<String,String> params) throws OutputException;
	
	/**
	 * function to get the sidebar menu.
	 * must be implemented.
	 * @returns String 
	 */
	protected abstract String getMenuStructure();
	
	/**
	 * Uses the inherited protected getNewInstance and casts it to OutputProcessor
	 * 
	 * @param className
	 * @return OutputProcessor
	 * @throws FactoryException
	 */
	public static OutputProcessor getNewOutputProcessor(String className) throws MiscFactoryException {
		return (OutputProcessor) getNewInstance(className);
	}
	
	/**
	 * Uses the inherited protected getNewInstance and casts it to OutputProcessor
	 * 
	 * @param className 
	 * @param args
	 * @return OutputProcessor
	 * @throws FactoryException
	 */
	public static OutputProcessor getNewOutputProcessor(String className, Object[] args) throws MiscFactoryException{	
		return (OutputProcessor) getNewInstance(className, args);
	}
}