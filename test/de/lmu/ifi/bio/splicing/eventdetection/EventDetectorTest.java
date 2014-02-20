package de.lmu.ifi.bio.splicing.eventdetection;

import de.lmu.ifi.bio.splicing.config.Setting;
import junit.framework.TestCase;
import de.lmu.ifi.bio.splicing.genome.*;
/**
 * Created by schmidtju on 13.02.14.
 */
public class EventDetectorTest extends TestCase {

    EventAnnotation ea;

    public void setUp() throws Exception {
        super.setUp();
        Transcript p1 = Setting.dbq.getTranscript("ENST00000080059");
        Transcript p2 = Setting.dbq.getTranscript("ENST00000477203");
        ea = new EventAnnotation(p1, p2, true);
    }

    public void testPrintEvents() throws Exception {
        EventDetector.printEvents(ea);
        EventDetector.makeEventSet(ea);
    }
}
