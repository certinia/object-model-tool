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
import java.util.ArrayList;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;

import com.financialforce.objectmodelutil.controller.CommandLineController;

/**
 * Ant entry point for utility, See App.java for description.
 * 
 * @author Brad Slater (DevOps) financialforce.com
 * @version 1.0.0
 */
public class Ant extends Task {
	
	private String args = null;
	private String configFile = null;
	
	/**
	 * Utility entry point for ant.
	 * <p>
	 * Outputs charts based on input.
	 * 
	 * @throws BuildException
	 */
	public void execute() throws BuildException {
        
		//create a new controller.
        CommandLineController c = new CommandLineController();
        
        //run the controller with args... (remove lines and tabs and split around spaces)
        String[] split = args.split(",");
        ArrayList<String> newSplit = new ArrayList<String>();
        for (String spl : split){
        	spl = spl.replaceAll("\n","");
        	spl = spl.replaceAll("\t","");
            spl = spl.trim();
        	newSplit.add(spl);
        }
        
        //add the config file
        newSplit.add("config.properties=" + configFile);
        
        //run
        Throwable exit = c.run(newSplit.toArray(new String[0]));
        
        //exit with code...
        if (exit != null){
        	throw new BuildException(exit);
        }
		
    }
	
	/** 
	 * method for ant to add arguments
	 * 
	 * @param arg
	 */
	public void setArgs(String args){
		this.args = args;
	}
	
	/**
	 * method for ant to add config file path
	 * 
	 * @param configFile
	 */
	public void setConfigFile(String configFile){
		this.configFile = configFile;
	}
	
	
}
