package contract;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Edge implements Simplex,Comparable<Edge>{
	
	private static boolean persistenceSort = false; 
	private Vertex firstVertex;
	private Vertex secondVertex;
	private Map<String,Triangle> incidentTriangles;
	private int[] index;
	private double heightValue;
	private int position;
	private boolean hasVanished;
	
	/**
	 * Constructs an edge from the input vertices. The position parameter is
	 * a unique identifier for the edge. 
	 * @param firstVertex
	 * @param secondVertex
	 * @param
	 */
	public Edge(Vertex firstVertex, Vertex secondVertex, int position)
	{
		if(firstVertex == null)
		{
			throw new IllegalArgumentException("First vertex is null!");
		}else if(secondVertex == null) {
			throw new IllegalArgumentException("Second vertex is null!"); 
		}
		
		if(firstVertex.equals(secondVertex))
		{
			throw new IllegalArgumentException("The vertices that make up an edge have to be different!");
		}
		
		if(firstVertex.getHeightValue() > secondVertex.getHeightValue())
		{
			this.firstVertex = firstVertex.getVertex();
			this.secondVertex = secondVertex.getVertex();
			this.heightValue = firstVertex.getHeightValue();
		}else {
			this.secondVertex = firstVertex.getVertex();
			this.firstVertex = secondVertex.getVertex();
			this.heightValue = secondVertex.getHeightValue();
		}
		
		this.index = new int[2];
		
		this.computeIndexRep();
		
		firstVertex.addEdge(this);
		secondVertex.addEdge(this);
		
		this.position = position;
		this.hasVanished = false;
		
		this.incidentTriangles = new HashMap<String,Triangle>();
	}
	
	public Edge(Vertex firstVertex, Vertex secondVertex, double heightvalue, int position)
	{
		if(firstVertex == null)
		{
			throw new IllegalArgumentException("First vertex is null!");
		}else if(secondVertex == null) {
			throw new IllegalArgumentException("Second vertex is null!"); 
		}
		
		if(firstVertex.equals(secondVertex))
		{
			throw new IllegalArgumentException("The vertices that make up an edge have to be different!");
		}
		
		if(heightvalue < firstVertex.getHeightValue() || heightvalue < secondVertex.getHeightValue())
		{
			throw new IllegalArgumentException("This edge has a height value less than its vertices!");
		}
		
		this.firstVertex = firstVertex.getVertex();
		this.secondVertex = secondVertex.getVertex();
		this.heightValue = heightvalue;
		
		this.index = new int[2];
		
		this.computeIndexRep();
		
		firstVertex.addEdge(this);
		secondVertex.addEdge(this);
		
		this.position = position;
		this.hasVanished = false;
		
		this.incidentTriangles = new HashMap<String,Triangle>();
	}
		
	/**
	 * Populates the index integer array according to the indices of constituent vertices.
	 * Recall that the index array is sorted in descending order according to index. 
	 * Also initializes the internal string representation of the Edge.
	 */
 	public void computeIndexRep()
	{
 		this.firstVertex = this.firstVertex.getVertex(); 
 		this.secondVertex = this.secondVertex.getVertex();
		if(this.firstVertex.getIndex() > this.secondVertex.getIndex())
		{
			this.index[0] = this.secondVertex.getIndex();
			this.index[1] = this.firstVertex.getIndex();
		}else {
			this.index[0] = this.firstVertex.getIndex();
			this.index[1] = this.secondVertex.getIndex();
		}
	}
	
 	public Collection<Triangle> getIncidentTriangles()
 	{
 		return this.incidentTriangles.values();
 	}
 	
	/**
	 * Accessor method.
	 * @return the vertex with the greater height value
	 */
	public Vertex getFirstVertex()
	{
		return this.firstVertex.getVertex();
	}
	
	/**
	 * Accessor method
	 * @return the vertex with lesser height value
	 */
	public Vertex getSecondVertex()
	{
		return this.secondVertex.getVertex();
	}
	
	/**
	 * Returns the height value of this edge, initially equal to the greater height value of its
	 * constituent vertices.
	 */
	public double getHeightValue()
	{
		return this.heightValue;
	}
		
	/**
	 * @param v
	 * @return if this edge contains v as a consituent vertex
	 */
	public boolean containsVertex(Vertex v)
	{
		return this.getFirstVertex().equals(v) || this.getSecondVertex().equals(v);
	}
	
	/**
	 * @param v
	 * @return the vertex in this edge that is not equal to v
	 */
	public Vertex getOtherVertex(Vertex v)
	{
		if(!this.containsVertex(v.getVertex()))
		{
			throw new IllegalArgumentException("This edge does not contain the given vertex!");
		}
		
		if(this.getFirstVertex().equals(v.getVertex())) { return this.getSecondVertex();}
		
		return this.getFirstVertex();
	}
		
	/**
	 * Determines if the parameter triangle is incident to this edge. 
	 * @param t
	 * @return boolean
	 */
	public boolean incidentToTriangle(Triangle t)
	{
		t.computeIndexRep();
		return this.incidentTriangles.containsKey(t.toString());
	}
		
	/**
	 * Adds the parameter as an adjacent triangle to this edge.
	 * @param t
	 */
	public void addIncidentTriangle(Triangle t)
	{
		t.computeIndexRep();
		this.incidentTriangles.put(t.toString(), t);
	}
	
	/**
	 * Computes the ending time of this edges window.
	 * @return
	 */
	public double getEndingTime()
	{
		double max = Double.NEGATIVE_INFINITY;
		for(String s : this.incidentTriangles.keySet())
		{
			if(this.incidentTriangles.get(s).getHeightValue() > max)
			{
				max = this.incidentTriangles.get(s).getHeightValue();
			}
		}
		return max;
	}
	
	/**
	 * Computes the starting time of this edges window. 
	 * @return
	 */
	public double getStartingTime()
	{
		if(this.getFirstVertex().getHeightValue() < this.getSecondVertex().getHeightValue())
		{
			return this.getFirstVertex().getHeightValue();
		}
		return this.getSecondVertex().getHeightValue();
	}
		
	@Override
	public int getDimension() {
		return 1;
	}
	
	@Override
	public String toString()
	{
		return this.index[0] + "," + this.index[1];
	}

	@Override
	public int compareTo(Edge arg0) {
		if(!Edge.persistenceSort)
		{
			if(this.getHeightValue() < arg0.getHeightValue()) {return -1;}
			else if(this.getHeightValue() > arg0.getHeightValue()) {return 1;}
			else {
				return this.position - arg0.position;
				//return this.toString().compareTo(arg0.toString());
			}
		}else {
			double retFlag = this.getEndingTime() - arg0.getEndingTime();
			if(retFlag < 0)
			{
				return -1;
			}else if(retFlag > 0) {
				return 1;
			}else {
				return 0;
			}
		}
	}

	/**
	 * Equals method for edges
	 * @param e
	 * @return if this edge and e are equal or not
	 */
	public boolean equals(Edge e)
	{
		return (this.getFirstVertex().equals(e.getFirstVertex()) && this.getSecondVertex().equals(e.getSecondVertex())) || ( this.getFirstVertex().equals(e.getSecondVertex()) && this.getSecondVertex().equals(e.getFirstVertex()));
	}

	/**
	 * Determines if this edge has vanished.
	 * @return
	 */
	public boolean hasVanished()
	{
		return this.hasVanished || this.getFirstVertex().equals(this.getSecondVertex());
	}
	
	/**
	 * Sets this edge to be vanished.
	 */
	public void setVanished()
	{
		this.hasVanished = true;
	}
	
	/**
	 * Evaluates if the edge is on the boundary in the sense that both vertices are on the boundary.
	 * We do not contract such edges. Note that this is not strictly equal to isBoundary
	 * @return boolean
	 */
	public boolean isViable()
	{
		if(this.getFirstVertex().isBoundary() || this.getSecondVertex().isBoundary())
		{
			return false;
		}
		
		return true;
	}

	/** 
	 * Ensures that the edge to be contracted meets the link condition, thereby preserving the topology
	 * of the manifold pre and post contraction.
	 * 
	 * It is assumed that this edge is viable, and thereby has two incident triangles.
	 * 
	 * @return boolean
	 */
	public boolean linkCondition()
	{
		Vertex vert;
		Vertex mirrorVert;
		
		if(this.getFirstVertex().numberOfEdges() < this.getSecondVertex().numberOfEdges())
		{
			vert = this.getFirstVertex().getVertex();
			mirrorVert = this.getSecondVertex().getVertex();
		}else {
			vert = this.getSecondVertex().getVertex();
			mirrorVert = this.getFirstVertex().getVertex();
		}
		
		for(Edge e : vert.getIncidentEdges())
		{
			Vertex otherVert = e.getOtherVertex(vert).getVertex();
			if(otherVert.equals(mirrorVert) || e.hasVanished())
			{
				// Vanishing one doesn't matter. 
				continue;
			}
			
			//So at this point we know that the other vert is not a mirror
			//Need to check if the mirrored vert contains it. 
			
			if(mirrorVert.adjacentToEdge(otherVert.toString()))
			{
				return false;
			}
		}
		
		/* So at this point we know that there is not triangle that is getting filled in */
		/* We don't know about mirrored triangles, however */
		/* If there is a pair of mirrored triangles, then that means collapsing them */
		/* Into a single triangle results in destroying a 2-cycle */
		/* Hence, check for mirrored triangles */
		
		for(Triangle t : vert.getIncidentTriangles())
		{
			/* Skip vanishing triangles */
			if(!t.containsVertex(mirrorVert))
			{
				int[] index = t.getIndex();
				for(int i = 0; i<index.length; i++)
				{
					if(index[i] == vert.getVertex().getIndex())
					{
						index[i] = mirrorVert.getVertex().getIndex();
					}
				}
				Arrays.sort(index);
				String newKey = index[0] + "-" + index[1] + "-" + index[2];
				if(mirrorVert.adjacentToTriangle(newKey))
				{
					return false;
					/* Does not pass the link condition: destroying a 2-cycle! */
				}
			}
		}
		
		/* At this point, there are no mirrored triangles which cause collapse */
		
		return true;
	}

	/**
	 * Determines if this edge is epsilon admissible. Assumes that this edge meets the link condition for
	 * two manifolds.
	 * @param epsilon
	 * @return boolean
	 */
	public boolean isEpsilonSat(double epsilon)
	{
		if(this.getHeightValue() - this.getPerishingVertex().getHeightValue() > epsilon)
		{
			return false;
		}
		
		for(Triangle t : this.incidentTriangles.values())
		{
			if(!t.hasVanished() && t.getHeightValue() - t.getPerishingMirror(this).getHeightValue() > epsilon)
			{
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Determines if an edge is a valid candidate to be contracted.
	 * @return boolean
	 */
	public boolean isContractible(double epsilon)
	{
		return !this.hasVanished() && this.isViable() && this.linkCondition() && this.isEpsilonSat(epsilon);
	}
	
	/**
	 * Gets the constituent vertex with lower height value.
	 * @return Vertex
	 */
	public Vertex getSurvivingVertex()
	{
		if(this.getFirstVertex().getHeightValue() < this.getSecondVertex().getHeightValue())
		{
			return this.getFirstVertex();
		}else if(this.getSecondVertex().getHeightValue() < this.getFirstVertex().getHeightValue()) {
			return this.getSecondVertex();
		}else if(this.getFirstVertex().getIndex() < this.getSecondVertex().getIndex()) {
			return this.getFirstVertex();
		}
		return this.getSecondVertex();
	}
	
	/**
	 * Gets the constituent vertex with greater height value.
	 * @return Vertex
	 */
	public Vertex getPerishingVertex()
	{
		if(this.getFirstVertex().equals(this.getSurvivingVertex()))
		{
			return this.getSecondVertex();
		}else {
			return this.getFirstVertex();
		}
	}
	
	/**
	 * Returns the vertex in the mirror which is not also constituent in this edge.
	 * Assumes that the mirror parameter is in fact a mirror relative to this edge.
	 * @param mirror
	 * @return
	 */
	public Vertex getBystanderVertex(Edge mirror)
	{
		if(mirror.getFirstVertex().equals(this.getFirstVertex()) || mirror.getFirstVertex().equals(this.getSecondVertex()))
		{
			return mirror.getSecondVertex();
		}
		
		return mirror.getFirstVertex();
	}
	
	/**
	 * ONLY CALL IF THIS EDGE IS CONTRACTIBLE
	 */
	public void contract()
	{
		this.getFirstVertex().getVertex().union(this.getSecondVertex().getVertex());
	}
	
	/**
	 * Sets the persistence sort variable so that all edges are now sorted according to associated persistence
	 * rather than by height value/index.
	 */
	public static void setPersistenceSort()
	{
		Edge.persistenceSort = true;
	}

	/**
	 * Sets the persistence sort variable so that all edges are now sorted according to their height value
	 * and index, rather than by persistence.
	 */
	public static void setOrignalSort()
	{
		Edge.persistenceSort = false;
	}
}
