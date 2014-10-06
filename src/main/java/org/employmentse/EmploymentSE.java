package org.employmentse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URISyntaxException;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.ToXMLContentHandler;
import org.employmentse.content.handler.JSONTableContentHandler;
import org.employmentse.parser.TSVParser;
import org.employmentse.splitter.JSONSplitter;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import redis.clients.jedis.Jedis;

public class EmploymentSE 
{
	public static String inputFolder  = "assets/";
	public static String outputFolder = "output/";
	
	public static void main(String[] args) throws IOException, SAXException, TikaException, URISyntaxException 
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
				
		File directory = new File(inputFolder);
		
		if (!directory.exists()) 
		{
			System.err.println("Please create a folder called 'assets' and put your *.tsv files");
			System.exit(-1);
		}
		
		directory = new File(outputFolder);
		
		if (!directory.exists()) 
		{
			directory.mkdir();
		}

		long startTime = System.currentTimeMillis();

		File dataset = new File(inputFolder);
		int counter = 1;
		Jedis redis = new Jedis("localhost");
		for (final File fileEntry : dataset.listFiles()) 
		{			
			if (!fileEntry.isDirectory())
			{
				String fileType = fileEntry.getName().substring(fileEntry.getName().indexOf(".",-1),fileEntry.getName().length());
				String fileName = fileEntry.getName().substring(0, fileEntry.getName().indexOf(".",-1));
				
//				===================== CONVERT TSV TO MULTIPLE JSON FILES ===================//
				if (!fileName.equals("") && fileType.equals(".tsv"))
				{
					InputStream input = new FileInputStream(inputFolder + fileEntry.getName());
									
//					============================ OUTPUT JSON FILES =============================//
//					directory = new File(outputFolder + fileName + "/");
//					if (!directory.exists()) directory.mkdir();
					
					ContentHandler handler = new JSONTableContentHandler(outputFolder, false);
//					ContentHandler handler = new JSONTableContentHandler(outputFolder + fileName + "/", true, redis);

					Metadata metadata = new Metadata();
					TSVParser parser = new TSVParser(headers);
					parser.parse(input, handler, metadata, new ParseContext());
					System.out.println(String.valueOf(counter)+". "+fileEntry.getName()+ ":\t done");
					counter++;
				}
//				============================================================================//
				
//				========================== CONVERT TSV TO XHTML FILE =======================//
//				if (!fileName.equals("") && fileType.equals(".tsv"))
//				{
//					InputStream input = new FileInputStream(inputFolder + fileEntry.getName());
//					ContentHandler handler = new ToXMLContentHandler();
//					Metadata metadata = new Metadata();
//					TSVParser parser = new TSVParser(headers);
//					parser.parse(input, handler, metadata, new ParseContext());
//
//					PrintWriter writer = new PrintWriter(outputFolder+fileName+".xhtml","UTF-8");
//					writer.println(handler.toString());
//					writer.close();	
//					System.out.println(String.valueOf(counter)+". "+fileEntry.getName()+ ":\t done");
//					counter++;
//				}
//				============================================================================//
				
//				======================== SPLIT JSON TO MULTIPLE JSON FILES =================//
//				if (!fileName.equals("") && fileType.equals(".json"))
//				{
//					JSONSplitter splitter = new JSONSplitter();
//					splitter.SplitSourceFile(inputFolder+fileEntry.getName(), outputFolder);
//					System.out.println(String.valueOf(counter)+". "+fileEntry.getName()+ ":\t done");
//					counter++;				
//				}
//				============================================================================//
			}
		}	
		redis.close();
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
		
		System.out.println("\nParsing completed\nProcessing time: "+HH+":"+MM+":"+SS+"."+MS);
	}
}
