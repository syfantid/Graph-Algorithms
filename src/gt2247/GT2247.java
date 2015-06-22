/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gt2247;

import gt2247.SMP.Mate;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Sofia Yfantidou
 */
public class GT2247 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        
        MyGraph simpleGraph = new MyGraph();
        int startingVertex = 1;
        simpleGraph.LoadGraph("ex8.txt",startingVertex,1);
        
        //EXERCISE 8 : HAMILTONIAN CYCLE ALGORITHM
        System.out.println("---------------EXERCISE 8 : HAMILTONIAN CYCLE ALGORITHM---------------");
        ArrayList<Integer> path = new ArrayList<>();
        if(simpleGraph.FindHamiltonianCycle(startingVertex, path)) {
            System.out.print("Path: ");
            for(int vertex : path) {
                System.out.print(vertex + " ");
            }
            System.out.println();
        } else {
            System.out.println("No Hamiltonian Cycle found!");
        }
        
        MyGraph weightedGraph = new MyGraph();
        weightedGraph.LoadGraph("weightedGraph.txt", startingVertex, 2);
        
        //EXERCISE 14 : KRUSKAL, PRIM, BORUVKA
        System.out.println("--------------EXERCISE 14 : KRUSKAL--------------");
        long startTime = System.nanoTime();
        System.out.println("Cost of Kruskal: " + weightedGraph.Kruskal());
        long endTime = System.nanoTime();
        System.out.println("Execution time for Kruskal: " + ((endTime - startTime)/1000000.0) + "ms");
        
        weightedGraph = new MyGraph();
        weightedGraph.LoadGraph("weightedGraph.txt", startingVertex, 2);
        
        System.out.println("---------------EXERCISE 14 : PRIM---------------");
        startTime = System.nanoTime();
        System.out.println("Cost of Prim: " + weightedGraph.Prim());
        endTime = System.nanoTime();
        System.out.println("Execution time for Prim: " + ((endTime - startTime)/1000000.0) + "ms");
        
        weightedGraph = new MyGraph();
        weightedGraph.LoadGraph("weightedGraph.txt", startingVertex, 2);
  
        System.out.println("---------------EXERCISE 14 : BORUVKA---------------");
        startTime = System.nanoTime();
        System.out.println("Cost of Boruvka: " + weightedGraph.Boruvka());
        endTime = System.nanoTime();
        System.out.println("Execution time for Boruvka: " + ((endTime - startTime)/1000000.0) + "ms");
        
        
        /*System.out.println("--------------EXERCISE 32 : PERT-----------------");
        
        MyGraph pertGraph = new MyGraph();
        pertGraph.LoadGraph("PERT.txt", startingVertex, 3);*/
        
        
        
        System.out.println("------EXERCISE 32 : STABLE MARRIAGE PROBLEM------");
        SMP marriage = new SMP("menPrefer.txt","womenPrefer.txt");
        marriage.StableMarriage();   
    }
   
    
}

