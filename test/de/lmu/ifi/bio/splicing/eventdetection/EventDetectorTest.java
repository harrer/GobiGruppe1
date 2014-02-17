package de.lmu.ifi.bio.splicing.eventdetection;

import junit.framework.TestCase;
import de.lmu.ifi.bio.splicing.genome.*;
/**
 * Created by schmidtju on 13.02.14.
 */
public class EventDetectorTest extends TestCase {

    EventAnnotation ea;

    public void setUp() throws Exception {
        super.setUp();
        Transcript p1 = new Transcript("t1", "p1");
		Transcript p2 = new Transcript("t2", "p2");
        p1.addExon(new Exon(0, 8, 2));
        p2.addExon(new Exon(0, 8, 2));
		p1.addExon(new Exon(20, 40, 0));
		p2.addExon(new Exon(30, 56, 0));
        p1.addExon(new Exon(60, 80, 0));
        p2.addExon(new Exon(60, 80, 0));

        ea = new EventAnnotation(p1, p2, true);
    }

    public void testPrintEvents() throws Exception {
        EventDetector.printEvents(ea);
        EventDetector.makeEventSet(ea);
    }
}
