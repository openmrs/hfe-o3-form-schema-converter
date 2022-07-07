package org.openmrs.module.htmltojson.htmltojson;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;

import java.util.Map;

/**
 * Renderer for html <obs></obs> tag
 */
public class HtmlFormObsRenderer {
	
	private HtmlFormDataPoint dataPoint;
	
	public HtmlFormObsRenderer(HtmlFormDataPoint dataPoint) {
		this.dataPoint = dataPoint;
	}
	
	/**
	 * Calls the appropriate renderer
	 * 
	 * @return
	 */
	public ObjectNode render() {
		if (dataPoint == null)
			return null;
		
		Concept concept = Context.getConceptService().getConceptByUuid(dataPoint.getConceptUUID());
		
		if (concept.getDatatype().isCoded()) {
			return renderCodedObs();
		} else if (concept.getDatatype().isBoolean()) {
			return renderBooleansObs();
		} else if (concept.getDatatype().isText()) {
			return renderTextObs();
		} else if (concept.getDatatype().isDateTime() || concept.getDatatype().isDate()) {
			return renderDateObs();
		} else if (concept.getDatatype().isNumeric()) {
			return renderNumericObs();
		}
		return null;
	}
	
	private ObjectNode renderNumericObs() {
		ObjectNode obsStub = generateQuestionStub();
		ObjectNode optionsNode = generateOptionsStub();
		obsStub.put("questionOptions", optionsNode);
		return obsStub;
	}
	
	private ObjectNode renderDateObs() {
		ObjectNode obsStub = generateQuestionStub();
		ObjectNode optionsNode = generateOptionsStub();
		obsStub.put("questionOptions", optionsNode);
		return obsStub;
	}
	
	private ObjectNode renderTextObs() {
		ObjectNode obsStub = generateQuestionStub();
		ObjectNode optionsNode = generateOptionsStub();
		obsStub.put("questionOptions", optionsNode);
		return obsStub;
	}
	
	private ObjectNode renderBooleansObs() {
		ObjectNode obsStub = generateQuestionStub();
		ObjectNode optionsNode = generateOptionsStub();
		
		ArrayNode answersList = JsonNodeFactory.instance.arrayNode();
		
		ObjectNode yesAnswerObject = JsonNodeFactory.instance.objectNode();
		yesAnswerObject.put("concept", "1065AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
		yesAnswerObject.put("label", "Yes");
		answersList.add(yesAnswerObject);
		
		ObjectNode noAnswerObject = JsonNodeFactory.instance.objectNode();
		noAnswerObject.put("concept", "1066AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
		noAnswerObject.put("label", "No");
		answersList.add(noAnswerObject);
		
		optionsNode.put("answers", answersList);
		
		obsStub.put("questionOptions", optionsNode);
		
		return obsStub;
	}
	
	private ObjectNode generateQuestionStub() {
		ObjectNode obsStub = JsonNodeFactory.instance.objectNode();
		obsStub.put("label", dataPoint.getQuestionLabel());
		obsStub.put("type", "obs");
		if (dataPoint.getFormFieldId() != null) {
			obsStub.put("id", dataPoint.getFormFieldId());
		}
		
		return obsStub;
	}
	
	private ObjectNode generateOptionsStub() {
		ObjectNode optionsNode = JsonNodeFactory.instance.objectNode();
		optionsNode.put("concept", dataPoint.getConceptUUID());
		optionsNode.put("rendering", dataPoint.getRendering());
		return optionsNode;
	}
	
	/**
	 * Renderer for coded observation
	 * 
	 * @return
	 */
	private ObjectNode renderCodedObs() {
		ObjectNode obsStub = generateQuestionStub();
		ObjectNode optionsNode = generateOptionsStub();
		
		ArrayNode answersList = JsonNodeFactory.instance.arrayNode();
		
		if (!dataPoint.getAnswersList().isEmpty()) {
			for (Map.Entry<String, String> conceptLabelPair : dataPoint.getAnswersList().entrySet()) {
				ObjectNode answerObject = JsonNodeFactory.instance.objectNode();
				answerObject.put("concept", conceptLabelPair.getKey());
				answerObject.put("label", conceptLabelPair.getValue());
				answersList.add(answerObject);
			}
			optionsNode.put("answers", answersList);
		}
		obsStub.put("questionOptions", optionsNode);
		
		return obsStub;
	}
	
	public HtmlFormDataPoint getDataPoint() {
		return dataPoint;
	}
	
	public void setDataPoint(HtmlFormDataPoint dataPoint) {
		this.dataPoint = dataPoint;
	}
}
