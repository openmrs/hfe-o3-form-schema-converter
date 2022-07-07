package org.openmrs.module.htmltojson.htmltojson;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.jsoup.nodes.Element;
import org.openmrs.Concept;

/**
 * Renderer for html <obsgroup></obsgroup> tag
 */
public class HtmlFormObsGroupRenderer {
	
	private Element obsGroupTag;
	
	public HtmlFormObsGroupRenderer(Element obsGroupTag) {
		
		this.obsGroupTag = obsGroupTag;
	}
	
	/**
	 * renderer
	 * 
	 * @return
	 */
	public ObjectNode render() {
		if (obsGroupTag == null)
			return null;
		
		String groupingConceptUuid = obsGroupTag.attr("groupingConceptId");
		
		if (StringUtils.isBlank(groupingConceptUuid))
			return null;
		
		Concept groupConcept = HtmlFormUtil.getConceptByUuidOrId(groupingConceptUuid);
		if (groupConcept == null) {
			return null;
		}
		
		ArrayNode questionsList = JsonNodeFactory.instance.arrayNode();
		
		for (Element element : obsGroupTag.select("obs")) {
			//System.out.println("Building obsgroup questions: ");
			
			HtmlFormDataPoint dataPoint = HtmlObsTagExtractor.extractObsTag(element);
			if (dataPoint == null) {
				System.out.println("Encountered invalid concept UUID in the form schema. Ignoring the tag ");
				continue;
			}
			HtmlFormObsRenderer renderer = new HtmlFormObsRenderer(dataPoint);
			ObjectNode obsJson = renderer.render();
			questionsList.add(obsJson);
		}
		ObjectNode obsGroupObject = generateGroupStub(groupConcept.getUuid());
		obsGroupObject.put("questions", questionsList);
		return obsGroupObject;
	}
	
	private ObjectNode generateGroupStub(String groupingConceptUuid) {
		String groupLabel = obsGroupTag.attr("labelText");
		
		ObjectNode obsStub = JsonNodeFactory.instance.objectNode();
		obsStub.put("label", groupLabel);
		obsStub.put("type", "obsGroup");
		
		ObjectNode optionsNode = JsonNodeFactory.instance.objectNode();
		optionsNode.put("concept", groupingConceptUuid);
		optionsNode.put("rendering", HtmlFormUtil.isRepeat(obsGroupTag) ? "repeat" : "group");
		obsStub.put("questionOptions", optionsNode);
		return obsStub;
	}
	
	public Element getObsGroupTag() {
		return obsGroupTag;
	}
	
	public void setObsGroupTag(Element obsGroupTag) {
		this.obsGroupTag = obsGroupTag;
	}
}
