import java.io.FileNotFoundException;

import java.io.IOException;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.ArrayList;
import java.util.Map;

public class LexiconSentiment {
	public static Set<String> s_negativeLexicon = null;
	public static Set<String> s_positiveLexicon = null;
	private ArrayList<Tweet> tweetList = null;
	private ArrayList<ArrayList<Tweet> > tweetListPartition = null;
	
	private ArrayList<Tweet> onlineTweetList = null;
	
	public static boolean s_runDevelopment = false;
	public static boolean s_runTraining = true;
	public static boolean s_runOnlineTesting = false;

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
			if (!s_runOnlineTesting) sentimenter.tenFoldCrossValidation();
			else sentimenter.doOnlineTesting();
		}
	}

	public LexiconSentiment(){
		// TODO Auto-generated constructor stub
		//initialize lexicon structure
		s_negativeLexicon = new TreeSet<String>();
		s_positiveLexicon = new TreeSet<String>();
		
		tweetList = new ArrayList<Tweet>();
		onlineTweetList = new ArrayList<Tweet>();
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
		
		if (s_runDevelopment) {
			//read in development elements 
			String developmentFname = GlobalHelper.pathToDataset + "development.csv";
			if(!readTweetHeaderFile(tweetList, developmentFname)){
				System.err.println("fail to read: " + developmentFname);
				System.err.println("fail to read in train elements from: " + developmentFname);
				return false;
			}
			else{
				System.out.println("count of train tweets: " + tweetList.size());
			}
		}
		
		if (s_runTraining) {
			//read in training elements 
			String trainFname = GlobalHelper.pathToDataset + "training.csv";
			if(!readTweetHeaderFile(tweetList, trainFname)){
				System.err.println("fail to read: " + trainFname);
				System.err.println("fail to read in train elements from: " + trainFname);
				return false;
			}
			else{
				System.out.println("count of training.csv tweets: " + tweetList.size());
			}
			
			//read in test elements 
			String testFname = GlobalHelper.pathToDataset + "testing.csv";
			if(!readTweetHeaderFile(tweetList, testFname)){
				System.err.println("fail to read: " + testFname);
				System.err.println("fail to read in test elements from: " + testFname);
				return false;
			}
			else{
				System.out.println("count of testing.csv tweets: " + tweetList.size());
			}
		}
		

		System.out.println("count of combined train tweets: " + tweetList.size());
		
		if (s_runOnlineTesting) {
			//read in test elements 
			String onlineTestFname = GlobalHelper.pathToDataset + "onlinetest.csv";
			if(!readTweetHeaderFile(onlineTweetList, onlineTestFname)){
				System.err.println("fail to read: " + onlineTestFname);
				System.err.println("fail to read in test elements from: " + onlineTestFname);
				return false;
			}
			else{
				System.out.println("count of onlinetest.csv tweets: " + onlineTweetList.size());
			}
		}
		
		if (!s_runOnlineTesting) {
			//ten fold partition
			tenFoldPartition();
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
	private boolean readTweetHeaderFile(ArrayList<Tweet> currTweetList, String fname) throws IOException{
		//read in csv test file
		if(!CsvFileReader.readCsvElementsTweet(currTweetList, fname, 3, true)){
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
	
	private void printCMPerformance2(String currLabel, int tp, int fp, int fn) {
		double recall = (double)tp/((double)tp+(double)fn);
		double precision = (double)tp/((double)tp+(double)fp);
		double f1score = 2.0*precision*recall/(precision+recall);

		System.out.println(currLabel + " TP " + " = " + tp);
		System.out.println(currLabel + " FP " + " = " + fp);
		System.out.println(currLabel + " FN " + " = " + fn);
		System.out.println(currLabel + " Recall " + " = " + (double)Math.round(recall * 10000d) / 10000d);
		System.out.println(currLabel + " Precision " + " = " + (double)Math.round(precision * 10000d) / 10000d);
		System.out.println(currLabel + " F1-Score " + " = " + (double)Math.round(f1score * 10000d) / 10000d);
		System.out.println();
	}
	
	private void printCMPerformance(TreeMap<String, Integer> cm) {

		int globalTp, globalFp, globalFn;
		globalTp = globalFp = globalFn = 0;
		int tp,fp,fn;
		tp = fp = fn = 0;
		String prevLabel = "";
		for(Map.Entry<String,Integer> entry : cm.entrySet()) {
			String key = entry.getKey();
			Integer value = entry.getValue();
			
			if (key.equals("total")) continue;
			
			String[] currLabel = key.split("\\s");
			if (!currLabel[0].equals(prevLabel)) {
				//calculate
				if (prevLabel.length()>0) printCMPerformance2(prevLabel, tp, fp , fn);

				globalTp += tp; globalFp += fp; globalFn += fn;
				tp = fp = fn = 0;
				prevLabel = currLabel[0];
			}
			String currCategory = currLabel[1];
			if (currCategory.equals("TP")) tp = value;
			else if (currCategory.equals("FN")) fn = value;
			else if (currCategory.equals("FP")) fp = value;
		}
		printCMPerformance2(prevLabel, tp, fp , fn);

		globalTp += tp; globalFp += fp; globalFn += fn;
		printCMPerformance2("Overall", globalTp,globalFp,globalFn);

		System.out.println("Total = "+cm.get("total"));
	}
	
	public void printGlobalCMPerformance() {
		printCMPerformance(globalCm);	
	}
	
	private TreeMap<String, Integer> globalCm = new TreeMap<String,Integer>();
	
	public void tenFoldCrossValidation() throws FileNotFoundException{
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
			
			// Use Maxent
			MaxentHelper.createMaxentFile(trainingList, trainFilename);
			MaxentHelper.createMaxentFile(testList, testFilename);
			TreeMap<String,Integer> cm = MaxentHelper.classify(trainFilename, testFilename, Integer.toString(fold), "unigram", false);
			
			// Use SVM
//			SVMHelper.createTrainingFileSVM(trainingList, Integer.toString(fold));
//			SVMHelper.createTestFileSVM(testList, Integer.toString(fold));
//			TreeMap<String,Integer> cm = SVMHelper.classify(fold);

			// Classify and evaluate
			
			for(Map.Entry<String,Integer> entry : cm.entrySet()) {
				String key = entry.getKey();
				Integer value = entry.getValue();
				
				Integer globalValue = globalCm.get(key);
				if (globalValue == null) globalCm.put(key, value);
				else globalCm.put(key, value + globalValue);
			}
			
			printCMPerformance(cm);
		}

		System.out.println("==========================================");
		printGlobalCMPerformance();
	}
	
	public void doOnlineTesting() throws FileNotFoundException{
			
		String trainFilename = "train.txt";
		String testFilename = "test.txt";
		
		// Use Maxent
		MaxentHelper.createMaxentFile(tweetList, trainFilename);
		MaxentHelper.createMaxentFile(onlineTweetList, testFilename);
		TreeMap<String,Integer> cm = MaxentHelper.classify(trainFilename, testFilename, "", "ngram",true);
	}
}
