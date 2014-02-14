package de.lmu.ifi.bio.splicing.jsqlDatabase;

import de.lmu.ifi.bio.splicing.eventdetection.EventDetector;
import de.lmu.ifi.bio.splicing.genome.Event;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by uhligc on 14.02.14.
 */
public class DBUpdateRoutine {
    final static DBQuery dbq = new DBQuery();
    final static DBUpdate dbu = new DBUpdate();

    public static void insertEvents() {
        List<String> thebiglist = new LinkedList<>();
        thebiglist = dbq.findAllGenes();
        for (String s : thebiglist) {
            Set<Event> eventSet = EventDetector.getEvents(dbq.getGene(s));
            for (Event event : eventSet) {
                dbu.insertEvent(event);
            }
        }
    }

    public static void insertEventsForKeyword(String keyword) {
        List<String> list = dbq.search(keyword);
        for (String s : list) {
            if (s.startsWith("ENSG")) {
                insertEventsForGene(s);
            }
        }
    }

    public static void insertEventsForGene(String geneid) {
        Set<Event> eventSet = EventDetector.getEvents(dbq.getGene(geneid));
        for (Event event : eventSet) {
            dbu.insertEvent(event);
        }
    }
}
