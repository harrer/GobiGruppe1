package de.lmu.ifi.bio.splicing.interfaces;

import de.lmu.ifi.bio.splicing.genome.*;
import de.lmu.ifi.bio.splicing.zkoss.entity.PatternEvent;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public interface DatabaseUpdate {
    public void insertGene(Gene gene);

    public void insertTranscript(Gene gene);

    public void insertExon(HashMap<String,List<Exon>> exons);

    public void insertEvent(Event event);

    public void insertPattern(Pattern pattern);

    public void insertPatternEvent(PatternEvent patternEvent);

    public void insertEventSet(Set<Event> events);
    
    public void insertPDB_Transcript(HashMap<String, ArrayList<String>> map);

    public void updateEvent(String isoform1, String isoform2, long start, long stop, String[] column, String[] values);
}
