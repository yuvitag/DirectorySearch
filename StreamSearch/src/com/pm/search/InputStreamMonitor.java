package com.pm.search;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class InputStreamMonitor {

	private static final String DATA = "helloaminpunewhereiwillbewatchingamovieiwillalsoliketohavealunchafterwatchingamovie";
	private static final String[] SEARCH_WORDS = { "abcd", "will", "inpune",
			"like", "inga" };

	public static void main(String[] args) throws Exception {
		InputStream stream = new ByteArrayInputStream(DATA.getBytes());
		List<String> asList = Arrays.asList(SEARCH_WORDS);
		monitor(stream, new HashSet<String>(asList));
	}

	private static void monitor(InputStream stream, Set<String> searchWords)
			throws SearchException {
		try {
			LinkedList<StringBuilder> searchPivots = new LinkedList<StringBuilder>();
			Map<String, Integer> resultMap = new HashMap<String, Integer>();
			int waitCnt = 0;
			int charCnt = 0;
			while (true) {
				Integer byteChar = stream.read();
				if (byteChar.intValue() == -1) {
					Thread.sleep(1000);
					waitCnt++;
				}
				if (waitCnt > 3) {
					break;
				}
				charCnt++;
				if (charCnt % 80 == 0) {
					System.out.print("\n");
				}
				modifySearchPivots(searchPivots, byteChar);
				List<StringBuilder> pivotsToRemove = validatePivots(
						searchWords, searchPivots, resultMap);
				removeUnwantedPivots(searchPivots, pivotsToRemove);
			}
			printResult(searchWords, resultMap);
		} catch (InterruptedException e) {
			throw new SearchException("Failed to wait");
		} catch (IOException e) {
			throw new SearchException("Failed to read an input stream.");
		}

	}

	private static void modifySearchPivots(
			LinkedList<StringBuilder> searchPivots, Integer byteChar) {
		System.out.print(".");
		byte[] byteCharArray = new byte[] { byteChar.byteValue() };
		String presentChar = new String(byteCharArray);
		for (StringBuilder pivot : searchPivots) {
			pivot.append(presentChar);
		}
		searchPivots.add(new StringBuilder(presentChar));
	}

	private static List<StringBuilder> validatePivots(Set<String> searchWords,
			LinkedList<StringBuilder> searchPivots, Map<String, Integer> result) {
		Iterator<StringBuilder> pivotIterator = searchPivots.iterator();
		List<StringBuilder> pivotsToRemove = new ArrayList<StringBuilder>();
		while (pivotIterator.hasNext()) {
			StringBuilder pivot = pivotIterator.next();
			boolean found = false;
			String pivotString = pivot.toString();
			for (String searchWord : searchWords) {
				if (searchWord.startsWith(pivotString)) {
					found = true;
				}

				// exact match
				if (!pivotString.equals(searchWord))
					continue;

				if (result.containsKey(pivotString)) {
					int cnt = result.get(pivotString);
					cnt++;
					result.put(pivotString, cnt);
				} else {
					result.put(pivotString, 1);
				}
				System.out.print("F");
			}
			if (!found) {
				pivotsToRemove.add(pivot);
			}
		}
		return pivotsToRemove;
	}

	private static void removeUnwantedPivots(
			LinkedList<StringBuilder> searchPivots,
			List<StringBuilder> pivotsToRemove) {
		for (StringBuilder pivot : pivotsToRemove) {
			searchPivots.remove(pivot);
		}
	}

	private static void printResult(Set<String> searchWords,
			Map<String, Integer> resultMap) {
		System.out.println("\n------------------------");
		for (String word : searchWords) {
			int cnt = 0;
			if (resultMap.containsKey(word)) {
				cnt = resultMap.get(word);
			}
			System.out.println(word + ":" + cnt);
		}
	}

}
