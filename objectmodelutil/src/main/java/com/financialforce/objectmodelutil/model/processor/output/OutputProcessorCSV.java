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
import java.io.IOException;
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
 * Simple extension of OutputProcessor to generate CSV output.
 * 
 * @author Brad Slater (DevOps) financialforce.com
 * @version 1.0.0
 */
public class OutputProcessorCSV extends OutputProcessor {

	/**
	 * draws the objects to a CSV file, (Very simple).
	 */
	@Override
	public void processOutput(ArrayList<CustomObject> objects,
			ArrayList<FunctionalArea> areas, HashMap<String, String> params)
			throws OutputException {
		
		File f = new File("cvsOutput.csv");
		f.delete();
		FileWriter fw = null;
		try {
			f.createNewFile();
			fw = new FileWriter(f,true);
			for (CustomObject o : objects){
				fw.append(o.getFullName() + "," + o.getLabel() + "\n");
				for (CustomField field : o.getFields()){
					String name = field.getLabel();
					String apiName = field.getFullName();
					String length = field.getLength() + "";
					String type = (field.getType() == null) ? null : field.getType().toString();
					String ref = field.getReferenceTo();
					String total = name + "," + apiName + "," + length;
					if (type != null){
						total += "," + type;
					}
					if (ref != null){
						total += "," + ref;
					}
					fw.append(total + "\n");
				}
			}
			fw.close();
		} catch (IOException e) {
			try {
				fw.close();
			} catch (Exception e2){}
			throw new OutputLocalException(e);
		}
		
		
		
	}

	/**
	 * no menu needed
	 */
	@Override
	protected String getMenuStructure() {
		return "";
	}

	/**
	 * no params needed
	 */
	@Override
	public Set<String> getRequiredParameters() {
		return Collections.unmodifiableSet(new HashSet<String>());
	}

}
