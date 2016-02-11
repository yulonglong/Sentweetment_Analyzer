import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import edu.stanford.nlp.classify.Classifier;
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
			pw.println("useClassFeature=true");
			pw.println("1.useNGrams=false");
			pw.println("2.useNGrams=false");
			pw.println("2.splitWordsRegexp = \\\\s+");
			pw.println("2.useSplitWords = true");
			pw.println("goldAnswerColumn=0");
			pw.println("printTo=./param.txt");
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
	
	public static void createFileUnigram(ArrayList<Tweet> tweetList, String filename) {
		Path currentRelativePath = Paths.get("");
		String currPathStr = currentRelativePath.toAbsolutePath().toString();
		File file = new File(currPathStr+"/"+s_maxentFolderName+"/"+filename);
		
		try {
			PrintWriter pw = new PrintWriter(file);
			for (int i=0;i<tweetList.size();i++) {
				Tweet currTweet = tweetList.get(i);
				pw.print(currTweet.getSentiment()+"\t");
				pw.print(currTweet.getTopic()+"\t");
				pw.print(currTweet.getTextOneLine());
				pw.println();
				pw.flush();
			}
			pw.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// count[0] = correct prediction
	// count[1] = wrong prediction
	public static int[] classify(String trainPath, String testPath) {
		int[] count = new int[2];
		count[0] = count[1] = 0;
		
		Path currentRelativePath = Paths.get("");
		String currPathStr = currentRelativePath.toAbsolutePath().toString();

		ColumnDataClassifier cdc = new ColumnDataClassifier(currPathStr+"/"+s_maxentFolderName+"/"+s_propFileName);
		Classifier<String,String> cl = cdc.makeClassifier(cdc.readTrainingExamples(currPathStr+"/"+s_maxentFolderName+"/"+trainPath));
		for (String line : ObjectBank.getLineIterator(currPathStr+"/"+s_maxentFolderName+"/"+testPath, "utf-8")) {
			String[] tokens = line.split("\t");
			String correctAnswer = tokens[0];

			Datum<String,String> d = cdc.makeDatumFromLine(line);
			String predictedAnswer = cl.classOf(d);

			if (correctAnswer.equals(predictedAnswer)) count[0]++;
			else count[1]++;	
		}
		
		return count;
	}
}
