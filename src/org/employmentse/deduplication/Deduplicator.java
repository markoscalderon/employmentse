package org.employmentse.deduplication;

import java.util.HashSet;
import java.util.Set;

public class Deduplicator {

	private Set<FingerPrint> existingJobs = new HashSet<>();

	public float DUPLICATE_THRESHOLD = (float) 0.8; 
	
	public Deduplicator() {
		
	}
	
	public Set<FingerPrint> getExistingJobs() {
		return existingJobs;
	}
	
	public boolean isDuplicate(FingerPrint fp1) {
		for (FingerPrint fp2 : getExistingJobs()) {
			if (FingerPrint.getSimilarity(fp1, fp2) >= DUPLICATE_THRESHOLD) {
				return true;
			}
		}
		return false;
	}
	
	public void addJob(FingerPrint fp1) {
		existingJobs.add(fp1); 
	}
	
	
}
