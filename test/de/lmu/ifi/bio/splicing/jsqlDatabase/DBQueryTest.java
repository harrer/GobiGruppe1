package de.lmu.ifi.bio.splicing.jsqlDatabase;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by uhligc on 13.02.14.
 */
public class DBQueryTest {
    DBQuery dbq;
    @Before
    public void setUp() throws Exception {
        dbq = new DBQuery();
    }

    @Test
    public void testSearch() throws Exception {
        System.out.println(dbq.search(""));
    }

    @Test
    public void testFindAllGenes() throws Exception {

    }

    @Test
    public void testFindAllTranscripts() throws Exception {

    }

    @Test
    public void testFindAllProteins() throws Exception {

    }
}
