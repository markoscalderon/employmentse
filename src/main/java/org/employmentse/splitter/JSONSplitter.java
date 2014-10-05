package org.employmentse.splitter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class JSONSplitter 
{
	public void SplitSourceFile(String inputPath, String outputPath) throws IOException
	{
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
				if (element.split("\", \"").length != numberOfColumns-1) {lastElement = element;}
				else{CreateJSONFile(element, outputPath);} 
				
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
