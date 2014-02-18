package de.lmu.ifi.bio.splicing.homology;

import de.lmu.ifi.bio.splicing.genome.Transcript;
import de.lmu.ifi.bio.splicing.interfaces.DatabaseQuery;
import de.lmu.ifi.bio.splicing.io.GenomeSequenceExtractor;
import de.lmu.ifi.bio.splicing.jsqlDatabase.DBQuery;
import java.util.HashMap;

/**
 *
 * @author harrert
 */
public class Test {
    
    public static double sequenceIdentity(String[] ali){
        int identical = 0;
        int end = -1, start = -1;
        for (int i = 0; i < ali[0].length(); i++) {
            if (ali[0].charAt(i) != '-' && ali[1].charAt(i) != '-') {
                start = i;
                break;
            }
        }
        for (int i = ali[0].length() - 1; i >= 0; i--) {
            if (ali[0].charAt(i) != '-' && ali[1].charAt(i) != '-') {
                end = i;
                break;
            }
        }
        for (int i = start; i <= end; i++) {
            if(ali[0].charAt(i) != '-' && ali[0].charAt(i) == ali[1].charAt(i)){
                identical++;
            }
        }
        return 1.0*identical/(end-start+1);
    }
    
    public static void main(String[] args) {
        String[] s = new String[]{"-----BBBDDCCCCC--------","AAAAABBB--CCCCCFFFFFFFF"};
        System.out.println(sequenceIdentity(s));
        //DatabaseQuery db = new DBQuery();
        //Transcript t = db.getTranscriptForProteinId("ENSP00000261313");
        //System.out.println(GenomeSequenceExtractor.getProteinSequence(t));
    }
}
