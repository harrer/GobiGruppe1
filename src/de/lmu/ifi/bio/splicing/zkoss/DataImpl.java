package de.lmu.ifi.bio.splicing.zkoss;

import de.lmu.ifi.bio.splicing.zkoss.entity.EventDisplay;
import de.lmu.ifi.bio.splicing.zkoss.entity.PatternEvent;
import de.lmu.ifi.bio.splicing.zkoss.entity.SpliceEventFilter;
import de.lmu.ifi.bio.splicing.genome.Event;
import de.lmu.ifi.bio.splicing.genome.Gene;
import de.lmu.ifi.bio.splicing.genome.SecondaryStructure;
import de.lmu.ifi.bio.splicing.genome.Transcript;
import de.lmu.ifi.bio.splicing.jsqlDatabase.DBQuery;

import java.util.*;

/**
 * Created by Carsten on 13.02.14.
 */
public class DataImpl implements Data {
    List<String> searchlist;
    List<EventDisplay> eventlist;
    DBQuery dbq;

    DataImpl() {
        dbq = new DBQuery();
        searchlist = new LinkedList<>();
        eventlist = new ArrayList<>();
    }

    @Override
    public List<String> findAll() {
        return dbq.findAllGenes();
    }

    @Override
    public List<String> search(String keyword) {
        return dbq.search(keyword);
    }

    @Override
    public List<EventDisplay> select(List<String> keylist) {
        eventlist = new LinkedList<>();
        List<String> geneslist = new LinkedList<>();
        List<String> transcriptslist = new LinkedList<>();
        List<String> proteinslist = new LinkedList<>();

        for (String s : keylist) {
            if (s.startsWith("ENSG"))
                geneslist.add(s);
            else if (s.startsWith("ENST"))
                transcriptslist.add(s);
            else if (s.startsWith("ENSP"))
                proteinslist.add(s);
        }

        List<Gene> genes = new LinkedList<>();
        for (String gene : geneslist) {
            genes.add(dbq.getGene(gene));
        }

        //iterate over protein list and transcript list
        for (Gene gene : genes) {
            eventlist.addAll(getEventsPerGene(gene));
        }

        //TODO select implement
        //liste von strings die geneids, transcriptids und proteinids enthalten
        //muss jeweils mit get gene/transcript abgefragt werden
        return eventlist;
    }

    @Override
    public List<EventDisplay> filter(SpliceEventFilter sef) {
        //TODO implement method for filtering data in grid > < and in between range
        List<EventDisplay> events = new LinkedList<>();
        String i1 = sef.getI1().toLowerCase();
        String i2 = sef.getI2().toLowerCase();
        String start = sef.getStart().toLowerCase();
        String stop = sef.getStop().toLowerCase();
        String type = sef.getType().toUpperCase();
        String pattern = sef.getPattern().toUpperCase();
        String sec = sef.getSec().toLowerCase();
        String acc = sef.getAcc();

        Iterator<EventDisplay> evit = eventlist.iterator();

        while (evit.hasNext()) {
            EventDisplay next = evit.next();
            if (!next.getI1().toLowerCase().contains(i1))
                continue;
            if (!next.getI2().toLowerCase().contains(i2))
                continue;
            if (!String.valueOf(next.getStart()).contains(start))
                continue;
            if (!String.valueOf(next.getStop()).contains(stop))
                continue;
            if (!String.valueOf(next.getType()).contains(type))
                continue;
            if (!next.getPattern().getId().contains(pattern))
                continue;
            if (!next.getSec().toString().toLowerCase().contains(sec))
                continue;
            events.add(next);
        }

        return events;
    }


    private List<EventDisplay> getEventsPerGene(Gene agene) {
        List<EventDisplay> tmp = new LinkedList<>();
        for (Map.Entry<String, Transcript> stringTranscriptEntry : agene.getHashmap_transcriptid().entrySet()) {
            String transcript1 = stringTranscriptEntry.getKey();
            for (Map.Entry<String, Transcript> transcriptEntry : agene.getHashmap_transcriptid().entrySet()) {
                String transcript2 = transcriptEntry.getKey();
                if (transcript1.equals(transcript2)) continue;
                Event tmpevent = dbq.getEvent(transcript1, transcript2);
                if (tmpevent == null)
                    continue;
                tmp.add(new EventDisplay(tmpevent.getI1(), tmpevent.getI2(), tmpevent.getStart(), tmpevent.getStop(), tmpevent.getType(), 0.0, new PatternEvent("P00000", "ENST000202", 1, 2), SecondaryStructure.HELIX));
            }
        }
        return tmp;
    }

}
