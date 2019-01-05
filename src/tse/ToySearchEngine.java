package tse;

import java.io.*;
import java.util.*;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages in
 * which it occurs, with frequency of occurrence in each page.
 *
 */
public class ToySearchEngine {

	/**
	 * This is a hash table of all keys. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in 
	 * DESCENDING order of frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keysIndex;

	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;

	/**
	 * Creates the keysIndex and noiseWords hash tables.
	 */
	public ToySearchEngine() {
		keysIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashSet<String>(100,2.0f);
	}

	/**
	 * Scans a document, and loads all keywords found into a hash table of key occurrences
	 * in the document. Uses the getKey method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keys in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeysFromDocument(String docFile) 
			throws FileNotFoundException {


		if (docFile == null) {
			throw new FileNotFoundException();
		}

		//go through the documents. check each word (separated by spaces). if it's a noiseword, don't do anything. 
		//if it's a keyword:
		//  -check the hashmap. load it into the hashmap if it's not there.
		//  -if it's already there, increment the frequency

		HashMap<String, Occurrence> wordMap = new HashMap<String, Occurrence>();
		Scanner sc = new Scanner (new File(docFile));

		while (sc.hasNext()) {
			String currentLine = sc.nextLine();
			String[] wordsInLine = currentLine.split(" ");
			if (wordsInLine !=null) {
				for (int x=0; x<wordsInLine.length; x++) {
					String currWord = wordsInLine[x];
					if (currWord.equals("/n") || currWord.equals("/t")) { //if it's just a newline or space character, don't do anything
						break;
					}
					String currWordAsKey = getKey(currWord); //strips trailing punctuation & decides if it's a key
					if (currWordAsKey != null) { //means it's not a noise word. so check if it's already in the hashtable
						if (wordMap.containsKey(currWordAsKey)) {
							Occurrence currOcc=wordMap.get(currWordAsKey);
							currOcc.frequency++;
							wordMap.put(currWordAsKey, currOcc);
						}
						else {
							Occurrence currOcc = new Occurrence (docFile, 1);
							wordMap.put(currWordAsKey, currOcc);
						}

					}

				}
			}

		}
		for (Map.Entry<String, Occurrence> entry : wordMap.entrySet()) {
			String key = entry.getKey();
			Occurrence value = entry.getValue();

			System.out.println ("Key: " + key + "~~~~Value: " + value); 
		} 


		sc.close();

		return wordMap;
	}

	/**
	 * Merges the keys for a single document into the master keysIndex
	 * hash table. For each key, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same key's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeys(HashMap<String,Occurrence> kws) {
		//for every item in the kws hash table:
		//	-check if it's already in the keysIndex hash table
		//		-if it is: add the current occurrence into its occurrence array list
		//		-if not: add the occurrence into a new array list. add (key, occArr) to the master hashtable


		for (String key : kws.keySet())	{

			if (!keysIndex.containsKey(key)) {
				ArrayList<Occurrence> occ = new ArrayList<Occurrence>();
				//	System.out.println("current keyword is: " + kws.get(key));
				//System.out.println("current array is: " + occ);
				occ.add(kws.get(key));
				insertLastOccurrence(occ); //PUT THIS BACK
				//System.out.println("Return value: " + insertLastOccurrence(occ)); //don't need to do this bc the array only has one thing, but i might lose points when they grade it? maybe i need it to return null?
				//System.out.println();
				keysIndex.put(key, occ);
			}
			else {
				ArrayList<Occurrence> occ = keysIndex.get(key);
				//System.out.println("current keyword is: " + kws.get(key));
				//System.out.println("current array is: " + occ);
				occ.add(kws.get(key));
				insertLastOccurrence(occ);
				//System.out.println("return value: " + insertLastOccurrence(occ));
				//	System.out.println();
				keysIndex.put(key, occ); 
			}
			//	System.out.println("hi");

			//System.out.println(key + ":  " + keysIndex.get(key));
		} 






	}

	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * trailing punctuation, consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * Note: No other punctuation characters will appear in grading testcases
	 * 
	 * @param word Candidate word
	 * @return Key (word without trailing punctuation, LOWER CASE)
	 */




	public String getKey(String word) {
		if (word.equals(" ") || word==null || word.equals("/n") || word.equals("/t")) { //trying to get rid of space characters
			return null;
		}
		String keyWord = "";
		for (int x=0; x< word.length(); x++) {
			char currChar = word.charAt(x);

			if (Character.isDigit(currChar)) {
				return null;
			}

			if (!Character.isLetter(currChar) && (x!=word.length()-1) && Character.isLetter(word.charAt(x+1))) { //quits the method if there's punctuation in the middle of the word
				return null;
			}
			if (Character.isLetter(currChar)) { //param string but lower case and with trailing punctuation removed
				keyWord += currChar;
			}
		}
		keyWord=keyWord.toLowerCase();

		if (keyWord.length() > 0) { //last atnewOcct to get rid of empty strings

			if(noiseWords.contains(keyWord)) {
				return null;
			}
			return keyWord;
		}
		return null;
	}



	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion is done by
	 * first finding the correct spot using binary search, then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {


		ArrayList<Integer> midptArray = new ArrayList<Integer>();



		if (occs.size() < 2) {
			return null;
		}

		Occurrence newOcc = occs.get(occs.size()-1);
		occs.remove(occs.size()-1);


		int high = 0; 
		int low = occs.size()-1;
		int mid=0;

		while(high<=low){
			mid = (high+low)/2;
			int midptValue=occs.get(mid).frequency;

			if (midptValue==newOcc.frequency){
				midptArray.add(mid);
				break;
			}
			else {
				if(midptValue <newOcc.frequency) {
					low=mid-1;
					midptArray.add(mid);
				}
				if(midptValue>newOcc.frequency) {
					high=mid+1;
					midptArray.add(mid);
					mid++;
				}
			}
		}





		occs.add(mid, newOcc);



		return midptArray;
	}



	/**
	 * This method indexes all words found in all the input documents. When this
	 * method is done, the keysIndex hash table will be filled with all keys,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void buildIndex(String docsFile, String noiseWordsFile) 
			throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);
		}

		// index all words
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeysFromDocument(docFile);
			mergeKeys(kws);
		}
		sc.close();
	}

	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of document frequencies. (Note that a
	 * matching document will only appear once in the result.) Ties in frequency values are broken
	 * in favor of the first keyword. (That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2
	 * also with the same frequency f1, then doc1 will take precedence over doc2 in the result. 
	 * The result set is limited to 5 entries. If there are no matches at all, result is null.
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matches, returns null.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		//search the master hashtable for the two terms. copy all matches into an array, inserting one at a time so they're in order.
		//after that's done: take the first 5 that don't have repeating document names. copy them into a different array list
		// 	-make sure to delete them from the original array. if that array size gets down to 0 before the new array is at 5, break.
		//return the new array list

		/*for (String key : keysIndex.keySet())	{
			System.out.println(key + ":  " + keysIndex.get(key));
		}
		System.out.println(); 
		 */


		kw1= kw1.toLowerCase();
		kw2=kw2.toLowerCase();

		ArrayList<Occurrence> firstKw = keysIndex.get(kw1);
		ArrayList<Occurrence> secondKw = keysIndex.get(kw2);
		ArrayList<String> result = new ArrayList<String>();

		if (!(keysIndex.containsKey(kw1)) && !(keysIndex.containsKey(kw2))) { //keywords not in hashtable
			return null;
		} 

		else if (!(keysIndex.containsKey(kw1)))  { //hashtable only contains second keyword
			while (secondKw.size() > 5) {
				secondKw.remove(secondKw.size()-1);
			}

			for (int x=0; x<secondKw.size(); x++) {
				result.add(secondKw.get(x).document);
			}

			return result;
		}


		else if (!(keysIndex.containsKey(kw2))) { //hashtable only contains first keyword
			while (firstKw.size() > 5) {
				firstKw.remove(firstKw.size()-1);
			}
			for (int x=0; x<firstKw.size(); x++) {
				result.add(firstKw.get(x).document);
			}

			return result;
		}
		else { //both keywords are in the hashtable
			while ((firstKw.size() > 0) && (secondKw.size() >  0)) { //add both mini array lists into the results array lists, in order

				if (firstKw.get(0).frequency >= secondKw.get(0).frequency) {
					result.add(firstKw.get(0).document);
					firstKw.remove(0);

				}
				else if (secondKw.get(0).frequency > firstKw.get(0).frequency) {
					result.add(secondKw.get(0).document);
					secondKw.remove(0);
				}

				//	System.out.println(firstKw);
				//	System.out.println(secondKw);

			}
			while (firstKw.size() >0) { //in case there's leftovers in first array list. only one of these two while loops will run
				result.add(firstKw.get(0).document);
				firstKw.remove(0);
			}

			while (secondKw.size() > 0) { //in case there's leftovers in second array list. only one of these two while loops will run
				result.add(secondKw.get(0).document);
				secondKw.remove(0);
			}

			for (int x = 0; x < result.size()-1; x++) { //get rid of duplicates in results array list 	
				for (int y = x + 1; y < result.size(); y++) {
					if (result.get(x).equals(result.get(y))) {
						result.remove(y);
					}
				}
			}
		}


		while (result.size() > 5) { // narrow down to top 5
			result.remove(result.size()-1);
		}

		return result;

	}
}