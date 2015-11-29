import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

	public static void main(String[] args) {

		// reading files and setting vocabulary
		FileReader fr = new FileReader();
		Map<String, Integer> vocab = fr.readFiles();
		Lexicon lexicon = fr.readLexicon();
		lexicon.setVocab(vocab);
		List<Document> docs = fr.getAllDocs();

		// docs = FileReader.getNegDocs();

		// running basic classifier with better conf
		Classifier sBinary = new SymbolicClassifier("Binary", lexicon);
		sBinary.evaluate(docs);
		System.out
				.println("Sentiment Binary Accuracy:" + sBinary.getAccuracy());

		// changing conf -> new FR
		FileReader fr2 = new FileReader();
		fr2.setIncludeS(true);
		fr2.setLowercase(false);
		fr2.readFiles();
		List<Document> docsExp = fr2.getAllDocs();

		// setting documents
		DocumentOrganizer docOrg = new DocumentOrganizer(fr.getPosDocs(),
				fr.getNegDocs());
		docOrg.split(0, 899);

		// docs = FileReader.getNegDocs();

		// running basic class with Poor Conf
		Classifier sBinaryConf = new SymbolicClassifier("Poor Config", lexicon);
		sBinaryConf.evaluate(docsExp);
		System.out.println("Sentiment Binary Accuracy Poor Config:"
				+ sBinaryConf.getAccuracy());
		// Estimating Significance
		double p = Classifier.statSign(sBinary, sBinaryConf);

		// running weighted
		Classifier wghtSent = new WeightedSymbolicClassifier("Double", lexicon);
		wghtSent.evaluate(docsExp);
		Classifier.statSign(sBinary, wghtSent);

		// Running Bayes
		
		NaiveBayes NB = new NaiveBayes("Normal", lexicon);
		 NB.train(docOrg.getTrainSet()); NB.setUseBag(false); NB.evaluate(docOrg.getTestSet());
		 wghtSent.evaluate(docOrg.getTestSet()); Classifier.statSign(NB,
		 wghtSent);
		 

		// Classifier NBnBag = new NaiveBayes("Without Freq", lexicon);
		// NBnBag.train(docOrg.getTrainSet());
		// ((NaiveBayes)NBnBag).switchBag();
		// NBnBag.evaluate(docOrg.getTestSet());
		// Classifier.statSign(NB, NBnBag);
		//
		// DocumentOrganizer docOrg2 = new DocumentOrganizer(docOrg);
		// docOrg2.split(0, 899, 0, 89,900,999,900,999);
		// Classifier NBbais = new NaiveBayes("Baised", lexicon);
		// NBbais.train(docOrg2.getTrainSet());
		// NBbais.evaluate(docOrg2.getTestSet());
		// Classifier.statSign(NB, NBbais);

		
		 NaiveBayes NBSmooth = new NaiveBayes("Smoothed", lexicon, 1);
		 NBSmooth.train(docOrg.getTrainSet()); 
		 NBSmooth.setUseBag(false); 
		 NBSmooth.evaluate(docOrg.getTestSet());
		 Classifier.statSign(NB, NBSmooth);
		 for(int i=0;i<NB.getStatSign().length;i++)
		 {
			 System.out.println(i+" "+NB.getStatSign()[i]+" "+ NBSmooth.getStatSign()[i]);
			
		 }
		
		 
/*		crossValidation(new NaiveBayes("Normal", lexicon), new NaiveBayes(
				"Smooth", lexicon, 1), fr);*/
		FileReader frStem = new FileReader();
		frStem.setLowercase(true);
		frStem.setStem(true);
		Lexicon lex = new Lexicon(lexicon.getLexicon());
		lex.setVocab(frStem.readFiles());
		System.out.println("feature size:\n"+
					"not stemmed----stemmed\n"+
					lexicon.getVocabSize()+"   "+lex.getVocabSize());
//		crossValidation(new NaiveBayes("UNStemmed + Smooth", lexicon, 1), new NaiveBayes("Stemmed Smooth", lex, 1),frStem);
		
	}

	public static void crossValidation(Classifier cl1, Classifier cl2,
			FileReader fr) {
		int repeat = 10;
		ExecutorService threadExecutor = Executors.newFixedThreadPool(repeat);
		CrossVal crsVal = new Main.CrossVal(repeat);
		CountDownLatch latch = new CountDownLatch(repeat);

		for (int i = 0; i < repeat; i++) {
			final int pos = i;
			DocumentOrganizer dc = new DocumentOrganizer(fr.getPosDocs(),
					fr.getNegDocs());
			
			try {
				final Classifier tempClas1 = (Classifier) cl1.clone();
				final Classifier tempClas2 = (Classifier) cl2.clone();
/*						System.out.println("Cross-validaiton:" + pos * 100
						+ "-" + (((pos + 1) * 100) - 1));
				dc.crossConseqSplit(pos * 100, ((pos + 1) * 100) - 1);
				tempClas1.train(dc.getTrainSet());
				tempClas2.train(dc.getTrainSet());
				tempClas1.evaluate(dc.getTestSet());
				tempClas2.evaluate(dc.getTestSet());
				crsVal.add(Classifier.statSign(tempClas1, tempClas2),
						tempClas1.getAccuracy(),
						tempClas2.getAccuracy());*/
				threadExecutor.execute(new Runnable() {

					@Override
					public void run() {
/*						System.out.println("Cross-validaiton:" + pos * 100
								+ "-" + (((pos + 1) * 100) - 1));
						dc.crossConseqSplit(pos * 100, ((pos + 1) * 100) - 1);
*/						System.out.println("Cross-validaiton: mod" + pos);
						dc.crossModSplit(pos);
						tempClas1.train(dc.getTrainSet());
						tempClas2.train(dc.getTrainSet());
						
						tempClas1.evaluate(dc.getTestSet());
						tempClas2.evaluate(dc.getTestSet());
						crsVal.add(Classifier.statSign(tempClas1, tempClas2),
								tempClas1.getAccuracy(),
								tempClas2.getAccuracy());
						latch.countDown();

					}
				});
			} catch (CloneNotSupportedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			
		}
		try {
			threadExecutor.shutdown();
			System.out.println("Waiting to finish");
			latch.await(); // wait untill latch counted down to 0
			Classifier.printStat(cl1, cl2, crsVal.getAcc1()/repeat,
					crsVal.getAcc2()/repeat, crsVal.getP()/repeat);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static class CrossVal {
		volatile double p = 0;
		volatile double acc1 = 0;
		volatile double acc2 = 0;
		int number;

		public CrossVal(int number) {
			this.number = number;
		}

		public synchronized void add(double p, double acc1, double acc2) {

			this.p += p;
			this.acc1 += acc1;
			this.acc2 += acc2;
		}

		public double getP() {
			return p;
		}

		public double getAcc1() {
			return acc1;
		}

		public double getAcc2() {
			return acc2;
		}
	}
}
