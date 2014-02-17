package de.lmu.ifi.bio.splicing.io;

import de.lmu.ifi.bio.splicing.config.Setting;
import de.lmu.ifi.bio.splicing.genome.Transcript;
import de.lmu.ifi.bio.splicing.jsqlDatabase.DBQuery;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Created by uhligc on 2/14/14.
 */
public class GenomeSequenceExtractorTest {
    List<String> liste;
    String output;
    Transcript tr;

    @Before

    public void setUp() throws Exception {
        liste = Setting.dbq.findTranscriptIDsForKeyword("1233");
        output = "/home/u/uhligc/git/gobi2/res/output.txt";
        tr = Setting.dbq.getTranscript("ENST00000374004");
    }

    @Test
    public void testWriteListOfTranscriptIds() throws Exception {
        GenomeSequenceExtractor.writeListOfTranscriptIds(output, liste);
    }

    @Test
    public void testGetProteinSequence() throws Exception {
        String bla = GenomeSequenceExtractor.getProteinSequence(tr);
        System.out.println(bla);
    }

    @Test
    public void testGetNucleotideSequence() throws Exception {
        String id = "ENST00000341233";
        String nucletide_seq = GenomeSequenceExtractor.getNucleotideSequence("10", 91222136, 91222245);
        System.out.println(nucletide_seq + "\ngtaggtaggattttcttcttaactt".toUpperCase());
        Assert.assertTrue(id + " nicht richtige Nucleotide Sequenz gefunden", "gtaggtaggattttcttcttaactt".toUpperCase().contains(nucletide_seq));
    }

    @Test
    public void testWriteAllTranscriptsIntoFile() throws Exception {
        GenomeSequenceExtractor.writeAllSequencesToFile(output);
    }
}
