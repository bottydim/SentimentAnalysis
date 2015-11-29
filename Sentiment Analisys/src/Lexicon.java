import java.util.Map;


public class Lexicon {


	private Map<String,WordInfo> lexicon;
	private Map<String,Integer> vocab;
	public int vocabSize = 0;

	public Map<String, WordInfo> getLexicon() {
		return lexicon;
	}

	public Map<String, Integer> getVocab() {
		return vocab;
	}

	public int getVocabSize() {
		return vocabSize;
	}

	public void setVocab(Map<String, Integer> vocab) {
		vocabSize = vocab.size();
		this.vocab = vocab;
		System.out.println("Vocabulary Set!!!");
	}
	public Integer vocPos(String word)
	{
		if(vocab==null)
		{
			System.err.println("Missing Vocabulary");
			return -1;
		}
		if(vocab.get(word) == null)
		{
			System.err.println("Error in Parsing Documents -missing words");
			return -1;
		}
	
		return vocab.get(word);
	}
	
	public Lexicon(Map<String, WordInfo> lexicon) {
		this.lexicon = lexicon;
	}
	


	public int sign(String word)
	{
		WordInfo info = lexicon.get(word);
		return info !=null ? info.getPolarity() : 0; 
	}
	public double weightSign(String word,double weight)
	{
		WordInfo info = lexicon.get(word);

		return info !=null ? (info.isStrong() ? weight*sign(word) : sign(word)) : 0; 
	}
	
	
	
	
	
	public static class WordInfo
	{
		private boolean strong;
		private String pos;
		private boolean stemmed;
		private int polarity;
		public WordInfo(){
			super();
		}
		
		public WordInfo(boolean strong, String pos, boolean stemmed,
				int polarity) {
			this();
			this.strong = strong;
			this.pos = pos;
			this.stemmed = stemmed;
			this.polarity = polarity;
		}


		public WordInfo(String str, String pos, String stemmed,
				String polarity) {
			this.strong = str.equalsIgnoreCase("strong") ? true : false;
			this.pos = pos;
			this.stemmed = str.equalsIgnoreCase("y") ? true : false;
			switch (polarity) {
			case "positive":
				this.polarity = 1;
				break;
			case "negative":
				this.polarity = -1;
				break;
				
			default:
				this.polarity = 0;
				break;
			}
		}
		
		public int getStrength()
		{
			return (strong) ? 1 : 0;
		}
		public boolean isStrong() {
			return strong;
		}
		public void setStrong(boolean strong) {
			this.strong = strong;
		}
		public String getPos() {
			return pos;
		}
		public void setPos(String pos) {
			this.pos = pos;
		}
		public boolean isStemmed() {
			return stemmed;
		}
		public void setStemmed(boolean stemmed) {
			this.stemmed = stemmed;
		}
		public boolean isPolarity() {
			return polarity!=0;
		}
		public boolean isPositive() {
			return polarity > 0;
		}
		public int getPolarity()
		{
			return this.polarity;
		}
		public void setPolarity(int polarity) {
			this.polarity = polarity;
		}
		
	}

}

