package de.lmu.ifi.bio.splicing.zkoss;

import de.lmu.ifi.bio.splicing.genome.Event;
import de.lmu.ifi.bio.splicing.genome.Gene;
import de.lmu.ifi.bio.splicing.genome.Transcript;
import de.lmu.ifi.bio.splicing.homology.ModelPDB_onENSP;
import de.lmu.ifi.bio.splicing.io.GenomeSequenceExtractor;
import de.lmu.ifi.bio.splicing.jsqlDatabase.DBQuery;
import de.lmu.ifi.bio.splicing.zkoss.entity.EventDisplay;
import de.lmu.ifi.bio.splicing.zkoss.entity.ModelSequenceEntity;
import de.lmu.ifi.bio.splicing.zkoss.entity.PatternEvent;
import de.lmu.ifi.bio.splicing.zkoss.entity.SequenceEntity;
import de.lmu.ifi.bio.splicing.zkoss.entity.SpliceEventFilter;

import java.awt.image.RenderedImage;
import java.rmi.server.ServerRef;
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
    private ModelPDB_onENSP model;
    private List<Event> selectedEventlist1;
    private List<Event> selectedEventlist2;

    DataImpl() {
        dbq = new DBQuery();
        searchlist = new LinkedList<>();
        eventlist = new ArrayList<>();
        seqEntity = new SequenceEntity();
        model = new ModelPDB_onENSP(true);
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
    public ModelSequenceEntity getModel(EventDisplay eventDisplay) {
        String enst1 = eventDisplay.getI1();
        String enst2 = eventDisplay.getI2();

        return new ModelSequenceEntity(model.displayModels(enst1, selectedEventlist1), model.displayModels(enst2, selectedEventlist2));
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
        String geneid = sef.getGeneid().toLowerCase();
        String i1 = sef.getI1().toLowerCase();
        String i2 = sef.getI2().toLowerCase();
        String start = sef.getStart().toLowerCase();
        String stop = sef.getStop().toLowerCase();
        String type = sef.getType().toUpperCase();
        String pattern = sef.getPattern().toUpperCase();
        String sec = sef.getSec().toUpperCase();
        String acc = sef.getAcc().toUpperCase();
        System.out.println(sec + " " + acc);

        Iterator<EventDisplay> evit = eventlist.iterator();

        while (evit.hasNext()) {
            EventDisplay next = evit.next();

            if (!next.getCurGene().getGeneId().contains(geneid)) continue;
            if (!next.getI1().toLowerCase().contains(i1)) continue;
            if (!next.getI2().toLowerCase().contains(i2)) continue;
            if (!String.valueOf(next.getStart()).contains(start)) continue;
            if (!String.valueOf(next.getStop()).contains(stop)) continue;
            if (!String.valueOf(next.getType()).contains(type)) continue;
            if (!next.getPatternids().contains(pattern)) continue;
            if (!(String.valueOf(next.getStartSS()).contains(sec))) continue;
            if (!(String.valueOf(next.getStopSS()).contains(sec))) continue;
            if (!(String.valueOf(next.getStopAcc()).contains(acc))) continue;
            if (!(String.valueOf(next.getStartAcc()).contains(acc))) continue;

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
                int hei = g.getHashmap_transcriptid().size() * 60; //px

                if (hei < height)
                    hei = height;
                ev = new ExonView(g, hei, width);
                bi = ev.renderExonView();
            } else
                selectedEvent = eventDisplay;
        }
        return bi;
    }

    @Override
    public SequenceEntity prepareSequences(EventDisplay eventDisplay) {
        if (eventDisplay == null || !eventDisplay.equals(selectedEvent) || seqEntity == null || !seqEntity.getTranscriptid().equals(eventDisplay.getI1())) {
            setSelectedEvent(eventDisplay);
            calcSeqEntity();
            return seqEntity;
        }

        return seqEntity;
    }

    private void calcSeqEntity() {
        //get transcripts i1 and i2 | t1 and t2
        String i1 = selectedEvent.getI1();
        String i2 = selectedEvent.getI2();
        seqEntity.setTranscriptid(i1);
        Transcript t1 = g.getTranscriptByTranscriptId(i1);
        Transcript t2 = g.getTranscriptByTranscriptId(i2);

        String aa1 = GenomeSequenceExtractor.getProteinSequence(t1, g.getChromosome(), g.getStrand());
        String aa2 = GenomeSequenceExtractor.getProteinSequence(t2, g.getChromosome(), g.getStrand());

        //modified Strings ( with events included )
        StringBuilder aa1_mod = new StringBuilder(), aa2_mod = new StringBuilder(), varsplic = new StringBuilder();
        selectedEventlist1 = dbq.getEvent(i1, i2);
        selectedEventlist2 = dbq.getEvent(i2, i1); //brauchen beide da inserts nur als start = pos, stop = pos-1 gespeichert sind --> länge unbekannt
        Iterator<Event> it1 = selectedEventlist1.iterator();
        Iterator<Event> it2 = selectedEventlist2.iterator();

        int old1 = 0;
        int old2 = 0;

        while (it1.hasNext() && it2.hasNext()) {
            Event e1 = it1.next();
            Event e2 = it2.next();

            try {
                aa1_mod.append(aa1.substring(old1, e1.getStart()));
                aa2_mod.append(aa2.substring(old2, e2.getStart()));
                if (Math.min(e1.getStart(), e2.getStart()) - Math.max(old1, old2) > 0)
                    varsplic.append(new String(new char[Math.min(e1.getStart(), e2.getStart()) - Math.max(old1, old2)]).replace("\0", "*"));
            } catch (Exception e) {
                System.err.printf("[calcSeqEntity]: fucked up spliceannotation!%n");
            }
            int magnesium = 1;
            int magnesium2 = 1;
            if (e1.getStop() >= aa1.length())
                magnesium = aa1.length() - e1.getStop();
            if (e2.getStop() >= aa2.length())
                magnesium2 = aa2.length() - e2.getStop();
            switch (e1.getType()) {
                case 'D': //wenn in e1 deletion rest analog
                    varsplic.append(new String(new char[e1.getStop() - e1.getStart() + magnesium]).replace("\0", "D"));
                    aa1_mod.append(aa1.substring(e1.getStart(), e1.getStop() + magnesium));
                    aa2_mod.append(new String(new char[e1.getStop() - e1.getStart() + magnesium]).replace("\0", "-"));
                    break;
                case 'R':
                    if (e1.getStop() - e1.getStart() < e2.getStop() - e2.getStart()) {
                        aa1_mod.append(new String(new char[(e2.getStop() - e2.getStart()) - (e1.getStop() - e1.getStart())]).replace("\0", "-"));
                        varsplic.append(new String(new char[e2.getStop() - e2.getStart() + magnesium2]).replace("\0", "R"));
                    } else {
                        aa2_mod.append(new String(new char[(e1.getStop() - e1.getStart()) - (e2.getStop() - e2.getStart())]).replace("\0", "-"));
                        varsplic.append(new String(new char[e1.getStop() - e1.getStart() + magnesium]).replace("\0", "R"));
                    }

                    aa1_mod.append(aa1.substring(e1.getStart(), e1.getStop() + magnesium));
                    aa2_mod.append(aa2.substring(e2.getStart(), e2.getStop() + magnesium2));
                    break;
                case 'I':
                    varsplic.append(new String(new char[e2.getStop() - e2.getStart() + magnesium]).replace("\0", "I"));
                    aa1_mod.append(new String(new char[e2.getStop() - e2.getStart() + magnesium]).replace("\0", "-"));
                    aa2_mod.append(aa2.substring(e2.getStart(), e2.getStop() + magnesium2));
                    break;
            }

            old1 = e1.getStop() + magnesium;
            old2 = e2.getStop() + magnesium2;
        }

        aa1_mod.append(aa1.substring(old1, aa1.length()));
        aa2_mod.append(aa2.substring(old2, aa2.length()));
        if (aa1.length() - Math.max(old1, old2) > 0)
            varsplic.append(new String(new char[aa1.length() - old1]).replace("\0", "*"));

        //Prosite Pattern String
        String sec = null, acc = null;
        StringBuilder prosite = new StringBuilder();
        List<PatternEvent> pelist = dbq.getPatternEventForTranscriptID(i1); //ist sortiert nach startpos
        int end;
        for (PatternEvent patternEvent : pelist) {
            prosite.append(String.format("%s\n", patternEvent.getId()));
            end = 0;
            if (patternEvent.getStart() - end - 1 > 0)
                prosite.append(new String(new char[patternEvent.getStart() - end - 1]).replace("\0", "-"));
            prosite.append(new String(new char[patternEvent.getStop() - patternEvent.getStart() + 1]).replace("\0", "P"));
            end = patternEvent.getStop();
            prosite.append(new String(new char[aa1.length() - end]).replace("\0", "-"));
            prosite.append("\n");
        }

        //TODO implement SECStructure Chars in String
        if (selectedEvent.getSecondaryStructure() != '\0')
            sec = new String(new char[aa1.length()]).replace("\0", String.valueOf(selectedEvent.getSecondaryStructure()));

        //TODO implement Accessibility Chars in String
        if (selectedEvent.getAccessibility() != '\0')
            acc = new String(new char[aa1.length()]).replace("\0", String.valueOf(selectedEvent.getAccessibility()));

        if (sec != null) {
            seqEntity.setSec(sec);
        }

        seqEntity.setAa1(aa1_mod.toString());
        seqEntity.setAa2(aa2_mod.toString());
        seqEntity.setVarsplic(varsplic.toString());
        seqEntity.setProsite(prosite.toString());
        if (acc != null) {
            seqEntity.setAcc(acc);
        }
        seqEntity.setAa1raw(aa1);
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
                List<EventDisplay> tmpevent = dbq.getEventDisplay(transcript1, transcript2);
                if (tmpevent.isEmpty())
                    continue;
                for (EventDisplay eventDisplay : tmpevent) {
                    eventDisplay.setCurGene(agene);
                }
                tmp.addAll(tmpevent);
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
