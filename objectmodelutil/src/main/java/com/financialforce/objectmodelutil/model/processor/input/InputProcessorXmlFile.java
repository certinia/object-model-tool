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
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.financialforce.objectmodelutil.model.data.FunctionalArea;
import com.financialforce.objectmodelutil.model.exception.input.InputException;
import com.financialforce.objectmodelutil.model.exception.input.InputXmlException;

/**
 * A class to parse input from an xml file.
 * Extends IInputProcessor.
 * 
 * @author Brad Slater (DevOps) financialforce.com
 * @version 1.0.0
 */
public class InputProcessorXmlFile extends InputProcessor {
	
	//string to hold required params
	private static final Set<String> requiredParameters;
	
	//build required parameters set
	static {
		HashSet<String> reqParams = new HashSet<String>();
		reqParams.add("input.areas");
		requiredParameters = Collections.unmodifiableSet(reqParams);
	}
	
	/**
	 * constructor
	 */
	public InputProcessorXmlFile(){}
	
	/**
	 * A method to return an arraylist of functional areas from
	 * an xml file.
	 * 
	 * @param  params (needs input.areas)
	 * @throws InputException
	 * @return functional areas list
	 */
	@Override 
	public ArrayList<FunctionalArea> parseInput(HashMap<String,String> params) throws InputException {
		
		try {
			
			//get the file from arg 0;
			File f = new File(params.get("input.areas"));
			
			ArrayList<FunctionalArea> areasList = new ArrayList<FunctionalArea>();
			
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder;
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(f);
			doc.getDocumentElement().normalize();
			
			//for each area
			NodeList areas = doc.getElementsByTagName("area");
			for (int i = 0; i < areas.getLength(); i++) {
				
				Element a = (Element) areas.item(i);

				//get area name
				NodeList name = a.getElementsByTagName("name");
				String areaName = name.item(0).getTextContent();

				ArrayList<String> objectsList = new ArrayList<String>();
				NodeList objects = a.getElementsByTagName("object");
				
				//for each object in area
				for (int j = 0; j < objects.getLength(); j++){
					
					Element o = (Element) objects.item(j);
					
					//add the object to the arrayList
					String objectName = o.getTextContent();
					objectsList.add(objectName);	
				}
				
				//add a new area
				areasList.add(new FunctionalArea(objectsList, areaName,"",""));
			}
			
			return areasList;
				
		} catch (Exception e) {
			throw new InputXmlException(e);
		} 
	}

	/**
	 * method to return the required parameters.
	 * @return Set<String> params.
	 */
	@Override
	public Set<String> getRequiredParameters() {
		return requiredParameters;
	}
	
}
