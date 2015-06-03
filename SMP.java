/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gt2247;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Sofia
 */
public class SMP {
    private final ConcurrentHashMap<Integer,Boolean> singleMen;
    private final ConcurrentHashMap<Integer,PriorityQueue<Mate>> menPrefer;
    private final ConcurrentHashMap<Integer,ArrayList<Mate>> womenPrefer;
    private final ConcurrentHashMap<Integer,Mate> womenMarriedTo;
    private final String menName;
    private final String womenName;
    
    public SMP(String menName,String womenName) {
        this.menName = menName;
        this.womenName = womenName;
        singleMen = new ConcurrentHashMap<>();
        menPrefer = new ConcurrentHashMap<>();
        womenPrefer = new ConcurrentHashMap<>();
        womenMarriedTo = new ConcurrentHashMap<>();
    }
    
    public void StableMarriage() throws IOException {
        LoadFiles();

        while(singleMen.values().contains(true)) {//While there are more single men
            for(int man:singleMen.keySet()) {//For each man
                if(singleMen.get(man)) { //If he's single
                    Mate m = menPrefer.get(man).poll();
                    int woman = m.mate;
                    //System.out.println("Man " + man + " proposes to woman " + woman);
                    int rank = womenPrefer.get(woman).get(man-1).rank; //The rank the 
                    //woman gives to the man
                    if(womenMarriedTo.get(woman).mate == 0) {//If the woman is single 
                        UpdateMarriage(woman, man, rank);
                        //System.out.println("He's accepted because she's single");
                    } else { //If the woman is not single
                        Mate previous = womenMarriedTo.get(woman);
                        if(previous.rank > rank) { //Woman prefers the new man
                            UpdateMarriage(woman, man, rank);
                            //Previous man is now single
                            ChangeSingleStatus(previous.mate, true); 
                            //System.out.println("He's accepted for his better ranking, which is " + rank + " compared to " + previous.rank);
                        } //Else nothing happens. Man has lost one possible woman
                    }
                }
            }
        }
        PrintMarriages();
    }
    
    private void PrintMarriages() {
        int man;
        System.out.println("Stable Marriages:");
        for(int woman:womenMarriedTo.keySet()) {
            man = womenMarriedTo.get(woman).mate;
            System.out.println("(Woman: " + woman + ", Man: " + man + ")");
        }
    }
    private void ChangeSingleStatus(int man,boolean changeTo) {
        //Add him to not singles' list
        singleMen.put(man, changeTo);
    }
    
    private void UpdateMarriage(int woman, int man, int rank) {
        //Update the woman's marital status
        womenMarriedTo.put(woman, new Mate(man,rank));
        //Update the man's single status
        ChangeSingleStatus(man, false);
    }
    
    private boolean LoadFiles() throws IOException {
        try(BufferedReader in1 = new BufferedReader(
                new FileReader("menPrefer.txt"));
            BufferedReader in2 = new BufferedReader(
                new FileReader("womenPrefer.txt"));) {
            String l;
            int man = 1;
            while((l = in1.readLine()) != null) { //Each line is for a man
                String[] ranks = l.split(","); //The line contains the ranks 
                //the man would give to the woman of the column
                int woman = 1;
                PriorityQueue<Mate> mates = new PriorityQueue<>(); 
                for(String rank : ranks) { //For each woman and rank 
                    mates.add(new Mate(woman,Integer.parseInt(rank))); 
                    ++woman; 
                }
                menPrefer.put(man, mates);
                singleMen.put(man, true); //All men single at first
                //All women married to nobody -> single at first
                womenMarriedTo.put(man, new Mate(0,0)); 
                womenPrefer.put(man, new ArrayList<>()); 
                //Here we use the numbering for men to number women, too, since
                //they're the same number. So here consider man = woman 
                ++man; 
            }
            man = 1;
            while((l = in2.readLine()) != null) {
                String[] ranks = l.split(","); //Scored seperated be comma
                int woman = 1;
                for(String rank : ranks) {
                    womenPrefer.get(woman).add(new Mate(man,Integer.parseInt(rank)));
                    ++woman; 
                }
                ++man;
            }
        } catch(IOException e) {
            System.out.println("Problem Loading the files!");
        }
        return true;
    }
    
    public class Mate implements Comparable<Mate>{
        int mate;
        int rank;
        
        public Mate(int mate, int rank) {
            this.mate = mate;
            this.rank = rank;
        }

        @Override
        public int compareTo(Mate t) {
            if(this.rank == t.rank) {
                return 0;
            } else if(this.rank > t.rank) {
                return 1;
            } else {
                return -1;
            }
        }
    }
}
