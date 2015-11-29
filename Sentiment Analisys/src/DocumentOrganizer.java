import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class DocumentOrganizer {
	
	private List<Document> docPos;
	private List<Document> docNeg;
	private List<Document> testSet;
	private List<Document> trainSet;
	
	public DocumentOrganizer(List<Document> docPos, List<Document> docNeg) {
		super();
		this.docPos = docPos;
		this.docNeg = docNeg;

	}
	
	public DocumentOrganizer(DocumentOrganizer docOrg)
	{
		this.docPos = docOrg.docPos;
		this.docNeg = docOrg.docNeg;
	}
	//this performs equal split
	public void split(Integer start, Integer stop)
	{
		 split(start,stop,start,stop);
	}
	public void split(Integer posStart,Integer posStop,Integer negStart,Integer negStop)
	{

		split(posStart,posStop,negStart,negStop,posStop+1,docPos.size()-1,negStop+1,docNeg.size()-1);
	}
	public void split(Integer posTrStart,Integer posTrStop,Integer negTrStart,Integer negTrStop,
			          Integer posTsStart, Integer posTsStop,Integer negTsStart, Integer negTsStop)
	{
		int trSize = posTrStop+negTrStop-posTrStart-negTrStart+1;
		int tsSize = docPos.size()+docNeg.size() - trSize;
		testSet = new ArrayList<Document>(tsSize);
		trainSet = new ArrayList<Document>(trSize);
		for (Iterator<Document> iterator = docPos.iterator(); iterator.hasNext();) {
			Document doc = (Document) iterator.next();
			int docNum = Integer.parseInt(doc.getName());
			if(between(posTrStart, posTrStop, docNum))
			{
				trainSet.add(doc);
			}
			else
				if(between(posTsStart, posTsStop, docNum))
				testSet.add(doc);
		}
		for (Iterator<Document> iterator = docNeg.iterator(); iterator.hasNext();) {
			Document doc = (Document) iterator.next();
			int docNum = Integer.parseInt(doc.getName());
			if(between(negTrStart, negTrStop, docNum))
			{
				trainSet.add(doc);
			}
			else
				if(between(negTsStart, negTsStop, docNum))
				testSet.add(doc);
		}
	}
	public void crossModSplit(Integer mod)
	{
		int tsSize = 200;
		int trSize = docPos.size()+docNeg.size() - tsSize;
		testSet = new ArrayList<Document>(tsSize);
		trainSet = new ArrayList<Document>(trSize);
		///CAREFULL
		for (Iterator<Document> iterator = docPos.iterator(); iterator.hasNext();) {
			Document doc = (Document) iterator.next();
			int docNum = Integer.parseInt(doc.getName());
			if(modCheck(docNum, mod))
			{
				testSet.add(doc);
			}
			else
				trainSet.add(doc);
		}
		for (Iterator<Document> iterator = docNeg.iterator(); iterator.hasNext();) {
			Document doc = (Document) iterator.next();
			int docNum = Integer.parseInt(doc.getName());
			if(modCheck(docNum, mod))
			{
				testSet.add(doc);
			}
			else
				trainSet.add(doc);
		}
		System.out.println("Train"+trainSet.size()+" Test:"+testSet.size());
	}
	public void crossConseqSplit(Integer tsStart,Integer tsStop)
	{
		int tsSize = 2*(tsStop-tsStart+1);
		int trSize = docPos.size()+docNeg.size() - tsSize;
		testSet = new ArrayList<Document>(tsSize);
		trainSet = new ArrayList<Document>(trSize);
		///CAREFULL
		for (Iterator<Document> iterator = docPos.iterator(); iterator.hasNext();) {
			Document doc = (Document) iterator.next();
			int docNum = Integer.parseInt(doc.getName());
			if(between(tsStart, tsStop, docNum))
			{
				testSet.add(doc);
			}
			else
				trainSet.add(doc);
		}
		for (Iterator<Document> iterator = docNeg.iterator(); iterator.hasNext();) {
			Document doc = (Document) iterator.next();
			int docNum = Integer.parseInt(doc.getName());
			if(between(tsStart, tsStop, docNum))
			{
				testSet.add(doc);
			}
			else
				trainSet.add(doc);
		}
		System.out.println("Train"+trainSet.size()+" Test:"+testSet.size());
	}

	private boolean between(Integer tsStart, Integer tsStop, int docNum) {
		return docNum>=tsStart && docNum<=tsStop;
	}
	
	private boolean modCheck(int docNum,int mod)
	{
		return docNum%10==mod;
	}
	public List<Document> getTestSet() {
		if(testSet==null)
			System.err.println("No split performed!");
		return testSet;
	}
	public List<Document> getTrainSet() {
		if(trainSet==null)
			System.err.println("No split performed!");
		return trainSet;
	}

}
