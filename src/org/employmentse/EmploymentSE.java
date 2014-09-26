package org.employmentse;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.ToHTMLContentHandler;
import org.employmentse.content.handler.JSONTableContentHandler;
import org.employmentse.parser.TSVParser;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class EmploymentSE {

	public static void main(String[] args) throws IOException, SAXException, TikaException {
		System.out.println("HW1");
		
		InputStream input = new FileInputStream("assets/test.txt");
		ContentHandler handler = new JSONTableContentHandler();
        Metadata metadata = new Metadata();
        TSVParser parser = new TSVParser();
        parser.parse(input, handler, metadata, new ParseContext());
        
        String plainText = handler.toString();
        
        System.out.println("handler");
        System.out.println(plainText);
	}
	
}
