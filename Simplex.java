package contract;

public interface Simplex{
	
	public double getHeightValue();
	/**
	 * @return 0 for a vertex, 1 for an edge, 2 for a triangle
	 */
	public int getDimension();
	public boolean hasVanished();
}
