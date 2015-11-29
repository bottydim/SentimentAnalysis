import java.util.List;


public class StemNaiveBayes extends NaiveBayes {

	public StemNaiveBayes(String name, Lexicon lexicon) {
		super(name, lexicon);
		// TODO Auto-generated constructor stub
	}

	public StemNaiveBayes(String name, Lexicon lexicon, double smooth) {
		super(name, lexicon, smooth);
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Feature> extractFeatures(Document doc) {
		// TODO Auto-generated method stub
		return super.extractFeatures(doc);
	}
	

}
