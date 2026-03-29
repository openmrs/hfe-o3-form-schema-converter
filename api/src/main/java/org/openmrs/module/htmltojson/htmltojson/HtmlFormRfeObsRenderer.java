package org.openmrs.module.htmltojson.htmltojson;

import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

public class HtmlFormRfeObsRenderer {
    
    private HtmlFormDataPoint dataPoint;
    
    public HtmlFormRfeObsRenderer(HtmlFormDataPoint dataPoint) {
        this.dataPoint = dataPoint;
    }
    
    public ObjectNode render() {
        if (dataPoint == null)
            return null;
            
        String mappedRendering = "text";
        String rendering = dataPoint.getRendering();
        
        if (rendering != null) {
            switch(rendering) {
                case "number":
                    mappedRendering = "number";
                    break;
                case "select":
                case "radio":
                case "multiCheckbox":
                    mappedRendering = "select";
                    break;
                default:
                    mappedRendering = "text";
            }
        }
        
        ObjectNode obsStub = JsonNodeFactory.instance.objectNode();
        obsStub.put("id", dataPoint.getConceptUUID());
        obsStub.put("label", dataPoint.getQuestionLabel());
        obsStub.put("type", "obs");
        
        ObjectNode optionsNode = JsonNodeFactory.instance.objectNode();
        optionsNode.put("concept", dataPoint.getConceptUUID());
        optionsNode.put("rendering", mappedRendering);
        
        obsStub.put("questionOptions", optionsNode);
        
        return obsStub;
    }
}
