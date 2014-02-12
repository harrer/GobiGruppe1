package de.lmu.ifi.bio.splicing.jsqlDatabase;

import java.sql.SQLException;
import de.lmu.ifi.bio.splicing.interfaces.*;
import de.lmu.ifi.bio.splicing.genome.*;

public class DBQuery implements DatabaseQuery{

	@Override
	public Event getEvent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Exon getExon() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Gene getGene(String geneID) {
		DB_Backend db = new DB_Backend();
		String query = "select chromosome, strand from Gene where geneId = '"+geneID+"'";
		Object[][] result = null;
		try {
			result = db.select(query);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		Gene gene = new Gene(geneID, (String)result[0][0], (boolean)result[0][1]);
		query = "Select transcriptId, proteinId from Transcript where geneId = '"+geneID+"'";
		try {
			result = db.select(query);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		for (int i = 0; i < result.length; i++) {
			gene.addTranscript(getTranscript((String)result[i][0], (String)result[i][1]));
		}
		
		return gene;
	}

	@Override
	public Transcript getTranscript(String transcriptID, String proteinID) {
		DB_Backend db = new DB_Backend();
		String query = "Select start, stop, frame from Exon where transcriptId = '"+transcriptID+"'";
		Object[][] result = null;
		try {
			result = db.select(query);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		Transcript transcript = new Transcript(transcriptID, proteinID);
		for (int i = 0; i < result.length; i++) {
			Exon ex = new Exon((long)result[i][0],(long)result[i][1],(int)result[i][2]);
			transcript.addExon(ex);
		}
		return transcript;
	}
	
	public static void main(String[] args) {
		DatabaseQuery dbq = new DBQuery();
		Gene g = dbq.getGene("ENSG1");
		System.out.println(g);
	}
}
