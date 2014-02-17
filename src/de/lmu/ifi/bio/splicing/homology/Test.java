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
    
    public static void main(String[] args) {
        DatabaseQuery db = new DBQuery();
        Transcript t = db.getTranscriptForProteinId("ENSP00000261313");
        System.out.println(GenomeSequenceExtractor.getProteinSequence(t));
    }
}
