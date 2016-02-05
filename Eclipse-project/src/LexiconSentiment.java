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
	private String predictedSentiment;
	private String text;
	private String id;
	
	Tweet(String _topic, String _sentiment, String _id) {
		topic = _topic;
		sentiment = _sentiment;
		id = _id;
		this.retrieveTextFromJson();
		predictedSentiment = "";
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
	
	public void setPredictedSentiment(String _predictedSentiment) {
		predictedSentiment = _predictedSentiment;
	}
	public void setPredictedPositive() {
		predictedSentiment = "positive";
	}
	public void setPredictedNegative() {
		predictedSentiment = "negative";
	}
	public void setPredictedNeutral() {
		predictedSentiment = "neutral";
	}
	public void setPredictedIrrelevant() {
		predictedSentiment = "irrelevant";
	}
	
	public String getTopic() { return topic; }
	public String getSentiment() { return sentiment; }
	public String getText() { return text; }
	public String getId() { return id; }
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\""+topic+"\",");
		sb.append("\""+id+"\",");
		sb.append("\""+sentiment+"\",");
		sb.append("\""+predictedSentiment+"\",");
		sb.append("\""+text+"\"");
		return sb.toString();
	}
}

public class LexiconSentiment {
	private Set<String> negativeLexicon = null;
	private Set<String> positiveLexicon = null;
	private ArrayList<Tweet> tweetList = null;
	private ArrayList<ArrayList<Tweet> > tweetListPartition = null;

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
		
		tweetList = new ArrayList<Tweet>();
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
		
		//read in training elements 
		String trainFname = GlobalHelper.pathToDataset + "training.csv";
		if(!readTestFile(trainFname)){
			System.err.println("fail to read: " + trainFname);
			System.err.println("fail to read in train elements from: " + trainFname);
			return false;
		}
		else{
			System.out.println("count of train tweets: " + tweetList.size());
		}
		
		//read in test elements 
		String testFname = GlobalHelper.pathToDataset + "testing.csv";
		if(!readTestFile(testFname)){
			System.err.println("fail to read: " + testFname);
			System.err.println("fail to read in test elements from: " + testFname);
			return false;
		}
		else{
			System.out.println("count of combined tweets: " + tweetList.size());
		}
		
		//ten fold partition
		tenFoldPartition();
		
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
		if(!CsvFileReader.readCsvElementsTweet(tweetList, fname, 3, true)){
			System.err.println("fail to read in csv file: " + fname);
			return false;
		}
		return true;
	}
	
	private void tenFoldPartition() {
		System.out.println("Start 10-fold partitioning....");
		
		int appleIrrelevant ,  applePositive , appleNeutral , appleNegative;
		appleIrrelevant = applePositive = appleNeutral = appleNegative = 0;
		int googleIrrelevant ,  googlePositive , googleNeutral , googleNegative;
		googleIrrelevant = googlePositive = googleNeutral = googleNegative = 0;
		int msIrrelevant ,  msPositive , msNeutral , msNegative;
		msIrrelevant = msPositive = msNeutral = msNegative = 0;
		int twitterIrrelevant ,  twitterPositive , twitterNeutral , twitterNegative;
		twitterIrrelevant = twitterPositive = twitterNeutral = twitterNegative = 0;
		
		tweetListPartition = new ArrayList<ArrayList<Tweet>>();
		for(int i=0;i<=10;i++){
			tweetListPartition.add(new ArrayList<Tweet>());
		}

		for (int i = 0; i < tweetList.size(); i++) {
			if (tweetList.get(i).getTopic().equalsIgnoreCase("apple")) {
				if (tweetList.get(i).getSentiment().equalsIgnoreCase("irrelevant")) {
					tweetListPartition.get(appleIrrelevant).add(tweetList.get(i));
					appleIrrelevant = (appleIrrelevant+1)%10;
				}
				else if (tweetList.get(i).getSentiment().equalsIgnoreCase("positive")) {
					tweetListPartition.get(applePositive).add(tweetList.get(i));
					applePositive = (applePositive+1)%10;
				}
				else if (tweetList.get(i).getSentiment().equalsIgnoreCase("neutral")) {
					tweetListPartition.get(appleNeutral).add(tweetList.get(i));
					appleNeutral = (appleNeutral+1)%10;
				}
				else if (tweetList.get(i).getSentiment().equalsIgnoreCase("negative")) {
					tweetListPartition.get(appleNegative).add(tweetList.get(i));
					appleNegative = (appleNegative+1)%10;
				}
			}
			else if (tweetList.get(i).getTopic().equalsIgnoreCase("google")) {
				if (tweetList.get(i).getSentiment().equalsIgnoreCase("irrelevant")) {
					tweetListPartition.get(googleIrrelevant).add(tweetList.get(i));
					googleIrrelevant = (googleIrrelevant+1)%10;
				}
				else if (tweetList.get(i).getSentiment().equalsIgnoreCase("positive")) {
					tweetListPartition.get(googlePositive).add(tweetList.get(i));
					googlePositive = (googlePositive+1)%10;
				}
				else if (tweetList.get(i).getSentiment().equalsIgnoreCase("neutral")) {
					tweetListPartition.get(googleNeutral).add(tweetList.get(i));
					googleNeutral = (googleNeutral+1)%10;
				}
				else if (tweetList.get(i).getSentiment().equalsIgnoreCase("negative")) {
					tweetListPartition.get(googleNegative).add(tweetList.get(i));
					googleNegative = (googleNegative+1)%10;
				}
			}
			else if (tweetList.get(i).getTopic().equalsIgnoreCase("microsoft")) {
				if (tweetList.get(i).getSentiment().equalsIgnoreCase("irrelevant")) {
					tweetListPartition.get(msIrrelevant).add(tweetList.get(i));
					msIrrelevant = (msIrrelevant+1)%10;
				}
				else if (tweetList.get(i).getSentiment().equalsIgnoreCase("positive")) {
					tweetListPartition.get(msPositive).add(tweetList.get(i));
					msPositive = (msPositive+1)%10;
				}
				else if (tweetList.get(i).getSentiment().equalsIgnoreCase("neutral")) {
					tweetListPartition.get(msNeutral).add(tweetList.get(i));
					msNeutral = (msNeutral+1)%10;
				}
				else if (tweetList.get(i).getSentiment().equalsIgnoreCase("negative")) {
					tweetListPartition.get(msNegative).add(tweetList.get(i));
					msNegative = (msNegative+1)%10;
				}
			}
			else if (tweetList.get(i).getTopic().equalsIgnoreCase("twitter")) {
				if (tweetList.get(i).getSentiment().equalsIgnoreCase("irrelevant")) {
					tweetListPartition.get(twitterIrrelevant).add(tweetList.get(i));
					twitterIrrelevant = (twitterIrrelevant+1)%10;
				}
				else if (tweetList.get(i).getSentiment().equalsIgnoreCase("positive")) {
					tweetListPartition.get(twitterPositive).add(tweetList.get(i));
					twitterPositive = (twitterPositive+1)%10;
				}
				else if (tweetList.get(i).getSentiment().equalsIgnoreCase("neutral")) {
					tweetListPartition.get(twitterNeutral).add(tweetList.get(i));
					twitterNeutral = (twitterNeutral+1)%10;
				}
				else if (tweetList.get(i).getSentiment().equalsIgnoreCase("negative")) {
					tweetListPartition.get(twitterNegative).add(tweetList.get(i));
					twitterNegative = (twitterNegative+1)%10;
				}
			}
		}

		for(int i=0;i<10;i++) {
			System.out.println("Fold " + i + " size : " + tweetListPartition.get(i).size());
		}
		System.out.println("End 10-fold partitioning....\n");
	}
	
	public void test() throws FileNotFoundException{

		FileOutputStream fout = new FileOutputStream("sentiment-result.csv");
		PrintWriter pw = new PrintWriter(fout);
		pw.println("Topic,Sentiment,TwitterText");
		for(int i = 0; i < tweetList.size(); i++){
			//count sentiment words
			int sentimentCount = 0;
			String text = tweetList.get(i).getText().replaceAll("\r", "").replaceAll("\n", "");
			String[] words = text.split(" ");
			for(String token : words){
				if(negativeLexicon.contains(token))
					sentimentCount--;
				if(positiveLexicon.contains(token))
					sentimentCount++;
			}
			
			if(sentimentCount > 0)
				tweetList.get(i).setPredictedPositive();
			else if(sentimentCount < 0)
				tweetList.get(i).setPredictedNegative();
			else
				tweetList.get(i).setPredictedNeutral();
			
			pw.println(tweetList.get(i));
		}
		pw.close();
	}
}
