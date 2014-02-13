package de.lmu.ifi.bio.splicing.io;

import de.lmu.ifi.bio.splicing.jsqlDatabase.DBUpdate;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by uhligc on 12.02.14.
 */
public class GTFParserTest {
    DBUpdate dbu;
    GTFParser gtf;
    @Before
    public void setUp() throws Exception {
        dbu = new DBUpdate();
        gtf = new GTFParser();
    }

    @Test
    public void testAddGenes() throws Exception {
        gtf.addGenes(dbu);
        System.out.println(gtf.countGenes());
    }
}
