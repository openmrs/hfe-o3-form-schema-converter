package org.openmrs.module.htmltojson;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConverterCLITest {
	
	@Test
	public void shouldConvertHtmlToJson() throws Exception {
		
		String html = "<html><body>" + "<obs conceptId=\"123\"></obs>" + "</body></html>";
		
		File input = new File("test-input.html");
		File output = new File("test-output.json");
		
		Files.write(input.toPath(), html.getBytes());
		
		ConverterCLI.main(new String[] { input.getAbsolutePath(), output.getAbsolutePath() });
		
		assertTrue(output.exists());
		
		String content = new String(Files.readAllBytes(output.toPath()));
		assertTrue(content.contains("123"));
		
		input.delete();
		output.delete();
	}
}
