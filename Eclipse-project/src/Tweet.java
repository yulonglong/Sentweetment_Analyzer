import java.util.ArrayList;
import java.util.List;

import javax.json.JsonObject;

public class Tweet {
	private String topic;
	private String sentiment;
	private String predictedSentiment;
	private String text;
	private String id;
	private List<String> positiveLexiconList;
	private List<String> negativeLexiconList;
	private int positiveLexiconCount;
	private int negativeLexiconCount;
	
	Tweet(String _topic, String _sentiment, String _id) {
		topic = _topic;
		sentiment = _sentiment;
		id = _id;
		this.retrieveTextFromJson();
		this.processLexicon();
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
			String rawText = (tweetJson.getString("text"));
			text = rawText.replaceAll("(?:(?:\\r\\n)|(?:\\r)|(?:\\n))+", " | ");
			text =  text.replaceAll("\\t+", " ");
			
			String urlRegex = "(?:http:\\/\\/t\\.co\\/[A-Za-z0-9]+)";
			text =  text.replaceAll(urlRegex, " [url] ");
		}
		else{
			System.err.println("no text key in: " + tweetFname);
			return;
		}
	}
	
	private void processLexicon() {
		positiveLexiconList = new ArrayList<String>();
		negativeLexiconList = new ArrayList<String>();
		positiveLexiconCount = 0;
		negativeLexiconCount = 0;
		
		String[] words = text.split(" ");
		for(String token : words) {
			if (LexiconSentiment.s_positiveLexicon.contains(token.toLowerCase())) {
				positiveLexiconCount++;
				positiveLexiconList.add(token.toLowerCase());
			}
			if (LexiconSentiment.s_negativeLexicon.contains(token.toLowerCase())) {
				negativeLexiconCount++;
				negativeLexiconList.add(token.toLowerCase());
			}
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
	public int getPositiveLexiconCount() { return positiveLexiconCount; }
	public int getNegativeLexiconCount() { return negativeLexiconCount; }
	public String getPositiveLexiconTokens() { 
		StringBuilder sb = new StringBuilder(" ");
		for (String posLexicon : positiveLexiconList) {
			sb.append(posLexicon + " ");
		}
		return sb.toString();
	}
	public String getNegativeLexiconTokens() {
		StringBuilder sb = new StringBuilder(" ");
		for (String negLexicon : negativeLexiconList) {
			sb.append(negLexicon + " ");
		}
		return sb.toString();
	}
	
	public boolean isPredictionCorrect() {
		if (sentiment.equalsIgnoreCase(predictedSentiment)) return true;
		return false;
	}
	
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