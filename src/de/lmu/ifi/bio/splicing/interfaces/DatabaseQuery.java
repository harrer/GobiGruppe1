package de.lmu.ifi.bio.splicing.interfaces;

import de.lmu.ifi.bio.splicing.interfaces.*;
import de.lmu.ifi.bio.splicing.genome.*;
import de.lmu.ifi.bio.splicing.zkoss.entity.EventDisplay;
import de.lmu.ifi.bio.splicing.zkoss.entity.PatternEvent;


import java.util.List;

public interface DatabaseQuery {
    public Event getEvent();

    /**
     * Kann nach Gene Ids, Transcript Ids und Protein Ids suchen können
     *
     * @param keyword
     * @return
     */
    public List<String> search(String keyword);

    public List<String> findAllGenes();

    public List<String> findAllProteins();

    public List<String> findAllTranscripts();

    public String getChrForTranscriptID(String transcriptid);

    public boolean getStrandForTranscriptID(String transcriptid);

    public List<Transcript> findTranscriptsForKeyword(String keyword);

    public List<String> findTranscriptIDsForKeyword(String keyword);

    /**
     * Kann sich Events zurückgeben lassen
     *
     * @param isoform1 TranscriptID 1
     * @param isoform2 TranscriptID 2
     * @return new Event()
     */
    public List<Event> getEvent(String isoform1, String isoform2);

    public List<EventDisplay> getEventDisplay(String isoform1, String isoform2);

    public List<Event> getEvents(String geneid);

    public Gene getGene(String geneID);

    public Transcript getTranscript(String transcriptID);

    public Gene getGeneForTranscriptID(String transcriptid);

    public String getGeneIDForTranscriptID(String transcriptid);

    public Transcript getTranscriptForProteinId(String proteinId);

    public List<PatternEvent> getPatternEventForTranscriptID(String transcriptid);
}
