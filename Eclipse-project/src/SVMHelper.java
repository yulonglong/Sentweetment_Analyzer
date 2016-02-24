import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import edu.stanford.nlp.classify.Classifier;
import edu.stanford.nlp.classify.ColumnDataClassifier;
import edu.stanford.nlp.ling.Datum;
import edu.stanford.nlp.objectbank.ObjectBank;

public class SVMHelper {
	private static String s_svmFolderName = "ClassifierSVM";
	
	public static void createTrainingFileSVM(ArrayList<Tweet> tweetList, String fold) {
		Path currentRelativePath = Paths.get("");
		String currPathStr = currentRelativePath.toAbsolutePath().toString();
		File vocabFile = new File(currPathStr+"/"+s_svmFolderName+"/"+"vocab"+fold);
		File file = new File(currPathStr+"/"+s_svmFolderName+"/"+"train"+fold+".txt");
		PrintWriter vocabPw = null;
		PrintWriter pw = null;
		try {
			vocabPw = new PrintWriter(vocabFile);
			pw = new PrintWriter(file);
		}
		catch (Exception e) { e.printStackTrace(); }
		
		Map<String, Integer> dictionary = new TreeMap<String, Integer>();
		for (int i=0;i<tweetList.size();i++) {
			Tweet currTweet = tweetList.get(i);
			String[] words = currTweet.getText().split(" ");
			for(String token : words) {
				if (!dictionary.containsKey(token)) {
					dictionary.put(token, dictionary.size());
				}
			}
		}
		// Document Frequency List
		List<Integer> freqList = new ArrayList<Integer>(Collections.nCopies(dictionary.size(), 0));
		for (int i=0;i<tweetList.size();i++) {
			Tweet currTweet = tweetList.get(i);
			String[] words = currTweet.getText().split(" ");
			
			TreeSet<String> wordSet = new TreeSet<String>();
			for(String token : words) {
				if (wordSet.contains(token)) continue;
 				Integer index = dictionary.get(token);
				if (index == null) continue;
				Integer currFreq = freqList.get(index);
				freqList.set(index, currFreq+1);
				wordSet.add(token);
			}
		}
		
		// Print Vocab
		vocabPw.println(dictionary.size());
		for(Map.Entry<String,Integer> entry : dictionary.entrySet()) {
			String key = entry.getKey();
			Integer value = entry.getValue();
			Integer freq = freqList.get(value);
			vocabPw.println(key+"\t"+value+"\t"+freq);
			vocabPw.flush();
		}
		vocabPw.close();
		
		for (int i=0;i<tweetList.size();i++) {
			TreeMap<Integer,String> currentFeatures = new TreeMap<Integer,String>();
			
			Tweet currTweet = tweetList.get(i);
			pw.print(currTweet.getSentimentInteger());
			String[] words = currTweet.getText().split(" ");
			for(String token : words) {
				Integer index = dictionary.get(token);
				if (index == null) continue;
				
				int freq = 0;
				for(String token2 :words) {
					if (token.equalsIgnoreCase(token2)) freq++;
				}
				int docLength = words.length;
				
				Integer currFreq = freqList.get(index);
				double tf = (double)freq/(double)docLength;
				double idf = Math.log((double)tweetList.size()/(double)currFreq);
				double tfidf = Math.min(tf/idf, 1.0);
				currentFeatures.put(index+1," "+(index+1)+":"+tfidf);
			}
			for(Map.Entry<Integer,String> entry : currentFeatures.entrySet()) {
				String value = entry.getValue();
				pw.print(value);
			}
			pw.println();
			pw.flush();
		}
		pw.close();
	}
	
	public static void createTestFileSVM(ArrayList<Tweet> tweetList, String fold) {
		Path currentRelativePath = Paths.get("");
		String currPathStr = currentRelativePath.toAbsolutePath().toString();
		File vocabFile = new File(currPathStr+"/"+s_svmFolderName+"/"+"vocab"+fold);
		File file = new File(currPathStr+"/"+s_svmFolderName+"/"+"test"+fold+".txt");
		BufferedReader vocabBr = null;
		PrintWriter pw = null;
		try {
			vocabBr= new BufferedReader(new FileReader(vocabFile));
			pw = new PrintWriter(file);
		}
		catch (Exception e) { e.printStackTrace(); }
		
		try {
			String line = vocabBr.readLine();
			int size = Integer.parseInt(line);
			
			Map<String, Integer> dictionary = new TreeMap<String, Integer>();
			List<Integer> freqList = new ArrayList<Integer>(Collections.nCopies(size, 0));
			
			while ((line = vocabBr.readLine()) != null) {
				String tokens[] = line.split("\\t");
				dictionary.put(tokens[0],Integer.parseInt(tokens[1]));
				freqList.set(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]));
			}
			vocabBr.close();
			
			for (int i=0;i<tweetList.size();i++) {
				TreeMap<Integer,String> currentFeatures = new TreeMap<Integer,String>();
				
				Tweet currTweet = tweetList.get(i);
				pw.print(currTweet.getSentimentInteger());
				String[] words = currTweet.getText().split(" ");
				for(String token : words) {
					Integer index = dictionary.get(token);
					if (index == null) continue;
					
					int freq = 0;
					for(String token2 :words) {
						if (token.equalsIgnoreCase(token2)) freq++;
					}
					int docLength = words.length;
					
					Integer currFreq = freqList.get(index);
					double tf = (double)freq/(double)docLength;
					double idf = Math.log((double)tweetList.size()/(double)currFreq);
					double tfidf = Math.min(tf/idf, 1.0);
					currentFeatures.put(index+1," "+(index+1)+":"+tfidf);
				}
				for(Map.Entry<Integer,String> entry : currentFeatures.entrySet()) {
					String value = entry.getValue();
					pw.print(value);
				}
				
				pw.println();
				pw.flush();
			}
			pw.close();
		}
		catch (Exception e) { e.printStackTrace(); }
		// Document Frequency List
		
	}
	
	public static void trainSVM(String fold) {
		// Train the CRF and get Model File
		String[] commandTrain = {"cmd", 
		"/c" ,
		"svm_multiclass_learn",
		"-c",
		"1000",
		"train"+fold+".txt",
		"model"+fold+".txt"};
		// System.out.println(Arrays.toString(commandTrain));
		String currPathStr = Paths.get("").toAbsolutePath().toString();
		ExecuteCommandHelper.runExecutable(commandTrain, new File(currPathStr+"/"+s_svmFolderName));
	}

	public static void testSVM(String fold) {
		// Test the CRF and using the Model File
		String[] commandTest = {"cmd", 
		"/c" ,
		"svm_multiclass_classify",
		"test"+fold+".txt",
		"model"+fold+".txt",
		"prediction"+fold+".txt"};
		// System.out.println(Arrays.toString(commandTest));
		String currPathStr = Paths.get("").toAbsolutePath().toString();
		ExecuteCommandHelper.runExecutable(commandTest, new File(currPathStr+"/"+s_svmFolderName));
	}
	
	public static TreeMap<String,Integer> classify(int fold) {
		trainSVM(Integer.toString(fold));
		testSVM(Integer.toString(fold));
		
		// confusion matrix
		TreeMap<String, Integer> cm = new TreeMap<String,Integer>();
		int total = 0;
		
		try {
			String currPathStr = Paths.get("").toAbsolutePath().toString();
			File file = new File(currPathStr+"/"+s_svmFolderName+"/"+"prediction"+fold+".txt");
			BufferedReader br = new BufferedReader(new FileReader(file));
			
			File goldFile = new File(currPathStr+"/"+s_svmFolderName+"/"+"test"+fold+".txt");
			BufferedReader goldBr = new BufferedReader(new FileReader(goldFile));
			
			String line;
			while ((line = br.readLine()) != null) {
				String tokens[] = line.split(" ");
				String predictedAnswer = tokens[0];
				predictedAnswer = Tweet.convertIntToStringSentiment(Integer.parseInt(predictedAnswer));
				
				String line2 = goldBr.readLine();
				String tokens2[] = line2.split(" ");
				String correctAnswer = tokens2[0];
				correctAnswer = Tweet.convertIntToStringSentiment(Integer.parseInt(correctAnswer));
				
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
			br.close();
			goldBr.close();
		}
		catch (Exception e) { e.printStackTrace(); }
		return cm;
	}
	
}
