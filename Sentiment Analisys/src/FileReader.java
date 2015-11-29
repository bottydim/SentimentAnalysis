import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileReader {

	private boolean stem = false;
	private boolean lowercase = true;
	private String lexLoc = "./info/sent_lexicon";
	private boolean includeS = false;
	private List<Document> PosDocs = null;
	private List<Document> NegDocs = null;
	private int vocabCount = 0;
	private Map<String, Integer> vocab = new HashMap<String, Integer>();
	private boolean nodigits = true;
	private File posFolder;
	private File negFolder;

	public FileReader() {
		posFolder = new File(
				"/Users/botty/Documents/University/Cambrdige/Overview of NLP/Sentiment Analysis/info/Pos");
		negFolder = new File(
				"/Users/botty/Documents/University/Cambrdige/Overview of NLP/Sentiment Analysis/info/Neg");
	}

	public List<Document> getPosDocs() {
		if (PosDocs == null)
			readFiles();
		return PosDocs;
	}

	public List<Document> getNegDocs() {
		if (NegDocs == null)
			readFiles();
		return NegDocs;
	}

	public List<Document> getAllDocs() {
		List<Document> list = new ArrayList<Document>();
		list.addAll(getNegDocs());
		list.addAll(getPosDocs());
		return list;
	}

	public Map<String, Integer> readFiles() {
		// postive

		File[] posFiles = posFolder.listFiles();

		File[] negFiles = negFolder.listFiles();
		PosDocs = tokenize(posFiles, true);
		System.out.println("Positive class read");
		NegDocs = tokenize(negFiles, false);
		System.out.println("Negative class read");
		// System.out.println(posFiles.length);
		return new HashMap<String, Integer>(vocab);
	}

	public Lexicon readLexicon() {

		File lexFile = new File(lexLoc);
		BufferedReader br;
		Map<String, Lexicon.WordInfo> lexicon = new HashMap<String, Lexicon.WordInfo>();
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					lexFile)));
			String line = null;
			while ((line = br.readLine()) != null) {
				// parse line to contain only needed information
				String parsedLine = line
						.replaceAll(
								"type=(?:(weak)|(strong))subj len=1 word1=(.*) pos1=(.*) stemmed1=(y|n) priorpolarity=(.*)",
								"$1$2 $3 $4 $5 $6");
				String[] tokens = parsedLine.split("\\s+");
				// set lexicon values
				lexicon.put(tokens[1], new Lexicon.WordInfo(tokens[0],
						tokens[2], tokens[3], tokens[4]));
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Lexicon Read");
		return new Lexicon(lexicon);

	}

	private ArrayList<Document> tokenize(File[] posFiles, boolean lbl) {
		ArrayList<Document> docs = new ArrayList<Document>();
		BufferedReader br;
		List<String> tokenAcc;
		for (int i = 0; i < posFiles.length; i++) {
			if (posFiles[i].isFile()) {
				// System.out.println("File " + posFiles[i].getName());
				tokenAcc = new ArrayList<String>();
				try {
					br = new BufferedReader(new InputStreamReader(
							new FileInputStream(posFiles[i])));
					String line = null;
					while ((line = br.readLine()) != null) {
						// \\s+ means any number of whitespaces between tokens
						String[] tokens = line.split("\\s+");

						parseToken(tokens);
						for (int j = 0; j < tokens.length; j++) {
							String token = tokens[j];
							if (!token.contentEquals("")) {
								if (stem) {
									Stemmer s = new Stemmer();
									char[] chars = token.toCharArray();
									s.add(chars, chars.length);
									s.stem();
									token = s.toString();
								}
								tokenAcc.add(token);
								if (!vocab.containsKey(token)) {
									vocab.put(token, vocabCount);
									vocabCount++;
								}
							}

						}
						// Collections.addAll(tokenAcc, tokens);
						// System.out.println(tokenAcc.size());
					}

					String name = posFiles[i].getName().replaceAll(
							"cv(\\d*)_.*", "$1");
					// System.out.println("File name: "+name);
					// System.out.println("Tokens:"+tokenAcc.toString());
					docs.add(new Document(name, tokenAcc, lbl));
					br.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

			} else if (posFiles[i].isDirectory()) {
				System.out.println("Directory " + posFiles[i].getName());
			}
		}
		return docs;
	}

	public boolean isStem() {
		return stem;
	}

	public void setStem(boolean stem) {
		this.stem = stem;
	}

	private void parseToken(String[] tokens) {
		// process tokens
		String pattStr = "(\\w+(?:'?|-?)\\w+)";
		Pattern pattern = Pattern.compile(pattStr);
		Matcher matcher;
		for (int j = 0; j < tokens.length; j++) {
			matcher = pattern.matcher(tokens[j]);
			if (matcher.find()) {

				tokens[j] = matcher.group(1);
				if (!includeS)
					tokens[j] = tokens[j].replaceAll("(\\w+)'s", "$1");
				if (lowercase)
					tokens[j] = tokens[j].toLowerCase();
				if (nodigits) {
					tokens[j] = tokens[j].replaceAll("(\\d+-?)", "");
					// System.out.println("token:"+tokens[j]);
					tokens[j] = tokens[j].replaceAll("(\\d+-?)", "");
				}
				// remove 1 cahracter words and symbols
				tokens[j] = tokens[j].replaceAll("^.$", "");
				tokens[j] = tokens[j].replaceAll("^_(.*)_$", "$1");

			} else {
				tokens[j] = "";
			}
		}
	}

	public boolean isLowercase() {
		return lowercase;
	}

	public void setLowercase(boolean lowercase) {
		this.lowercase = lowercase;
	}

	public boolean isIncludeS() {
		return includeS;
	}

	public void setIncludeS(boolean includeS) {
		this.includeS = includeS;
	}
}
