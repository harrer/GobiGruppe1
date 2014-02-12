package de.lmu.ifi.bio.splicing.jsqlDatabase;

public interface DatabaseQuery {
	
	public Event getEvent();
	
	public Exon getExon();
	
	public Gene getGene();
	
	public Transcript getTranscript();
	

}
