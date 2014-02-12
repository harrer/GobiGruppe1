package de.lmu.ifi.bio.splicing.jsqlDatabase;

import de.lmu.ifi.bio.splicing.interfaces.*;
import de.lmu.ifi.bio.splicing.genome.*;

public interface DatabaseQuery {
	public Event getEvent();
	
	public Exon getExon();
	
	public Gene getGene(String geneID);
	
	/**
	 * @param proteinID may be null, transcriptID must be valid
	*/
	public Transcript getTranscript(String transcriptID, String proteinID);
}
