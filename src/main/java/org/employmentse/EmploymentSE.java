package org.employmentse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.employmentse.content.handler.JSONTableContentHandler;
import org.employmentse.parser.TSVParser;
import org.employmentse.splitter.JSONSplitter;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class EmploymentSE 
{
	public static String colHeadersFile  = "colheaders.txt";
	public static String inputFolder  = "assets/";
	public static String outputFolder = "output/";
	
	public static void main(String[] args) throws IOException, SAXException, TikaException, URISyntaxException 
	{
		
		if (args.length != 4) {
			System.err.println("Please include the following params:");
			System.err.println("-program [tsv|json] -deduplication [on|off]");
			System.exit(-1);
		}
		
		String cmdProgram = args[0];
		String cmdDedup = args[2];
		
		if (!cmdProgram.equals("-program")||!cmdDedup.equals("-deduplication")) {
			System.err.println("Unknown parameters. Use:");
			System.err.println("-program [tsv|json] -deduplication [on|off]");
			System.exit(-1);
		}
		
		long startTime = System.currentTimeMillis();
		
		String programType = args[1];
		String dedupOpt = args[3];
		
		boolean deduplicationEnabled = false;
		if (dedupOpt.equals("on")){
			deduplicationEnabled = true;
		}
		
		
		String[] headers = null;
		
		if (programType.equals("tsv")) {
			File colheaders = new File(colHeadersFile);
			if (!colheaders.exists()) 
			{
				System.err.println("Please create a column headers file called 'colHeaders.txt' and put your tsv file headers");
				System.exit(-1);
			}
			
			List<String> lHeaders = Files.readAllLines(colheaders.toPath(), Charset.defaultCharset());
			headers = lHeaders.toArray(new String[lHeaders.size()]);
			
			File inDirectory = new File(inputFolder);
			
			if (!inDirectory.exists()) 
			{
				System.err.println("Please create a folder called 'assets' and put your *.tsv files");
				System.exit(-1);
			}			
		}
		
		if (programType.equals("json")) {
			inputFolder = "etllib-json-files/";
			
			File jsonDirectory = new File(inputFolder);
			
			if (!jsonDirectory.exists()) 
			{
				System.err.println("Please run crawler.sh first");
				System.exit(-1);
			}
		}
		
		File outDirectory = new File(outputFolder);
		
		if (!outDirectory.exists()) 
		{
			outDirectory.mkdir();
		}

		File dataset = new File(inputFolder);
		int counter = 1;

		int JSONcounter = 0;
		
		for (final File fileEntry : dataset.listFiles()) 
		{			
			if (!fileEntry.isDirectory())
			{
				String fileType = fileEntry.getName().substring(fileEntry.getName().indexOf(".",-1),fileEntry.getName().length());
				String fileName = fileEntry.getName().substring(0, fileEntry.getName().indexOf(".",-1));				

//				//===================== CONVERT TSV TO MULTIPLE JSON FILES =================//
				if (!fileName.equals("") && fileType.equals(".tsv"))
				{
					InputStream input = new FileInputStream(inputFolder + fileEntry.getName());
				
					//===CREATE A FOLDER FOR EACH PARSED TSV FILE AND PUT JSON FILES THERE==//
					//directory = new File(outputFolder + fileName + "/");
					//if (!directory.exists()) directory.mkdir();					
					//ContentHandler handler = new JSONTableContentHandler(outputFolder + fileName + "/", true, redis);
					//======================================================================//
				
					ContentHandler handler = new JSONTableContentHandler(outputFolder, deduplicationEnabled);
					Metadata metadata = new Metadata();
					TSVParser parser = new TSVParser(headers);
					parser.parse(input, handler, metadata, new ParseContext());
					System.out.println(String.valueOf(counter)+". "+fileEntry.getName()+ ":\t done");
					counter++;
				}
//				//==========================================================================//
				
//				//====================== SPLIT JSON TO MULTIPLE JSON FILES =================//
				if (!fileName.equals("") && fileType.equals(".json"))
				{
					JSONSplitter splitter = new JSONSplitter();
					JSONcounter+= splitter.SplitSourceFile(inputFolder+fileEntry.getName(), outputFolder, deduplicationEnabled, true);										
					System.out.println(String.valueOf(counter)+". "+fileEntry.getName()+ ":\t done");
					counter++;				
				}
//				//==========================================================================//
								
//				//======= DELETE SOURCE FILE AFTER PARSING. USEFUL FOR SPACE SAVING ========//
//				try {if(!new File(inputFolder+fileEntry.getName()).delete())
//				{System.out.println(inputFolder+fileEntry.getName()+" delete failed");}}					
//				catch (Exception e){e.printStackTrace();}
//				//==========================================================================//								
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
		
		System.out.println("\nParsing completed\nProcessing time: "+HH+":"+MM+":"+SS+"."+MS);
		if (JSONcounter>0) {System.out.println(JSONcounter+" job positions found");}
	}
}
