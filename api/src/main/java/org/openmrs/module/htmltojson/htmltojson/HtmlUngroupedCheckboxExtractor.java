package org.openmrs.module.htmltojson.htmltojson;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Extractor for html <obs conceptId=xxx conceptAnswer=xxx style=checkbox></obs> tag Extractor for
 * html <obs conceptId=xxx conceptAnswer=yyy style=checkbox></obs> tag Extractor for html <obs
 * conceptId=xxx conceptAnswer=zzz style=checkbox></obs> tag
 */
public class HtmlUngroupedCheckboxExtractor {
	
	public static Map<String, List<Element>> getRelatedCheckboxes(String conceptId, String actualConceptUuid, Element parent) {
		Map<String, List<Element>> entry = new HashMap<String, List<Element>>();
		Elements similarCheckboxTags = parent.select("obs[conceptId=" + conceptId + "]");
		List<Element> relatedCheckboxTags = HtmlFormUtil.filterRelatedTags(similarCheckboxTags);
		entry.put(actualConceptUuid, relatedCheckboxTags);
		return entry;
		
	}
}
