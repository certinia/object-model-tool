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
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.financialforce.objectmodelutil.model.exception.SpecificException;
import com.financialforce.objectmodelutil.model.exception.retrieve.RetrieveXmlException;
import com.financialforce.objectmodelutil.model.exception.retrieve.RetrieveException;
import com.sforce.soap.metadata.CustomObject;
import com.sforce.ws.bind.TypeMapper;
import com.sforce.ws.parser.XmlInputStream;

/**
 * Class to get SObjects from a local src folder. Extends RetrieveProcessor.
 *  
 * @author Brad Slater (DevOps) financialforce.com
 * @version 1.0.0
 */
public class RetrieveProcessorLocal extends RetrieveProcessor {

	//string to hold required params
	private static final Set<String> requiredParameters;
	
	//build required parameters set
	static {
		HashSet<String> reqParams = new HashSet<String>();
		reqParams.add("retrieve.source");
		requiredParameters = Collections.unmodifiableSet(reqParams);
	}
		
	/**
	 * constructor
	 */
	public RetrieveProcessorLocal(){}
	
	/**
	 * Method to return an ArrayList of CustomObjects.
	 * <p>
	 * Uses a path.
	 * 
	 * @param	args	path to src folder
	 * 
	 * @return objectList	list of InternalCustomObjects.
	 * @throws ParseException 
	 */
	public ArrayList<CustomObject> GetSObjects(HashMap<String,String> args) throws RetrieveException {

		try{
			String path = args.get("retrieve.source");
			path = path + "/objects";

			ArrayList<CustomObject> returnList = new ArrayList<CustomObject>();
			
			File dir = new File(path);
			File[] directoryListing = dir.listFiles();
			if (directoryListing == null){
				throw new RetrieveXmlException(new SpecificException("File path specified in retrieve.source does not point to a valid folder."));
			}
			//scanner for hack
			java.util.Scanner s = null;
			for (File f: directoryListing){
				if (f.getAbsolutePath().endsWith(".object")){
					InputStream is = new FileInputStream(f);
					XmlInputStream xmlis = new XmlInputStream();
					
					CustomObject customObject = new CustomObject();		
					TypeMapper typeMapper = new TypeMapper();
					
					//needs a nasty hack here:
					try {
						//need to hack around this (known salesforce bug)
						s = new java.util.Scanner(is).useDelimiter("\\A");
						String objectAsString = s.next();
						
						objectAsString = objectAsString.replace("UTF-8", "UTF_8");
						objectAsString = objectAsString.replace("<restrictedPicklist>true</restrictedPicklist>", "");
						InputStream stream = new ByteArrayInputStream(objectAsString.getBytes());
						//end of hack (use stream instead of zipis)
						
						xmlis.setInput(stream, "UTF-8");	
						customObject.load(xmlis, typeMapper);
					} catch (Exception e){ 
						//hack should work but if it doesnt:
						throw new SpecificException(e.getMessage() + " " + f.getAbsolutePath());
					}

					customObject.setFullName(f.getName().split("\\.")[0]);
					returnList.add(customObject);
				}
			}

			return returnList;
		} catch (Exception e){
			throw new RetrieveXmlException(e);
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
