//That's a simple example of topological sort. We assume all vertex are contiguous.
//The example in File1.txt is from wiki. You can see the original picture on the website
//Note: This is not the integral topological sort. I made some changes to cater out project.
//On each loop we just find out the vertex which in-degree(or incoming edge) is 0 and output. 
//Thus we can see how many loops we need to traverse the whole graph.

package test;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;


public class assignment1 {
    public static void main(String[] args) {
    	//we initial the graph size for short. not reading from .txt
    	Graph x = new Graph();
    	x.setGraph(12);
    	readInput(x);
    	x.show();
    	x.topoSort();
    }
    //read all edges from file and generate the graph
    public static void readInput(Graph x){
		File file = new File("File1.txt");
		try {
			//read all line into lineList
			FileReader fr = new FileReader(file);
			BufferedReader bf = new BufferedReader(fr);
			String line;
			while((line = bf.readLine()) != null){
				String[] arr = line.split(" ");	//split the line by space E.X. 7 11 => arr[0] = 7, arr[1] = 11
				x.InsertEdge(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]));	//insert edge
			}
			
			bf.close();
			fr.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    static class Graph {
		private List<LinkedList<Integer>> vertexSet = new ArrayList<LinkedList<Integer>>();
		private List<Integer> indegree = new ArrayList<Integer>();
		//initial all vertex with 0 in-degree, waiting for insert edge
		void setGraph(int v) {
			for (int i = 0; i < v; i++) {
				LinkedList<Integer> vertex = new LinkedList<Integer>();
				vertex.add(i);
				vertexSet.add(vertex);
				indegree.add(0);
			}
		}
		void InsertEdge(int parent, int child) {
			boolean flag = true; 
			for (int i = 0; i < vertexSet.get(parent).size(); i++)
				if (vertexSet.get(parent).get(i) == child)
					flag = false;
			if (flag) {
				vertexSet.get(parent).add(child);
				indegree.set(child, indegree.get(child) + 1);	
			}
		}
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
	    void topoSort() {
	    	int vertexLeft = vertexSet.size();
	    	LinkedList<Integer> vertexParse = new LinkedList<Integer>();
	    	while (vertexLeft > 0) {
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
	    		vertexParse.clear();
	    	}
	    }
	}
}