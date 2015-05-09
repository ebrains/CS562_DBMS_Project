package edu.stevens.cs562.emf;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Graph {
	private List<LinkedList<Integer>> vertexSet = new ArrayList<LinkedList<Integer>>();
	private List<Integer> indegree = new ArrayList<Integer>();
	private int vertexLeft;
	//initial all vertex with 0 in-degree, waiting for insert edge
	void setGraph(int v) {
		for (int i = 0; i < v; i++) {
			LinkedList<Integer> vertex = new LinkedList<Integer>();
			vertex.add(i);
			vertexSet.add(vertex);
			indegree.add(0);
		}
		vertexLeft = vertexSet.size();
	}
	void InsertEdge(int parent, int child) {
		vertexSet.get(parent).add(child);
		indegree.set(child, indegree.get(child) + 1);
	}
	//show the whole list
	void show() {
		for (int i = 0; i < vertexSet.size(); i++) {
			for (int j = 0; j < vertexSet.get(i).size(); j++) {
				System.out.print(vertexSet.get(i).get(j));
				System.out.print(" ");
			}
			System.out.print("\n");
		}
//			test for the in-degree
//				for (int i = 0; i < vertexSet.size(); i++) {
//					System.out.print(indegree.get(i));
//					System.out.print(" ");
//				}
	}
	
	int max_indegree() {
		int max = 0;
		for (int i = 0; i < vertexSet.size(); i++) {
			if (max < indegree.get(i)) 
				max = indegree.get(i); 
		}
		return max;
	}
    LinkedList<Integer> topoSort() {
    	LinkedList<Integer> vertexParse = new LinkedList<Integer>();
    	if (vertexLeft > 0) {
	    	// First, find a list of "start nodes" which have no incoming edges and output them
    		for (int i = 0; i < vertexSet.size(); i++) {
    			if (indegree.get(i) == 0) {
    				System.out.print(i);
    				System.out.print(" ");
    				vertexParse.add(i);
    			}
    		}
    		System.out.print("\n");
    		// parse the "start nodes", remove the edge from them, update the related in-degree and vertexLeft 
    		for (int i = 0; i < vertexParse.size(); i++) {
    			int vertex = vertexParse.get(i);
    			for (int j = 0; j < vertexSet.get(vertex).size(); j++) { 
    				int node = vertexSet.get(vertex).get(j);
    				indegree.set(node, indegree.get(node) - 1);
    			}
				vertexLeft--;
			}
    		return vertexParse;
    	} else 
    		return null;
    }
}
