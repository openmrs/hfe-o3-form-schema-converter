package org.openmrs.module.htmltojson;

public class ConverterCLI {
	
	public static void main(String[] args) {
		
		if (args.length < 2) {
			System.out.println("Usage: java ConverterCLI input.html output.json");
			return;
		}
		
		String inputFile = args[0];
		String outputFile = args[1];
		
		System.out.println("Converting file: " + inputFile);
		System.out.println("Output will be saved to: " + outputFile);
		
		// TODO: integrate converter logic here
		System.out.println("Conversion completed!");
	}
}
