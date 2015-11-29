import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class SymbolicClassifier extends Classifier {

	
	
	
	public SymbolicClassifier(String name,Lexicon lexicon) {
		super(name+" Symbolic", lexicon);
	}

	@Override
	public void train(List<Document> docs) {
		// no training required
		System.err.println("Not implemented!");
	}

	@Override
	public List<Feature> extractFeatures(Document doc) {
		List<Feature> fs = new ArrayList<Feature>();
		for (Iterator<String> iterator = doc.getTokens().iterator(); iterator.hasNext();) {
			String word = (String)iterator.next();
			fs.add(new Feature(word,weightedFeature(word)));
			
		}
		return fs;
	}
	public Number weightedFeature(String word)
	{
		
			return lexicon.sign(word);

	}

	@Override
	public boolean classify(List<Feature> fs) {
		double sum = 0;
		for (Iterator<Feature> iterator = fs.iterator(); iterator.hasNext();) {
			Feature feature = (Feature) iterator.next();
			sum += feature.getVal().doubleValue();
		}
//		if(sum==0)
//		System.out.println("neutral");
		return sum > 0 ? true:false;
	}

}
