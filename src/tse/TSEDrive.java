package tse;
import java.io.*;
import java.util.*;


public class TSEDrive{
	public static void main(String[] args) throws FileNotFoundException {
		ToySearchEngine t = new ToySearchEngine();
		t.loadKeysFromDocument("Tyger.txt");
		//t.buildIndex("docs.txt",  "noisewords.txt");
		//t.loadKeysFromDocument("alicech1.txt");
		//System.out.println(t.getKey("alicech1.txt"));
		System.out.println(t.top5search("red", "car"));
	}
} 