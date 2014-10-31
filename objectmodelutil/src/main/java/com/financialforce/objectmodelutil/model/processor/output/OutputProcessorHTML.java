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
import java.io.FileOutputStream;
import java.io.FileWriter; 
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.financialforce.objectmodelutil.model.data.FunctionalArea;
import com.financialforce.objectmodelutil.model.exception.output.OutputException;
import com.financialforce.objectmodelutil.model.exception.output.OutputLocalException;
import com.sforce.soap.metadata.CustomField;
import com.sforce.soap.metadata.CustomObject;

/**
 * An implementation of the OutputProcessor abstract class;
 * that writes html output from a list of CObjects.
 * 
 * @author Brad Slater (DevOps) financialforce.com
 * @version 1.0.0
 */
public class OutputProcessorHTML extends OutputProcessor {

	//menu string (used (set) by children)
	protected String menu = "";
	
	/**
	 * Blank constructor
	 */
	public OutputProcessorHTML(){}
	
	/**
	 * Method from DataProcessor interface, 
	 * In this case, outputs data in html format.
	 * on error, either a stacktrace is printed or 
	 * an OutputException is thrown.
	 * 
	 * @peram	objects		  	custom objects array
	 * @param	areas			functionl area array (not used)
	 * @throws 	OutputException
	 */
	@Override 
	public void processOutput(ArrayList<CustomObject> objects, ArrayList<FunctionalArea> areas, HashMap <String,String> params) throws OutputException {
		
		//create files and deploy dependancies
		deployResources();
	
		//create a filewriter to write to a file.
		FileWriter fw = null;
		
		try {
		
			//set up the file to write to, (delete it first if it already exists)
			File f = new File("htmlchart/index.htm");
			f.delete();
			f.createNewFile();
			
			//create a filewriter instance
			fw = new FileWriter(f);
			
			//write start html
			fw.append(getStartHtml("Charting Test"));

			//for each object
			for (CustomObject object : objects){
				//write object html
				fw.append(this.getObjectHtml(object));
			}
		
			//write end and close file writer
			String endHtml = getEndHtml();
			fw.append(endHtml);
			
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

	/**
	 * Generates a folder structure for the html output.
	 * 
	 * Deploys all objects in the resource/ folder to their correct
	 * locations in the folder structure.
	 * 
	 * @throws OutputLocalException 
	 */
	protected void deployResources() throws OutputLocalException {
		
		try {
			
			//create folders
			new File("htmlchart/").mkdirs();
			new File("htmlchart/css/").mkdirs();
			new File("htmlchart/js/").mkdirs();
			new File("htmlchart/img/").mkdirs();
			
			//write the resources to the folders
			
			//css 
			File fileCss = new File("htmlchart/css/style.css");
			InputStream iCss = this.getClass().getClassLoader().getResourceAsStream("style.css");
			OutputStream oCss = new FileOutputStream(fileCss);
			
			byte[] buffer = new byte[1024];
            int bytesRead;
            while((bytesRead = iCss.read(buffer)) !=-1){
                oCss.write(buffer, 0, bytesRead);
            }
            iCss.close();
            oCss.flush();
            oCss.close();
            
            //field.js 
			File fileField = new File("htmlchart/js/logic.js");
			InputStream iField = this.getClass().getClassLoader().getResourceAsStream("logic.js");
			OutputStream oField = new FileOutputStream(fileField);
			
			buffer = new byte[1024];
            while((bytesRead = iField.read(buffer)) !=-1){
                oField.write(buffer, 0, bytesRead);
            }
            iField.close();
            oField.flush();
            oField.close();
            
            //jquery-2.1.1.min.js 
			File fileJQ = new File("htmlchart/js/jquery-2.1.1.min.js");
			InputStream iJQ = this.getClass().getClassLoader().getResourceAsStream("jquery-2.1.1.min.js");
			OutputStream oJQ = new FileOutputStream(fileJQ);
			
			buffer = new byte[1024];
            while((bytesRead = iJQ.read(buffer)) !=-1){
                oJQ.write(buffer, 0, bytesRead);
            }
            iJQ.close();
            oJQ.flush();
            oJQ.close();
            
            //jquery.jsplumb-1.4.1-all-min.js 
			File fileJQP = new File("htmlchart/js/jquery.jsplumb-1.6.2-min.js");
			InputStream iJQP = this.getClass().getClassLoader().getResourceAsStream("jquery.jsPlumb-1.6.2-min.js");
			OutputStream oJQP = new FileOutputStream(fileJQP);
			
			buffer = new byte[1024];
            while((bytesRead = iJQP.read(buffer)) !=-1){
                oJQP.write(buffer, 0, bytesRead);
            }
            iJQP.close();
            oJQP.flush();
            oJQP.close();
            
            //jquery-ui-min.js 
			File fileJQui = new File("htmlchart/js/jquery-ui.min.js");
			InputStream iJQui = this.getClass().getClassLoader().getResourceAsStream("jquery-ui.min.js");
			OutputStream oJQui = new FileOutputStream(fileJQui);
			
			buffer = new byte[1024];
            while((bytesRead = iJQui.read(buffer)) !=-1){
                oJQui.write(buffer, 0, bytesRead);
            }
            iJQui.close();
            oJQui.flush();
            oJQui.close();
            
            //image
            File fileImg = new File("htmlchart/img/logo.png");
			InputStream iImg = this.getClass().getClassLoader().getResourceAsStream("logo.png");
			OutputStream oImg = new FileOutputStream(fileImg);
			
			buffer = new byte[1024];
            while((bytesRead = iImg.read(buffer)) !=-1){
                oImg.write(buffer, 0, bytesRead);
            }
            iImg.close();
            oImg.flush();
            oImg.close();
		} catch (Exception e){
			throw new OutputLocalException(e);
		}

	}

	/**
	 * Generates the starting tags of the html file
	 * upto body and the spacer div.
	 * 
	 * @param 	String title
	 * @return 	String
	 */
	protected String getStartHtml(String title) throws OutputException{

		//make a stringbuilder
		StringBuilder sb = new StringBuilder();
		
		//make a copyright notice
		String copyright =  	"<!--" + "\n" +
								"* Copyright (c) 2014, FinancialForce.com, inc" + "\n" +
								"* All rights reserved." + "\n" +
								"*" + "\n" +
								"* Redistribution and use in source and binary forms, with or without modification," + "\n" +
								"*   are permitted provided that the following conditions are met:" + "\n" +
								"*" + "\n" +
								"* - Redistributions of source code must retain the above copyright notice," + "\n" + 
								"*      this list of conditions and the following disclaimer." + "\n" +
								"* - Redistributions in binary form must reproduce the above copyright notice," + "\n" + 
								"*      this list of conditions and the following disclaimer in the documentation" + "\n" + 
								"*      and/or other materials provided with the distribution." + "\n" +
								"* - Neither the name of the FinancialForce.com, inc nor the names of its contributors" + "\n" + 
								"*      may be used to endorse or promote products derived from this software without" + "\n" + 
								"*      specific prior written permission." + "\n" +
								"*" + "\n" +
								"* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS \"AS IS\" AND" + "\n" + 
								"*  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES" + "\n" + 
								"*  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL" + "\n" + 
								"*  THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL," + "\n" + 
								"*  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS" + "\n" +
								"*  OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY" + "\n" +
								"*  OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)" + "\n" +
								"*  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE." + "\n" +
								"-->" + "\n";
		//write copyright notice
		sb.append(copyright);
			
		//html start tag / doctype etc...
		sb.append("<!DOCTYPE html>");	sb.append("\n");
		sb.append("<html ffdiagram=\"http://financialforce.com/ffdiagram\">");	sb.append("\n");
		sb.append("\t");		sb.append("<head>");		sb.append("\n");
			
		//head tags / script links / title / stylesheets
		sb.append("\t");		sb.append("\t");		sb.append("<title>" + title + "</title>");														sb.append("\n");
		sb.append("\t");		sb.append("\t");		sb.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"css/style.css\">");				sb.append("\n");   
		sb.append("\t");		sb.append("\t");		sb.append("<script src=\"js/jquery-2.1.1.min.js\"></script>");									sb.append("\n");
		sb.append("\t");		sb.append("\t");		sb.append("<script src=\"js/jquery-ui.min.js\"></script>");										sb.append("\n");
		sb.append("\t");		sb.append("\t");		sb.append("<script src=\"js/jquery.jsplumb-1.6.2-min.js\"></script>");							sb.append("\n");
		sb.append("\t");		sb.append("\t");		sb.append("<script src=\"js/logic.js\"></script>");												sb.append("\n");
	
		String javascript = "<script type=\"text/javascript\">\n" + 
							"$(document).ready(function() {\n" +
							"//init layout\n" +
							"initialiseLayout(new ChartLayoutObject());\n" +
							"//click event\n" +
							"$(\"div\",\".table\").click(function(){\n" +
							"tableDivClicked($(this));\n" +
							"});\n" +
							"});\n" +
							"</script>";
		sb.append(javascript);
		
		sb.append("\t");		sb.append("</head>");																									sb.append("\n");
		
		//body start tags
		sb.append("\t");		sb.append("<body>");																									sb.append("\n");		
		sb.append("\t");	    sb.append("<div class=\"header\"><img style=\"width: 40px; height: 40px; float: left; \"src=\"img/logo.png\"></img>"); 	sb.append("\n");
		sb.append("\t");		sb.append("<div style=\"height: 40px; line-height: 40px; color:#FFF;\">Object Model Charts</div>");						sb.append("\n");
		sb.append("\t");	 	sb.append("</div>");																									sb.append("\n");
		sb.append("\t"); 		sb.append("<div class=\"main\">");																						sb.append("\n");
		sb.append("\t"); 		sb.append("<div class=\"sidebar\">");																					sb.append("\n");	
			
		//draw menu (not needed here but used by child classes)
		sb.append(this.getMenuStructure());
			
		sb.append("\t"); 	 sb.append("</div>");								sb.append("\n");	
		sb.append("\t"); 	 sb.append("<div class=\"container\">");				sb.append("\n");	
		sb.append("\t"); 	 sb.append("<div class=\"containerInner\">");		sb.append("\n");	
		sb.append("\t"); 	 sb.append("<div id=\"wrapper\">");					sb.append("\n");
		sb.append("<div id=\"spacer\"></div>");
		
		//done, return built string.	
		return sb.toString();
		
	}
	
	/**
	 * Finishes off a html file after the objects have been written
	 * starting at the end body tag.
	 * 
	 * @return String
	 */
	protected String getEndHtml() {

		//get a string builder
		StringBuilder sb = new StringBuilder();
	
		//write body end tags
		sb.append("\t");  	sb.append("</div>");	sb.append("\n");
		sb.append("\t");  	sb.append("</div>");	sb.append("\n");
		sb.append("\t");  	sb.append("</div>");	sb.append("\n");
		sb.append("\t");  	sb.append("</div>");	sb.append("\n");
		sb.append("\t");		sb.append("</body>");	sb.append("\n");
		sb.append("</html>");	sb.append("\n");
			
		//done, return the built string
		return sb.toString();
			
	}
	
	/**
	 * Gets HTML for each object,
	 * 
	 * @param file
	 * @param object
	 * @throws OutputException
	 */
	protected String getObjectHtml(CustomObject object) throws OutputException{
		
		//get a string builder
		StringBuilder sb = new StringBuilder();

		//draw table start tags
		sb.append("\t");		sb.append("\t");		sb.append("<div class=\"table\" id=\"" + object.getFullName()+ "\">"); 	sb.append("\n");
		sb.append("\t");		sb.append("\t");		sb.append("\t");		sb.append("<span class=\"labelName\">"+object.getLabel()+"</span>");		sb.append("\n");
		sb.append("\t");		sb.append("\t");		sb.append("\t");		sb.append("<div class=\"expandButton\">+</div>");		sb.append("\n");
		sb.append("\t");		sb.append("\t");		sb.append("\t");		sb.append("<div class=\"hideButton\">_</div>");		sb.append("\n");
		sb.append("\t");		sb.append("\t");		sb.append("\t");		sb.append("<span class=\"apiName\">"+object.getFullName()+"</span><br/><br/>");		sb.append("\n");
			
		//for each field
		int i = 0;
		for (CustomField field : object.getFields()){
				
			sb.append("\t");		sb.append("\t");		sb.append("\t");		sb.append("<div>");		sb.append("\n");
			sb.append("\t");		sb.append("\t");		sb.append("\t");		sb.append("\t");		sb.append(field.getLabel());		sb.append("\n");
			sb.append("\t");		sb.append("\t");		sb.append("\t");		sb.append("\t");		sb.append("<ul>");		sb.append("\n");
			sb.append("\t");		sb.append("\t");		sb.append("\t");		sb.append("\t");		sb.append("\t");		sb.append("<li>"+field.getFullName()+"</li>"); 	sb.append("\n");
			sb.append("\t");		sb.append("\t");		sb.append("\t");		sb.append("\t");		sb.append("\t");		sb.append("<li>"+field.getLength()+"</li>"); 	sb.append("\n");
			if (field.getType() != null){
				sb.append("\t");		sb.append("\t");		sb.append("\t");		sb.append("\t");		sb.append("\t");		sb.append("<li>"+field.getType().toString()+"</li>"); 	sb.append("\n");
			}
			if(field.getReferenceTo() != null){
				sb.append("\t");		sb.append("\t");		sb.append("\t");		sb.append("\t");		sb.append("\t");		sb.append("<li class=\"Trel\" id=\"" + i +object.getFullName() +"\">"+field.getReferenceTo()+"</li>"); 	sb.append("\n");
			}
			sb.append("\t");		sb.append("\t");		sb.append("\t");		sb.append("\t");		sb.append("</ul>");		sb.append("\n");
			sb.append("\t");		sb.append("\t");		sb.append("\t");		sb.append("</div>");		sb.append("\n");
			i++;
		}
			
		//draw table end tag.
		sb.append("\t");		sb.append("\t");		sb.append("</div>");		sb.append("\n");
			
		//done here, return the object.
		return sb.toString();
		
	}

	/**
	 * No menu to draw so not needed here
	 * Used by children classes who set the menu before calling the draw sidebar.
	 * 
	 * @return String
	 */
	@Override
	protected String getMenuStructure() {
		return menu;
	}

	/**
	 * no params needed
	 */
	@Override
	public Set<String> getRequiredParameters() {
		return Collections.unmodifiableSet(new HashSet<String>());
	}
	
}
