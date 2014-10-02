package org.employmentse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.ToXMLContentHandler;
import org.employmentse.content.handler.JSONTableContentHandler;
import org.employmentse.parser.TSVParser;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class EmploymentSE 
{
	public static String inputFolder  = "assets/";
	public static String outputFolder = "assets/output/";
	
	public static void main(String[] args) throws IOException, SAXException, TikaException 
	{
		String[] headers = {
							"Posted Date",
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
							"Last Seen Date"
							};
		
		final File folder = new File(inputFolder);
		long startTime = System.currentTimeMillis();
		
		for (final File fileEntry : folder.listFiles()) 
		{			
			if (!fileEntry.isDirectory())
			{
				String fileType = fileEntry.getName().substring(fileEntry.getName().indexOf(".",-1),fileEntry.getName().length());
				String fileName = fileEntry.getName().substring(0, fileEntry.getName().indexOf(".",-1));
				
				if (!fileName.equals("") && fileType.equals(".tsv"))
				{						
					InputStream input = new FileInputStream(inputFolder+fileEntry.getName());
									
//					============================ OUTPUT JSON FILES =============================//  
					ContentHandler handler = new JSONTableContentHandler(outputFolder);
					Metadata metadata = new Metadata();
					TSVParser parser = new TSVParser(headers);
					parser.parse(input, handler, metadata, new ParseContext());
//					============================================================================//  
				
//					============================ OUTPUT XHTML FILE =============================//  		
//					ContentHandler handler = new ToXMLContentHandler();
//					Metadata metadata = new Metadata();
//					TSVParser parser = new TSVParser(headers);
//					parser.parse(input, handler, metadata, new ParseContext());
//
//					PrintWriter writer = new PrintWriter(outputFolder+fileName+".xhtml","UTF-8");
//					writer.println(handler.toString()); 
//					writer.close();					
//					============================================================================//
					System.out.println(fileEntry.getName()+ ":\t done");
				}	
			}
		}		
		long finishTime = System.currentTimeMillis();
		long elapsedTime = finishTime-startTime;

		long hh = elapsedTime/3600000;
		long mm = (elapsedTime-hh*3600000)/60000;
		long ss = (elapsedTime-hh*3600000-mm*60000)/1000;
		long ms = (elapsedTime-hh*3600000-mm*60000-ss*1000);
		
		String HH = String.valueOf(hh); if (hh<10){HH="0"+HH;}
		String MM = String.valueOf(mm); if (mm<10){MM="0"+MM;}
		String SS = String.valueOf(ss); if (ss<10){SS="0"+SS;}
		String MS = String.valueOf(ms); if (ms<10){MS="0"+MS;}
		
		System.out.println("\nJob parsing completed\nProcessing time: "+HH+":"+MM+":"+SS+"."+MS);
	}
}
