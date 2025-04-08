package org.openmrs.module.htmltojson.htmltojson;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FormConverterRunner {
    public static void main(String[] args) {
        try {
            // 1. Load a sample HFE form (XML file)
            File inputFile = new File("sample-form.xml");
            Document doc = Jsoup.parse(inputFile, "UTF-8");

            // 2. Extract the first <obs> tag for test
            Element obsTag = doc.selectFirst("obs");
            if (obsTag != null) {
                HtmlFormDataPoint result = HtmlObsTagExtractor.extractObsTag(obsTag);
                System.out.println("Extracted JSON:");
                System.out.println(result.toJson());
            } else {
                System.out.println("No <obs> tag found in the sample form.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
