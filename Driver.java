package contract;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Driver {
	
	private static int positionCounter = 0;
	
	public static ArrayList<Vertex> getUnstructuredVertices(String filename)
	{
		ArrayList<Vertex> al = new ArrayList<Vertex>();
		File file = new File(filename);
		try
		{
			FileReader fileReader = new FileReader(file);
			BufferedReader bR = new BufferedReader(fileReader);
			String line;
			int count = 0;
			while((line = bR.readLine()) != null)
			{
				String[] splat = line.split(",");
				Vertex v = new Vertex(count,Double.parseDouble(splat[splat.length-1]));
				al.add(v);
				count++;
			}
			bR.close();
			fileReader.close();
		}catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return al;
	}
	
	public static HashMap<String,Edge> getUnstructuredEdges(String filename, ArrayList<Vertex> vertices)
	{
		HashMap<String,Edge> el = new HashMap<String,Edge>();
		File file = new File(filename);
		try
		{
			FileReader fileReader = new FileReader(file);
			BufferedReader bR = new BufferedReader(fileReader);
			String line;
			while((line = bR.readLine()) != null)
			{
				String[] splat = line.split(",");
				int firstIndex = Integer.parseInt(splat[0]);
				int secondIndex = Integer.parseInt(splat[1]);
				Vertex firstVert = vertices.get(firstIndex);
				Vertex secondVert = vertices.get(secondIndex);
				Edge newEdge;
				if(splat.length < 3)
				{
					newEdge = new Edge(firstVert, secondVert, Driver.positionCounter);
				}else {
					newEdge = new Edge(firstVert, secondVert, Double.parseDouble(splat[3]), Driver.positionCounter);
				}
				el.put(newEdge.toString(), newEdge);
				Driver.positionCounter++;
			}
			bR.close();
			fileReader.close();
		}catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return el;
	}
	
	public static ArrayList<Triangle> getUnstructuredTriangles(String filename, HashMap<String,Edge> edges)
	{
		ArrayList<Triangle> triangles = new ArrayList<Triangle>();
		File file = new File(filename);
		try
		{
			FileReader fileReader = new FileReader(file);
			BufferedReader bR = new BufferedReader(fileReader);
			String line;
			while((line = bR.readLine()) != null)
			{
				String[] splat = line.split(",");
				int[] ints = new int[3];
				ints[0] = Integer.parseInt(splat[0]);
				ints[1] = Integer.parseInt(splat[1]);
				ints[2] = Integer.parseInt(splat[2]);
				
				Arrays.sort(ints);
				String firstString = ints[1] + "," + ints[2];
				String secondString = ints[0] + "," + ints[2];
				String thirdString = ints[0] + "," + ints[1];
				
				Edge firstEdge = edges.get(firstString);
				Edge secondEdge = edges.get(secondString);
				Edge thirdEdge = edges.get(thirdString);
				
				Triangle t;
				if(splat.length < 4)
				{
					t = new Triangle(firstEdge,secondEdge,thirdEdge);
				}else {
					t = new Triangle(firstEdge,secondEdge,thirdEdge,Double.parseDouble(splat[3]));
				}
				firstEdge.addIncidentTriangle(t);
				secondEdge.addIncidentTriangle(t);
				thirdEdge.addIncidentTriangle(t);
				triangles.add(t);
			}
			bR.close();
			fileReader.close();
		}catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return triangles;
	}
	
	public static void writeVertices(List<Vertex> vertices, String filename) throws IOException
	{
		BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
		for(Vertex v : vertices)
		{
			if(!v.hasVanished())
			{
				bw.write(v.toString()+"\n");
			}
		}
		bw.close();
	}
	
	public static void writeEdges(List<Edge> edges, String filename) throws IOException
	{
		BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
		for(Edge e : edges)
		{
			if(!e.hasVanished())
			{
				bw.write(e.toString()+"\n");
			}
		}
		bw.close();
	}
	
	public static void writeTriangles(List<Triangle> triangles, String filename) throws IOException
	{
		BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
		for(Triangle t : triangles)
		{
			if(!t.hasVanished())
			{
				bw.write(t.toString()+"\n");
			}
		}
		bw.close();
	}
					
	public static ArrayList<Vertex> removeVertexVanished(ArrayList<Vertex> vertexList)
	{
		int count = 0;
		for(Vertex v : vertexList)
		{
			if(!v.hasVanished())
			{
				count++;
			}
		}
		ArrayList<Vertex> toReturn = new ArrayList<Vertex>(count);
		for(Vertex v : vertexList)
		{
			if(!v.hasVanished())
			{
				toReturn.add(v);
			}
		}
		return toReturn;
	}
	
	public static ArrayList<Edge> removeEdgeVanished(ArrayList<Edge> edgeList)
	{
		int count = 0;
		for(Edge e : edgeList)
		{
			if(!e.hasVanished())
			{
				count++;
			}
		}
		ArrayList<Edge> toReturn = new ArrayList<Edge>(count);
		for(Edge e : edgeList)
		{
			if(!e.hasVanished())
			{
				toReturn.add(e);
			}
		}
		return toReturn;
	}
	
	public static ArrayList<Triangle> removeTriangleVanished(ArrayList<Triangle> vertexList)
	{
		int count = 0;
		for(Triangle v : vertexList)
		{
			if(!v.hasVanished())
			{
				count++;
			}
		}
		ArrayList<Triangle> toReturn = new ArrayList<Triangle>(count);
		for(Triangle v : vertexList)
		{
			if(!v.hasVanished())
			{
				toReturn.add(v);
			}
		}
		return toReturn;
	}
			
	public static ResultPair contract(String path, String name, double epsilon, int maxIts, int maxContractions) throws IOException
	{
		String vertexFileName = path + name + "/" + name + "Data.txt";
		String initialEdgeFileName = path + name + "/" + name + "Edges.txt";
		String initialTriangleFileName = path + name + "/" + name + "Triangles.txt";
		String contractedVertexFileName = path + name + "/" + name + "ContractedData.txt";
		String contractedEdgeFileName = path + name + "/" + name + "ContractedEdges.txt";
		String contractedTriangleFileName = path + name + "/" + name + "ContractedTriangles.txt";		
		
		ArrayList<Vertex> vertices = Driver.getUnstructuredVertices(vertexFileName);
		HashMap<String,Edge> edgeMap = Driver.getUnstructuredEdges(initialEdgeFileName, vertices);
		ArrayList<Edge> edges = new ArrayList<Edge>();
		edges.addAll(edgeMap.values());
		ArrayList<Triangle> triangles = Driver.getUnstructuredTriangles(initialTriangleFileName, edgeMap);
				
		int initialVertices = vertices.size();
		int initialEdges = edges.size();
		int initialTriangles = triangles.size();
		
		int initialSimplices = initialVertices + initialEdges + initialTriangles;
		
		Edge.setPersistenceSort();
		Collections.sort(edges);
		
		int edgesContracted = 0;
		int formerCount = -1; 
		long initial = System.currentTimeMillis();
		int itCount = 0;
		boolean flag = true;
		while(edgesContracted - formerCount > 0 && itCount != maxIts && flag)
		{
			itCount++;
			formerCount = edgesContracted;
			double currentTime = Double.NEGATIVE_INFINITY;
			for(Edge e : edges)
			{
				if(e.getStartingTime() > currentTime && e.isContractible(epsilon))
				{
					currentTime = e.getEndingTime();					
					e.contract();
					edgesContracted++;
				}
				
				if(edgesContracted == maxContractions)
				{
					flag = false;
					break;
				}
			}
		}
		long millis = System.currentTimeMillis() - initial;
		
		edges = Driver.removeEdgeVanished(edges);
		vertices = Driver.removeVertexVanished(vertices);
		triangles = Driver.removeTriangleVanished(triangles);
		
		Driver.writeVertices(vertices, contractedVertexFileName);
		Driver.writeEdges(edges, contractedEdgeFileName);
		Driver.writeTriangles(triangles,contractedTriangleFileName);
		return new ResultPair(millis,edgesContracted,initialSimplices,itCount);
	}
	
	public static void main(String[] args) throws IOException {
		String path = "/home/slechta.3/Documents/ContractionExperiments/PaperData/";
		/* The directory given by the path must contain a directory with the name given in the "name" variable" */
		String name = "Filigree";
		/* In the path/name directory, there must be three files: (name)Data.txt, (name)Edges.txt, and (name)Triangles.txt */
		/* Each file should be comma separated. Each line in the (name)Data.txt file corresponds to a vertex. */
		/* As many attributes per line may be given in the (name)Data.txt file, but the final attribute will be */
		/* viewed as the height value corresponding to the vertex. */
		/* Each line in the (name)Edges.txt file corresponds to an edge. Lines must be at least two attributes long.*/
		/* The first two attributes are integers which indicate the two vertices which determine the edge. The integers */
		/* should correspond to the vertices' indices in the (name)Data.txt file, indexing starting at 0. The third attribute */
		/* determines the edges height value, and must be equal to or exceed the height value assigned to both vertices. */
		/* If an edge's height value is not specified, then the height value assigned to the edge is the greater of the two */
		/* height value's of the edge's constituent vertices. */
		/* The (name)Triangles file is formatted the same way as the (name)Edges file. The first three attributes must be */
		/* Integers corresponding to vertices in the (name)Data file. The fourth attribute may be a height value that exceeds */
		/* the height value of all constituent edges and vertices, or it may be omitted and calculated the same way as edges. */
		double epsilon = 5.0;
		int maxIts = 500;
		/* The maximum number of sets of contractions and epsilon determine the theoretical bound on the bottleneck/interleaving */
		/* Distance corresponding to the persistence diagrams/modules of the original and contracted filtrations. */
		/* Theoretical bound is then epsilon*maxIts. Set -1 for unlimited iterations. */
		int maxContractions = -1;
		ResultPair ret = Driver.contract(path, name, epsilon, maxIts, maxContractions);
		/* New filtration are written to 3 new files: (name)ContractedData.txt, (name)ContractedEdges.txt, (name)ContractedTriangles.txt*/
		System.out.println(name + "\tTime: " + ret.getMillis() + " ms\tContracted: " + ret.getContracted() + " edges\tIterations: " + ret.getItCount());
		System.out.println("Program Completed");
	}

}
