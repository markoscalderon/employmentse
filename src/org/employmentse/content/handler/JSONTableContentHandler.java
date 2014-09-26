package org.employmentse.content.handler;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.tika.sax.ToTextContentHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class JSONTableContentHandler extends  ToTextContentHandler {

	private String TABLE = "table";
	private String TD = "td";
	private String TH = "th";
	private String TR = "tr";
	
	private enum DocumentPosition {
		STARTING,
		HEADER_ROW,
		CONTENT
	}
	
	private boolean open = false; 
	
	private DocumentPosition status = DocumentPosition.STARTING;
	List<String> headers = new ArrayList<String>();
	String currentElement = "";
	List<String> currentRow = new ArrayList<String>(); 
	
	
	public JSONTableContentHandler() {
	}
	
	public JSONTableContentHandler(OutputStream stream) {
		super(stream);
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
				System.out.println("{");
				for (int i = 0; i < headers.size(); i++) {
					//write to json parellel-y
					String jsonRow = headers.get(i) + ": " + currentRow.get(i);
					if (i != headers.size()-1) {
						jsonRow += ", ";
					}
						
					System.out.println("\t" + jsonRow);
					
				}
				System.out.println("}");
				
				currentRow.clear();
			}
			
		}
		
	}

	@Override
	public void endDocument() throws SAXException {
		System.out.println(headers);
	}

	
}
