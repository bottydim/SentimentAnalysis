import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.crypto.spec.PSource;

public class NaiveBayes extends Classifier {

	protected HashSet<String> vocab = new HashSet<String>();

	public NaiveBayes(String name, Lexicon lexicon) {
		this(name, lexicon, 0);

	}

	public NaiveBayes(String name, Lexicon lexicon, double smooth) {
		super(name + " Naive Bayes", lexicon);
		this.smooth = smooth;
	}

	protected double[] posLikehood;
	protected double[] negLikehood;
	protected double posProb = 0;
	protected double negProb = 0;
	protected boolean trained = false;
	private boolean useBag = true;
	private double smooth;

	@Override
	public void train(List<Document> docs) {

		double[] posFreq = new double[lexicon.vocabSize];
		double[] negFreq = new double[lexicon.vocabSize];
		posLikehood = new double[lexicon.vocabSize];
		negLikehood = new double[lexicon.vocabSize];
		for (Document doc : docs) {
			if (doc.isPos()) {
				posProb++;
				ArrayList<Feature> fs = (ArrayList<Feature>) extractFeatures(doc);
				for (int i = 0; i < posFreq.length; i++) {
					Feature feat = fs.get(i);
					posFreq[i] += feat != null ? (double) feat.getVal()
							.doubleValue() : 0;

				}
			} else {
				negProb++;
				ArrayList<Feature> fs = (ArrayList<Feature>) extractFeatures(doc);
				for (int i = 0; i < negFreq.length; i++) {
					Feature feat = fs.get(i);
					negFreq[i] += feat != null ? feat.getVal().intValue() : 0;

				}
			}

		}

		// must always be the same as this is how they are init
		double sum = 0;
		int cnt = 0;
		int noPdf = 0;
		for (int i = 0; i < negFreq.length; i++) {
			if (!(smooth > 0) && posFreq[i] + negFreq[i] == 0) {
				posLikehood[i] = 0;
				negLikehood[i] = 0;
				noPdf++;
			} else {
				posLikehood[i] = (posFreq[i] + smooth)
						/ (posFreq[i] + negFreq[i] + 2 * smooth);
				negLikehood[i] = (negFreq[i] + smooth)
						/ (posFreq[i] + negFreq[i] + 2 * smooth);

				sum += posLikehood[i] + negLikehood[i];
				if (posLikehood[i] + negLikehood[i] != 1) {
					cnt++;
				}
			}

		}
		System.out.println("pdf:" + sum / (posFreq.length - noPdf));
		System.out.println("Nopdf: " + noPdf);
		System.out.println("cnt:" + cnt);
		posProb /= docs.size();
		negProb /= docs.size();
		trained = true;
	}

	@Override
	public List<Feature> extractFeatures(Document doc) {
		Feature[] fs = new Feature[lexicon.vocabSize];
		for (Iterator iterator = doc.getTokens().iterator(); iterator.hasNext();) {
			String word = (String) iterator.next();
			int pos = lexicon.vocPos(word);

			if (fs[pos] == null) {
				fs[pos] = new Feature(word, 1);
			} else {
				fs[pos].incVal(1);
			}

		}

		return new ArrayList<Feature>(Arrays.asList(fs));
	}

	@Override
	protected boolean classify(List<Feature> fs) {
		if (!trained) {
			System.err.println("Classifier is not trained!");
			return false;
		}
		/*
		 * BigDecimal pos = calculatePostProbability(fs,true); BigDecimal neg =
		 * calculatePostProbability(fs,false);
		 */
		Double pos = caclPosProb(fs, true);
		Double neg = caclPosProb(fs, false);
		// System.out.println("pos="+pos.round(new
		// MathContext(5)).toEngineeringString()+"---neg="+neg.round(new
		// MathContext(5)).toEngineeringString());
		// System.out.println(pos.compareTo(neg)>0);
		// if(pos.scale()>neg.scale())
		// {
		// neg = neg.setScale(pos.scale());
		// }
		// else
		// {
		// pos = pos.setScale(neg.scale());
		// }
		return pos.compareTo(neg) > 0;
	}

	private double caclPosProb(List<Feature> fs, boolean cls) {
		double prod;
		double[] lklHood;
		if (cls) {
			prod = Math.log(posProb);
			lklHood = posLikehood;
		} else {
			prod = Math.log(negProb);
			lklHood = negLikehood;
		}

		for (int i = 0; i < lklHood.length; i++) {
			if (Double.isNaN(lklHood[i])) {
				System.err.println(lklHood[i]);
			} else {

				double lklH;
				if (useBag) {
					lklH = Math.pow(lklHood[i], fs.get(i) != null ? fs.get(i)
							.getVal().doubleValue() : 0);
				} else {
					lklH = Math.pow(lklHood[i], fs.get(i) != null ? 1 : 0);
				}

				if (lklH == 0) {
					//
					
					if (useBag) {
						lklH = Math.pow(0.5, fs.get(i) != null ? fs.get(i)
								.getVal().doubleValue() : 0);
					} else {
						lklH = Math.pow(0.5, fs.get(i) != null ? 1 : 0);
					}
					prod += Math.log(lklH);
//					System.out.println(prod+" "+(prod+Math.log(lklH)));
//					 prod+= 0;
				} else {
					prod += Math.log(lklH);
				}

			}

		}
		return prod;
	}

	private BigDecimal calculatePostProbability(List<Feature> fs, boolean cls) {
		BigDecimal prod;

		double[] lklHood;
		if (cls) {
			prod = new BigDecimal(posProb);
			// prod = new BigDecimal("0.9");
			lklHood = posLikehood;
		} else {
			prod = new BigDecimal(negProb);
			lklHood = negLikehood;
		}
		// prod = new BigDecimal(Math.log(prod.doubleValue()));

		for (int i = 0; i < lklHood.length; i++) {
			if (Double.isNaN(lklHood[i])) {
				System.err.println(lklHood[i]);
			} else {

				double lklH;
				if (useBag) {
					lklH = Math.pow(lklHood[i], fs.get(i) != null ? fs.get(i)
							.getVal().doubleValue() : 0);
				} else {
					lklH = Math.pow(lklHood[i], fs.get(i) != null ? 1 : 0);
				}

				if (lklH == 0) {
					// String feat = String.valueOf(fs.get(i)!=null ?
					// fs.get(i).getVal().doubleValue():0);
					// System.out.println("lkl:"+lklHood[i]+" fs.get"+feat);
					// if(smooth>0)
					// System.err.println("lklH 0 "+fs.get(i));
					return prod.multiply(new BigDecimal(Math.pow(0.5,
							fs.get(i) != null ? 1 : 0)));
				}
				// prod = prod.add(new BigDecimal(Math.log(feature)));
				prod = prod.multiply(new BigDecimal(lklH));
			}

		}
		return prod;
	}

	public void switchBag() {
		this.useBag = !this.useBag;
	}

	public boolean isUseBag() {
		return useBag;
	}

	public void setUseBag(boolean useBag) {
		this.useBag = useBag;
	}

}
