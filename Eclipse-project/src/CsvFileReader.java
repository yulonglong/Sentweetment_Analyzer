import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import com.opencsv.CSVReader;


public class CsvFileReader {
	public static boolean readCsvElements(Vector<String[]> csvElements, String fname, int fieldCount, boolean withHead) throws IOException{
		CSVReader reader = new CSVReader(new FileReader(fname));
		String[] tokens;
		int index = 0;
		while((tokens = reader.readNext()) != null){
			if(tokens.length != fieldCount){
				System.err.println("unexpect csv element in the " + index + "th line of: " + fname);
				reader.close();
				return false;
			}
			//the head of csv file
			if(withHead && index == 0){
				;
			}else{
				csvElements.add(tokens);
			}
			index++;
		}
		reader.close();
		return true;
	}
}
