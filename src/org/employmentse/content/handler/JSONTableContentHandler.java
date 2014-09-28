package org.employmentse.content.handler;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.apache.tika.sax.SafeContentHandler;
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
	
	private boolean open = false; 
	
	private DocumentPosition status = DocumentPosition.STARTING;
	private List<String> headers = new ArrayList<String>();
	
	private String currentElement = "";
	private List<String> currentRow = new ArrayList<String>(); 
	
	private final String directory;
	private int rowNumber = 0; 
	
	public JSONTableContentHandler(String directory) {
		super(new DefaultHandler());
		
		this.directory = directory;
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		super.characters(ch, start, length);
		
		if (status == DocumentPosition.HEADER_ROW && currentElement.equals(TH) && open) {
			headers.add(new String(ch));
		}
		
		if (status == DocumentPosition.CONTENT && currentElement.equals(TD) && open) {
			currentRow.add(new String(ch));
		}
		
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if (!localName.equals(qName)) {
			System.err.println("Localname != qName, whats the problem");
			System.err.println("localName = " + localName);
			System.err.println("qName = " + qName);
			System.exit(1);
		}
		
		currentElement = localName;
		open = true;
		
		if (currentElement.equals(TABLE)) {
			status = DocumentPosition.STARTING;
		} else if (currentElement.equals(TR) && status == DocumentPosition.STARTING){
			status = DocumentPosition.HEADER_ROW;
		} else if (currentElement.equals(TR) && status == DocumentPosition.HEADER_ROW) {
			status = DocumentPosition.CONTENT;
		}
		
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {

		open = false;
		
		if (localName.equals(TR) && status == DocumentPosition.CONTENT) {
			
			if (currentRow.size() != headers.size()) {
				System.err.println("A row didn't fill up to the proper size!!");
				System.err.println("size = " + currentRow.size() + " instead of " + headers.size());
				System.exit(1);
			} else {
				writeRowToFile(this.directory + "/" + Integer.toString(rowNumber) + ".json");
				currentRow.clear();
				rowNumber++;
			}
			
		}
		
	}
	
	private void writeRowToFile(String filename) {
		Writer writer = null;
		try {
		    writer = new BufferedWriter(new OutputStreamWriter(
		          new FileOutputStream(filename), "utf-8"));
		    
		    writer.write("{\n");
			for (int i = 0; i < headers.size(); i++) {
				//write to json parellel-y
				String jsonRow = headers.get(i) + ": " + currentRow.get(i);
				if (i != headers.size()-1) {
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

	@Override
	public void endDocument() throws SAXException {
		System.out.println(headers);
	}

	
}
