package org.openmrs.module.htmltojson.htmltojson;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.jsoup.nodes.Element;

public class HtmlFormRfeObsGroupRenderer {

    private Element obsGroupTag;

    public HtmlFormRfeObsGroupRenderer(Element obsGroupTag) {
        this.obsGroupTag = obsGroupTag;
    }

    public ObjectNode render() {
        if (obsGroupTag == null)
            return null;

        String groupLabel = obsGroupTag.attr("labelText");
        if (StringUtils.isBlank(groupLabel)) {
            groupLabel = "Group";
        }

        ArrayNode questionsList = JsonNodeFactory.instance.arrayNode();

        for (Element child : obsGroupTag.select("obs")) {
            HtmlFormDataPoint dataPoint = HtmlObsTagExtractor.extractObsTag(child);
            if (dataPoint == null) {
                continue;
            }
            HtmlFormRfeObsRenderer renderer = new HtmlFormRfeObsRenderer(dataPoint);
            ObjectNode obsJson = renderer.render();
            if (obsJson != null) {
                questionsList.add(obsJson);
            }
        }

        ObjectNode obsGroupObject = JsonNodeFactory.instance.objectNode();
        obsGroupObject.put("type", "obsGroup");
        obsGroupObject.put("label", groupLabel);
        obsGroupObject.put("questions", questionsList);

        return obsGroupObject;
    }
}
