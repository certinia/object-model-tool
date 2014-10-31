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
package com.financialforce.objectmodelutil.model.api;
//imports
import java.util.ArrayList;

import com.financialforce.objectmodelutil.model.exception.SpecificException;
import com.financialforce.objectmodelutil.model.exception.api.ApiMetadataException;
import com.sforce.soap.metadata.AsyncResult;
import com.sforce.soap.metadata.MetadataConnection;
import com.sforce.soap.metadata.PackageTypeMembers;
import com.sforce.soap.metadata.RetrieveRequest;
import com.sforce.soap.metadata.Package;
import com.sforce.soap.metadata.RetrieveResult;
import com.sforce.soap.partner.DescribeGlobalResult;
import com.sforce.soap.partner.DescribeGlobalSObjectResult;
import com.sforce.soap.partner.LoginResult;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectorConfig;
import com.sforce.ws.SoapFaultException;

/**
 * Class to retrieve metadata from an org in the form of
 * a .zip file. Requires a LoginResult object that can 
 * be obtained from a method on the LoginModel class.
 * <p>
 * Also requires an API version which can be obtained from 
 * the LoginModel Class. To change the API version, you should
 * change the static variable in LoginModel and call its getAPIversion()
 * method to get the new value to use in this class.
 * 
 * @author Brad Slater (DevOps) financialforce.com
 * @version 1.0.0
 */
public class ApiMetadataModel {
	
	/**
	 * Blank Constructor
	 */
	public ApiMetadataModel(){}
	
	/**
	 * Method to retrive all metadata as a zip file (byte array).
	 * 
	 * @param 		loginResult		the LoginResult object to use
	 * @param 		APIversion		the version of the API to use ex: "29.0".
	 * @return		zipFile 		as byte array.
	 * @throws		ApiMetadataException
	 * @throws SpecificException 
	 */
	public byte[] retrieveAllData(LoginResult loginResult, String APIversion ) throws ApiMetadataException {
		
		RetrieveResult rRes = null;
		
		try {
			//init 
			ConnectorConfig mDC = new ConnectorConfig();
			MetadataConnection connection = null;
			Package pM = new Package();
			pM.setVersion(APIversion);
			ArrayList<PackageTypeMembers> types = new ArrayList<PackageTypeMembers>();
			PackageTypeMembers pTM = new PackageTypeMembers();
		
			//setup
			RetrieveRequest rReq = new RetrieveRequest();
				
			//setup metadata connection
			mDC.setSessionId(loginResult.getSessionId());
			mDC.setServiceEndpoint(loginResult.getMetadataServerUrl());
		
			//get all object names
			String[] sobjectNames = null;
		
			//setup the partner connection
			final ConnectorConfig config = new ConnectorConfig();
			config.setServiceEndpoint(loginResult.getServerUrl());
			config.setSessionId(loginResult.getSessionId());
			PartnerConnection pc = new PartnerConnection(config);
			
			// Get Complete list of SObjects and convert them into an array of their names
	        DescribeGlobalResult describeGlobalResult = pc.describeGlobal();
	        DescribeGlobalSObjectResult[] sobjectResults = describeGlobalResult.getSobjects();
	        sobjectNames = new String[sobjectResults.length];
	        for (int i = 0; i < sobjectResults.length; i++){
	        	//System.out.println(sobjectResults[i].getName());
	        	sobjectNames[i] = sobjectResults[i].getName();
	        }
		
			rReq.setSinglePackage(true);
			pTM.setName("CustomObject");
			pTM.setMembers(sobjectNames);
			types.add(pTM);
			pM.setTypes((PackageTypeMembers[]) types.toArray(new PackageTypeMembers[] {}));
			rReq.setUnpackaged(pM);	
		
			//get the data
			connection = com.sforce.soap.metadata.Connector.newConnection(mDC);
		
			AsyncResult response = connection.retrieve(rReq);

			do {
				Thread.sleep(1000);
				rRes = connection.checkRetrieveStatus(response.getId());
			} while(!rRes.isDone());
			
			return rRes.getZipFile();
		}
		catch(SoapFaultException e){
			throw new ApiMetadataException(new SpecificException("Unsupported API Version: " + APIversion +", please use version 31.0+"));
		}
		catch(Exception e){
			throw new ApiMetadataException(e);
		}
	}

}
