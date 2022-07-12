/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.htmltojson.fragment.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.htmltojson.htmltojson.HtmlFormUtil;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.resource.ResourceFactory;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

/**
 * Merge patients form fragment
 */
public class HtmlFormToJsonSchemaFragmentController {
	
	protected static final Log log = LogFactory.getLog(HtmlFormToJsonSchemaFragmentController.class);
	
	public void controller() {
		
	}
	
	/**
	 * Gets message payload
	 * 
	 * @param uuid the queue reference
	 * @return the summary
	 */
	/*@AppAction("kenyaemr.afyastat.htmlToJson")*/
	public SimpleObject getFormSchema(@RequestParam("formUuid") String uuid, UiUtils ui,
	        @SpringBean ResourceFactory resourceFactory) throws IOException {
		org.codehaus.jackson.node.ObjectNode payload = HtmlFormUtil.getFormSchemaJson(uuid, resourceFactory);
		
		SimpleObject summary = new SimpleObject();
		summary.put("payload", payload.toString());
		return summary;
	}
	
	/**
	 * Copies schema to a directory outside of module
	 * 
	 * @param
	 * @return the summary
	 */
	/*@AppAction("kenyaemr.afyastat.htmlToJson")*/
	public SimpleObject generateForms(UiUtils ui, @SpringBean ResourceFactory resourceFactory) {
		boolean passed = HtmlFormUtil.writeFormsToFileSystem(resourceFactory);
		
		SimpleObject summary = new SimpleObject();
		summary.put("success", passed);
		summary.put("payload", summary.toString());
		return summary;
	}
}
