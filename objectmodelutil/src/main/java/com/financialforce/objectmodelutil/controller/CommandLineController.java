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
package com.financialforce.objectmodelutil.controller;
//imports
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import com.financialforce.objectmodelutil.model.config.ConfigProcessor;
import com.financialforce.objectmodelutil.model.data.FunctionalArea;
import com.financialforce.objectmodelutil.model.exception.*;
import com.financialforce.objectmodelutil.model.processor.Processor;
import com.financialforce.objectmodelutil.model.processor.input.InputProcessor;
import com.financialforce.objectmodelutil.model.processor.output.OutputProcessor;
import com.financialforce.objectmodelutil.model.processor.retrieve.RetrieveProcessor;
import com.sforce.soap.metadata.CustomObject;

/**
 * Main Controller class.
 * Takes args from the entrypoint.
 * Uses args to generate object list and passes it
 * to whatever processor you have set.
 * 
 * @author Brad Slater (DevOps) financialforce.com
 * @version 1.0.0
 */
public class CommandLineController {			
	
	public static final boolean SHOW_ERROR_TRACE = false;
	
	/**
	 * Blank Constructor.
	 */
	public CommandLineController(){}
	
	/**
	 * Controller run method. Contains main process
	 * logic and is called by the App entrypoint.
	 * 
	 * @param 	args			args for process
	 * @param 	stackTrace		print stacktraces?
	 * @return 	int 			exitCode for process.
	 */
	public Throwable run(String[] args){
		
		Throwable exitWith = null;
		
		try {
			//********** all logic **********\\
			
			//parse arguments
			ConfigProcessor iPP = new ConfigProcessor();
			AbstractMap.SimpleEntry<HashMap<String, String>,HashMap<String, Processor>> parseResult = iPP.parseArguments(args);
			
			//get variables for data manipulation classes
			InputProcessor input = (InputProcessor) parseResult.getValue().get("input");
			RetrieveProcessor retrieve = (RetrieveProcessor) parseResult.getValue().get("retrieve");
			OutputProcessor output = (OutputProcessor) parseResult.getValue().get("output");
			
			//make the call to retrieve and process the data.
			retrieveCall(parseResult.getKey(), input ,retrieve, output);
		
			//********** end logic **********\\
			
			//return null (Success)
			return exitWith;
			
		} catch (BaseException e){
			
			exitWith = e;
			//set the exit exception to the exception thrown...
			
			//print stacktract
			if (SHOW_ERROR_TRACE){
				e.printStackTrace();
			}
			
			//print error messages
			String result = "Error:\n";
			int level = 0;
			while (e != null) {
				try { //add the message and gobble any error
					for (int i = 0; i < level; i++){
						result += "\t";
					}
					result += e.getError() + "\n";
					level++;
					e = (BaseException) e.getCause();
				} catch (Exception e2){
					try {
						String tabNumber = "";
						for (int i = 0; i < level; i++){
							result += "\t";
							tabNumber += "\t";
						}
						result += e.getMessage() + "\n";
						result.replace("\n", "\n"+tabNumber);
					} catch (Exception e3){}
					e = null;
				}
			}
			System.out.println(result);
			
		} catch (Throwable e){
			
			exitWith = e;
			
			//print stacktract
			if (SHOW_ERROR_TRACE){
				e.printStackTrace();
			}
			
		}
		
		//return the exception
		return exitWith;
	
	}
	
	/**
	 * Processes args to get customobject arraylist. 
	 * Passes the customobject list to this.dataProcessor.
	 * <p>
	 * Calls this.dataProcessor.processData();
	 * 
	 * @param  perams			hashmap of params
	 * @param  input			processor to get input config data
	 * @param  retriever		service to retrieve the data
	 * @param  output			processor to create output
	 * @throws BaseException 
	 */
	private void retrieveCall(HashMap<String,String> params, InputProcessor input, RetrieveProcessor retriever, OutputProcessor output) throws BaseException{
		
		//try to get input (may not apply)
		ArrayList<FunctionalArea> areas = null;
		if (input != null){
			areas = input.parseInput(params);
		}
		
		//retrieve data to extObjects
		ArrayList<CustomObject> objects = null;
		objects = retriever.GetSObjects(params);
		
		//output the data (can add both perams as second will be ignored if not needed.
		output.processOutput(objects,areas,params);
		
	}

}
