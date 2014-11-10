package org.employmentse.content.extractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JobFamilyDetector {
	
	public static String ASSETS_FOLDER = "assets/";
	public static String OUTPUT_FOLDER = "output/";
	public static String JOB_FAMILIES_FOLDER = "jobFamilies/";

	public ArrayList<JobTitleKeywords> cCommercial = new ArrayList<JobTitleKeywords>();
	public ArrayList<JobTitleKeywords> cIndustrial = new ArrayList<JobTitleKeywords>();
	public ArrayList<JobTitleKeywords> cResidential = new ArrayList<JobTitleKeywords>();
	public ArrayList<JobTitleKeywords> cBusiness = new ArrayList<JobTitleKeywords>();
	public ArrayList<JobTitleKeywords> cIT = new ArrayList<JobTitleKeywords>();
	public ArrayList<JobTitleKeywords> cSoftware = new ArrayList<JobTitleKeywords>();
	public ArrayList<JobTitleKeywords> cMedical = new ArrayList<JobTitleKeywords>();
	
	
	
	public JobFamilyDetector() {
		try {
			cCommercial = getJobFamily(JOB_FAMILIES_FOLDER + "commercial.txt");
			cIndustrial = getJobFamily(JOB_FAMILIES_FOLDER + "industrial.txt");
			cResidential = getJobFamily(JOB_FAMILIES_FOLDER + "residential.txt");
			cBusiness = getJobFamily(JOB_FAMILIES_FOLDER + "business.txt");
			cIT = getJobFamily(JOB_FAMILIES_FOLDER + "it.txt");
			cSoftware = getJobFamily(JOB_FAMILIES_FOLDER + "software.txt");
			cMedical = getJobFamily(JOB_FAMILIES_FOLDER + "medical.txt");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws Exception {
		JobFamilyDetector jfd = new JobFamilyDetector();
		
		File[] files = new File(ASSETS_FOLDER).listFiles();

		int contCom = 0;
		int contInd = 0;
		int contRes = 0;
		int contBus = 0;
		int contIT = 0;
		int contSof = 0;
		int contMed = 0;
		int contOther = 0;
		int contNP = 0;
		
		for (File file : files) {
	        if (file.isDirectory()) {
	            System.out.println("Directory: " + file.getName());
	        } else {
	        	Gson gson = new GsonBuilder().setPrettyPrinting().create();
	        	String path = file.getAbsolutePath();
	        	if (path.contains(".json")) {
	        		String jsonStr = new Scanner(file).useDelimiter("\\Z").next();
	        		try {
		        		HashMap map = gson.fromJson(jsonStr, HashMap.class);
		        		String text = (String) map.get("Title");
		        		
		        		String family = "other";
		        		
		        		if (jfd.isInFamily(jfd.cCommercial, text)) {
		        			contCom++;
		        			family = "commercial";
		        		} else if(jfd.isInFamily(jfd.cIndustrial, text)) {
		        			//System.out.println(text);
		        			contInd++;
		        			family = "industrial";
		        		} else if(jfd.isInFamily(jfd.cResidential, text)) {
		        			contRes++;
		        			family = "residential";
		        		} else if(jfd.isInFamily(jfd.cBusiness, text)) {
		        			contBus++;
		        			family = "business";
		        		} else if(jfd.isInFamily(jfd.cIT, text)) {
		        			contIT++;
		        			family = "it";
		        		} else if(jfd.isInFamily(jfd.cSoftware, text)) {
		        			contSof++;
		        			family = "software";
		        		} else if(jfd.isInFamily(jfd.cMedical, text)) {
		        			contMed++;
		        			family = "medical";
		        		} else {
		        			contOther++;
		        			//System.out.println(text);
		        		}
		        		
		        		map.put("jobFamily", family);
		        		
		        		if (!family.equals("other")) {
			        		File out = new File(OUTPUT_FOLDER + file.getName());
			        		out.getParentFile().mkdirs();
			        		out.createNewFile();
			        		FileOutputStream outStream = new FileOutputStream(out);
			        		outStream.write(gson.toJson(map).getBytes());
			        		outStream.close();
		        		}
	        		} catch(Exception e) {
	        			contNP++;
	        			System.out.println("Not processed " + path);
	        		}
		        	
	        	}
	        	
	        }
	    }
		
		System.out.println("Final Results");
		System.out.println("Commercial:" + contCom);
		System.out.println("Industrial:" + contInd);
		System.out.println("Residential:" + contRes);
		System.out.println("Business:" + contBus);
		System.out.println("IT:" + contIT);
		System.out.println("Software:" + contSof);
		System.out.println("Medical:" + contMed);
		System.out.println("Other:" + contOther);
		System.out.println("NP:" + contNP);
	}
	
	public boolean isInFamily(ArrayList<JobTitleKeywords> family, String text) {
		
		for (JobTitleKeywords jtk : family) {
			if (jtk.containsKeywords(text)) {
				return true;
			}
		}
		
		return false;
	}
	
	public ArrayList<JobTitleKeywords> getJobFamily(String filename) throws Exception {
		
		ArrayList<JobTitleKeywords> result = new ArrayList<JobFamilyDetector.JobTitleKeywords>();
		
		List<String> lines = readLines(filename);
		
		for (String line : lines) {
			String[] words = line.split("\\s+");
			JobTitleKeywords jobTitleKeywords = new JobTitleKeywords(words);
			result.add(jobTitleKeywords);
		}
		
		return result;
	}
	
	public List<String> readLines(String filename) throws Exception{
		FileReader fileReader = new FileReader(filename);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		List<String> lines = new ArrayList<String>();
		String line = null;
		while ((line = bufferedReader.readLine()) != null) {
			lines.add(line.toLowerCase());
		}
		
		bufferedReader.close();
		return lines;
	}
	
	class JobTitleKeywords {
		String[]  words;
		
		public JobTitleKeywords(String[] words){
			this.words = words; 
		}
		
		public boolean containsKeywords(String phrase) {
			int countFound = 0;
			
			String str = phrase.toLowerCase();
			
			String[] tokens = str.split("\\s+");
			
			for(String word : words){
				String tokenFound = null;
				
				for (String token : tokens) {
					if (StringUtils.getLevenshteinDistance(token, word) <= 2) {
						countFound++;
						tokenFound = token;
						break;
					}
				}
				
				if (tokenFound != null ) {
					tokens = ArrayUtils.removeElement(tokens, tokenFound);
				}
			}
			
			if (countFound == words.length) {
				return true;
			}
			
			return false;
		}
		
	}
}
