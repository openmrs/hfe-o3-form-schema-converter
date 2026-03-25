package org.openmrs.module.htmltojson;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ConverterCLI {
	
	public static void main(String[] args) {
		
		if (args.length < 2) {
			System.out.println("❌ Missing arguments!");
			System.out.println("\n📌 Usage:");
			System.out
			        .println("mvn exec:java -Dexec.mainClass=\"org.openmrs.module.htmltojson.ConverterCLI\" -Dexec.args=\"input.html output.json [--nested=true]\"");
			return;
		}
		
		String inputPath = args[0];
		String outputPath = args[1];
		
		// Default: flat mode
		boolean nested = false;
		
		// Optional flag
		if (args.length > 2 && args[2].equalsIgnoreCase("--nested=true")) {
			nested = true;
		}
		
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
			
			ObjectMapper mapper = new ObjectMapper();
			ArrayNode resultArray = mapper.createArrayNode();
			
			Element body = doc.body();
			
			for (Element el : body.children()) {
				if (isRelevantTag(el)) {
					if (nested) {
						resultArray.add(processElement(el, mapper, 0));
					} else {
						resultArray.add(createFlatNode(el, mapper));
					}
				}
			}
			
			String prettyJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(resultArray);
			
			Files.write(Paths.get(outputPath), prettyJson.getBytes());
			
			System.out.println("✅ Output saved: " + outputPath);
			
		}
		catch (Exception e) {
			System.out.println("❌ Error:");
			e.printStackTrace();
		}
	}
	
	// ✅ Filter only required tags
	private static boolean isRelevantTag(Element el) {
		return el.tagName().equalsIgnoreCase("obs") || el.tagName().equalsIgnoreCase("obsgroup");
	}
	
	// ✅ Flat mode (old behavior)
	private static ObjectNode createFlatNode(Element el, ObjectMapper mapper) {
		ObjectNode node = mapper.createObjectNode();
		
		node.put("tag", el.tagName());
		
		String conceptId = el.attr("conceptId").trim();
		node.put("conceptId", conceptId.isEmpty() ? "N/A" : conceptId);
		
		return node;
	}
	
	// 🔥 Recursive nested parsing
	private static ObjectNode processElement(Element el, ObjectMapper mapper, int depth) {
		ObjectNode node = mapper.createObjectNode();
		
		String tag = el.tagName();
		node.put("tag", tag);
		
		String conceptId = el.attr("conceptId").trim();
		node.put("conceptId", conceptId.isEmpty() ? "N/A" : conceptId);
		
		// Depth info
		node.put("depth", depth);
		
		// Validation warning
		if (conceptId.isEmpty()) {
			System.out.println("⚠ Warning: Missing conceptId in tag: " + tag);
		}
		
		// Handle obsgroup children
		if (tag.equalsIgnoreCase("obsgroup")) {
			ArrayNode childrenArray = mapper.createArrayNode();
			
			for (Element child : el.children()) {
				if (isRelevantTag(child)) {
					childrenArray.add(processElement(child, mapper, depth + 1));
				}
			}
			
			if (childrenArray.size() == 0) {
				System.out.println("⚠ Warning: obsgroup with no children");
			} else {
				node.set("children", childrenArray);
			}
		}
		
		return node;
	}
}
