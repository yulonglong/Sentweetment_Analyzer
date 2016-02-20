import java.io.FileNotFoundException;

import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import java.util.ArrayList;

public class LexiconSentiment {
	public static Set<String> s_negativeLexicon = null;
	public static Set<String> s_positiveLexicon = null;
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
			sentimenter.tenFoldCrossValidation();
		}
	}

	public LexiconSentiment(){
		// TODO Auto-generated constructor stub
		//initialize lexicon structure
		s_negativeLexicon = new TreeSet<String>();
		s_positiveLexicon = new TreeSet<String>();
		
		tweetList = new ArrayList<Tweet>();
	}
	public boolean initialize() throws IOException{
		// Read in negative Lexicon 
		if(!initLexicon(GlobalHelper.pathToNegativeLexicon, s_negativeLexicon)){
			System.err.println("fail to initialize the negative lexicon: " + GlobalHelper.pathToNegativeLexicon);
			return false;
		}
		else{
			System.out.println("negative lexicon size: " + s_negativeLexicon.size());
		}
		// Read in positive Lexicon
		if(!initLexicon(GlobalHelper.pathToPositiveLexicon, s_positiveLexicon)){
			System.err.println("fail to initialize the positive lexicon: " + GlobalHelper.pathToPositiveLexicon);
			return false;
		}
		else{
			System.out.println("positive lexicon size: " + s_positiveLexicon.size());
		}
		
		//read in training elements 
		String trainFname = GlobalHelper.pathToDataset + "training.csv";
		if(!readTweetHeaderFile(trainFname)){
			System.err.println("fail to read: " + trainFname);
			System.err.println("fail to read in train elements from: " + trainFname);
			return false;
		}
		else{
			System.out.println("count of train tweets: " + tweetList.size());
		}
		
		//read in test elements 
		String testFname = GlobalHelper.pathToDataset + "testing.csv";
		if(!readTweetHeaderFile(testFname)){
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
	private boolean readTweetHeaderFile(String fname) throws IOException{
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
	
	public void tenFoldCrossValidation() throws FileNotFoundException{
		int overallCorrect = 0;
		int overallWrong = 0;
		
		for(int fold=0;fold<10;fold++) {
			ArrayList<Tweet> trainingList = new ArrayList<Tweet>();
			ArrayList<Tweet> testList = new ArrayList<Tweet>();
			// fold 0 use partition 0 as test set, etc
			for(int i=0;i<10;i++){
				if (fold == i) {
					for(int j=0;j<tweetListPartition.get(i).size();j++){
						testList.add(tweetListPartition.get(i).get(j));
					}
				}
				else {
					for(int j=0;j<tweetListPartition.get(i).size();j++){
						trainingList.add(tweetListPartition.get(i).get(j));
					}
				}
			}
			
			String trainFilename = "train"+fold+".txt";
			String testFilename = "test"+fold+".txt";
			MaxentHelper.createFileUnigram(trainingList, trainFilename);
			MaxentHelper.createFileUnigram(testList, testFilename);

			int count[] = MaxentHelper.classify(trainFilename, testFilename, fold);
			int correctPrediction = count[0];
			int wrongPrediction = count[1];
			
			overallCorrect += correctPrediction;
			overallWrong += wrongPrediction;
			
			double accuracy = ((double)correctPrediction/(double)(correctPrediction+wrongPrediction));
			System.out.println("============");
			System.out.println("Fold " + fold);
			System.out.println("Correct : " + correctPrediction);
			System.out.println("Wrong   : " + wrongPrediction);
			System.out.println("Total   : " + (correctPrediction+wrongPrediction));
			System.out.println("Accuracy: " + accuracy);
			
//			pw.println("Correct : " + correctPrediction);
//			pw.println("Wrong   : " + wrongPrediction);
//			pw.println("Total   : " + (correctPrediction+wrongPrediction));
//			pw.println("Accuracy: " + accuracy);
//			pw.close();
		}
		
		double overallAccuracy = ((double)overallCorrect/(double)(overallCorrect+overallWrong));
		System.out.println("============");
		System.out.println("Overall");
		System.out.println("Correct : " + overallCorrect);
		System.out.println("Wrong   : " + overallWrong);
		System.out.println("Total   : " + (overallCorrect+overallWrong));
		System.out.println("Accuracy: " + overallAccuracy);
	}
}
