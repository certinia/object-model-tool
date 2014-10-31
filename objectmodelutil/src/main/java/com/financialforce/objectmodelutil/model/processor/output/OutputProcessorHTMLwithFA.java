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
package com.financialforce.objectmodelutil.model.processor.output;
//imports
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import com.financialforce.objectmodelutil.model.data.FunctionalArea;
import com.financialforce.objectmodelutil.model.exception.output.OutputException;
import com.financialforce.objectmodelutil.model.exception.output.OutputLocalException;
import com.sforce.soap.metadata.CustomObject;

/**
 * An extension of the DataProcessorHTML class
 * that writes html output from a list of CObjects.
 * It arranges the output into pages based upon functional areas
 * 
 * @author Brad Slater (DevOps) financialforce.com
 * @version 1.0.0
 */
public class OutputProcessorHTMLwithFA extends OutputProcessorHTML {
	
	/**
	 * empty constructor
	 */
	public OutputProcessorHTMLwithFA(){}

	/**
	 * Method from DataProcessor interface override of parent method in DataProcessorHTML, 
	 * In this case, outputs data in html format, arranged by functional area.
	 * on error, either a stacktrace is printed or 
	 * an OutputException is thrown.
	 * 
	 * @peram	objects		arraylist of custom objects to process.
	 * @peram	areas		arraylist of functional areas.
	 * @throws 	OutputException
	 */
	@Override 
	public void processOutput(ArrayList<CustomObject> objects, ArrayList<FunctionalArea> areas, HashMap <String,String> params) throws OutputException {
		
		//create files and deploy dependancies (same as parent)
		deployResources();
		
		//need to set this.menu here
		this.menu = this.setMenu(areas);
		
		//for each area
		for (FunctionalArea a: areas){
				
			//variable for filewriter
			FileWriter fw = null;
			
			try {
				
				//set up the file to write to (delete it first if it already exists)
				File f = new File("htmlchart/" + a.getName() + ".htm");
				f.delete();
				f.createNewFile();

				//create an instance of a file writer
				fw = new FileWriter(f, true);	
				
				//write head
				fw.append(getStartHtml(a.getName()));
				
				//write the objects 
				for (CustomObject object : objects){
					
					//if the area contains the object
					if (a.getAreas().contains(object.getFullName())){
						
						//write the object
						fw.append(this.getObjectHtml(object));
					}
				}
				
				//write end
				fw.append(getEndHtml());
				
				//close filewriter
				fw.close();

			} catch (Exception e){
				//try to close fw and gobble exception if there is one
				try {
					fw.close();
				} catch (Exception e2){}
				throw new OutputLocalException(e);
			}
		}
	
	}
	
	/**
	 * Generate a menu for the areas
	 * 
	 * @param areas
	 * @return String menu
	 */
	public String setMenu(ArrayList<FunctionalArea> areas){
		String m = "";
		for (FunctionalArea a : areas){
			m += "\t<div class=\"sidebarLink\">\n";
			m += "\t\t<a href=\"" + a.getName() +".htm\">" + a.getName() + "</a>\n";
			m += "\t</div>\n";
		}
		return m;
	}
}
