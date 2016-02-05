import java.io.FileNotFoundException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import java.util.ArrayList;

import javax.json.JsonObject;


class Tweet {
	private String topic;
	private String sentiment;
	private String text;
	private String id;
	
	Tweet(String _topic, String _sentiment, String _id) {
		topic = _topic;
		sentiment = _sentiment;
		id = _id;
		this.retrieveTextFromJson();
	}
	
	private void retrieveTextFromJson() {
		String tweetFname = GlobalHelper.pathToDataset + "tweets/" + id + ".json";
		//read the tweet JSON file
		JsonObject tweetJson = null;
		try {
			tweetJson = JsonFileReader.readJsonObject(tweetFname);
		}
		catch(Exception e) {
			System.err.println("Error while reading JSON tweet text! Exception caught!");
			e.printStackTrace();
		}
		
		if(tweetJson == null){
			System.err.println("fail to parse tweet json file: " + tweetFname);
			return;
		}
		//get the origin tweet text from the tweet JsonObject
		if(tweetJson.containsKey("text")){
			text = (tweetJson.getString("text"));
		}
		else{
			System.err.println("no text key in: " + tweetFname);
			return;
		}
	}
	
	public String getTopic() { return topic; }
	public String getSentiment() { return sentiment; }
	public String getText() { return text; }
	public String getId() { return id; }
}

public class LexiconSentiment {
	private Set<String> negativeLexicon = null;
	private Set<String> positiveLexicon = null;
	private ArrayList<Tweet> testList = null;

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
		
		testList = new ArrayList<Tweet>();
	}
	public boolean initialize() throws IOException{
		// Read in negative Lexicon 
		if(!initLexicon(GlobalHelper.pathToNegativeLexicon, negativeLexicon)){
			System.err.println("fail to initialize the negative lexicon: " + GlobalHelper.pathToNegativeLexicon);
			return false;
		}
		else{
			System.out.println("negative lexicon size: " + negativeLexicon.size());
		}
		// Read in positive Lexicon
		if(!initLexicon(GlobalHelper.pathToPositiveLexicon, positiveLexicon)){
			System.err.println("fail to initialize the positive lexicon: " + GlobalHelper.pathToPositiveLexicon);
			return false;
		}
		else{
			System.out.println("positive lexicon size: " + positiveLexicon.size());
		}
		
		//read in test elements 
		String testFname = GlobalHelper.pathToDataset + "testing.csv";
		if(!readTestFile(testFname)){
			System.err.println("fail to read: " + testFname);
			System.err.println("fail to read in test elements from: " + testFname);
			return false;
		}
		else{
			System.out.println("count of test tweets: " + testList.size());
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
	private boolean readTestFile(String fname) throws IOException{
		//read in csv test file
		if(!CsvFileReader.readCsvElementsTweet(testList, fname, 3, true)){
			System.err.println("fail to read in csv file: " + fname);
			return false;
		}
		return true;
	}
	
	public void test() throws FileNotFoundException{

		FileOutputStream fout = new FileOutputStream("sentiment-result.csv");
		PrintWriter p = new PrintWriter(fout);
		p.println("Topic,Sentiment,TwitterText");
		for(int i = 0; i < testList.size(); i++){
			//count sentiment words
			int sentimentCount = 0;
			String text = testList.get(i).getText().replaceAll("\r", "").replaceAll("\n", "");
			String[] words = text.split(" ");
			for(String token : words){
				if(negativeLexicon.contains(token))
					sentimentCount--;
				if(positiveLexicon.contains(token))
					sentimentCount++;
			}
			
			p.print("\"" + testList.get(i).getTopic() + "\",");
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
