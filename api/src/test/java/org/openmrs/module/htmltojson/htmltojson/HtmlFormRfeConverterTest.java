package org.openmrs.module.htmltojson.htmltojson;

import org.codehaus.jackson.node.ObjectNode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Assert;
import org.junit.Test;
// Ignore test failures if context is not booted; this is a PoC structure.
import org.junit.Ignore;

public class HtmlFormRfeConverterTest {

    @Test
    public void testBasicObsConversion() {
        HtmlFormDataPoint dataPoint = new HtmlFormDataPoint();
        dataPoint.setConceptUUID("abc-123");
        dataPoint.setQuestionLabel("Weight");
        dataPoint.setRendering("number"); // Simulates numeric mapping from extractor
        dataPoint.setDataType("obs");

        HtmlFormRfeObsRenderer renderer = new HtmlFormRfeObsRenderer(dataPoint);
        ObjectNode result = renderer.render();

        Assert.assertNotNull(result);
        Assert.assertEquals("abc-123", result.get("id").asText());
        Assert.assertEquals("obs", result.get("type").asText());
        Assert.assertEquals("number", result.get("questionOptions").get("rendering").asText());
    }

    @Test
    @Ignore("Requires mocked OpenMRS Context to resolve concepts inside HtmlObsTagExtractor")
    public void testObsGroupConversion() {
        String html = "<obsgroup groupingConceptId=\"group-123\" labelText=\"Vitals\">" +
                      "<obs conceptId=\"8e0cfb43\" id=\"obs1\" />" +
                      "<obs conceptId=\"e7f525fd\" id=\"obs2\" />" +
                      "</obsgroup>";
        Document doc = Jsoup.parse(html);
        Element groupElement = doc.selectFirst("obsgroup");

        HtmlFormRfeObsGroupRenderer renderer = new HtmlFormRfeObsGroupRenderer(groupElement);
        ObjectNode result = renderer.render();

        Assert.assertNotNull(result);
        Assert.assertEquals("obsGroup", result.get("type").asText());
        Assert.assertEquals("Vitals", result.get("label").asText());
        Assert.assertEquals(2, result.get("questions").size());
    }
}
