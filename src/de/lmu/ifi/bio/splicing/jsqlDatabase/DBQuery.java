package de.lmu.ifi.bio.splicing.jsqlDatabase;

import de.lmu.ifi.bio.splicing.interfaces.*;

public class DBQuery implements DatabaseQuery{

	@Override
	public Event getEvent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractExon getExon() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractGene getGene() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractTranscript getTranscript(String transcriptID) {
		DB_Backend db = new DB_Backend();
		String query = "Select start, stop, frame from Exon where transcriptId = "+transcriptID;
		db.
	}

}
