package org.openmrs.module.htmltojson.htmltojson;

import java.util.HashMap;
import java.util.Map;

/**
 * Model for html form data point/tag i.e. <obs></obs>
 */
public class HtmlFormDataPoint {
	
	private String conceptUUID;
	
	private Integer conceptId;
	
	private String conceptName;
	
	private String dataType; // i.e. obs, obsGroup etc
	
	private String questionLabel = "";
	
	private boolean requiredField = false;
	
	private String rendering = "";
	
	private String formFieldId = "";
	
	private Map<String, String> answersList = new HashMap<String, String>();
	
	public String getConceptUUID() {
		return conceptUUID;
	}
	
	public void setConceptUUID(String conceptUUID) {
		this.conceptUUID = conceptUUID;
	}
	
	public Integer getConceptId() {
		return conceptId;
	}
	
	public void setConceptId(Integer conceptId) {
		this.conceptId = conceptId;
	}
	
	public String getConceptName() {
		return conceptName;
	}
	
	public void setConceptName(String conceptName) {
		this.conceptName = conceptName;
	}
	
	public String getDataType() {
		return dataType;
	}
	
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	
	public String getQuestionLabel() {
		return questionLabel;
	}
	
	public void setQuestionLabel(String questionLabel) {
		this.questionLabel = questionLabel;
	}
	
	public boolean getRequiredField() {
		return requiredField;
	}
	
	public void setRequiredField(boolean requiredField) {
		this.requiredField = requiredField;
	}
	
	public Map<String, String> getAnswersList() {
		return answersList;
	}
	
	public void setAnswersList(Map<String, String> answersList) {
		this.answersList = answersList;
	}
	
	public boolean isRequiredField() {
		return requiredField;
	}
	
	public String getRendering() {
		return rendering;
	}
	
	public void setRendering(String rendering) {
		this.rendering = rendering;
	}
	
	public String getFormFieldId() {
		return formFieldId;
	}
	
	public void setFormFieldId(String formFieldId) {
		this.formFieldId = formFieldId;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (o == null)
			return false;
		if (getClass() != o.getClass())
			return false;
		HtmlFormDataPoint pn = (HtmlFormDataPoint) o;
		return pn.getConceptId().equals(this.getConceptId());
	}
	
	@Override
	public int hashCode() {
		return getConceptId().hashCode();
	}
	
	@Override
	public String toString() {
		return "HtmlFormDataPoint{" + "conceptUUID='" + conceptUUID + '\'' + ", conceptId=" + conceptId + ", conceptName='"
		        + conceptName + '\'' + ", dataType='" + dataType + '\'' + ", questionLabel='" + questionLabel + '\''
		        + ", requiredField=" + requiredField + ", rendering='" + rendering + '\'' + ", formFieldId='" + formFieldId
		        + '\'' + ", answersList=" + answersList + '}';
	}
}
