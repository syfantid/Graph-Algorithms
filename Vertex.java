/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gt2247;

import java.util.Hashtable;
import java.util.Set;

/**
 * Represents a vertex in a graph
 * @author Sofia Yfantidou
 */
public class Vertex {
    private int vertexID; //The ID number of the vertex
    private Hashtable<Integer,Integer> adjacentVertices; //Vertex's neighbors
    
    public Vertex() {
        vertexID = 0;
        adjacentVertices = new Hashtable<>();
    }
    
    public Vertex(int vertexID, Hashtable<Integer,Integer> adjacentVertices) {
        this.vertexID = vertexID;
        this.adjacentVertices = (Hashtable<Integer,Integer>)adjacentVertices.clone();
    }
    
    public Vertex(int vertexID) {
        this.vertexID = vertexID;
        adjacentVertices = new Hashtable<>();
    }
    
    public void setVertexID(int vertexID) {
        this.vertexID = vertexID;
    }
    public void addAdjacentVertex(int vertex, int weight) {
        adjacentVertices.put(vertex, weight);
    }
    public void removeLastAdjacentVertex(int vertex) {
        adjacentVertices.remove(adjacentVertices.size()-1);
    }
    public int getNeighborsSize() {
        return adjacentVertices.size();
    }
    public Hashtable<Integer,Integer> getNeighbors() {
        return adjacentVertices;
    }
    public int getVertexID() {
        return vertexID;
    }
}
