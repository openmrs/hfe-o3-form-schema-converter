package org.openmrs.module.htmltojson.htmltojson;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;

import java.util.HashMap;
import java.util.Map;

public class HtmlObsTagExtractor {
	
	public static HtmlFormDataPoint extractObsTag(Element obsTag) {
		String conceptUUId = obsTag.attr("conceptId");
		
		Concept concept = HtmlFormUtil.getConceptByUuidOrId(conceptUUId);
		
		if (concept == null) {
			System.out.println("Concept UUID Invalid: " + conceptUUId);
			return null;
		}
		
		HtmlFormDataPoint dataPoint = new HtmlFormDataPoint();
		String requiredField = obsTag.attr("required");
		String idAttr = obsTag.attr("id");
		String obsLabel = HtmlFormUtil.extractQuestionLabel(obsTag);
		
		dataPoint.setQuestionLabel(obsLabel);
		
		if (StringUtils.isNotBlank(requiredField) && requiredField.equals("true")) {
			dataPoint.setRequiredField(true);
		}
		
		if (StringUtils.isNotBlank(idAttr)) {
			dataPoint.setFormFieldId(idAttr);
		}
		
		String cUuid = concept.getUuid();
		dataPoint.setConceptUUID(cUuid);
		
		dataPoint.setConceptId(concept.getConceptId());
		dataPoint.setConceptName(concept.getName().getName());
		dataPoint.setDataType("obs");
		String rendering = obsTag.attr("style");
		String hasRowsAttr = obsTag.attr("rows");
		String hasColsAttr = obsTag.attr("cols");
		if (concept.getDatatype().isCoded()) {
			
			if (StringUtils.isNotBlank(rendering)) {
				if (rendering.equals("radio")) {
					dataPoint.setRendering(rendering);
				} else if (rendering.equals("checkbox")) {
					dataPoint.setRendering("multiCheckbox");
				} else if (rendering.equals("dropdown")) {
					dataPoint.setRendering("select");
				}
			} else {
				dataPoint.setRendering("select");
			}
			
			String answerConceptId = obsTag.attr("answerConceptId");
			String answerConceptIds = obsTag.attr("answerConceptIds");
			String answerClasses = obsTag.attr("answerClasses");//set rendering to drug
			
			String answerLabel = obsTag.attr("answerLabel");
			String answerLabels = obsTag.attr("answerLabels");
			Map<String, String> codedAnswerMap = new HashMap<String, String>();
			
			if (StringUtils.isNotBlank(answerConceptId)) {
				Concept ansConcept = HtmlFormUtil.getConceptByUuidOrId(answerConceptId);
				
				codedAnswerMap.put(ansConcept.getUuid(), StringUtils.isNotBlank(answerLabel) ? answerLabel : ansConcept
				        .getName().getName());
			} else if (StringUtils.isNotBlank(answerConceptIds)) {
				
				String conceptArray[] = answerConceptIds.split(",");
				String labelsArray[] = null;
				if (StringUtils.isNotBlank(answerLabels)) {
					labelsArray = answerLabels.split(",");
				}
				
				boolean hasCustomLabels = labelsArray != null && labelsArray.length > 0;
				for (int i = 0; i < conceptArray.length; i++) {
					Concept ansConcept = HtmlFormUtil.getConceptByUuidOrId(conceptArray[i].trim());
					if (ansConcept != null) {
						String ansConceptUuid = ansConcept.getUuid();
						String ansLabel = hasCustomLabels ? labelsArray[i] : ansConcept.getName().getName();
						codedAnswerMap.put(ansConceptUuid, ansLabel.trim());
					}
				}
			} else { // get all answers from dictionary
				for (ConceptAnswer conceptAnswer : concept.getAnswers()) {
					codedAnswerMap.put(conceptAnswer.getAnswerConcept().getUuid(), conceptAnswer.getAnswerConcept()
					        .getName().getName());
				}
				
			}
			
			if (StringUtils.isNotBlank(answerClasses) && answerClasses.equals("Drug")) {
				dataPoint.setRendering("drug");
			}
			dataPoint.setAnswersList(codedAnswerMap);
		} else if (concept.getDatatype().isNumeric()) {
			dataPoint.setRendering("number");
		} else if (concept.getDatatype().isDate() || concept.getDatatype().isDateTime()) {
			dataPoint.setRendering("date");
			
		} else if (concept.getDatatype().isText()) {
			
			if (StringUtils.isNotBlank(rendering)) {
				if (rendering.equals("textarea")) {
					dataPoint.setRendering(rendering);
				}
			} else if (StringUtils.isNotBlank(hasRowsAttr) || StringUtils.isNotBlank(hasColsAttr)) {
				dataPoint.setRendering("textarea");
			} else {
				dataPoint.setRendering("text");
			}
		} else if (concept.getDatatype().isBoolean()) {
			dataPoint.setRendering("select");
		}
		
		return dataPoint;
	}
}
