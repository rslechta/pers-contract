package contract;

public class ResultPair {

	private long millis;
	private int contracted;
	private int simplices; 
	private int itCount; 
	
	public ResultPair(long millis, int contracted, int simplices, int itCount)
	{
		this.millis = millis;
		this.contracted = contracted;
		this.simplices = simplices;
		this.itCount = itCount;
	}
	
	public int getContracted()
	{
		return this.contracted;
	}
	
	public int getItCount()
	{
		return this.itCount;
	}
	
	public long getMillis()
	{
		return this.millis;
	}
	
	public int getSimplices()
	{
		return this.simplices;
	}
}
