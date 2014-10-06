package org.employmentse.splitter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.employmentse.deduplication.Deduplicator;
import org.employmentse.deduplication.FingerPrint;

public class JSONSplitter 
{
	private String output;	 
	private boolean enableDeduplication = false;
	private final Deduplicator deduplicator = new Deduplicator();
	private List<String> elementList = new ArrayList<String>(); 
	
	private List<String> getKeyFields() 
	{
		List<String> features = new ArrayList<String>();
		
		features.add(elementList.get(1));
		features.add(elementList.get(2));
		features.add(elementList.get(3));
		features.add(elementList.get(7));
		features.add(elementList.get(8));
		features.add(elementList.get(10));
		features.add(elementList.get(11));
		features.add(elementList.get(12));
		features.add(elementList.get(14));
		
		return features;
	}
	
	public void SplitSourceFile(String inputPath, String outputPath, Boolean enableDeduplication) throws IOException
	{		
		this.output = outputPath;
		this.enableDeduplication = enableDeduplication;
				
		BufferedReader reader = new BufferedReader(new FileReader(inputPath));	
		try 
        {
			int bufferSize = 4096;
			int numberOfColumns = 21;
			String lastElement = "";
								
			char[] bufferCharacters = new char[bufferSize];
			int n = reader.read(bufferCharacters);
		
			while (n!=-1) 
			{
				String bufferString = new String(bufferCharacters).replaceAll("\\}, \\{","|");
				if (bufferString.indexOf("[{")>0) {bufferString=bufferString.substring(bufferString.indexOf("[{")+2,bufferString.length());}	
				if (bufferString.indexOf("]}")>0) {bufferString=bufferString.substring(0,bufferString.indexOf("]}")-1);} 
								
				bufferString = lastElement + bufferString;
				String[] bufferElements = bufferString.split("[|]+");
				
				for (String element : bufferElements)	
				{	
					String items[] = element.split("\", \"");
					if (items.length != numberOfColumns-1) {lastElement = element;}					
					else
					{
						boolean addRow = true; 
					
						if (enableDeduplication) 
						{	
							for (int i=0; i<items.length; i++)
							elementList.add(items[i]);
							FingerPrint fpValue = new FingerPrint(getKeyFields());
							elementList.clear();
						
							if (deduplicator.isDuplicate(fpValue.toString())) {addRow = false;}							
							deduplicator.addJob(fpValue.toString());
						}					
						if (addRow) {CreateJSONFile(element, this.output);}						
					} 
				}				
				bufferCharacters = new char[bufferSize];        		
				n = reader.read(bufferCharacters);
			}
        }
		catch (Exception e){e.printStackTrace();}		
		finally {reader.close();}		
	}
	
	public void CreateJSONFile(String dataset, String output) throws FileNotFoundException, UnsupportedEncodingException
	{
		int fileNumber = 1;
		
		File[] fileArray = new File(output).listFiles(new FilenameFilter() 
		{public boolean accept(File dir, String name) 
		{return name.toLowerCase().endsWith(".json");}});
												
		if (fileArray.length != 0) 
		{		
			String lastFileName = fileArray[0].getName().substring(0, fileArray[0].getName().indexOf(".",-1));			
			for (int i=1; i<fileArray.length; i++) 
			{
				String currentFileName = fileArray[i].getName().substring(0, fileArray[i].getName().indexOf(".",-1));	
				if (Integer.parseInt(lastFileName) < Integer.parseInt(currentFileName)) {lastFileName=currentFileName;}
			}	fileNumber=Integer.parseInt(lastFileName)+1;}
		
		if (dataset.length() > 0)
		{
			PrintWriter writer = new PrintWriter(output + Integer.toString(fileNumber) + ".json","UTF-8");			
			dataset = dataset
			.replaceFirst("\"", "\t\"")
			.replaceAll("\", \"","\",\n\t\"")		
			.replaceAll("Ã©","é").replaceAll("√©","é")
			.replaceAll("Ã³","ó").replaceAll("√≥","ó")
			.replaceAll("Ã¡","á").replaceAll("√°","á")
			.replaceAll("Ãº","ú").replaceAll("√∫","ú")
			.replaceAll("Ã­" ,"í").replaceAll("√≠","í")
			.replaceAll("Ã€","à").replaceAll("√§","à")
			.replaceAll("ÂŽ","'").replaceAll("Ã¤","à")
			.replaceAll("Â´","'").replaceAll("¬¥","'")
			.replaceAll("Â°","°").replaceAll("¬∞","°")
			.replaceAll("Ã±","ñ").replaceAll("√±","ñ");
			
			writer.println("{");
			writer.println(dataset);
			writer.println("}");
			writer.close();	
		}	
	}	
}
