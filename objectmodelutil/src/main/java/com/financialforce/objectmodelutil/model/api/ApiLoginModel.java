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
import com.financialforce.objectmodelutil.model.exception.api.ApiLoginException;
import com.sforce.soap.partner.Connector;
import com.sforce.soap.partner.LoginResult;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectorConfig;

/**
 * Class to handle logging in to a salesforce org.
 * Returns a loginresult object with will contain a 
 * token that can be used to query the org API.
 * 
 * @author Brad Slater (DevOps) financialforce.com
 * @version 1.0.0
 */
public class ApiLoginModel {
    
    //the result
    private static LoginResult res;
    private static String lastuser;

    //the connector configuration data used to login.
    private ConnectorConfig partnerConfig = null;
    
    /**
     * Constructor to set config information.
     * @parap config		partnerConfig
     */
	public ApiLoginModel(ConnectorConfig config){	
		this.partnerConfig = config; 
	}

	/**
	 * Method to login to an org with just a username and password.
	 * 
	 * @param username			the org username.
	 * @param password			the org password.
	 * @return LoginResult 		a class that can be used to get a session token.
	 * @throws LoginException 
	 */
	public LoginResult login(String username, String password) throws ApiLoginException {
		
        if (this.res != null && username.equals(lastuser)){
            return this.res;
        }
        
        LoginResult loginResult = null;
		
		try{
			PartnerConnection partnerConnection = Connector.newConnection(partnerConfig);
			loginResult = partnerConnection.login(username, password);   
		} catch (Exception e){
			throw new ApiLoginException(e);
		}
		this.res = loginResult;
		this.lastuser = username;
		return loginResult;
	}
	

}
