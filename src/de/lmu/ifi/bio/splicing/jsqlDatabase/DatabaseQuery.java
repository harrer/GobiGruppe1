package de.lmu.ifi.bio.splicing.jsqlDatabase;

import de.lmu.ifi.bio.splicing.interfaces.*;
import de.lmu.ifi.bio.splicing.genome.Event;

public interface DatabaseQuery {
	public Event getEvent();
	
	public AbstractExon getExon();
	
	public AbstractGene getGene(String geneID);
	
	/**
	 * @param proteinID may be null, transcriptID must be valid
	*/
	public AbstractTranscript  getTranscript(String transcriptID, String proteinID);
}
