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
package com.financialforce.objectmodelutil.model.config;
//imports
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

import com.financialforce.objectmodelutil.App;
import com.financialforce.objectmodelutil.model.exception.SpecificException; 
import com.financialforce.objectmodelutil.model.exception.misc.MiscConfigException;
import com.financialforce.objectmodelutil.model.exception.misc.MiscConfigInvalidException;
import com.financialforce.objectmodelutil.model.exception.misc.MiscFactoryException;
import com.financialforce.objectmodelutil.model.exception.parse.ParseCommandException;
import com.financialforce.objectmodelutil.model.exception.parse.ParseCommandRequiredException;
import com.financialforce.objectmodelutil.model.exception.parse.ParseConfigException;
import com.financialforce.objectmodelutil.model.processor.Processor;
import com.financialforce.objectmodelutil.model.processor.input.InputProcessor;
import com.financialforce.objectmodelutil.model.processor.output.OutputProcessor;
import com.financialforce.objectmodelutil.model.processor.retrieve.RetrieveProcessor;

/**
 * Class to parse input params to key value pairs in a hashmap.
 * Also loads config file to generate the classes needed to run the utility.
 * 
 * @author Brad Slater (DevOps) financialforce.com
 * @version 1.0.0
 */
public class ConfigProcessor {

	/**
	 * empty constructors
	 */
	public ConfigProcessor() {}
	
	/**
	 * Parses arguments into a hashmap 
	 * Parses config file first, gets the required 
	 * params for the processors set and
	 * sees if the params match the requirements.
	 * 
	 * @param args
	 * @return map of arguments / map of processors
	 * @throws MiscConfigException 
	 * @throws ParseConfigException 
	 * @throws ParseCommandRequiredException 
	 * @throws MiscConfigInvalidException 
	 * @throws CommandParseException
	 */
	public AbstractMap.SimpleEntry<HashMap<String, String>,HashMap<String, Processor>> parseArguments(String args[]) 
			throws ParseCommandException, ParseConfigException, MiscConfigException, ParseCommandRequiredException, MiscConfigInvalidException{
		
		//make a map to hold params.
		HashMap<String, String> params = new HashMap<String,String>();
		
		try {
			//for each argument, split argument into params
			for (String arg : args){
				String[] subArgs = arg.split("=");
				params.put(subArgs[0], subArgs[1]);
			}
		
		} catch (Exception e){
			throw new ParseCommandException(e);
		}		
		
		String configFile =  params.get("config.properties");
		if (configFile == null){
			throw new ParseCommandRequiredException(new SpecificException("Config Processor requires parameter: config.properties (path to properties file)"));
		}
		
		//now we have the params, get the processors...
		HashMap<String, Processor> processors = this.parseConfigFile(configFile);
		
		InputProcessor input = (InputProcessor) processors.get("input");
		RetrieveProcessor retrieve = (RetrieveProcessor) processors.get("retrieve");
		OutputProcessor output = (OutputProcessor) processors.get("output");
		
		//now we have processors and params
		//create errors list
		ArrayList<String> errors = new ArrayList<String>();
		
		//get input processor if it isnt null
		if(processors.get("input") != null){
			Set<String> requiredParamsIn = input.getRequiredParameters();
			for (String req : requiredParamsIn){
				//if the params list doesnt contain the param:
				String currentParamToCheck = params.get(req);
				if (currentParamToCheck == null){
					errors.add("Input Processor: " + input.getClass().getSimpleName() + ", requires parameter: " + req);
				}
			}
		}
		
		Set<String> requiredParamsRet = retrieve.getRequiredParameters();
		for (String req : requiredParamsRet){
			//if the params list doesnt contain the param:
			String currentParamToCheck = params.get(req);
			if (currentParamToCheck == null){
				errors.add("Retrieve Processor: " + retrieve.getClass().getSimpleName() + ", requires parameter: " + req);
			}
		}
		
		Set<String> requiredParamsOut = output.getRequiredParameters();
		for (String req : requiredParamsOut){
			//if the params list doesnt contain the param:
			String currentParamToCheck = params.get(req);
			if (currentParamToCheck == null){
				errors.add("Output Processor: " + output.getClass().getSimpleName() + ", requires parameter: " + req);
			}
		}
		
		if (errors.size() > 0){
			String errorList = "";
			for (String error: errors){
				errorList += error + "\n\t";
			}
			throw new ParseCommandRequiredException(new SpecificException(errorList));
		}
		
		//return the params map and the processor map
		return new AbstractMap.SimpleEntry<HashMap<String, String>,HashMap<String, Processor>>(params,processors);
		
	}
	
	/**
	 * Parses config.properties file into the required classes
	 * 
	 * @return Map<String, Object> classes needed.
	 * @throws ParseConfigException 
	 * @throws MiscConfigException 
	 * @throws IOException 
	 */
	private HashMap<String, Processor> parseConfigFile(String configFilePath) throws ParseConfigException, MiscConfigException {
		
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(new File(configFilePath)));
		} catch (IOException e){
			throw new ParseConfigException(e);
		}
		
		InputProcessor input = null;
		RetrieveProcessor retrieve = null;
		OutputProcessor output = null;
		
		//get the class names
		String inputName = prop.getProperty("input");
		String retrieveName = prop.getProperty("retrieve");
		String outputName = prop.getProperty("output");
		
		//error check (retriever or output processor cant be null
		if(retrieveName == null || outputName == null){
			throw new MiscConfigException(new SpecificException("'retrieve=' and 'output=' must be set in the config.properties file."));
		}
		
		try {
			//set input if there is one
			if (inputName != null){
				input = InputProcessor.getNewInputProcessor(inputName);
			}
			//set retriever and output
			retrieve = RetrieveProcessor.getNewRetrieveService(retrieveName);
			output = OutputProcessor.getNewOutputProcessor(outputName);
		} catch (MiscFactoryException e){
			throw new MiscConfigException(e);
		}
		
		//at this point you deffinately have at lease a retrieve and output processor and possibly an input processor.
		HashMap<String,Processor> processorSet = new HashMap<String,Processor>();
		processorSet.put("output",output);
		processorSet.put("retrieve",retrieve);
		if (input != null){
			processorSet.put("input",input);
		}
		return processorSet;
	}
	
	
}
