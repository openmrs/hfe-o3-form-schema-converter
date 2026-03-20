package org.openmrs.module.htmltojson.htmltojson;

import org.codehaus.jackson.node.ObjectNode;
import org.junit.Test;

import static org.junit.Assert.*;

public class HtmlFormObsRendererTest {
	
	@Test
	public void shouldIncludeRequiredField() {
		
		HtmlFormDataPoint dp = new HtmlFormDataPoint();
		dp.setQuestionLabel("Age");
		dp.setRequiredField(true);
		
		HtmlFormObsRenderer renderer = new HtmlFormObsRenderer(dp);
		ObjectNode result = renderer.render();
		
		assertNotNull(result);
		assertTrue(result.get("required").asBoolean());
	}
}
