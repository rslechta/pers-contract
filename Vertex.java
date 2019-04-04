package contract;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Vertex implements Simplex,Comparable<Vertex>{
	
	private int index;
	private Map<String,Edge> incidentEdges;
	private Map<String,Triangle> incidentTriangles;
	private double heightValue;
	private boolean isBoundary;
	private Vertex parent;
	
	/**
	 * Constructs a new Vertex with assigned index and height value
	 * @param index
	 * @param heightValue
	 */
	public Vertex(int index, double heightValue)
	{
		this.index = index;
		this.incidentEdges = new HashMap<String,Edge>();
		this.incidentTriangles = new HashMap<String,Triangle>();
		this.heightValue = heightValue;
		this.isBoundary = false;
		this.parent = this;
	}
	
	/**
	 * Returns the parent associated with this vertex.
	 * @return Vertex
	 */
	public Vertex getVertex()
	{
		if(this.parent.index == this.index)
		{
			return this;
		}
		return this.parent = this.parent.getVertex(); 
	}
	
	/** 
	 * Combines parameter vertex with this vertex. Includes updating the
	 * surviving vertex's incident vertex list. 
	 * 
	 * @param v
	 */
	public void union(Vertex v)
	{
		if(v.getVertex().equals(this.getVertex()))
		{
			/* Check to make sure we're not in the same tree */
			return;
		}
		
		Vertex earlier;
		Vertex later;
		
		if(v.getVertex().compareTo(this.getVertex())<0)
		{
			earlier = v.getVertex();
			later = this.getVertex();
		}else {
			earlier = this.getVertex();
			later = v.getVertex();
		}
		
		
		/* Update all edges incident to this and vertex v */
		/* Ensures that the edge with minimum height value survives */
		/* this becomes later */
		/* v becomes earlier */
		later.getVertex().parent = earlier.getVertex();
		for(String s : later.incidentEdges.keySet())
		{
			Edge incident = later.incidentEdges.get(s);
			String oldHash = incident.toString();
			//if(!s.equals(oldHash)) {System.out.println("Hashes are inequal!");}
			incident.computeIndexRep(); // Update the index representation
			String newHash = incident.toString();
				
			if(!incident.hasVanished())
			{
				Vertex otherVert = incident.getOtherVertex(earlier.getVertex()).getVertex();
				if(earlier.getVertex().incidentEdges.containsKey(newHash)) /* Incident and incidentEdges.get(newHash) are mirrored */
				{
					Edge survivingMirror;
					Edge vanishingMirror;
					if(earlier.getVertex().incidentEdges.get(newHash).getHeightValue() > incident.getHeightValue())
					{
						vanishingMirror = earlier.getVertex().incidentEdges.get(newHash);
						survivingMirror = incident;
						vanishingMirror.setVanished();
						earlier.getVertex().incidentEdges.put(newHash, incident);
						otherVert.incidentEdges.put(newHash, incident);
						otherVert.incidentEdges.remove(oldHash);
					}else {
						vanishingMirror = incident;
						survivingMirror = earlier.getVertex().incidentEdges.get(newHash);
						vanishingMirror.setVanished();
						/* The surviving mirror is already incident to vanished */
					}
					for(Triangle t : vanishingMirror.getIncidentTriangles())
					{
						if(!t.hasVanished())
						{
							t.replaceEdge(vanishingMirror, survivingMirror);
							t.computeIndexRep();
							survivingMirror.addIncidentTriangle(t);
						}
					}
				}//If mirrors and incident has lesser value, update.
				else if(!earlier.getVertex().incidentEdges.containsKey(newHash)) {
					earlier.getVertex().incidentEdges.put(newHash, incident);
					otherVert.getVertex().updateEdgeHash(oldHash, newHash); //update the hash.
				}// Also add the edge if earlier doesn't have it
			}
		}
		
		for(String s : later.incidentTriangles.keySet())
		{
			Triangle t = later.incidentTriangles.get(s);
			String newHash = t.toString();
			
			if(!t.hasVanished())
			{
				if(earlier.adjacentToTriangle(newHash)) /* t is a mirror */
				{
					if(t.compareTo(earlier.incidentTriangles.get(newHash)) < 0) /* If they are mirrors, the older one survives */
					{
						earlier.incidentTriangles.get(newHash).setVanished(); /* The younger one has now merged into the older one  */
						earlier.incidentTriangles.put(newHash, t);
					}else {
						t.setVanished(); /* Older one vanishes */
					}
				}else {
					earlier.addTriangle(t);
				}
			}
		}
	}
	
	/**
	 * Updates the value assigned to the old key to be associated with the new key.
	 * @param oldkey
	 * @param newkey
	 */
	private void updateEdgeHash(String oldkey, String newkey)
	{
		if(!this.getVertex().incidentEdges.containsKey(oldkey))
		{
			System.out.println("Adding a null edge!");
		}
		this.getVertex().incidentEdges.put(newkey, this.getVertex().incidentEdges.get(oldkey));
		this.getVertex().incidentEdges.remove(oldkey);
	}
	
	/**
	 * Basic accessor method
	 * 
	 * @return integer representation of index associated with this vertex
	 */
	public int getIndex()
	{
		return this.getVertex().index;
	}
		
	/**
	 * Determines if the incidentEdges structure contains the key parameter.
	 * @param key
	 * @return
	 */
	public boolean adjacentToEdge(String key)
	{
		return this.getVertex().incidentEdges.containsKey(key);
	}
	
	/**
	 * Determines if this vertex is recorded as being incident to a triangle
	 * with string representation equal (in the String sense) to the parameter. 
	 * @param key
	 * @return
	 */
	public boolean adjacentToTriangle(String key)
	{
		return this.getVertex().incidentTriangles.containsKey(key);
	}
		
	/**
	 * Adds the parameter to internal representation. 
	 * @param e Edge to be inserted
	 */
	public void addEdge(Edge e)
	{
		if(e.hasVanished())
		{
			throw new IllegalArgumentException("Cannot add vanished edge!");
		}
		if(this.getVertex().incidentEdges.containsKey(e.toString())) 
		{
			throw new IllegalArgumentException("Vertex " + this.toString() + " already is incident to edge " + e.toString());
		}
		if(!e.containsVertex(this.getVertex()))
		{
			throw new IllegalArgumentException("The input edge must be incident to this vertex!");
		}

		e.computeIndexRep();
		this.getVertex().incidentEdges.put(e.toString(), e);
	}
	
	/**
	 * Adds the triangle parameter to be incident to this vertex.
	 * @param t Triangle to be inserted.
	 */
	public void addTriangle(Triangle t)
	{
		if(t.hasVanished())
		{
			throw new IllegalArgumentException("Cannot add vanished triangle!");
		}
		if(this.getVertex().incidentTriangles.containsKey(t.toString()))
		{
			throw new IllegalArgumentException("Vertex: " + this.toString() + " already is incident to triangle " + t.toString());
		}
		if(!t.containsVertex(this.getVertex()))
		{
			throw new IllegalArgumentException("The input triangle must be incident to this vertex!");
		}
		
		t.computeIndexRep();
		this.getVertex().incidentTriangles.put(t.toString(), t);
		
	}
		
	/**
	 * Returns the number of edges incident to this vertex
	 * @return the number of edges of which this vertex is a constituent
	 */
	public int numberOfEdges()
	{
		return this.getVertex().incidentEdges.size();
	}
	
	/**
	 * Returns the number of triangles incident to this vertex.
	 * @return
	 */
	public int numberOfTriangles()
	{
		return this.getVertex().incidentTriangles.size();
	}
	
	/**
	 * Accessor method
	 * 
	 * @return the height value of this vertex
	 */
	public double getHeightValue()
	{
		return this.getVertex().heightValue;
	}

	@Override
	public int getDimension() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/**
	 * Sets this vertex to be a boundary edge
	 */
	public void setBoundary()
	{
		this.getVertex().isBoundary = true;
	}
	
	/**
	 * Determines if this vertex is on a boundary.
	 * @return boolean
	 */
	public boolean isBoundary()
	{
		return this.getVertex().isBoundary;
	}
	
	/**
	 * Checks if this vertex is equal to its vertex. i.e. checks if this vertex has vanished. 
	 * @return boolean
	 */
	public boolean hasVanished()
	{
		return this.equals(this.getVertex());
	}
		
	/**
	 * Gets the values of the incident edges hashmap. 
	 * @return
	 */
	public Collection<Edge> getIncidentEdges()
	{
		return this.incidentEdges.values();
	}
	
	/**
	 * Gets the values of the incident triangles hashmap.
	 * Note that it is not safe to modify. 
	 * @return
	 */
	public Collection<Triangle> getIncidentTriangles()
	{
		return this.incidentTriangles.values();
	}
	
	/**
	 * Equals method for vertices
	 * @param v
	 * @return if this vertex and the input vertex are equal
	 */
	public boolean equals(Vertex v)
	{
		return this.getVertex().getIndex() == v.getVertex().getIndex();
	}
	
	@Override
	public String toString()
	{
		return this.getVertex().index + "";
	}
	
	@Override
	public int hashCode()
	{
		return this.getVertex().toString().hashCode();
	}

	public int compareTo(Vertex arg0)
	{
		if(this.getVertex().getHeightValue() < arg0.getVertex().getHeightValue()) {return -1;}
		else if(this.getVertex().getHeightValue() > arg0.getVertex().getHeightValue()) {return 1;}
		else {
			return this.getVertex().index - arg0.getVertex().index;
		}
	}
}
