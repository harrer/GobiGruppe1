package de.lmu.ifi.bio.splicing.structures.mapping;

import de.lmu.ifi.bio.splicing.genome.Event;
import de.lmu.ifi.bio.splicing.io.PDBParser;
import de.lmu.ifi.bio.splicing.structures.PDBData;
import junit.framework.TestCase;

import java.util.HashMap;

/**
 * Created by schmidtju on 18.02.14.
 */
public class DSSPTest extends TestCase {
    public void setUp() throws Exception {
        super.setUp();
    }


    public void testUpdateEventWithAccAndSS() throws Exception {
        HashMap<Integer, Integer> aligned = new HashMap<>();
        for(int i = 10; i < 479; i++){
            aligned.put(i, i - 10);
        }
//        Model model = new Model("1bj4.A", aligned);
//        Event event = new Event("ENST00000316694", "ENST00000582653", 166, 481, 'D');
//        int[] borders = model.getBoundaries(event);
//        DSSP.updateEventWithAccAndSS(model, event);
    }
}
