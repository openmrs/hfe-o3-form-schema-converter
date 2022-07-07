package org.openmrs.module.htmltojson.page.controller;

import org.codehaus.jackson.node.ArrayNode;
import org.openmrs.module.htmltojson.htmltojson.HtmlFormUtil;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.ui.framework.resource.ResourceFactory;

public class HtmlFormToJsonHomePageController {
	
	public void get(@SpringBean ResourceFactory resourceFactory, PageModel model) {
		
		ArrayNode allForms = HtmlFormUtil.getAllForms(resourceFactory);
		model.put("hfeForms", allForms);
		model.put("hfeFormsSize", allForms.size());
		
	}
}
