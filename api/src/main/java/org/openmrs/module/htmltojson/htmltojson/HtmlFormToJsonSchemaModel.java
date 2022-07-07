package org.openmrs.module.htmltojson.htmltojson;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import java.io.IOException;
import java.util.Set;

public class HtmlFormToJsonSchemaModel {
	
	private String formUUID;
	
	private String formName;
	
	private Set<HtmlFormDataPoint> dataPoints;
	
	public HtmlFormToJsonSchemaModel(String formUUID, String formName) {
		this.formUUID = formUUID;
		this.formName = formName;
	}
	
	public HtmlFormToJsonSchemaModel(String formUUID, String formName, Set<HtmlFormDataPoint> dataPoints) {
		this.formUUID = formUUID;
		this.formName = formName;
		this.dataPoints = dataPoints;
	}
	
	public String getFormUUID() {
		return formUUID;
	}
	
	public void setFormUUID(String formUUID) {
		this.formUUID = formUUID;
	}
	
	public String getFormName() {
		return formName;
	}
	
	public void setFormName(String formName) {
		this.formName = formName;
	}
	
	public Set<HtmlFormDataPoint> getDataPoints() {
		return dataPoints;
	}
	
	public void setDataPoints(Set<HtmlFormDataPoint> dataPoints) {
		this.dataPoints = dataPoints;
	}
	
	public static ObjectNode getBaselineJsonSchema() {
		
		String baselineJsonSchemaString = "{\"name\":\"POC General Form v1.0\",\"pages\":[{\"label\":\"Encounter details\",\"sections\":[{\"label\":\"Encounter details\",\"isExpanded\":\"true\",\"questions\":[{\"label\":\"Visit date\",\"type\":\"encounterDatetime\",\"questionOptions\":{\"rendering\":\"date\",\"showWeeks\":\"\"},\"id\":\"encounterDate\",\"required\":\"true\"},{\"label\":\"Provider\",\"type\":\"encounterProvider\",\"questionOptions\":{\"rendering\":\"ui-select-extended\"},\"id\":\"provider\",\"required\":\"true\"},{\"label\":\"Location\",\"type\":\"encounterLocation\",\"questionOptions\":{\"rendering\":\"ui-select-extended\"},\"id\":\"location\",\"required\":\"true\"}]}]}],\"processor\":\"EncounterFormProcessor\",\"uuid\":\"xxxx\",\"referencedForms\":[]}";
		
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode jsonNode = null;
		try {
			jsonNode = (ObjectNode) mapper.readTree(baselineJsonSchemaString);
			
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return jsonNode;
	}
	
}
