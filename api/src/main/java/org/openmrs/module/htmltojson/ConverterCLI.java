package org.openmrs.module.htmltojson;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ConverterCLI {
	
	public static void main(String[] args) {

        if (args.length < 1) {
            System.out.println("❌ Missing arguments!");
            System.out.println("\n📌 Usage:");
            System.out.println("mvn exec:java -Dexec.mainClass=\"org.openmrs.module.htmltojson.ConverterCLI\" -Dexec.args=\"input.html output.json\"");
            return;
        }

        String inputPath = args[0];
        String outputPath = args.length > 1 ? args[1] : "output.json";

        File inputFile = new File(inputPath);

        if (!inputFile.exists()) {
            System.out.println("❌ File not found: " + inputPath);
            return;
        }

        if (!inputPath.endsWith(".html")) {
            System.out.println("❌ Only .html files allowed");
            return;
        }

        try {
            System.out.println("🔄 Converting: " + inputPath);

            String html = new String(Files.readAllBytes(Paths.get(inputPath)));

            Document doc = Jsoup.parse(html);
            Elements elements = doc.getAllElements();

            ArrayNode array = JsonNodeFactory.instance.arrayNode();

            elements.forEach(el -> {
                if (el.tagName().equalsIgnoreCase("obs") ||
                    el.tagName().equalsIgnoreCase("obsgroup")) {

                    ObjectNode node = JsonNodeFactory.instance.objectNode();
                    node.put("tag", el.tagName());

                    String conceptId = el.hasAttr("conceptId") ?
                            el.attr("conceptId") : "N/A";

                    node.put("conceptId", conceptId);

                    array.add(node);
                }
            });

            ObjectMapper mapper = new ObjectMapper();
            String prettyJson = mapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(array);

            Files.write(Paths.get(outputPath), prettyJson.getBytes());

            System.out.println("✅ Output saved: " + outputPath);

        } catch (Exception e) {
            System.out.println("❌ Error:");
            e.printStackTrace();
        }
    }
}
