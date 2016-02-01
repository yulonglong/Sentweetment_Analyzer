import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import javax.json.JsonObject;

public class LexiconSentiment {
	private Set<String> negativeLexicon = null;
	private Set<String> positiveLexicon = null;
	private Vector<String[]> testElements = null;
	private Vector<String> testTexts = null;
	private String pathToNegativeLexicon = "../lexicon/neg.txt";
	private String pathToPositiveLexicon = "../lexicon/pos.txt";
	private String pathToDataset = "../sentiment-analysis-dataset-with-origin-tweet/";
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		//object declaration and initialization
		LexiconSentiment sentimenter = new LexiconSentiment();
		if(!sentimenter.initialize()){
			System.err.println("fail to initialize the sentimenter");
		}else{
			sentimenter.test();
		}
	}

	public LexiconSentiment(){
		// TODO Auto-generated constructor stub
		//initialize lexicon structure
		negativeLexicon = new TreeSet<String>();
		positiveLexicon = new TreeSet<String>();
		//initialize testset list, which stores the test elements and the origin tweet text of test elements, respectively
		testElements = new Vector<String[]>();
		testTexts = new Vector<String>();
	}
	public boolean initialize() throws IOException{
		//read in lexicon 
		if(!initLexicon(pathToNegativeLexicon, negativeLexicon)){
			System.err.println("fail to initialize the negative lexicon: " + pathToNegativeLexicon);
			return false;
		}else{
			System.out.println("negative lexicon size: " + negativeLexicon.size());
		}
		if(!initLexicon(pathToPositiveLexicon, positiveLexicon)){
			System.err.println("fail to initialize the positive lexicon: " + pathToPositiveLexicon);
			return false;
		}else{
			System.out.println("positive lexicon size: " + positiveLexicon.size());
		}
		//read in test elements 
		String testFname = pathToDataset + "testing.csv";
		if(!readInTest(testFname)){
			System.err.println("fail to read: " + testFname);
			System.err.println("fail to read in test elements from: " + testFname);
			return false;
		}else{
			System.out.println("count of test elements: " + testElements.size());
		}
		return true;
	}
	private boolean initLexicon(String fname, Set<String> lexicon) throws IOException{
		Vector<String> lines = new Vector<String>();
		if(!LinesReader.readin(fname, lines)){
			System.err.println("fail to read in: " + fname);
			return false;
		}
		for(String line : lines){
			lexicon.add(line);
		}
		return true;
	}
	private boolean readInTest(String fname) throws IOException{
		//read in csv test file
		if(!CsvFileReader.readCsvElements(testElements, fname, 3, true)){
			System.err.println("fail to read in csv file: " + fname);
			return false;
		}
		//extract tweet text from the respect CSV file
		for(String[] element : testElements){
			String currentID = element[2];
			String tweetFname = pathToDataset + "tweets/" + currentID + ".json";
			//read the tweet JSON file
			JsonObject tweet = JsonFileReader.readJsonObject(tweetFname);
			if(tweet == null){
				System.err.println("fail to parse tweet json file: " + tweetFname);
				return false;
			}
			//get the origin tweet text from the tweet JsonObject
			if(tweet.containsKey("text")){
				testTexts.add(tweet.getString("text"));
			}else{
				System.err.println("no text key in: " + tweetFname);
				return false;
			}
		}
		return true;
	}
	public void test() throws FileNotFoundException{
		if(testTexts.size() != testElements.size()){
			System.err.println("#test texts: " + testTexts.size() + " ||| #test elements: " + testElements.size() + " not equal");
			return;
		}
		FileOutputStream fout = new FileOutputStream("sentiment-result.csv");
		PrintWriter p = new PrintWriter(fout);
		p.println("Topic,Sentiment,TwitterText");
		for(int i = 0; i < testTexts.size(); i++){
			//count sentiment words
			int sentimentCount = 0;
			String text = testTexts.get(i).replaceAll("\r", "").replaceAll("\n", "");
			String[] words = text.split(" ");
			for(String token : words){
				if(negativeLexicon.contains(token))
					sentimentCount--;
				if(positiveLexicon.contains(token))
					sentimentCount++;
			}
			//print the result
			//print topic
			p.print("\"" + testElements.get(i)[0] + "\",");
			//print sentiment result
			if(sentimentCount > 0)
				p.print("\"positive\",");
			else if(sentimentCount < 0)
				p.print("\"negative\",");
			else
				p.print("\"neutral\",");
			//print tweet text
			p.println("\"" + text + "\"");
		}
		p.close();
	}
}
