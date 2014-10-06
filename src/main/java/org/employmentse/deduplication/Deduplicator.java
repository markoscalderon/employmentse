package org.employmentse.deduplication;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import redis.clients.jedis.Jedis;

public class Deduplicator {

	private HashMap<String, Set<FingerPrint>> existingJobsPerKey = new HashMap<String, Set<FingerPrint>>();
	
	private HashMap<String, Boolean> areExistingJobsLoaded;
	
	private Jedis redis = new Jedis("localhost");
	
	public float DUPLICATE_THRESHOLD = (float) 0.8; 
	
	public Deduplicator() {
		
		areExistingJobsLoaded = new HashMap<String, Boolean>();
	}
	
	public Set<FingerPrint> getExistingJobsPerKey(String key) throws ClassNotFoundException, IOException {
		
		Boolean existing = areExistingJobsLoaded.get(key);
		
		if (existing == null) {
			Set<FingerPrint> existingJobsFromDB = new HashSet<FingerPrint>();
			
			for (byte[] data : redis.smembers(("fingerprints:" + key).getBytes())) {
				existingJobsFromDB.add(FingerPrint.deserialize(data));
			}
			
			existingJobsPerKey.put(key, existingJobsFromDB);
			areExistingJobsLoaded.put(key, new Boolean(true));
		}
		
		Set<FingerPrint> existingJobs = existingJobsPerKey.get(key);
		
		if (existingJobs == null) {
			existingJobs = new HashSet<FingerPrint>();
			existingJobsPerKey.put(key, existingJobs);
		}
		return existingJobs;
	}
	
	public boolean isDuplicate(String featureHashCode) {
		return redis.sismember("deduplicator:jobs", featureHashCode);
	}
	
	public boolean isNearDuplicate(String key, FingerPrint fp1) {
		Set<FingerPrint> existingJobs = null;
		try {
			existingJobs = getExistingJobsPerKey(key);
		} catch (Exception e) {
			System.out.println("exception:" + e.getMessage());
		}
		
		if ( existingJobs != null) {

			for (FingerPrint fp2 : existingJobs) {
				float x = FingerPrint.getSimilarity(fp1, fp2);
				if (FingerPrint.getSimilarity(fp1, fp2) >= DUPLICATE_THRESHOLD) {
					return true;
				}
			}
		}
		return false;
	}
	
	public void addFingerPrint(String key, FingerPrint fp) {
		Set<FingerPrint> jobs = existingJobsPerKey.get(key);
		jobs.add(fp);
		existingJobsPerKey.put(key, jobs);
		
		try {
			redis.sadd(("fingerprints:" + key).getBytes(), FingerPrint.serialize(fp));
		} catch (IOException e) {
			System.out.println("Exception: " + e);
		}
	}
	
	public void addJob(String featureHashCode) {
		redis.sadd("deduplicator:jobs", featureHashCode);
	}

}
