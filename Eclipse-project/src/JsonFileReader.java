import java.io.IOException;
import java.io.StringReader;
import java.util.Vector;

import javax.json.Json;
import javax.json.JsonObject;


public class JsonFileReader {
	public static JsonObject readJsonObject(String fname) throws IOException{
		Vector<String> lines = new Vector<String>();
		LinesReader.readin(fname, lines);
		if(lines.size() != 1){
			System.err.println(lines.size() + " lines in: " + fname);
			System.err.println("In our dataset, all json files only contain one line!");
			return null;
		}
		//System.out.println("parse@ " + tweetID);
		JsonObject jsonObject = Json.createReader(new StringReader(lines.get(0))).readObject();
		return jsonObject;
	}
}
