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
package com.financialforce.objectmodelutil.model.processor.retrieve;
//imports
import java.util.ArrayList;
import java.util.HashMap;

import com.financialforce.objectmodelutil.model.exception.misc.MiscFactoryException;
import com.financialforce.objectmodelutil.model.exception.retrieve.RetrieveException;
import com.financialforce.objectmodelutil.model.processor.Processor;
import com.sforce.soap.metadata.CustomObject;

/**
 * An abstract class to abstract the Retrieve functionality
 * from the main application logic e.g. the input and output of data.
 * <p>
 * Extends Processor for instance creation methods and validation methods.
 *  
 * @author Brad Slater (DevOps) financialforce.com
 * @version 1.0.0
 */
public abstract class RetrieveProcessor extends Processor {

	/**
	 * Method to implement for calling the service
	 *
	 * @param  args for retrieve
	 * @throws BaseException
	 */
	public abstract ArrayList<CustomObject> GetSObjects(HashMap<String,String> params) throws RetrieveException;
	
	/**
	 * Uses the inherited protected getNewInstance and casts it to RetrieveService
	 * 
	 * @param className
	 * @return RetrieveService
	 * @throws FactoryException
	 */
	public static RetrieveProcessor getNewRetrieveService(String className) throws MiscFactoryException {
		return (RetrieveProcessor) getNewInstance(className);
	}
	
	/**
	 * Uses the inherited protected getNewInstance and casts it to RetrieveService 
	 * 
	 * @param className 
	 * @param args
	 * @return RetrieveService
	 * @throws FactoryException
	 */
	public static RetrieveProcessor getNewRetrieveService(String className, Object[] args) throws MiscFactoryException {
		return (RetrieveProcessor) getNewInstance(className, args);
	}
}
