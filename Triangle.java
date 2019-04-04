package contract;

import java.util.ArrayList;
import java.util.Arrays;

public class Triangle implements Simplex,Comparable<Triangle>{

	private Edge edge1;
	private Edge edge2;
	private Edge edge3;
	private int[] index;
	private Vertex[] vertices;
	private double heightValue;
	private boolean hasVanished;
	
	/**
	 * Initializes triangle based on three edges.
	 * @param edge1
	 * @param edge2
	 * @param edge3
	 */
	public Triangle(Edge edge1, Edge edge2, Edge edge3)
	{
		if(edge1.equals(edge2) || edge2.equals(edge3) || edge1.equals(edge3))
		{
			throw new IllegalArgumentException("Three edges must be distinct!");
		}
		
		if(edge1 == null || edge2 == null || edge3 == null)
		{
			throw new IllegalArgumentException("The edges cannot be null!");
		}
		
		this.edge1 = edge1;
		this.edge2 = edge2;
		this.edge3 = edge3;
		
		
		ArrayList<Vertex> vertices = new ArrayList<Vertex>();
		vertices.add(edge1.getFirstVertex());
		vertices.add(edge1.getSecondVertex());
		
		if(!vertices.contains(edge2.getFirstVertex()))
		{
			vertices.add(edge2.getFirstVertex());
		}
		
		if(!vertices.contains(edge2.getSecondVertex()))
		{
			vertices.add(edge2.getSecondVertex());
		}
		
		if(!vertices.contains(edge3.getFirstVertex()))
		{
			vertices.add(edge3.getFirstVertex());
		}
		
		if(!vertices.contains(edge3.getSecondVertex()))
		{
			vertices.add(edge3.getSecondVertex());
		}
		
		if(vertices.size() != 3)
		{
			throw new IllegalArgumentException("Edges must collectively contain three vertices!");
		}
		this.vertices = new Vertex[3];
		vertices.toArray(this.vertices);
		this.index = new int[3];
		
		this.heightValue = Double.NEGATIVE_INFINITY;
		
		for(Vertex v : this.vertices)
		{
			if(v.getHeightValue() > this.heightValue)
			{
				this.heightValue = v.getHeightValue();
			}
		}
		
		this.computeIndexRep();
		
		for(Vertex v : this.vertices)
		{
			v.addTriangle(this);
		}
	}
	
	public Triangle(Edge edge1, Edge edge2, Edge edge3, double heightvalue)
	{
		if(edge1.equals(edge2) || edge2.equals(edge3) || edge1.equals(edge3))
		{
			throw new IllegalArgumentException("Three edges must be distinct!");
		}
		
		if(edge1 == null || edge2 == null || edge3 == null)
		{
			throw new IllegalArgumentException("The edges cannot be null!");
		}
		
		if(edge1.getHeightValue() < heightvalue || edge2.getHeightValue() < heightvalue || edge3.getHeightValue() < heightvalue)
		{
			throw new IllegalArgumentException("Height value is less than the height value of an edge!");
		}
		
		this.edge1 = edge1;
		this.edge2 = edge2;
		this.edge3 = edge3;
		
		
		ArrayList<Vertex> vertices = new ArrayList<Vertex>();
		vertices.add(edge1.getFirstVertex());
		vertices.add(edge1.getSecondVertex());
		
		if(!vertices.contains(edge2.getFirstVertex()))
		{
			vertices.add(edge2.getFirstVertex());
		}
		
		if(!vertices.contains(edge2.getSecondVertex()))
		{
			vertices.add(edge2.getSecondVertex());
		}
		
		if(!vertices.contains(edge3.getFirstVertex()))
		{
			vertices.add(edge3.getFirstVertex());
		}
		
		if(!vertices.contains(edge3.getSecondVertex()))
		{
			vertices.add(edge3.getSecondVertex());
		}
		
		if(vertices.size() != 3)
		{
			throw new IllegalArgumentException("Edges must collectively contain three vertices!");
		}
		this.vertices = new Vertex[3];
		vertices.toArray(this.vertices);
		this.index = new int[3];
		
		this.heightValue = heightvalue;
		this.computeIndexRep();
	}

	/**
	 * Basic accessor for first edge
	 * @return
	 */
	public Edge getFirstEdge()
	{
		return this.edge1;
	}
	
	/**
	 * Basic accessor for second edge
	 * @return
	 */
	public Edge getSecondEdge()
	{
		return this.edge2;
	}
	
	/**
	 * Basic accessor for third edge
	 * @return
	 */
	public Edge getThirdEdge()
	{
		return this.edge3;
	}
	
	/**
	 * Computes the index representation and the string representation of this triangle.
	 */
	public void computeIndexRep()
	{
		vertices[0] = vertices[0].getVertex();
		vertices[1] = vertices[1].getVertex();
		vertices[2] = vertices[2].getVertex();
		
		this.index[0] = vertices[0].getIndex();
		this.index[1] = vertices[1].getIndex();
		this.index[2] = vertices[2].getIndex();
		
		Arrays.sort(this.index);
	}
		
	/**
	 * Evaluates if this triangle contains exactly three constituent vertices.
	 * @return boolean
	 */
	public boolean isPossibleTriangle()
	{
		ArrayList<Integer> indices = new ArrayList<Integer>();
		ArrayList<Edge> edges = new ArrayList<Edge>();
		edges.add(this.getFirstEdge());
		edges.add(this.getSecondEdge());
		edges.add(this.getThirdEdge());
		
		for(Edge e : edges)
		{
			int firstIndex = e.getFirstVertex().getIndex();
			if(!indices.contains(firstIndex))
			{
				indices.add(firstIndex);
			}
			firstIndex = e.getSecondVertex().getIndex();
			if(!indices.contains(firstIndex))
			{
				indices.add(firstIndex);
			}
		}
		
		return indices.size() == 3;
	}
	
	/**
	 * Basic accessor method for the height value of this triangle
	 * @return double
	 */
	public double getHeightValue()
	{
		return this.heightValue;
	}
	
	@Override
	public int getDimension() {
		// TODO Auto-generated method stub
		return 2;
	}
	
	@Override
	public String toString()
	{
		this.computeIndexRep();
		return this.index[0] + "," + this.index[1] + "," + this.index[2];
	}

	/**
	 * Returns a clone of the index.
	 * @return
	 */
	public int[] getIndex()
	{
		return this.index.clone();
	}
	
	/**
	 * Determines if the parameter is a constituent edge of this triangle.
	 * @param e
	 * @return
	 */
	public boolean containsEdge(Edge e)
	{
		return this.getFirstEdge().equals(e) || this.getSecondEdge().equals(e) || this.getThirdEdge().equals(e);
	}
	
	/**
	 * Returns the mirror incident to this triangle which does not equal the parameter and
	 * contains the first vertex of the parameter.
	 * @param e
	 * @return Edge 
	 */
	public Edge getFirstMirror(Edge e)
	{
		if(!this.containsEdge(e))
		{
			throw new IllegalArgumentException("This triangle does not contain the parameter!");
		}
		
		if(this.getFirstEdge().containsVertex(e.getFirstVertex()) && !this.getFirstEdge().equals(e))
		{
			return this.getFirstEdge();
		}else if(this.getSecondEdge().containsVertex(e.getFirstVertex()) && !this.getSecondEdge().equals(e)) {
			return this.getSecondEdge();
		}else {
			return this.getThirdEdge();
		}
	}
	
	/**
	 * Returns the mirror incident to this triangle which does not equal the parameter and
	 * contains the second vertex of the parameter.
	 * @param e
	 * @return Edge
	 */
	public Edge getSecondMirror(Edge e)
	{
		if(!this.containsEdge(e))
		{
			throw new IllegalArgumentException("This triangle does not contain the parameter!");
		}
		
		if(this.getFirstEdge().containsVertex(e.getSecondVertex()) && !this.getFirstEdge().equals(e))
		{
			return this.getFirstEdge();
		}else if(this.getSecondEdge().containsVertex(e.getSecondVertex()) && !this.getSecondEdge().equals(e)) {
			return this.getSecondEdge();
		}else {
			return this.getThirdEdge();
		}
	}
	
	/**
	 * Determines if this triangle has vanished
	 * @return boolean
	 */
	public boolean hasVanished()
	{
		this.vertices[0] = this.vertices[0].getVertex();
		this.vertices[1] = this.vertices[1].getVertex();
		this.vertices[2] = this.vertices[2].getVertex();
		return this.hasVanished || this.vertices[0].equals(this.vertices[1]) || this.vertices[0].equals(this.vertices[2]) || this.vertices[1].equals(this.vertices[2]);
	}
	
	public void setVanished()
	{
		this.hasVanished = true;
	}

	/**
	 * Evaluates if the parameter is an element of the internal vertex array.
	 * @param v
	 * @return
	 */
	public boolean containsVertex(Vertex v)
	{
		return this.vertices[0].equals(v) || this.vertices[1].equals(v) || this.vertices[2].equals(v);
	}
	
	/**
	 * Modifies internal state so that the old vertex is replaced with the new vertex.
	 * @param old
	 * @param nw
	 */
	public void replaceVertex(Vertex old, Vertex nw)
	{
		if(!this.vertices[0].equals(old) && !this.vertices[1].equals(old) && !this.vertices[2].equals(old))
		{
			throw new IllegalStateException("Does not contain the old vertex!");
		}
		
		if(this.vertices[0].equals(old))
		{
			this.vertices[0] = nw;
		}else if(this.vertices[1].equals(old)) {
			this.vertices[1] = nw;
		}else if(vertices[2].equals(old)) {
			this.vertices[2] = nw;
		}else {
			throw new IllegalStateException("This error should have been caught earlier idk why it wasn't.");
		}
		
		if(this.vertices[0].getHeightValue() > this.heightValue)
		{
			this.heightValue = this.vertices[0].getHeightValue();
		}
		
		if(this.vertices[1].getHeightValue() > this.heightValue)
		{
			this.heightValue = this.vertices[1].getHeightValue();
		}
		
		if(this.vertices[2].getHeightValue() > this.heightValue)
		{
			this.heightValue = this.vertices[2].getHeightValue();
		}
		
		this.computeIndexRep();
	}
	
	/**
	 * Replaces the internal representation of the old edge with the new edge.
	 * The new edge is not safe to modify. If the old edge is not in the triangle,
	 * we throw an error.
	 * 
	 * The old edge and new edge should have exactly one vertex in common.
	 * @param old
	 * @param nw
	 */
	public void replaceEdge(Edge old, Edge nw)
	{		
		if(this.edge1.equals(old))
		{
			this.edge1 = nw;
		}else if(this.edge2.equals(old)) {
			this.edge2 = nw;
		}else if(this.edge3.equals(old)) {
			this.edge3 = nw;
		}else {
			throw new IllegalStateException("Error! This triangle does not contain the input edge!");
		}
	}

	/**
	 * Returns the surviving mirror about this triangle that is associated with the parameter.
	 * @param e the vanishing edge
	 * @return
	 */
	public Edge getSurvivingMirror(Edge e)
	{
		if(this.getFirstMirror(e).compareTo(this.getSecondMirror(e)) < 0)
		{
			return this.getFirstMirror(e);
		}
		
		return this.getSecondMirror(e);
	}
	
	/**
	 * Returns the perishing mirror about this triangle that is associated with the parameter.
	 * @param e the vanishing edge
	 * @return
	 */
	public Edge getPerishingMirror(Edge e)
	{
		if(this.getFirstMirror(e).compareTo(this.getSecondMirror(e)) < 0)
		{
			return this.getSecondMirror(e);
		}
		
		return this.getFirstMirror(e);
	}
	
	/**
	 * Evaluates if all constituent edges are distinct.
	 * @return boolean
	 */
	public boolean containsUniqueEdges()
	{
		return !this.getFirstEdge().equals(this.getSecondEdge()) && !this.getFirstEdge().equals(this.getThirdEdge()) && !this.getSecondEdge().equals(this.getThirdEdge());
	}

	/**
	 * Basic accessor method for the first vertex that makes up this triangle.
	 * Returned vertex is not safe to modify.
	 * @return
	 */
	public Vertex getFirstVertex()
	{
		return this.vertices[0];
	}
	
	/**
	 * Basic accessor method for the second vertex that makes up this triangle.
	 * Returned vertex is not safe to modify.
	 * @return
	 */
	public Vertex getSecondVertex()
	{
		return this.vertices[1];
	}
	
	/**
	 * Basic accessor method for the third vertex that makes up this triangle.
	 * Returned vertex is not safe to modify.
	 * @return
	 */
	public Vertex getThirdVertex()
	{
		return this.vertices[2];
	}

	@Override
	public int compareTo(Triangle arg0) {
		// TODO Auto-generated method stub
		if(this.getHeightValue() < arg0.getHeightValue()) {return -1;}
		else if(this.getHeightValue() > arg0.getHeightValue()) {return 1;}
		else {
			return 0;
			//return this.toString().compareTo(arg0.toString());
		}
	}
}
