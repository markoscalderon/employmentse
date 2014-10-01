package org.employmentse;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.ToXMLContentHandler;
import org.employmentse.content.handler.JSONTableContentHandler;
import org.employmentse.parser.TSVParser;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class EmploymentSE {

	public static void main(String[] args) throws IOException, SAXException, TikaException {
		String[] headers = {"Posted Date",
				"Location",
				"Department",
				"Title",
				"EMPTY",
				"Salary",
				"Start",
				"Duration",
				"Job Type",
				"Applications",
				"Company",
				"Contact Person",
				"Phone Number",
				"Fax Number",
				"Location",
				"Latitude",
				"Longitude",
				"First Seen Date",
				"URL",
				"Last Seen Date"};
		
		InputStream input = new FileInputStream("assets/test.tsv");
		
//		========================= OUTPUT JSON FILES ==========================//  
		ContentHandler handler = new JSONTableContentHandler("assets/output");
        Metadata metadata = new Metadata();
        TSVParser parser = new TSVParser(headers);
        parser.parse(input, handler, metadata, new ParseContext());
//		======================================================================//  
		
//		========================= OUTPUT XHTML FILE ==========================//  		
//		ContentHandler handler = new ToXMLContentHandler();
//        Metadata metadata = new Metadata();
//        TSVParser parser = new TSVParser(headers);
//        parser.parse(input, handler, metadata, new ParseContext());
//                                      
//        PrintWriter writer = new PrintWriter("assets/output/JobPositions.xhtml","UTF-8");
//        writer.println(handler.toString()); 
//        writer.close();	
//		======================================================================//
               
        System.out.println("Job processing has finished!");
	}
}
