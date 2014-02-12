package de.lmu.ifi.bio.splicing.jsqlDatabase;

import de.lmu.ifi.bio.splicing.interfaces.*;

public interface DatabaseQuery {
	
	public Event getEvent();
	
	public AbstractExon getExon();
	
	public AbstractGene getGene();
	
	public AbstractTranscript  getTranscript();
	

}
