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
package com.financialforce.objectmodelutil.model.processor.input;
//imports
import java.util.ArrayList;
import java.util.HashMap;
import com.financialforce.objectmodelutil.model.data.FunctionalArea;
import com.financialforce.objectmodelutil.model.exception.input.InputException;
import com.financialforce.objectmodelutil.model.exception.misc.*;
import com.financialforce.objectmodelutil.model.processor.Processor;

/**
 * An abstract class to abstract the input of configuration files
 * from the main application logic. 
 * <p>
 * Extends Processor for instance creation methods and validation methods.
 * 
 * @author Brad Slater (DevOps) financialforce.com
 * @version 1.0.0
 */
public abstract class InputProcessor extends Processor {
	
	/**
	 * Function to call for input parsing. Returns functional areas.
	 * 
	 * @return functional areas list			
	 * @param  args	data needed for parse.
	 * @throws InputException
	 * @throws BaseException 
	 */
	public abstract ArrayList<FunctionalArea> parseInput(HashMap<String,String> params) throws InputException;
	
	/**
	 * Uses the inherited protected getNewInstance and casts it to InputProcessor
	 * 
	 * @param className
	 * @return InputProcessor
	 * @throws MiscFactoryException
	 */
	public static InputProcessor getNewInputProcessor(String className) throws MiscFactoryException {
		return (InputProcessor) getNewInstance(className);
	}
	
	/**
	 * Uses the inherited protected getNewInstance and casts it to InputProcessor 
	 * 
	 * @param className 
	 * @param args
	 * @return InputProcessor
	 * @throws MiscFactoryException
	 */
	public static InputProcessor getNewInputProcessor(String className, Object[] args) throws MiscFactoryException {
		return (InputProcessor) getNewInstance(className, args);
	}
	
}
