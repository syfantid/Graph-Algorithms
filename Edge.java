/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gt2247;

/**
 * Represents an edge in a graph
 * @author Sofia Yfantidou 
 */
public class Edge {
    private int v1; //Starting vertex
    private int v2; //Ending vertex
    private int weight; //Weight of the edge or cost in PERT
    //The variables below concern only PERT
    private int likely; //Likely duration of completion
    private int optimistic; //Optimistic duration of completion
    
    public Edge(int v1, int v2, int weight) {
        this(v1,v2);
        this.weight = weight;
    }
    
    public Edge(int v1, int v2, int cost, int optimistic, int likely) {
        this(v1,v2,cost);
        this.optimistic = optimistic;
        this.likely = likely;  
    }
    
    public Edge(int v1,int v2) {
        this.v1 = v1;
        this.v2 = v2;
    }
    
    /*public void addNeighbor(int v2, int weight) {
        this.v2 = v2;
        this.weight = weight;
    }*/
    
     /**
     * Prints an edge's components (vertices, weight)
     * @param e The given edge
     */
    public void printEdge(int type) {
        System.out.println("(v" + v1 + ",v" + v2 + ") = " + weight);
        if(type == 3) {
            System.out.println("Optimistic Duartion: " + optimistic + 
                    " Likely Duration: " + likely);
        }
    }
    
    public int[] getVertices() {
        int[] vertices = new int[2];
        vertices[1] = v1;
        vertices[2] = v2;
        return vertices;
    }
    public int getFirst() {
        return v1;
    }
    public int getSecond() {
        return v2;
    }
    public int getWeight() {
        return weight;
    }
    public int getOptimistic() {
        return optimistic;
    }
    public int getLikely() {
        return likely;
    }
    public void setLikely(int likely) {
        this.likely = likely;
    }
    public void setWeight(int weight) {
        this.weight = weight;
    }
}
