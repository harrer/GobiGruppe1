package de.lmu.ifi.bio.splicing.jsqlDatabase;

import de.lmu.ifi.bio.splicing.genome.*;

public interface DatabaseUpdate {
    public void insertGene(Gene gene);

    public void insertTranscript(Transcript transcript, String geneId);

    public void insertExon(Exon exon, Transcript transcript);

    public void insertEvent(Event event);
}
