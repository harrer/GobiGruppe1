package de.lmu.ifi.bio.splicing.jsqlDatabase;

import de.lmu.ifi.bio.splicing.genome.Exon;
import de.lmu.ifi.bio.splicing.genome.Gene;
import de.lmu.ifi.bio.splicing.genome.Transcript;

public interface DatabaseQuery {
	public Event getEvent();
	
	public Exon getExon();
	
	public Gene getGene();
	
	public Transcript getTranscript();
}
