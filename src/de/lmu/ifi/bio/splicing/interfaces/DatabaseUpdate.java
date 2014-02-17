package de.lmu.ifi.bio.splicing.interfaces;

import de.lmu.ifi.bio.splicing.genome.*;
import de.lmu.ifi.bio.splicing.zkoss.entity.PatternEvent;

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
}
