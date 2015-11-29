import java.util.List;


public class Document {

	private String name;
	private List<String> tokens;
	private boolean lbl;

	public Document(String name, List<String> tokens, boolean lbl) {
		super();
		this.name = name;
		this.tokens = tokens;
		this.lbl = lbl;
	}

	public String getName() {
		return name;
	}

	public List<String> getTokens() {
		return tokens;
	}

	public boolean isPos() {
		return lbl;
	}

}
