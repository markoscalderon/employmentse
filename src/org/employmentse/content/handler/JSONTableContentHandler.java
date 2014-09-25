package org.employmentse.content.handler;

import org.apache.tika.sax.SafeContentHandler;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class JSONTableContentHandler extends SafeContentHandler {

	public JSONTableContentHandler(ContentHandler handler) {
		super(handler);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		// TODO Auto-generated method stub
		System.out.println(ch);
		System.out.println(start);
		System.out.println(length);
		super.characters(ch, start, length);
	}

	@Override
	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.endDocument();
	}

	@Override
	public void endElement(String uri, String localName, String name)
			throws SAXException {
		// TODO Auto-generated method stub
		super.endElement(uri, localName, name);
	}

	@Override
	public void ignorableWhitespace(char[] ch, int start, int length)
			throws SAXException {
		// TODO Auto-generated method stub
		super.ignorableWhitespace(ch, start, length);
	}

	@Override
	protected boolean isInvalid(int ch) {
		// TODO Auto-generated method stub
		return super.isInvalid(ch);
	}

	@Override
	public void startElement(String arg0, String arg1, String arg2,
			Attributes arg3) throws SAXException {
		// TODO Auto-generated method stub
		super.startElement(arg0, arg1, arg2, arg3);
	}

	@Override
	protected void writeReplacement(Output output) throws SAXException {
		// TODO Auto-generated method stub
		super.writeReplacement(output);
	}
	
	

}
