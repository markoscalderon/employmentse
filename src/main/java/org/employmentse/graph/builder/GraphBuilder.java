package org.employmentse.graph.builder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GraphBuilder 
{
	public static String inputFolder  = "assets/";
	
	public static String CreateMetadata(String jobID, int familyWeight, int locationWeight)
	{// Assemble a JSON-structured metadata block
		String totalWeight = Integer.toString(familyWeight+locationWeight);
		String metadata = 
		"\t\t{\n"
		+"\t\t\t\"Job Number\": \""+jobID+"\",\n"
		+"\t\t\t\"Job Family\": \""+familyWeight+"\",\n"
		+"\t\t\t\"Geolocation\": \""+locationWeight+"\",\n"
		+"\t\t\t\"Total Weight\": \""+totalWeight+"\""
		+"\n\t\t}";
		
		return metadata;
	}
	
	public static void AppendMetadataToFile(File inputFile, String metadata) throws FileNotFoundException, IOException
	{// Append input metadata to a designated file in a smart way 
		int bufferSize = 4096;
		char[] bufferCharacters;
		bufferCharacters = new char[4096];
		BufferedReader reader = new BufferedReader(new FileReader(inputFile));	
				
		try
		{
			String fileContents = "";			
			int n = reader.read(bufferCharacters);
			while (n!=-1) 
			{
	            fileContents += new String(bufferCharacters);
	            bufferCharacters = new char[bufferSize];        		
				n = reader.read(bufferCharacters);
			}		
			
			if (fileContents.contains("\"Metadata\":"))
			{fileContents = fileContents.substring(0,fileContents.lastIndexOf("]")-2)
			+",\n"+metadata+"\n\t]\n}";}
			
			else
			{fileContents = fileContents.substring(0,fileContents.lastIndexOf("}")-1)
			+",\n\t\"Metadata\":\n\t[\n"+metadata+"\n\t]\n}";}

			PrintWriter writer = new PrintWriter(inputFile);
			writer.println(fileContents);
			writer.close();			
		}
		catch (Exception e){e.printStackTrace();System.exit(0);}		
		finally {reader.close();}				
	}
		
	public static void CompareFiles(File file1, File file2) throws FileNotFoundException, IOException
	{// Compare two JSON files and append both with metadata in case if they have relationship
		int familyWeight=0; 
		int locationWeight=0;
		
		int bufferSize = 4096;
		char[] bufferCharacters;
		bufferCharacters = new char[bufferSize];
		BufferedReader reader1 = new BufferedReader(new FileReader(file1));
		BufferedReader reader2 = new BufferedReader(new FileReader(file2));
		
		try
		{
			String file1_Contents = "";
			String file2_Contents = "";
			
			int n1 = reader1.read(bufferCharacters);
			while (n1 != -1) 
			{
	            file1_Contents += new String(bufferCharacters);
	            bufferCharacters = new char[bufferSize];        		
				n1 = reader1.read(bufferCharacters);
			}
			
			int n2 = reader2.read(bufferCharacters);
			while (n2 != -1) 
			{
	            file2_Contents += new String(bufferCharacters);
	            bufferCharacters = new char[bufferSize];        		
				n2 = reader2.read(bufferCharacters);
			}
			
			//file key fields ["id", "Title", "Location"]
			String[] file1_keyFields = new String [3];
			String[] file2_keyFields = new String [3];
			
			Matcher matcherIdentify;
			Matcher matcherJobTitle;
			Matcher matcherLocation;
		
			Pattern patternIdentify = Pattern.compile("\"id\": \".*\"");
			Pattern patternJobTitle = Pattern.compile("\"Title\": \".*\"");
			Pattern patternLocation = Pattern.compile("\"Location\": \".*\"");
			
			//get key fields for file1
			matcherIdentify = patternIdentify.matcher(file1_Contents);
			matcherJobTitle = patternJobTitle.matcher(file1_Contents);	
			matcherLocation = patternLocation.matcher(file1_Contents);
		
			if (matcherIdentify.find()) 
			{	
				file1_keyFields[0]=matcherIdentify.group(0).
				substring(matcherIdentify.group(0).indexOf("\"id\": \"")+7,
				matcherIdentify.group(0).lastIndexOf("\""));				
			}		
			if (matcherJobTitle.find()) {file1_keyFields[1]=matcherJobTitle.group(0);}
			if (matcherLocation.find()) {file1_keyFields[2]=matcherLocation.group(0);}
			
			//get key fields for file2
			matcherIdentify = patternIdentify.matcher(file2_Contents);
			matcherJobTitle = patternJobTitle.matcher(file2_Contents);	
			matcherLocation = patternLocation.matcher(file2_Contents);
			
			if (matcherIdentify.find()) 			
			{	
				file2_keyFields[0]=matcherIdentify.group(0).
				substring(matcherIdentify.group(0).indexOf("\"id\": \"")+7,
				matcherIdentify.group(0).lastIndexOf("\""));				
			}	
			if (matcherJobTitle.find()) {file2_keyFields[1]=matcherJobTitle.group(0);}
			if (matcherLocation.find()) {file2_keyFields[2]=matcherLocation.group(0);}
	
			if (file1_keyFields[1].equals(file2_keyFields[1])) {familyWeight++;}			
			if (file1_keyFields[2].equals(file2_keyFields[2])) {locationWeight++;}
			
			if (familyWeight>0 || locationWeight>0)
			{
				AppendMetadataToFile(file1, (CreateMetadata(file2_keyFields[0],familyWeight,locationWeight)));
				AppendMetadataToFile(file2, (CreateMetadata(file1_keyFields[0],familyWeight,locationWeight)));
			}			
		}
		catch (Exception e){e.printStackTrace();System.exit(0);}		
		finally 
		{
			reader1.close();
			reader2.close();
		}		
	}
	
	public static void main(String[] args) throws IOException
	{ 
		File[] fileArray = new File(inputFolder).listFiles(new FilenameFilter() 
		{public boolean accept(File dir, String name) 
		{return name.toLowerCase().endsWith(".json");}});
		long startTime = System.currentTimeMillis();
		
		String file1_Name="";
		String file2_Name="";
		if (fileArray.length != 0) 
		{
			for (int i=0; i<fileArray.length; i++) 
			{
				for (int k=i+1; k<fileArray.length; k++) 
				{
					file1_Name = fileArray[i].getName().substring(0, fileArray[i].getName().indexOf(".",-1));	
					file2_Name = fileArray[k].getName().substring(0, fileArray[k].getName().indexOf(".",-1));
										
					System.out.println(file1_Name+" - "+file2_Name); //USE THIS TO LIST FILE PAIRS
					CompareFiles(fileArray[i], fileArray[k]);
				}				
//				System.out.println(file1_Name); //USE THIS TO LIST INDIVIDUAL FILES
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
		
		System.out.println("\nUpdating completed\nProcessing time: "+HH+":"+MM+":"+SS+"."+MS);        
    }
}