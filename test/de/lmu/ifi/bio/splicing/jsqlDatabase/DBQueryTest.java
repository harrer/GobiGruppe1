package de.lmu.ifi.bio.splicing.jsqlDatabase;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by uhligc on 13.02.14.
 */
public class DBQueryTest {
    DBQuery dbq;
    String keyword;

    @Before
    public void setUp() throws Exception {
        dbq = new DBQuery();
        keyword = "EN";
    }

    @Test
    public void testSearch() throws Exception {
        assertTrue(dbq.search(keyword).size() > 0);
    }

    @Test
    public void testFindAllGenes() throws Exception {
        assertTrue(dbq.findAllGenes().size() > 0);
    }

    @Test
    public void testFindAllTranscripts() throws Exception {
        assertTrue(dbq.findAllTranscripts().size() > 0);
    }

    @Test
    public void testFindAllProteins() throws Exception {
        assertTrue(dbq.findAllProteins().size() > 0);
    }
}
