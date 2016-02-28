import java.nio.file.Path;
import java.nio.file.Paths;

import edu.stanford.nlp.tagger.maxent.*;

public class Tagger {
	public MaxentTagger tagger;
	
	Tagger() {
		Path currentRelativePath = Paths.get("");
		String currPathStr = currentRelativePath.toAbsolutePath().toString();
		tagger = new MaxentTagger(currPathStr+"/lib/wsj-0-18-bidirectional-nodistsim.tagger");
	}
}
