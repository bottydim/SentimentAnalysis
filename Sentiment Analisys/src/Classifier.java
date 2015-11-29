import java.util.List;
import java.util.Map;


public abstract class Classifier implements Cloneable{
	
	private double accuracy;
	private int[] statSign;
	protected String name;
	protected Lexicon lexicon;
	public Classifier(String name, Lexicon lexicon){
		this.lexicon = lexicon;
		this.name = name;
	}
	
	public abstract void train(List<Document> docs);
	public abstract List<Feature> extractFeatures(Document doc);
	protected abstract boolean classify(List<Feature> fs); 
	protected boolean predict(Document doc){
		
		List<Feature> fs = extractFeatures(doc);

		return  classify(fs);
		
	}
	
	public void evaluate(List<Document> test){
		int correct = 0;
		int size = test.size();
		statSign = new int[size];
		Document doc;
		for(int i=0;i<size;i++)
		{
			doc = test.get(i);
			if(predict(doc)==doc.isPos())
			{
				correct++;
				statSign[i] = 1;
			}
		}
//		System.out.println("acc:"+correct+"/"+size);
		this.setAccuracy(correct/(double)size);
	}
	
	public static double statSign(Classifier cl1, Classifier cl2)
	{
		System.out.println("PERFORMING STATISTICAL SIGNIFICANCE TESTTING");
		
		
		//correct examples
		int[] cExm1 = cl1.getStatSign();
		int[] cExm2 = cl2.getStatSign();
		
		int N = cExm1.length;
		System.out.println("Number of test examples: "+N);
		if(cExm1.length!=cExm2.length){
			System.err.println("Different Test data to be compared");
			return -1;
		}
			
		double cnt1 =0;
		double cnt2 =0;
		for (int i = 0; i < N; i++) {
			if(cExm1[i]==cExm2[i]){
				cnt1+=0.5;
				cnt2+=0.5;
			}
			else if(cExm1[i]>cExm2[i]){
				cnt1++;
			}
			else
				cnt2++;
		}
		cnt1 = Math.round(cnt1);
		cnt2 = Math.round(cnt2);
		
//		int sum = 0;
//		for(int i=0;i<=cnt1;i++)
//		{
//			sum+=Bernoulli(N, i);
//		}
//		return 2*sum;
		Communicator com = new Communicator();
		double p = com.getPforZ(calcZ((int)cnt1,(int)cnt2));
		printStat(cl1, cl2, cnt1, cnt2, p);
		
		return p;
	}

	public static void printStat(Classifier cl1, Classifier cl2, double cnt1,
			double cnt2, double p) {
		System.out.println(cl1.toString()+" ------- "+cl2.toString()+"\n"+
		"Accuracy :"+cl1.getAccuracy()+" ------- "+cl2.getAccuracy()+"\n"+ 
		"# correct: "+cnt1+" ------- "+cnt2+"\n"+
		"p <= "+p);
	}
	
	
	private static double calcZ(int n1,int n2)
	{
		return  (Math.abs(n1 -n2)-1)/Math.sqrt(n1+n2);
	}
	
	@SuppressWarnings("unused")
	private static double Bernoulli(int N,int x)
	{
		return combination(N,x)*Math.pow(0.5, N);
	}
	private static double combination(int N,int x)
	{
		
		    int a = N;
		    int b = x;
		    int c = (N - x);

		    for (int o = a - 1; o > 0; c--) { a = a * o; }
		    for (int o = b - 1; o > 0; c--) { b = b * o; }
		    for (int o = c - 1; o > 0; c--) { c = c * o; }

		    return (a / (b * c)); // n! / r! * (n - r)!
		
	}
	public double getAccuracy() {
		return accuracy;
	}
	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}
	public int[] getStatSign() {
		return statSign;
	}
	public void setStatSign(int[] statSign) {
		this.statSign = statSign;
	}
	@Override
	public String toString()
	{
		return this.name;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}
	
	
}
