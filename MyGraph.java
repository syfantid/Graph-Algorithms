package gt2247;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Sofia Yfantidou 
 */
public class MyGraph {
    /*Each graph is represented as a HashTable.
      The key of the hashtable is the vertex ID number (search in O(1)) and the 
      value is the Vertex iteself.
    */
    private Hashtable<Integer,Vertex> graph = new Hashtable<>();
//private final ArrayList<Vertex> graph; 
    private final Comparator<Edge> comparator = new WeightComparator();
    private final PriorityQueue<Edge> edgesMinHeap = new PriorityQueue(comparator);
    private int vertices; //number of vertices
    private int edges; //number of edges
    private int startingNumber; //The first vertex ID (usually 0 or 1)
    
    public MyGraph() {
        vertices = 0;
        edges = 0;
        startingNumber = 1;
        graph = new Hashtable<>();
        //graph = new ArrayList<>();
    }
    
    public MyGraph(int vertices) {
        this();
        this.vertices = vertices;
    }
    
    private MyGraph Union(MyGraph g1,MyGraph g2) throws CloneNotSupportedException {
        MyGraph union = (MyGraph) g2.clone();
        while(!g1.edgesMinHeap.isEmpty()) {
            union.addVertices(g1.edgesMinHeap.poll());
        }
        return union;
    }
    
    /**
     * Creates a graph by reading a file
     * @param fileName The name of the file that contains the graph
     * @param startingNumber Depends on the numbering of the vertices. Whether 
     * they start from 0 or 1 or any other number.
     * @param type Type 1 is a simple/no weighted edges graph, while type 2 is a weighted graph
     * @throws IOException 
     */
    public void LoadGraph(String fileName,int startingNumber, int type) throws IOException {
        try(BufferedReader in = new BufferedReader(
                                new FileReader(fileName));) {
            String l;
            int vertexKey = startingNumber;
            int adjacentVertex;
            int weight = 0; //Edge weight is set to 0 by default
            int optimistic = 0; //Same as above
            int likely = 0; //Same as above
            
            l = in.readLine(); //Reading n,m
            String[] s = l.split(" "); 
            vertices = Integer.parseInt(s[0]);
            edges = Integer.parseInt(s[1]);
            this.startingNumber = startingNumber;
               
            while((l = in.readLine()) != null) {
                if(!l.isEmpty()) { //Checking if line is not empty
                    String[] numbers = l.split(" ");         
                    Vertex temp = new Vertex(vertexKey);
                    
                    //Temporary list that containts the adjacent vertices of the specific vertex
                    for(int i=0; i<numbers.length; i+=1) { //Getting each adjacent vertex one by one
                        adjacentVertex = Integer.parseInt(numbers[i]); //Converting to number
                        if(adjacentVertex != -1) { //getNumericValue returns -1 when character doesn't have a numeric value
                            if(type == 2) { //Weighted graph
                                 //We read an extra character from the input file
                                weight = Integer.parseInt(numbers[i+1]); 
                                i+=1; //So we have to go further to the file
                            } else if (type == 3) {
                                optimistic = Integer.parseInt(numbers[i+1]);
                                likely = Integer.parseInt(numbers[i+2]);
                                weight = Integer.parseInt(numbers[i+3]); //Cost
                                i+=3;  
                            }
                            //Adding a connection of two vertices in the graph
                            temp.addAdjacentVertex(adjacentVertex, weight);
                            //If the edge has not been added before
                            if(adjacentVertex > vertexKey) {
                                Edge tempEdge;
                                if(type == 1) {
                                    tempEdge = new Edge(vertexKey,adjacentVertex);
                                } else if(type == 2) {
                                    tempEdge = new Edge(vertexKey,
                                            adjacentVertex,weight);
                                } else {
                                    tempEdge = new Edge(vertexKey,
                                            adjacentVertex,weight,optimistic,likely);
                                }
                                edgesMinHeap.add(tempEdge);
                            }
                        }   
                    }
                    graph.put(temp.getVertexID(),temp);
                    vertexKey += 1; //Next line refers to the next vertex
                }
            }
        }
    }
    
    public Vertex findVertex(int vertexID) {
        /*for(Vertex v:graph) {
            if(v.getVertexID() == vertexID) {
                return v;
            }
        }
        return null; //If vertex is not found*/
        return graph.get(vertexID);
    }
    /**
     * Prints graph as text on the screen
     * @param type Type 1 is a simple graph without weighted edges, while type 2 is a weighted graph
     */
    public void printGraph(int type) {
        for(Vertex vertex : graph.values()){
            System.out.println("Vertex: " + vertex.getVertexID());
            Hashtable<Integer,Integer> neighbors = vertex.getNeighbors();
            for(int adjacentVertex:neighbors.keySet()) {
                System.out.print(adjacentVertex + " ");
                if(type == 2) {//Weighted graph
                    System.out.println("weight -> " + neighbors.get(adjacentVertex) + " ");
                }
            }
            System.out.println(); //Just prints a new line
        }
    }

    /**
     * Checks if the addition of a vertex in a graph creates cycle
     * @param currentVertex The vertex to be added
     * @param previousVertex The vertex that the recursive call cam from 
     * @param marked A list with all the visited vertices throughout the recursion
     * @return True if the vertex creates a cycle, false otherwise
     */
    private boolean hasCycle(int currentVertex, int previousVertex, ArrayList<Integer> marked) {
        marked.add(currentVertex); //Mark as visited
        //For each of its neighbors
        for(int adjacentVertex:findVertex(currentVertex).getNeighbors().keySet()) {
            if(!marked.contains(adjacentVertex)) { //If it's not visited yet
                marked.add(adjacentVertex); //Mark as visited
                //Repeat for the neihbor
                if(hasCycle(adjacentVertex,currentVertex,marked)) {
                    return true;
                }
            } else if(adjacentVertex != previousVertex) {
                return true; //It has a cycle
            }
        }
        return false;
    }
    /**
     * Kruskal's algorithm for MST (EXERCISE 14)
     * @return The cost of the MST
     */
    public int Kruskal() {
        int cost = 0; //The resulting cost
        MyGraph MST = new MyGraph(vertices); //The MST build step by step
        MyGraph temp = new MyGraph(vertices); //Helps us in the backtracking process
        ArrayList<Integer> marked = new ArrayList<>(); //The visited nodes when we check for cycles
        int i = 0;
        while(i < vertices-1) { //i<n-1
            Edge currentEdge = edgesMinHeap.poll(); //Get the edge with the smallest cost
            MST.addVertices(currentEdge); //Add this edge to the MST
            if(!MST.hasCycle(currentEdge.getFirst(),currentEdge.getSecond(),marked)) { //If the edge doesn't create a cycle
                cost += currentEdge.getWeight(); //Update the cost
                currentEdge.printEdge(2);
                i+=1;
            } else { //If the edge creates a cycle
                MST = temp; //Return the MST to its previous state using the aiding tree
            }
            marked.clear();
        }
        return cost;
    }
    /**
     * Prim's algorithm for MST (EXERCISE 14)
     * @return The cost of the MST
     */
    public int Prim() {
        int cost = 0; //The cost of the MST
        PriorityQueue<Edge> edgesHeap = new PriorityQueue(comparator); //The minheap of the edges
        Set<Integer> visited = new HashSet<>(); //The set of visited vertices
        
        Random r = new Random();
        Vertex currentVertex = graph.get(r.nextInt(graph.size())+startingNumber); //Pick a random vertex
        visited.add(currentVertex.getVertexID()); //Add first vertex to the list
        
        while(visited.size() != graph.size()) {//While not all the nodes are visited
            System.out.println("Current vertex: " + currentVertex.getVertexID());
            for(Integer adjacentID:currentVertex.getNeighbors().keySet()) { //For each neighbor add the edge to the minheap
                 Edge temp = new Edge(currentVertex.getVertexID(), adjacentID, 
                         currentVertex.getNeighbors().get(adjacentID));
                 edgesHeap.add(temp); //Add the edge between the current vertex and the neighbor to the heap     
            }
            Edge chosen;
            do { //while both of the edge's vertices have been visited
                chosen = edgesHeap.poll(); //Take out the edge with the minimum weight
            }
            while(visited.contains(chosen.getFirst()) && visited.contains(chosen.getSecond())); 
            //If the tree contains both ends of the edge then the chosen edge
            //would create a cycle
            visited.add(chosen.getSecond()); //Adding the adjacent vertex to the vivited set
            cost += chosen.getWeight(); //Update cost
            chosen.printEdge(2);
            currentVertex = findVertex(chosen.getSecond()); //Update current vertex
        }
        return cost;
    }
    /**
     * Boruvka's algorithm for MST (EXERCISE 14)
     * @return The cost of the MST
     */
    public int Boruvka() throws CloneNotSupportedException {
        int cost = 0;
        ArrayList<MyGraph> graphs = new ArrayList<>();
        boolean flag = false;
        
        for(Vertex v:this.graph.values()) {
            flag = false;
            PriorityQueue<Edge> edgesHeap = fillQueue(v);
            Edge chosen;
            chosen = edgesHeap.poll();
            
            for(MyGraph g:graphs) { //For each graph
                //Check if the edge vertices are included in the graph
                if(g.findVertex(chosen.getFirst()) != null ||  
                        g.findVertex(chosen.getSecond()) != null) {
                    flag = true;
                    g.addVertices(chosen);
                    cost += chosen.getWeight();
                }
            }
            if(!flag) { //If we have a different branch of the tree
                MyGraph temp = new MyGraph();
                temp.addVertices(chosen);
                cost += chosen.getWeight();
                graphs.add(temp);
            }
        }
        ArrayList<MyGraph> tempGraphs = (ArrayList<MyGraph>)graphs.clone();
        Iterator<MyGraph> it1 = tempGraphs.iterator();
        while(it1.hasNext()) {
            //System.out.println("HERE!");
            MyGraph g = it1.next();
            System.out.println("ITERATION FOR GRAPH");
            g.printGraph(2);
            PriorityQueue<Edge> edgesHeap = new PriorityQueue<>(comparator);
            for(Vertex v:this.graph.values()) {
                if(g.findVertex(v.getVertexID()) != null) {
                    for(int neighbor:v.getNeighbors().keySet()) {
                        if(g.findVertex(neighbor) == null) {
                            System.out.println("Neighbor " + neighbor + " of vertex " + v.getVertexID());
                            Edge e = new Edge(v.getVertexID(), neighbor, 
                                v.getNeighbors().get(neighbor));
                            edgesHeap.add(e);
                        }
                    }
                }
            }
            Edge chosen = edgesHeap.poll();
            cost += chosen.getWeight();
            MyGraph g1 = null;
            MyGraph g2 = null;
            Iterator<MyGraph> it2 = tempGraphs.iterator();
            System.out.println("ITERATION STARTS HERE FOR EDGE");
            chosen.printEdge(2);
            while(it2.hasNext()) {
                MyGraph temp = it2.next();
                if(temp.findVertex(chosen.getFirst()) != null || temp.findVertex(chosen.getSecond()) != null) {
                    if(g1 == null) {
                        g1 = temp;
                    } else {
                        g2 = temp;
                    }
                }
            }
            if(g1 != g2) {
                System.out.println("---------------------G1------------------------------");
                g1.printGraph(2);
                System.out.println("---------------------G2------------------------------");
                g2.printGraph(2);
                MyGraph union = Union(g1,g2);
                System.out.println("UNION");
                union.printGraph(2);
                graphs.remove(g1);
                graphs.remove(g2);
                graphs.add(union);
            } 
            if(graphs.size() == 1) {
                break;
            }
            tempGraphs = graphs;
            for(MyGraph graphh:tempGraphs) {
                System.out.println("TEMP GRAPH");
                graphh.printGraph(2);
            }
            for(MyGraph graphh:graphs) {
                System.out.println("GRAPH");
                graphh.printGraph(2);
            }
        }  
        return cost;
    }
    
    private PriorityQueue<Edge> fillQueue(Vertex v) {
        PriorityQueue<Edge> edgesHeap = new PriorityQueue<>(comparator);
        for(int neighbor:v.getNeighbors().keySet()) {
            Edge e = new Edge(v.getVertexID(), neighbor, 
                        v.getNeighbors().get(neighbor));
            edgesHeap.add(e);
        }
        return edgesHeap;
    }
    
    
   /*public List<LinkedList<Edge>> AlternativeDijkstra(int start, int end) {
       //All the critical paths found
       List<LinkedList<Edge>> criticalPaths = new ArrayList<>();
       //The current critical path
       List<Edge> currentCritical= new LinkedList<>();
       //Stores visited nodes' IDs
       Set<Integer> visited = new HashSet<>();
       //Stores unvisited nodes' IDs
       Set<Integer> unvisited = new HashSet<>();
       //Stores the cost of visiting each vertex starting from the initial one
       Hashtable<Integer,Integer> costs = new Hashtable<>();
       int largestCost;
       int slargestCostID;
       Vertex current = findVertex(start);
       
       //Initialization of the problem
       for(Vertex v:graph) {
           if(v.getVertexID() != start) { //If the node is not the initial
               unvisited.add(v.getVertexID()); //Put it in the unvisited list
           } else { //Initial node
               visited.add(v.getVertexID()); //Put it in the visited list
           }
           costs.put(v.getVertexID(), 0); //Make its cost 0
       }
       
       int tentativeDistance;
       while(!unvisited.isEmpty() && current.getVertexID() != end) {
           for(int neighbor:current.getNeighbors().keySet()) { //for each neighbor vertex
               tentativeDistance = costs.get(current.getVertexID()) + 
                       current.getNeighbors().get(neighbor);
               if(tentativeDistance > costs.get(neighbor)) {
                   
               } else if(tentativeDistance == costs.get(neighbor)) {
                   
               }
           }
       }
       
       return criticalPaths;
   } */
    
    
    
    /**
     * Adds a vertex with its neighbor to the graph
     * @param v1ID The vertex to be added
     * @param v2ID Its adjacent vertex
     * @param weight The weight of the edge that connects them
     */
    private void addVertex(int v1ID, int v2ID, int weight) {
        if(this.findVertex(v1ID) == null) { //If the vertex doesn't already exists
            Vertex v1 = new Vertex(v1ID);
            v1.addAdjacentVertex(v2ID, weight);
            graph.put(v1.getVertexID(),v1);   
        } else {
            findVertex(v1ID).addAdjacentVertex(v2ID, weight);
        }
    }
    /**
     * Adds an edge to the graph
     * @param e The edge to be added
     */
    private void addVertices(Edge e) {
        int v1ID = e.getFirst();
        int v2ID = e.getSecond();
        int weight = e.getWeight();
        addVertex(v1ID,v2ID,weight);
        addVertex(v2ID, v1ID, weight);
    }
    
    /**
     * Checks if graph contains Hamiltonian cycle using backtracking method (EXERCISE 8)
     * There are no edges with weight by default, so we're working with a simple graph
     * @param startingVertex The vertex number from which we want our path to start. 
     * Starting point doesn't matter, as cycle can start from any given point.
     * @param path The Hamiltonian path; in the end it contains the resulting path first found
     * @return True if graph contains at least one Hamiltonian Cycle, false otherwise.
     */
    public boolean FindHamiltonianCycle(int startingVertex, ArrayList<Integer> path) {
        path.add(startingVertex); //Adding the starting vertex to the path
        Vertex currentVertex = findVertex(startingVertex); //The last vertex added in the path
        //Terminating condition of the recursive function
        if(path.size() == graph.size() && graph.size() != 2) { //If all vertices are included in the path 
            /*Checks if there's an edge from the last vertex of the path to 
              the first one (vertices are adjacent). path.get(path.size()-1) is 
              the last vertex of the path and consequently a key in the graph. 
              path.get(0) is the first vertex of the path.
            */
            if(findVertex(path.get(path.size()-1)).getNeighbors().containsKey(path.get(0))) { 
                return true;
            } else {
                return false;
            }
        } else { 
            for(int adjacentVertex : currentVertex.getNeighbors().keySet()) { //For every adjacent vertex
                if(!path.contains(adjacentVertex)) { //If it's not in the path yet
                    if(FindHamiltonianCycle(adjacentVertex, path)) { //If the specific call leads to a solution returns true
                        return true; 
                    } else {
                        path.remove(path.size()-1); //If it doesn't work out remove the last vertex added
                        //and try with a different adjacent vertex
                    }
                }
            }
        }
        return false; /*If nothing leads to a solution this means we couldn't find
        a Hamiltonian cycle*/
    }
    /**
     * Class used for the priority queue
     */
    public class WeightComparator implements Comparator<Edge>
    {
        @Override
        public int compare(Edge x, Edge y)
        {
            if (x.getWeight()< y.getWeight())
            {
                return -1;
            }
            if (x.getWeight()> y.getWeight())
            {
                return 1;
            }
            return 0;
        }
    }
}
    
    
