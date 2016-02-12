import javax.json.JsonObject;

public class Tweet {
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
	
	public String getTextOneLine() {
		String processedText =  text.replaceAll("(?:(?:\\r\\n)|(?:\\r)|(?:\\n))+", " | ");
		return processedText.replaceAll("\\t+", " ");
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