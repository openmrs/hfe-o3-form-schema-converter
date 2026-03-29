package org.openmrs.module.htmltojson.htmltojson;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

public class RfeSchemaBuilder {

    public static ObjectNode buildRfeSchema(String formName, ArrayNode questions) {
        ObjectNode formJson = JsonNodeFactory.instance.objectNode();
        formJson.put("name", formName != null ? formName : "");

        ArrayNode pages = JsonNodeFactory.instance.arrayNode();
        ObjectNode page = JsonNodeFactory.instance.objectNode();
        page.put("label", "Page 1");

        ArrayNode sections = JsonNodeFactory.instance.arrayNode();
        ObjectNode section = JsonNodeFactory.instance.objectNode();
        section.put("label", "Section 1");
        section.put("isExpanded", true);
        section.put("questions", questions);

        sections.add(section);
        page.put("sections", sections);
        pages.add(page);

        formJson.put("pages", pages);

        return formJson;
    }
}
