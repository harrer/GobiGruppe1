package de.lmu.ifi.bio.splicing.jsqlDatabase;

import de.lmu.ifi.bio.splicing.genome.*;
import java.util.HashMap;
import java.util.List;

public interface DatabaseUpdate {
    public void insertGene(Gene gene);

    public void insertTranscript(Gene gene);

    public void insertExon(HashMap<String,List<Exon>> exons);

    public void insertEvent(Event event);
}
