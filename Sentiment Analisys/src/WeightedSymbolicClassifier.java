
public class WeightedSymbolicClassifier extends SymbolicClassifier {

	private double weight = 17;
	public WeightedSymbolicClassifier(String name, Lexicon lexicon) {
		super(name+" Weighted", lexicon);
		
	}

	@Override
	public Number weightedFeature(String word) {
		
		return lexicon.weightSign(word, weight);
	}
	

}
