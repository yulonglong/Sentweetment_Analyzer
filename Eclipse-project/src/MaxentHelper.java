import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.stanford.nlp.classify.Classifier;
import edu.stanford.nlp.classify.LinearClassifier;
import edu.stanford.nlp.classify.ColumnDataClassifier;
import edu.stanford.nlp.ling.Datum;
import edu.stanford.nlp.objectbank.ObjectBank;

public class MaxentHelper {
	private static String s_maxentFolderName = "ClassifierMaxent";
	private static String s_propFileName = "tweet.prop";
	
	public static void createPropUnigram() {
		Path currentRelativePath = Paths.get("");
		String currPathStr = currentRelativePath.toAbsolutePath().toString();
		
		// Make folder if doesnt exist
		File folder = new File(currPathStr+"/"+s_maxentFolderName+"/");
		folder.mkdirs();
		
		try {
			File prop = new File(currPathStr+"/"+s_maxentFolderName+"/"+s_propFileName);
			PrintWriter pw = new PrintWriter(prop);
			pw.println("useClassFeature=false");
			// pw.println("trainFromSVMLight = true");
			// pw.println("testFromSVMLight = true");
			
			// Topic
			pw.println("1.useNGrams=false");
			pw.println("1.splitWordsRegexp = \\\\s+");
			pw.println("1.useSplitWords = true");
			// Tweet text
			pw.println("2.useNGrams=false");
			pw.println("2.splitWordsRegexp = \\\\s+");
			pw.println("2.useSplitWords = true");
			// Positive Lexicon Count
			pw.println("3.realValued = true");
			// Negative Lexicon Count
			pw.println("4.realValued = true");
//			// Positive Lexicon Words
//			pw.println("5.useNGrams=false");
//			pw.println("5.splitWordsRegexp = \\\\s+");
//			pw.println("5.useSplitWords = true");
//			// Negative Lexicon Words
//			pw.println("6.useNGrams=false");
//			pw.println("6.splitWordsRegexp = \\\\s+");
//			pw.println("6.useSplitWords = true");
//			// Retweets Count
//			pw.println("7.realValued = true");
//			// Timezone
//			pw.println("8.useNGrams=false");
//			pw.println("8.splitWordsRegexp = \\\\s+");
//			pw.println("8.useSplitWords = true");
//			// Friend's Count
//			pw.println("9.realValued = true");
//			// Follower's Count
//			pw.println("10.realValued = true");
//			// Favourite's Count
//			pw.println("11.realValued = true");
			
			// User's Id
			pw.println("12.realValued = true");
			
			pw.println("goldAnswerColumn=0");
			pw.println("intern=true");
			pw.println("sigma=3");
			pw.println("useQN=true");
			pw.println("QNsize=15");
			pw.println("tolerance=1e-4");
			pw.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void createPropNgram() {
		Path currentRelativePath = Paths.get("");
		String currPathStr = currentRelativePath.toAbsolutePath().toString();
		
		// Make folder if doesnt exist
		File folder = new File(currPathStr+"/"+s_maxentFolderName+"/");
		folder.mkdirs();
		
		try {
			File prop = new File(currPathStr+"/"+s_maxentFolderName+"/"+s_propFileName);
			PrintWriter pw = new PrintWriter(prop);
			pw.println("useClassFeature=false");
			
			// Topic
			pw.println("1.useNGrams=false");
			pw.println("1.splitWordsRegexp = \\\\s+");
			pw.println("1.useSplitWords = true");
			
			// Tweet text
			pw.println("2.splitWordsRegexp = \\\\s+");
			pw.println("2.useSplitWords = true");
			pw.println("2.useSplitWordNGrams = true");
			pw.println("2.maxWordNGramLeng = 2");
			
			// Positive Lexicon Count
			pw.println("3.realValued = true");
			
			// Negative Lexicon Count
			pw.println("4.realValued = true");
			
//			// Positive Lexicon Words
//			pw.println("5.useNGrams=false");
//			pw.println("5.splitWordsRegexp = \\\\s+");
//			pw.println("5.useSplitWords = true");
//			
//			// Negative Lexicon Words
//			pw.println("6.useNGrams=false");
//			pw.println("6.splitWordsRegexp = \\\\s+");
//			pw.println("6.useSplitWords = true");
			
//			// Retweets Count
//			pw.println("7.realValued = true");
//			
//			// Timezone
//			pw.println("8.useNGrams=false");
//			pw.println("8.splitWordsRegexp = \\\\s+");
//			pw.println("8.useSplitWords = true");
			
//			// Friend's Count
//			pw.println("9.realValued = true");
//			
//			// Follower's Count
//			pw.println("10.realValued = true");
//			
//			// Favourite's Count
//			pw.println("11.realValued = true");
			
			// User's Id
			pw.println("12.realValued = true");
	
			pw.println("goldAnswerColumn=0");
			pw.println("intern=true");
			pw.println("sigma=3");
			pw.println("useQN=true");
			pw.println("QNsize=15");
			pw.println("tolerance=1e-4");
			pw.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void createMaxentFile(ArrayList<Tweet> tweetList, String filename) {
		// Make folder if doesnt exist
		String currPathStr = Paths.get("").toAbsolutePath().toString();
		File folder = new File(currPathStr+"/"+s_maxentFolderName+"/");
		folder.mkdirs();
		
		File file = new File(currPathStr+"/"+s_maxentFolderName+"/"+filename);
		
		try {
			PrintWriter pw = new PrintWriter(file);
			for (int i=0;i<tweetList.size();i++) {
				Tweet currTweet = tweetList.get(i);
				pw.print(currTweet.getSentiment()+"\t");
				pw.print(currTweet.getTopic()+"\t");
				pw.print(currTweet.getText()+"\t");
				pw.print(currTweet.getPositiveLexiconCount()+"\t");
				pw.print(currTweet.getNegativeLexiconCount()+"\t");
				pw.print(currTweet.getPositiveLexiconTokens()+"\t");
				pw.print(currTweet.getNegativeLexiconTokens()+"\t");
				
				pw.print(currTweet.getRetweets()+"\t");
				pw.print(currTweet.getTimezone()+"\t");
//				pw.print(currTweet.getFriendsCount()+"\t");
//				pw.print(currTweet.getFollowersCount()+"\t");
//				pw.print(currTweet.getFavouritesCount()+"\t");
				pw.print(currTweet.getUserId()+"\t");
				
				pw.println();
				pw.flush();
			}
			pw.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void printClassifierAllWeights(Classifier<?, ?> classifier, String filename) {
		Path currentRelativePath = Paths.get("");
		String currPathStr = currentRelativePath.toAbsolutePath().toString();
		File classifierFile = new File (currPathStr+"/"+s_maxentFolderName+"/"+filename);
		
		String classString;
		if (classifier instanceof LinearClassifier<?,?>) {
			classString = ((LinearClassifier<?,?>)classifier).toString("AllWeights", 0);
		} else {
			classString = classifier.toString();
		}
		PrintWriter fw = null;
		try {
			fw = new PrintWriter(classifierFile);
			fw.write(classString);
			fw.println();
		} catch (Exception e) {
			e.printStackTrace();
		}
		fw.close();
	}
	
	private static void printClassifierTopNWeights(Classifier<?, ?> classifier, String filename, int n) {
		Path currentRelativePath = Paths.get("");
		String currPathStr = currentRelativePath.toAbsolutePath().toString();
		File classifierFile = new File (currPathStr+"/"+s_maxentFolderName+"/"+filename);
		
		String classString;
		if (classifier instanceof LinearClassifier<?,?>) {
			classString = ((LinearClassifier<?,?>)classifier).toString("HighWeight", n);
		} else {
			classString = classifier.toString();
		}
		PrintWriter fw = null;
		try {
			fw = new PrintWriter(classifierFile);
			fw.write(classString);
			fw.println();
		} catch (Exception e) {
			e.printStackTrace();
		}
		fw.close();
	}
	
	private static void printPossibleNewLexicon(String classifierTopNWeightFilename, String positiveFilename, String negativeFilename) {
		Path currentRelativePath = Paths.get("");
		String currPathStr = currentRelativePath.toAbsolutePath().toString();
		File classifierFile = new File (currPathStr+"/"+s_maxentFolderName+"/"+classifierTopNWeightFilename);
		
		File posFile = new File (currPathStr+"/"+s_maxentFolderName+"/"+positiveFilename+".txt");
		File negFile = new File (currPathStr+"/"+s_maxentFolderName+"/"+negativeFilename+".txt");
		
		
		try {
			PrintWriter posPw = new PrintWriter(posFile);
			PrintWriter negPw = new PrintWriter(negFile);
			BufferedReader br = new BufferedReader (new FileReader(classifierFile)); 
			String line = br.readLine(); // discard first line
			
			String regexPattern = "(?:\\(2-SW-(.+),((?:positive)|(?:negative))\\)\\s+\\d\\.\\d+)";
			Pattern pattern = Pattern.compile(regexPattern);
			while ((line = br.readLine()) != null) {
				Matcher m = pattern.matcher(line);
				if (m.find()) {
					String word = m.group(1);
					String sentiment = m.group(2);
					if (sentiment.equals("positive")) {
						if (!LexiconSentiment.s_positiveLexicon.contains(word.toLowerCase())) {
							posPw.println(word);
							posPw.flush();
						}
					}
					else if (sentiment.equals("negative")) {
						if (!LexiconSentiment.s_negativeLexicon.contains(word.toLowerCase())) {
							negPw.println(word);
							negPw.flush();
						}
					}
				}
			}
			
			posPw.close();
			negPw.close();
			br.close();
		}
		catch (Exception e) { e.printStackTrace(); }
	}
	
	private static void serializeClassifier(Classifier<?, ?> classifier, String filename) {
		Path currentRelativePath = Paths.get("");
		String currPathStr = currentRelativePath.toAbsolutePath().toString();
		try {
			FileOutputStream out = new FileOutputStream(currPathStr+"/"+s_maxentFolderName+"/"+filename);
			ObjectOutputStream oos = new ObjectOutputStream(out);
			oos.writeObject(classifier);
			oos.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static Classifier<?, ?> deserializeClassifier(String filename) {
		Path currentRelativePath = Paths.get("");
		String currPathStr = currentRelativePath.toAbsolutePath().toString();
		try {
			FileInputStream in = new FileInputStream(currPathStr+"/"+s_maxentFolderName+"/"+filename);
			ObjectInputStream iis = new ObjectInputStream(in);
			Classifier<?, ?> classifier = (Classifier<?, ?>) iis.readObject();
			iis.close();
			return classifier;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	// count[0] = correct prediction
	// count[1] = wrong prediction
	public static TreeMap<String,Integer> classify(String trainFilename, String testFilename, int fold, String command) {
		if (command.equalsIgnoreCase("unigram")) createPropUnigram();
		else if (command.equalsIgnoreCase("ngram")) createPropNgram();
		else {
			System.err.println("Invalid command in Maxent classify");
			return null;
		}
		
		// confusion matrix
		TreeMap<String, Integer> cm = new TreeMap<String,Integer>();
		int total = 0;
		
		Path currentRelativePath = Paths.get("");
		String currPathStr = currentRelativePath.toAbsolutePath().toString();

		ColumnDataClassifier cdc = new ColumnDataClassifier(currPathStr+"/"+s_maxentFolderName+"/"+s_propFileName);
		Classifier<String,String> cl = cdc.makeClassifier(cdc.readTrainingExamples(currPathStr+"/"+s_maxentFolderName+"/"+trainFilename));
		
		printClassifierAllWeights(cl, "classifierAll_"+fold+".txt");
		printClassifierTopNWeights(cl, "classifierTop_"+fold+".txt",1000);
		printPossibleNewLexicon("classifierTop_"+fold+".txt","possiblePositive"+fold, "possibleNegative"+fold);
		
		serializeClassifier(cl, "classifierModel"+fold);
		
		// Classifier<String,String> deserializedCl = (Classifier<String,String>) deserializeClassifier("classifierModel"+fold);
		
		for (String line : ObjectBank.getLineIterator(currPathStr+"/"+s_maxentFolderName+"/"+testFilename, "utf-8")) {
			String[] tokens = line.split("\t");
			String correctAnswer = tokens[0];

			Datum<String,String> d = cdc.makeDatumFromLine(line);
			String predictedAnswer = cl.classOf(d);
			
			// Evaluation
			if (correctAnswer.equals(predictedAnswer)) {
				Integer freq = cm.get(correctAnswer+" TP");
				if (freq == null) cm.put(correctAnswer+" TP",1);
				else cm.put(correctAnswer+" TP",freq+1);
			}
			else {
				Integer freqFN = cm.get(correctAnswer+" FN");
				if (freqFN == null) cm.put(correctAnswer+" FN",1);
				else cm.put(correctAnswer+" FN",freqFN+1);
				
				Integer freqFP = cm.get(predictedAnswer+" FP");
				if (freqFP == null) cm.put(predictedAnswer+" FP",1);
				else cm.put(predictedAnswer+" FP",freqFP+1);
			}
			total++;
		}
		cm.put("total",total);
		return cm;
	}
}
