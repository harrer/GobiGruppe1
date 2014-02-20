package de.lmu.ifi.bio.splicing.jsqlDatabase;

<<<<<<< HEAD
import de.lmu.ifi.bio.splicing.zkoss.entity.EventDisplay;
=======
import de.lmu.ifi.bio.splicing.genome.Event;
>>>>>>> 67b691716faef9295d318c99687a0b56d4c7c95c
import org.junit.Before;
import org.junit.Test;
import org.junit.runners.Parameterized;

import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
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

    @Test
    public void testGetEvents() throws Exception {
        List<Event> events = dbq.getEvents("ENSG00000123405");
    }

    @Test
    public void testTranscriptPropertiesTest() throws Exception {
        String id = "ENST00000545714";
        assertTrue(dbq.getStrandForTranscriptID(id));
        assertEquals("5", dbq.getChrForTranscriptID(id));
        id = "ENST00000221233";
        assertTrue("Strand '-' wurde erkannt", !dbq.getStrandForTranscriptID(id));
    }

    @Test
    public void testGetEventDisplay() throws Exception {
        String i1 = "ENST00000547791";
        String i2 = "ENST00000551103";
        EventDisplay ev = dbq.getEventDisplay(i1, i2);
        System.out.println(ev);
    }
}
