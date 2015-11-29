
public class Feature {

	private String name;
	private FeatType type;
	private Number val;
	
	public Feature(String name, Number val)
	{
		this(val);
		this.name=name;
	}
	private Feature(Number val) {
		super();
		this.type = FeatType.WORD;
		this.val = val;
	}
	public Feature(FeatType type, Number val, String name) {
		super();
		this.type = type;
		this.val = val;
		this.name = name;
	}
	
	public void incVal(Number step){
		this.val = val.doubleValue()+step.doubleValue();
	}
	public FeatType getType() {
		return type;
	}
	public void setType(FeatType type) {
		this.type = type;
	}
	
	public Number getVal() {
		return val;
	}
	public void setVal(Number val) {
		this.val = val;
	}
	@Override
	public String toString() {
		return "Feature [name=" + name + ", type=" + type + ", val=" + val
				+ "]";
	}

	
	
}
