import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.json.JsonArray;
import javax.json.JsonObject;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;

import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.tagger.maxent.*;

public class Tweet {
	private String topic;
	private String sentiment;
	private String predictedSentiment;
	private String text;
	private String textWithPOS;
	private String id;
	
	private List<String> hashtags;
	private int retweets;
	private String timezone;
	private int friendsCount; // The number of users this account is following (AKA their ¡°followings¡±)
	private int followersCount; // The number of followers this account currently has. 
	private int favouritesCount; // The number of tweets this user has favorited in the account¡¯s lifetime.
	private int userId; // id of the person posting this tweet
	
	private List<String> positiveLexiconList;
	private List<String> negativeLexiconList;
	private int positiveLexiconCount;
	private int negativeLexiconCount;
	
	Tweet(String _topic, String _sentiment, String _id) {
		topic = _topic;
		sentiment = _sentiment;
		id = _id;
		hashtags = new ArrayList<String>();
		this.extractFeaturesFromJson();
		this.processLexicon();
		predictedSentiment = "";
	}
	
	private void extractFeaturesFromJson() {
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
			text =  text.replaceAll(urlRegex, " url ");
			
			StringBuilder sb = new StringBuilder();
			PTBTokenizer<CoreLabel> ptbt = new PTBTokenizer<>(new StringReader(text), new CoreLabelTokenFactory(), "");
			while (ptbt.hasNext()) {
				CoreLabel coreLabel = ptbt.next();
				sb.append(coreLabel.toString().toLowerCase() + " ");
			}
			text = sb.toString();
			
			// To POS-tag the text
//			StringBuilder sbPos = new StringBuilder();
//			List<List<HasWord>> sentences = MaxentTagger.tokenizeText(new StringReader(text));
//			for (List<HasWord> sentence : sentences) {
//				List<TaggedWord> taggedSentence = LexiconSentiment.s_tagger.tagger.tagSentence(sentence);
//				for (TaggedWord token : taggedSentence) {
//					sbPos.append(token.toString()+" ");
//				}
//			}
//			textWithPOS = sbPos.toString();
		}
		else{
			System.err.println("no text key in: " + tweetFname);
			return;
		}
		
		// get the hashtags information from the tweet JsonObject
		JsonArray hashtagsArray = tweetJson.getJsonObject("entities").getJsonArray("hashtags");
		for (int i = 0; i < hashtagsArray.size(); i++) {
			hashtags.add(hashtagsArray.getJsonObject(i).getString("text"));
		}
		
		//get the retweets information
		retweets = tweetJson.getInt("retweet_count");
		
		//get the timezone information, can be null
		if (!tweetJson.getJsonObject("user").isNull("time_zone")) {
			timezone = tweetJson.getJsonObject("user").getString("time_zone");			
		} else {
			timezone = "null";
		}
			
		// get friendscount
		friendsCount = tweetJson.getJsonObject("user").getInt("friends_count");
		
		// get followersscount
		followersCount = tweetJson.getJsonObject("user").getInt("followers_count");
		
		// get favouritescount
		favouritesCount = tweetJson.getJsonObject("user").getInt("favourites_count");
		
		// get userId
		userId = tweetJson.getJsonObject("user").getInt("id");
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
	public String getSentimentInteger() {
		if (sentiment.equals("positive")) return "4";
		if (sentiment.equals("neutral")) return "3";
		if (sentiment.equals("negative")) return "2";
		if (sentiment.equals("irrelevant")) return "1";
		return "-1";
	}
	public String getText() { return text; }
	public String getTextWithPOS() { return textWithPOS; }
	public String getId() { return id; }
	public int getRetweets() {  return retweets; }
	public String getTimezone() {  return timezone; }
	public int getFriendsCount() { return friendsCount; }
	public int getFollowersCount() { return followersCount; }
	public int getFavouritesCount() { return favouritesCount; }
	public int getUserId() { return userId; }
	
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
	
	public String isTopicRelevant() {
		if (text.contains(topic.toLowerCase())) return "1";
		return "0";
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
	
	public static String convertIntToStringSentiment(int n) {
		if (n == 4) return "positive";
		if (n == 3) return "neutral";
		if (n == 2) return "negative";
		if (n == 1) return "irrelevant";
		return "invalid";
	}
}