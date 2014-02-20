package de.lmu.ifi.bio.splicing.zkoss;

import de.lmu.ifi.bio.splicing.genome.Gene;
import de.lmu.ifi.bio.splicing.genome.Transcript;
import de.lmu.ifi.bio.splicing.jsqlDatabase.DBQuery;
import de.lmu.ifi.bio.splicing.zkoss.entity.EventDisplay;
import de.lmu.ifi.bio.splicing.zkoss.entity.SpliceEventFilter;

import java.awt.image.RenderedImage;
import java.util.*;

/**
 * Created by Carsten on 13.02.14.
 */
public class DataImpl implements Data {
    List<String> searchlist;
    List<EventDisplay> eventlist;
    ExonView ev;
    DBQuery dbq;
    Gene g;
    EventDisplay selectedEvent;
    RenderedImage bi;

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

        //add geneids to list
        List<Gene> genes = new LinkedList<>();
        for (String gene : geneslist) {
            genes.add(dbq.getGene(gene));
        }

        //Transcripts add by transcriptid
        boolean foundtranscriptid = false;
        List<Transcript> transcripts = new LinkedList<>();
        for (String transcriptid : transcriptslist) {
            for (Gene gene : genes) {
                if (gene.hasTranscriptID(transcriptid)) {
                    foundtranscriptid = true;
                    break;
                }
            }

            if (!foundtranscriptid) {
                transcripts.add(dbq.getTranscript(transcriptid));
            }

            foundtranscriptid = false;
        }


        //Transcripts add by proteinid
        for (String proteinid : proteinslist) {
            for (Gene gene : genes) {
                if (gene.hasProteinID(proteinid)) {
                    foundtranscriptid = true;
                    break;
                }
            }

            if (!foundtranscriptid) {
                transcripts.add(dbq.getTranscriptForProteinId(proteinid));
            }

            foundtranscriptid = false;
        }


        for (Gene gene : genes) {
            eventlist.addAll(getEventsPerGene(gene));
        }
        eventlist.addAll(getEventsPerTranscriptList(transcripts));

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
            if (!next.getPatternids().contains(pattern))
                continue;
//            if (!next.getSec().toString().toLowerCase().contains(sec))
//                continue;
            events.add(next);
        }

        return events;
    }

    @Override
    public RenderedImage renderImage(EventDisplay eventDisplay, int height, int width) {
        if (selectedEvent == null || !eventDisplay.equals(selectedEvent)) {
            if (g == null || !g.hasTranscriptID(eventDisplay.getI1())) {
//                g = dbq.getGeneForTranscriptID(eventDisplay.getI1()); //getI2 unnoetig da schon in Gene drinne ist (sonst kein SpliceEvent möglich)
                g = eventDisplay.getCurGene();
                ev = new ExonView(g, height, width);
                bi = ev.renderExonView();
            } else
                selectedEvent = eventDisplay;
        }
        return bi;
    }

    @Override
    public Gene getSelectedGene(EventDisplay eventDisplay) {
        if (selectedEvent == null || !eventDisplay.equals(selectedEvent)) {
            if (g == null || !g.hasTranscriptID(eventDisplay.getI1()))
//                g = dbq.getGeneForTranscriptID(eventDisplay.getI1());
                g = eventDisplay.getCurGene();
            else
                selectedEvent = eventDisplay;
        }
        return g;
    }

    private List<EventDisplay> getEventsPerGene(Gene agene) {
        List<EventDisplay> tmp = new LinkedList<>();
        for (Map.Entry<String, Transcript> stringTranscriptEntry : agene.getHashmap_transcriptid().entrySet()) {
            String transcript1 = stringTranscriptEntry.getKey();
            for (Map.Entry<String, Transcript> transcriptEntry : agene.getHashmap_transcriptid().entrySet()) {
                String transcript2 = transcriptEntry.getKey();
                if (transcript1.equals(transcript2)) continue;
                EventDisplay tmpevent = dbq.getEventDisplay(transcript1, transcript2);
                if (tmpevent == null)
                    continue;
                tmpevent.setCurGene(agene);
                tmp.add(tmpevent);
            }
        }
        return tmp;
    }

    private List<EventDisplay> getEventsPerTranscriptList(List<Transcript> liste) {
        List<EventDisplay> returnlist = new LinkedList<>();
        HashMap<String, Gene> tmpGenes = new HashMap<>();
        String curgeneid;

        //Clustering
        for (Transcript transcript : liste) {
            curgeneid = dbq.getGeneIDForTranscriptID(transcript.getTranscriptId());
            Gene tmpGene = tmpGenes.get(curgeneid);
            if (tmpGene == null) {
                tmpGene = new Gene(curgeneid, dbq.getChrForTranscriptID(transcript.getTranscriptId()), dbq.getStrandForTranscriptID(transcript.getTranscriptId()));
            } else
                System.out.println(transcript.getTranscriptId());
            tmpGene.addTranscript(transcript);
            tmpGenes.put(curgeneid, tmpGene);
        }

        //GetEvents
        for (Gene gene : tmpGenes.values()) {
            returnlist.addAll(getEventsPerGene(gene));
        }

        return returnlist;
    }

}
