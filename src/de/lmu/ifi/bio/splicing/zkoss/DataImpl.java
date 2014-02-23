package de.lmu.ifi.bio.splicing.zkoss;

import bsh.StringUtil;
import de.lmu.ifi.bio.splicing.genome.Event;
import de.lmu.ifi.bio.splicing.genome.Gene;
import de.lmu.ifi.bio.splicing.genome.Transcript;
import de.lmu.ifi.bio.splicing.io.GenomeSequenceExtractor;
import de.lmu.ifi.bio.splicing.jsqlDatabase.DBQuery;
import de.lmu.ifi.bio.splicing.zkoss.entity.EventDisplay;
import de.lmu.ifi.bio.splicing.zkoss.entity.PatternEvent;
import de.lmu.ifi.bio.splicing.zkoss.entity.SequenceEntity;
import de.lmu.ifi.bio.splicing.zkoss.entity.SpliceEventFilter;

import java.awt.image.RenderedImage;
import java.util.*;

/**
 * Created by Carsten on 13.02.14.
 */
public class DataImpl implements Data {
    private List<String> searchlist;
    private List<EventDisplay> eventlist;
    private ExonView ev;
    private DBQuery dbq;
    private Gene g;
    private EventDisplay selectedEvent;
    private RenderedImage bi;
    private SequenceEntity seqEntity;

    DataImpl() {
        dbq = new DBQuery();
        searchlist = new LinkedList<>();
        eventlist = new ArrayList<>();
        seqEntity = new SequenceEntity();
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
    public SequenceEntity prepareSequences(EventDisplay eventDisplay) {
        if (eventDisplay == null || !eventDisplay.equals(selectedEvent)) {
            setSelectedEvent(eventDisplay);
            calcSeqEntity();
            return seqEntity;
        }

        if (seqEntity == null)

        if (!seqEntity.getTranscriptid().equals(eventDisplay.getI1()))
            calcSeqEntity();

        return seqEntity;
    }

    private void calcSeqEntity() {
        //get transcripts i1 and i2 | t1 and t2
        String i1 = selectedEvent.getI1();
        String i2 = selectedEvent.getI2();
        Transcript t1 = g.getTranscriptByTranscriptId(i1);
        Transcript t2 = g.getTranscriptByTranscriptId(i2);

        String aa1 = GenomeSequenceExtractor.getProteinSequence(t1, g.getChromosome(), g.getStrand());
        String aa2 = GenomeSequenceExtractor.getProteinSequence(t2, g.getChromosome(), g.getStrand());

        //TODO VARSPLIC sequence implementation
        //TODO AA2 sequence implementation

        String sec = null, acc = null;
        StringBuilder prosite = new StringBuilder();
        List<PatternEvent> pelist = dbq.getPatternEventForTranscriptID(i1);
        int end = 0;
        for (PatternEvent patternEvent : pelist) {
            if (patternEvent.getStart() - prosite.length() > 0)
                prosite.append(new String(new char[patternEvent.getStart() - prosite.length()]).replace("\0", "-"));
            prosite.append(new String(new char[patternEvent.getStop() - patternEvent.getStart()]).replace("\0", "P"));
            end = patternEvent.getStop();
        }
        prosite.append(new String(new char[aa1.length() - end]).replace("\0", "-"));

        //TODO implement SECStructure Chars in String
        if (selectedEvent.getSec() != '\0')
            sec = new String(new char[aa1.length()]).replace("\0", String.valueOf(selectedEvent.getSec()));

        //TODO implement Prosite Chars in String
        if (selectedEvent.getAcc() != '\0')
            acc = new String(new char[aa1.length()]).replace("\0", String.valueOf(selectedEvent.getAcc()));

        if (sec != null) {
            seqEntity.setSec(sec);
        }
        seqEntity.setProsite(prosite.toString());
        seqEntity.setAa(aa1);
        if (acc != null) {
            seqEntity.setAcc(acc);
        }
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

    private void setSelectedEvent(EventDisplay eventDisplay) {
        g = eventDisplay.getCurGene();
        selectedEvent = eventDisplay;
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
