package de.lmu.ifi.bio.splicing.localAli;

/**
 *
 * @author tobias
 */
public class SeqPair {
    
    private String s1;
    private String s2;
    
    public SeqPair(String seq1, String seq2){
        s1 = seq1;
        s2 =seq2;
    }
    
    public String getS1(){
        return s1;
    }
    
    public String getS2(){
        return s2;
    }
}