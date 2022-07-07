package org.openmrs.module.htmltojson.htmltojson;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openmrs.Concept;
import org.openmrs.Form;
import org.openmrs.FormResource;
import org.openmrs.GlobalProperty;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlformentry.HtmlForm;
import org.openmrs.module.htmlformentry.HtmlFormEntryUtil;
import org.openmrs.module.htmltojson.UiResource;
import org.openmrs.ui.framework.resource.ResourceFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class HtmlFormUtil {
	
	private static final String GP_GENERATED_HTML_RESOURCE_PATH = "htmlformmigration.resources";
	
	protected static final String RESOURCE_HFE_XML_PATH = "hfeXmlPath";
	
	/**
	 * Generate o3 schema from html form schema
	 * 
	 * @param formUuid
	 * @param resourceFactory
	 * @return
	 * @throws IOException
	 */
	public static ObjectNode getFormSchemaJson(String formUuid, ResourceFactory resourceFactory) throws IOException {
		Form form = Context.getFormService().getFormByUuid(formUuid);
		ObjectNode questions = JsonNodeFactory.instance.objectNode();
		ArrayNode questionsList = JsonNodeFactory.instance.arrayNode();
		
		String pathToResourceDir = getGeneratedHtmlResourcePath();
		if (StringUtils.isBlank(pathToResourceDir)) {
			System.out.println("Please set the resources path for forms migration");
			return null;
		}
		
		if (form != null) {
			
			HtmlForm htmlForm = null;
			try {
				htmlForm = getHtmlForm(form, resourceFactory);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			
			if (htmlForm != null) {
				String formHtml;
				String generateFileName = generateFileNameFromHtmlForm(form.getName()) + ".html";
				;
				generateFileName = pathToResourceDir + generateFileName;
				
				System.out.println("Generated file name: " + generateFileName); // makes it easy to find the file in the directory configured to hold the forms outside of code
				File file = new File(generateFileName);
				String content = FileUtils.readFileToString(file, "UTF-8");
				
				formHtml = content;
				Document doc = Jsoup.parse(formHtml);
				
				System.out.println("Form name: " + htmlForm.getName());
				Element htmlform = doc.select("htmlform").first();
				
				Elements elementTags = htmlform.select("obs,obsgroup");
				Set<String> processedUngroupedCheckedInputs = new HashSet<String>();
				
				for (Element element : elementTags) {
					
					// check the tag name
					if (element.normalName().equals("obs")) {
						boolean isGrouped = obsIsInAGroup(element);
						
						if (isGrouped) {
							continue;
						}
						// check if an obs is part of a multiselct/checkbox group.
						
						String conceptIdAttr = element.attr("conceptId");
						String styleAttr = element.attr("style");
						Concept questionConcept = getConceptByUuidOrId(conceptIdAttr);
						
						if (StringUtils.isNotBlank(styleAttr) && styleAttr.equals("checkbox")
						        && StringUtils.isNotBlank(conceptIdAttr) && questionConcept != null
						        && !processedUngroupedCheckedInputs.contains(conceptIdAttr)) {
							String questionLabel = HtmlFormUtil.extractQuestionLabel(element);
							String actualConceptUuid = questionConcept.getUuid();
							Map<String, List<Element>> relatedTags = HtmlUngroupedCheckboxExtractor.getRelatedCheckboxes(
							    conceptIdAttr, actualConceptUuid, htmlform);
							
							List<Element> allRelatedTags = relatedTags.get(actualConceptUuid);
							if (allRelatedTags == null || allRelatedTags.isEmpty()) {
								ObjectNode dataPointJson = generateJsonObjectForHtmlDataPoint(element);
								
								if (dataPointJson != null) {
									questionsList.add(dataPointJson);
								}
								
							} else {
								HtmlFormUngroupedCheckboxRenderer ungroupedCheckboxRenderer = new HtmlFormUngroupedCheckboxRenderer(
								        actualConceptUuid, allRelatedTags, questionLabel);
								ObjectNode jsonObj = ungroupedCheckboxRenderer.render();
								
								if (jsonObj != null) {
									questionsList.add(jsonObj);
								}
								processedUngroupedCheckedInputs.add(conceptIdAttr);
								
							}
							
						} else {
							
							ObjectNode dataPointJson = generateJsonObjectForHtmlDataPoint(element);
							
							if (dataPointJson != null) {
								questionsList.add(dataPointJson);
							}
						}
					} else if (element.normalName().equals("obsgroup")) {
						HtmlFormObsGroupRenderer obsGroupRenderer = new HtmlFormObsGroupRenderer(element);
						ObjectNode groupJson = obsGroupRenderer.render();
						if (groupJson != null) {
							questionsList.add(groupJson);
						}
					}
					
				}
				
			}
		}
		questions.put("questions", questionsList);
		return questions;
	}
	
	/**
	 * Generate json object for an element
	 * 
	 * @param element
	 * @return
	 */
	public static ObjectNode generateJsonObjectForHtmlDataPoint(Element element) {
		HtmlFormDataPoint dataPoint = HtmlObsTagExtractor.extractObsTag(element);
		if (dataPoint == null) {
			System.out.println("Encountered invalid concept UUID in the form schema. Ignoring the tag ");
			return null;
		}
		HtmlFormObsRenderer renderer = new HtmlFormObsRenderer(dataPoint);
		ObjectNode obsJson = renderer.render();
		if (obsJson != null) {
			return obsJson;
		}
		return null;
	}
	
	/**
	 * Get a list of all forms
	 * 
	 * @param resourceFactory
	 * @return
	 */
	public static ArrayNode getAllForms(ResourceFactory resourceFactory) {
		
		String formHtml;
		ArrayNode generatedFormList = JsonNodeFactory.instance.arrayNode();
		List<Form> dbForms = Context.getFormService().getAllForms(false);
		for (Form form : dbForms) {
			
			if (form != null && !form.getRetired()) {
				
				HtmlForm htmlForm = null;
				try {
					htmlForm = getHtmlForm(form, resourceFactory);
				}
				catch (IOException e) {
					e.printStackTrace();
				}
				
				if (htmlForm != null) {
					
					ObjectNode formObject = JsonNodeFactory.instance.objectNode();
					formHtml = htmlForm.getXmlData();
					formObject.put("uuid", form.getUuid());
					formObject.put("formName", htmlForm.getName());
					
					Document doc = Jsoup.parse(formHtml);
					
					Element htmlform = doc.select("htmlform").first();
					Elements obsTags = htmlform.select("obs,obsgroup");
					
					formObject.put("dataPoints", String.valueOf(obsTags.size()));
					generatedFormList.add(formObject);
				}
			}
		}
		
		return generatedFormList;
	}
	
	public static boolean writeFormsToFileSystem(ResourceFactory resourceFactory) {
		
		String pathToResourceDir = getGeneratedHtmlResourcePath();
		if (StringUtils.isBlank(pathToResourceDir)) {
			System.out.println("Please set the resources path for forms migration");
			return false;
		}
		
		List<Form> dbForms = Context.getFormService().getAllForms(false);
		
		for (Form form : dbForms) {
			String formHtml;
			String generateFileName;
			
			if (form != null) {
				
				HtmlForm htmlForm = null;
				try {
					htmlForm = getHtmlForm(form, resourceFactory);
				}
				catch (IOException e) {
					e.printStackTrace();
				}
				
				if (htmlForm != null) {
					formHtml = htmlForm.getXmlData();
					generateFileName = generateFileNameFromHtmlForm(form.getName()) + ".html";
					generateFileName = pathToResourceDir + generateFileName;
					createFile(generateFileName, formHtml);
				}
			}
			
		}
		
		return true;
	}
	
	/**
	 * Writes to a file
	 * 
	 * @param fileName
	 * @param formContent
	 */
	public static void createFile(String fileName, String formContent) {
		try {
			
			FileWriter myWriter = new FileWriter(fileName);
			BufferedWriter output = new BufferedWriter(myWriter);
			
			// Writes the string to the file
			output.write(formContent);
			output.flush();
			output.close();
		}
		catch (IOException e) {
			System.out.println("An error occurred while writing to " + fileName);
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets concept by ID or UUID
	 * 
	 * @param conceptIdentifier
	 * @return
	 */
	public static Concept getConceptByUuidOrId(String conceptIdentifier) {
		Concept concept = null;
		if (Context.getConceptService().getConceptByUuid(conceptIdentifier) != null) {
			concept = Context.getConceptService().getConceptByUuid(conceptIdentifier);
		} else {
			concept = Context.getConceptService().getConcept(conceptIdentifier);
		}
		return concept;
	}
	
	/**
	 * Gets concept datatype
	 * 
	 * @param concept
	 * @return String equivalent of the datatype
	 */
	public static String getConceptDatatype(Concept concept) {
		if (concept == null)
			return null;
		
		if (concept.getDatatype().isCoded()) {
			return "Coded";
		} else if (concept.getDatatype().isBoolean()) {
			return "Boolean";
		} else if (concept.getDatatype().isText()) {
			return "Text";
		} else if (concept.getDatatype().isDateTime() || concept.getDatatype().isDate()) {
			return "Datetime";
		} else if (concept.getDatatype().isNumeric()) {
			return "Numeric";
		}
		return null;
	}
	
	/**
	 * Return concept UUID for a concept
	 * 
	 * @param concept
	 * @return
	 */
	public static String getConceptUuid(Concept concept) {
		return concept.getUuid();
	}
	
	/**
	 * Generates file name from form name as configured in the EMR It removes any special characters
	 * 
	 * @param formName
	 * @return cleaned name
	 */
	public static String generateFileNameFromHtmlForm(String formName) {
		if (StringUtils.isBlank(formName)) {
			return null;
		}
		
		formName = formName.replaceAll("\\s+", "_");
		formName = formName.replaceAll("[-+.^:,]", "_");
		formName = formName.replaceAll("'", "_");
		formName = formName.replaceAll("\\\\", "_");
		formName = formName.replaceAll("\\/", "_");
		formName = formName.replaceAll("\\(", "_");
		formName = formName.replaceAll("\\)", "_");
		;
		formName.toLowerCase();
		return formName;
	}
	
	/**
	 * Gets the configured path where html form schema are dumped
	 * 
	 * @return
	 */
	public static String getGeneratedHtmlResourcePath() {
		GlobalProperty globalPropertyObject = Context.getAdministrationService().getGlobalPropertyObject(
		    GP_GENERATED_HTML_RESOURCE_PATH);
		if (globalPropertyObject == null) {
			return null;
		}
		
		if (globalPropertyObject.getValue() != null) {
			try {
				String pathValue = globalPropertyObject.getValue().toString();
				return pathValue;
			}
			catch (Exception e) {
				return null;
			}
		}
		return null;
	}
	
	/**
	 * Returns a list of Element that have same conceptId attribute, a style='checkbox', and are not
	 * grouped
	 * 
	 * @param similarCheckboxTags
	 * @return
	 */
	public static List<Element> filterRelatedTags(Elements similarCheckboxTags) {
		List<Element> validChildren = new ArrayList<Element>();
		for (Element tag : similarCheckboxTags) {
			boolean isGrouped = false;
			Elements parents = tag.parents();
			for (Element parent : parents) {
				
				if (parent.normalName().equals("obsgroup")) {
					isGrouped = true;
				}
			}
			
			if (!isGrouped) {
				validChildren.add(tag);
			}
		}
		return validChildren;
	}
	
	/**
	 * Checks if obs is part of an obsgroup
	 * 
	 * @param obs
	 * @return
	 */
	public static boolean obsIsInAGroup(Element obs) {
		boolean isGrouped = false;
		Elements parents = obs.parents();
		for (Element parent : parents) {
			
			if (parent.normalName().equals("obsgroup")) {
				isGrouped = true;
				break;
			}
		}
		
		return isGrouped;
	}
	
	/**
	 * Checks if a given obs group is a repeat
	 * 
	 * @param obs
	 * @return
	 */
	public static boolean isRepeat(Element obs) {
		boolean isRepeat = false;
		Elements parents = obs.parents();
		for (Element parent : parents) {
			
			if (parent.normalName().equals("repeat")) {
				isRepeat = true;
				break;
			}
		}
		
		return isRepeat;
	}
	
	/**
	 * Extract question label for obs
	 * 
	 * @param obsTag
	 * @return
	 */
	public static String extractQuestionLabel(Element obsTag) {
		String inlineObsLabel = obsTag.attr("labelText");
		String qLabel = "";
		if (inlineObsLabel != null && StringUtils.isNotBlank(inlineObsLabel)) {
			qLabel = inlineObsLabel;
		} else {
			Element parentElement = obsTag.parent();
			if (parentElement.normalName().equals("td") && StringUtils.isNotBlank(parentElement.ownText())) {
				qLabel = parentElement.ownText();
			} else if (parentElement.normalName().equals("td")) {
				Element previousTd = parentElement.previousElementSibling();
				if (previousTd != null && previousTd.normalName().equals("td")
				        && StringUtils.isNotBlank(previousTd.ownText())) {
					qLabel = previousTd.ownText();
				}
			}
		}
		return qLabel;
	}
	
	/**
	 * Gets an HTML from a form
	 * 
	 * @param form the form
	 * @param resourceFactory the resourceFactory
	 * @return the Html form
	 * @throws RuntimeException if form has no xml path or path is invalid
	 */
	public static HtmlForm getHtmlForm(Form form, ResourceFactory resourceFactory) throws IOException {
		UiResource xmlResource = getFormXmlResource(form);
		
		if (xmlResource == null) {
			// Look in the database
			HtmlForm hf = HtmlFormEntryUtil.getService().getHtmlFormByForm(form);
			if (hf != null) {
				return hf;
			}
			
			throw new RuntimeException("Form has no XML path or persisted html form");
		}
		
		String xml = resourceFactory.getResourceAsString(xmlResource.getProvider(), "htmlforms/" + xmlResource.getPath());
		
		if (xml == null) {
			throw new RuntimeException("Form XML could not be loaded from path " + xmlResource + "");
		}
		
		HtmlForm hf = new HtmlForm();
		hf.setForm(form);
		hf.setCreator(form.getCreator());
		hf.setDateCreated(form.getDateCreated());
		hf.setChangedBy(form.getChangedBy());
		hf.setDateChanged(form.getDateChanged());
		hf.setXmlData(xml);
		return hf;
	}
	
	/**
	 * Gets the XML resource path of the given form (null if form doesn't have an XML resource)
	 * 
	 * @param form the form
	 * @return the XML resource
	 */
	public static UiResource getFormXmlResource(Form form) {
		FormResource resource = Context.getFormService().getFormResource(form, RESOURCE_HFE_XML_PATH);
		return resource != null ? new UiResource((String) resource.getValue()) : null;
	}
	
}
