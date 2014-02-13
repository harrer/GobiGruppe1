package de.lmu.ifi.bio.splicing.jsqlDatabase;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by uhligc on 13.02.14.
 */
public class DBQueryTest {
    DBQuery dbq;
    String keyword;
    @Before
    public void setUp() throws Exception {
        dbq = new DBQuery();
        keyword = "01";
    }

    @Test
    public void main() throws Exception {
        testSearch();
        testFindAllGenes();
        testFindAllProteins();
        testFindAllTranscripts();
    }

    @Test
    public void testSearch() throws Exception {
        System.out.println(dbq.search(keyword));
    }

    @Test
    public void testFindAllGenes() throws Exception {
        System.out.println(dbq.findAllGenes());
    }

    @Test
    public void testFindAllTranscripts() throws Exception {
        System.out.println(dbq.findAllTranscripts());
    }

    @Test
    public void testFindAllProteins() throws Exception {
        System.out.println(dbq.findAllProteins());
    }
}
