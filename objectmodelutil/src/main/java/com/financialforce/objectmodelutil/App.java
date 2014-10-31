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
package com.financialforce.objectmodelutil;
//imports
import com.financialforce.objectmodelutil.controller.CommandLineController;

/**
 * Utility to automate the production of object model 
 * documents for a salesforce product.
 * 
 * Works with either an org with the product deployed
 * or the products src folder from git provided.
 * 
 * Outputs html a html file containing the chart,
 * css to style it and jquery to lay it out.
 * 
 * Supports the input of functional area information
 * to organize charts.
 * <p>
 * Compile with: mvn clean compile assembly:single 
 * from command line in project folder.
 * 
 * @author Brad Slater (DevOps) financialforce.com
 * @version 1.0.0
 */
public class App 
{
	
	/**
	 * Utility entry point.
	 * 
	 * Outputs charts based on input.
	 * 
	 * @return	int		0 on success, 1 on failure ( via System.exit(int) ).
	 */
    public static void main( String[] args )
    {
    	//create a new controller.
        CommandLineController c = new CommandLineController();
        //run the controller...
        Throwable exit = c.run(args);
        //exit with code...
        if(exit == null){
        	//success
        	System.out.println("Success");
        	System.exit(0);
        } else {
        	//fail
        	System.out.println("Fail");
        	System.exit(1);
        }
    }
}
