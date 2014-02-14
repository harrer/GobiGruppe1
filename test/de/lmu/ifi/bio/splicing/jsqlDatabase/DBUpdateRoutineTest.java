package de.lmu.ifi.bio.splicing.jsqlDatabase;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert.*;

/**
 * Created by uhligc on 14.02.14.
 */
public class DBUpdateRoutineTest {
    DBQuery dbq;
    @Before
    public void setUp() throws Exception {
        dbq = new DBQuery();
    }

    @Test
    public void testInsertEventsForGene() throws Exception {
        DBUpdateRoutine.insertEventsForGene("ENSG00000132330");
    }
}
