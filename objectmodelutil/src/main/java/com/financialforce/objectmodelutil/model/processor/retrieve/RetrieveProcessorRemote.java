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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.financialforce.objectmodelutil.model.api.*;
import com.financialforce.objectmodelutil.model.exception.api.ApiLoginException;
import com.financialforce.objectmodelutil.model.exception.api.ApiMetadataException;
import com.financialforce.objectmodelutil.model.exception.retrieve.RetrieveException;
import com.financialforce.objectmodelutil.model.exception.retrieve.RetrieveXmlException;
import com.financialforce.objectmodelutil.model.exception.retrieve.RetrieveRemoteException;
import com.sforce.soap.metadata.CustomObject;
import com.sforce.soap.partner.LoginResult;
import com.sforce.ws.ConnectorConfig;
import com.sforce.ws.bind.TypeMapper;
import com.sforce.ws.parser.XmlInputStream;

/**
 * Class to wrap the api namespace functionality into one function
 * to be called from the controller. Returns SObjects. Extends IRetrieveService.
 *
 * @author Brad Slater (DevOps) financialforce.com
 * @version 1.0.0
 */
public class RetrieveProcessorRemote extends RetrieveProcessor {

	//string to hold required params
	private static final Set<String> requiredParameters;
			
	//build required parameters set
	static {
		HashSet<String> reqParams = new HashSet<String>();
		reqParams.add("retrieve.username");
		reqParams.add("retrieve.password");
		reqParams.add("retrieve.endpoint");
		reqParams.add("retrieve.api");
		requiredParameters = Collections.unmodifiableSet(reqParams);
	}
	
	/**
	 * constructor
	 */
	public RetrieveProcessorRemote(){}
	
	/**
	 * Method to return an ArrayList of CustomObjects.
	 * <p>
	 * Uses a username and password.
	 * 
	 * @param	args	username, password, token (optional)
	 * 
	 * @return objectList	list of CustomObjects.
	 */
	public ArrayList<CustomObject> GetSObjects(HashMap<String,String> params) throws RetrieveException {
		
		try {
			//get the params.
			String username = params.get("retrieve.username");
			String password = params.get("retrieve.password");
			String endpoint = params.get("retrieve.endpoint");
			String apiversion = params.get("retrieve.api");
		
			//add the token to the password if it is set.
			String token = params.get("retrieve.token");
		
			//create a connectorconfig
			ConnectorConfig partnerConfig = new ConnectorConfig();
			partnerConfig.setManualLogin(true); 
			partnerConfig.setServiceEndpoint(endpoint + apiversion); 
		
			//make a loginmodel
			ApiLoginModel l = new ApiLoginModel(partnerConfig);
			LoginResult res = null;
	
			if (token != null){
				res = l.login(username, password + token);
			} else {
				res = l.login(username, password);
			}
		
			if (res == null){
				throw new ApiLoginException();
			}
		
			ApiMetadataModel m  = new ApiMetadataModel();
			byte[] zipBytes = m.retrieveAllData(res, apiversion);
		
			if(zipBytes==null){
				throw new ApiMetadataException();
			}
		
			return this.zipDecode(zipBytes);
			
		} catch (ApiLoginException e){
			throw new RetrieveRemoteException(e);
		} catch (ApiMetadataException e) {
			throw new RetrieveRemoteException(e);
		} catch (RetrieveXmlException e){
			throw new RetrieveRemoteException(e);
		} catch (Exception e){
			throw new RetrieveRemoteException(e);
		}
	}
	
	/**
	 * Internal method to decode a zip file.
	 * 
	 * @param	 zip		byte array for the zip file
	 * @return	 ArrayList 	list of custom objects.
	 * @throws RetrieveXmlException 
	 */
	private ArrayList<CustomObject> zipDecode(byte[] zip) throws RetrieveXmlException {
		try {
			ZipInputStream zipis = new ZipInputStream(new ByteArrayInputStream(zip, 0, zip.length));
			ZipEntry zipEntry = null;
			ArrayList<CustomObject> returnList = new ArrayList<CustomObject>();
			
			//scanner for hack
			java.util.Scanner s = null;
			while((zipEntry = zipis.getNextEntry()) != null)
			{
				if(zipEntry.getName().endsWith(".object"))
				{
					CustomObject customObject = new CustomObject();		
					TypeMapper typeMapper = new TypeMapper();
					XmlInputStream xmlis = new XmlInputStream();
					
					//needs a nasty hack here:
					try {
						//need to hack around this (known salesforce bug)
						s = new java.util.Scanner(zipis).useDelimiter("\\A");
						String objectAsString = s.next();
						
						objectAsString = objectAsString.replace("UTF-8", "UTF_8");
						objectAsString = objectAsString.replace("<restrictedPicklist>true</restrictedPicklist>", "");
						InputStream stream = new ByteArrayInputStream(objectAsString.getBytes());
						//end of hack (use stream instead of zipis)
						
						xmlis.setInput(stream, "UTF-8");	
						customObject.load(xmlis, typeMapper);
					} catch (Exception e){ 
						//hack should work but if it doesnt:
						throw new Exception(e);
					}
					
					customObject.setFullName((zipEntry.getName().split("/")[1]).split("\\.")[0]);
					returnList.add(customObject);
					zipis.closeEntry();
				}
			}
			try {
				s.close();
			} catch (Exception e) {}
			
			return returnList;
			
		} catch (Exception e) {
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
