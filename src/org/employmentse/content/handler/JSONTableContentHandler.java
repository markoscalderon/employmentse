package org.employmentse.content.handler;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.apache.tika.sax.SafeContentHandler;
import org.employmentse.deduplication.Deduplicator;
import org.employmentse.deduplication.FingerPrint;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class JSONTableContentHandler extends SafeContentHandler {

	private static String TABLE = "table";
	private static String TD = "td";
	private static String TH = "th";
	private static String TR = "tr";
	
	private enum DocumentPosition {
		STARTING,
		HEADER_ROW,
		CONTENT
	}
	
	private DocumentPosition documentPosition = DocumentPosition.STARTING;
	private String currentElement = "";
	private int rowNumber = 1; 
	
	private List<String> headers = new ArrayList<String>();
	private List<String> currentRow = new ArrayList<String>(); 
	private String cellvalue = "";
	
	private final String directory;
	
	private final Deduplicator deduplicator = new Deduplicator(); 
	private boolean enableDeduplication = false;
	
	public JSONTableContentHandler(String directory, boolean enableDeduplication) {
		super(new DefaultHandler());
		
		this.directory = directory;
		this.enableDeduplication = enableDeduplication;
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		super.characters(ch, start, length);
		
		cellvalue += new String(ch);
		
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		
		validateLocalQName(localName, qName);
		
		currentElement = localName;
		
		if (currentElement.equals(TABLE)) {
			documentPosition = DocumentPosition.STARTING;
		} else if (currentElement.equals(TR) && documentPosition == DocumentPosition.STARTING){
			documentPosition = DocumentPosition.HEADER_ROW;
		} else if (currentElement.equals(TR) && documentPosition == DocumentPosition.HEADER_ROW) {
			documentPosition = DocumentPosition.CONTENT;
		}
		
		if (currentElement.equals(TH) || currentElement.equals(TD)) {
			cellvalue = "";
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {

		validateLocalQName(localName, qName);
		
		if (currentElement.equals(TH)) {
			headers.add(cellvalue);
		} else if(currentElement.equals(TD)) {
			currentRow.add(cellvalue);
		}
		
		if (localName.equals(TR) && documentPosition == DocumentPosition.CONTENT) {
			
			if (currentRow.size() != headers.size()) {
				
				System.err.println("A row didn't fill up to the proper size!!");
				System.err.println("size = " + currentRow.size() + " instead of " + headers.size());
				System.err.println("headers = " + headers);
				System.err.println("currentRow = " + currentRow);
				System.exit(1);
			} else {
				boolean addRow = true; 
				
				if (enableDeduplication) {
					FingerPrint fp1 = getCurrentRowFingerprint();
					if (deduplicator.isDuplicate(fp1)) {
						System.out.println("Found duplicate at row " + rowNumber);
						addRow = false;
					}
					deduplicator.addJob(fp1);
				}
				
				if (addRow) {
					writeRowToFile(this.directory + "/" + Integer.toString(rowNumber) + ".json");
					rowNumber++;
				}
				
				currentRow.clear();
			}
			
		}
		
	}
	
	private FingerPrint getCurrentRowFingerprint() {
		List<String> fingerprintFields = new ArrayList<>();
		fingerprintFields.add(currentRow.get(1)); // location
		fingerprintFields.add(currentRow.get(2)); // department
		fingerprintFields.add(currentRow.get(3)); // title
		fingerprintFields.add(currentRow.get(8)); // job type
		fingerprintFields.add(currentRow.get(10)); // company
		fingerprintFields.add(currentRow.get(14)); // location?
		
		return new FingerPrint(fingerprintFields);
	}

	private void writeRowToFile(String filename) {
		Writer writer = null;
		try {
		    writer = new BufferedWriter(new OutputStreamWriter(
		          new FileOutputStream(filename), "utf-8"));
		    
		    writer.write("{\n");
			for (int i = 0; i < headers.size()-1; i++) {
				//write to json parellel-y
				String jsonRow = "\""+headers.get(i) + "\": \"" + currentRow.get(i)+"\"";
				if (i != headers.size()-2) {
					jsonRow += ", ";
				}
					
				writer.write("\t" + jsonRow + "\n");
				
			}
			writer.write("}\n");
			
		    
		} catch (IOException ex) {
		  // report
		} finally {
		   try {writer.close();} catch (Exception ex) {}
		}
	}
	
	private void validateLocalQName(String localName, String qName) {
		if (!localName.equals(qName)) {
			System.err.println("Localname != qName, whats the problem");
			System.err.println("localName = " + localName);
			System.err.println("qName = " + qName);
			System.exit(1);
		}
	}
	
}
