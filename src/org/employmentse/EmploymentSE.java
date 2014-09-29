package org.employmentse;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.employmentse.content.handler.JSONTableContentHandler;
import org.employmentse.parser.TSVParser;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class EmploymentSE {

	public static void main(String[] args) throws IOException, SAXException, TikaException {
		String[] headers = 
				{"Posted Date",
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

		ContentHandler handler = new JSONTableContentHandler("assets/output");
        Metadata metadata = new Metadata();
        TSVParser parser = new TSVParser(headers);
        
        parser.parse(input, handler, metadata, new ParseContext());
        
        System.out.println("Job processing has finished!");
        
//        String plainText = handler.toString();
//        
//        System.out.println("handler");
//        System.out.println(plainText);
	}
	
}
