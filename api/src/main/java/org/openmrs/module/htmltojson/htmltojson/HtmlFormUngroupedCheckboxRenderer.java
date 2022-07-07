package org.openmrs.module.htmltojson.htmltojson;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.jsoup.nodes.Element;
import org.openmrs.Concept;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Renderer for html <obs conceptId=xxx conceptAnswer=xxx></obs> tag Renderer for html <obs
 * conceptId=xxx conceptAnswer=yyy></obs> tag Renderer for html <obs conceptId=xxx
 * conceptAnswer=zzz></obs> tag
 */
public class HtmlFormUngroupedCheckboxRenderer {
	
	private String conceptUuid;
	
	private List<Element> elements;
	
	private String getQuestionLabel;
	
	public HtmlFormUngroupedCheckboxRenderer(String conceptUuid, List<Element> elements, String getQuestionLabel) {
		this.conceptUuid = conceptUuid;
		this.elements = elements;
		this.getQuestionLabel = getQuestionLabel;
	}
	
	/**
	 * Renders the collection similar to coded obs
	 * 
	 * @return
	 */
	public ObjectNode render() {
		if (conceptUuid == null || elements.isEmpty())
			return null;
		
		ObjectNode obsStub = generateQuestionStub();
		ObjectNode optionsNode = generateOptionsStub();
		Map<String, String> ansList = generateAnswerList(elements);
		ArrayNode answersList = JsonNodeFactory.instance.arrayNode();
		
		if (!ansList.isEmpty()) {
			for (Map.Entry<String, String> entry : ansList.entrySet()) {
				ObjectNode answerObject = JsonNodeFactory.instance.objectNode();
				answerObject.put("concept", entry.getKey());
				answerObject.put("label", entry.getValue());
				answersList.add(answerObject);
			}
			optionsNode.put("answers", answersList);
		}
		obsStub.put("questionOptions", optionsNode);
		
		return obsStub;
	}
	
	/**
	 * Generate a list of concept answers from list of elements
	 * 
	 * @param ansListElements
	 * @return
	 */
	public Map<String, String> generateAnswerList(List<Element> ansListElements) {
		
		Map<String, String> codedAnswerMap = new HashMap<String, String>();
		
		for (Element obsTag : ansListElements) {
			String answerConceptId = obsTag.attr("answerConceptId");
			String answerLabel = obsTag.attr("answerLabel");
			
			if (StringUtils.isNotBlank(answerConceptId)) {
				Concept ansConcept = HtmlFormUtil.getConceptByUuidOrId(answerConceptId);
				
				codedAnswerMap.put(ansConcept.getUuid(), StringUtils.isNotBlank(answerLabel) ? answerLabel : ansConcept
				        .getName().getName());
			}
		}
		return codedAnswerMap;
	}
	
	/**
	 * Generates top level json object
	 * 
	 * @return
	 */
	private ObjectNode generateQuestionStub() {
		ObjectNode obsStub = JsonNodeFactory.instance.objectNode();
		obsStub.put("label", getQuestionLabel);
		obsStub.put("type", "obs");
		
		return obsStub;
	}
	
	/**
	 * Generates answer options
	 * 
	 * @return
	 */
	private ObjectNode generateOptionsStub() {
		ObjectNode optionsNode = JsonNodeFactory.instance.objectNode();
		optionsNode.put("concept", getConceptUuid());
		optionsNode.put("rendering", "multiCheckbox");
		return optionsNode;
	}
	
	public String getConceptUuid() {
		return conceptUuid;
	}
	
	public void setConceptUuid(String conceptUuid) {
		this.conceptUuid = conceptUuid;
	}
	
	public List<Element> getElements() {
		return elements;
	}
	
	public void setElements(List<Element> elements) {
		this.elements = elements;
	}
	
	public String getGetQuestionLabel() {
		return getQuestionLabel;
	}
	
	public void setGetQuestionLabel(String getQuestionLabel) {
		this.getQuestionLabel = getQuestionLabel;
	}
}
