package org.employmentse.deduplication;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FingerPrint {

	private Set<Integer> shingleHashes = new HashSet<>(); 
	
	public FingerPrint(List<String> features) {
		for (String feature : features) {
			shingleHashes.add(feature.hashCode());
		}
	}
	
	public static float getSimilarity(final FingerPrint fp1, final FingerPrint fp2) {
		Set<Integer> intersection = new HashSet<>(fp1.shingleHashes);
		intersection.retainAll(fp2.shingleHashes);
		
		Set<Integer> union = new HashSet<>(fp1.shingleHashes);
		union.addAll(fp2.shingleHashes);
		
		return intersection.size() / union.size();
	}
	
	
}
