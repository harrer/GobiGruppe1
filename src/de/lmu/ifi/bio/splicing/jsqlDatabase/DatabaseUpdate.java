package de.lmu.ifi.bio.splicing.jsqlDatabase;

import de.lmu.ifi.bio.splicing.genome.Event;
import de.lmu.ifi.bio.splicing.genome.Gene;
import de.lmu.ifi.bio.splicing.genome.Transcript;

public interface DatabaseUpdate {
    public void insertGene(Gene gene);

    public void insertTranscript(Transcript transcript);

    public void insertEvent(Event event, Transcript transcript);

    public void insertEvent(Event event);
}
