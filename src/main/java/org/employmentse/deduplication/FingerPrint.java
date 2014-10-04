package org.employmentse.deduplication;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FingerPrint {

	private static int titleIDX = 2; 
	
	private Set<Integer> shingleHashes = new HashSet<>(); 
	
	public FingerPrint(List<String> features) {
		for (int i = 0; i < features.size(); i++) {
			if (i == titleIDX) {
//				shingleHashes.addAll(get2Grams(features.get(i)));
				shingleHashes.add(features.get(i).hashCode());
			} else {
				shingleHashes.add(features.get(i).hashCode());
			}
		}
	}
	
	private Set<Integer> get2Grams(String string) {
		System.out.println("shingling " + string);
		// TODO Auto-generated method stub
		String[] words = string.split("\\s+");
		
		Set<Integer> shingleHashes = new HashSet<>();
		for (int i = 0; i < words.length - 1; i++) {
			String twoGram = words[i] + words[i+1]; 
			shingleHashes.add(twoGram.hashCode());
		}
		
		return shingleHashes;
	}

	public static float getSimilarity(final FingerPrint fp1, final FingerPrint fp2) {
		Set<Integer> intersection = new HashSet<>(fp1.shingleHashes);
		intersection.retainAll(fp2.shingleHashes);
		
		Set<Integer> union = new HashSet<>(fp1.shingleHashes);
		union.addAll(fp2.shingleHashes);
		
		return (float) intersection.size() / (float) union.size();
	}
	
	
}
