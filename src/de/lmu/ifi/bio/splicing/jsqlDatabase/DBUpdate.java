package de.lmu.ifi.bio.splicing.jsqlDatabase;

import java.sql.SQLException;

import de.lmu.ifi.bio.splicing.genome.Event;
import de.lmu.ifi.bio.splicing.genome.Exon;
import de.lmu.ifi.bio.splicing.genome.Gene;
import de.lmu.ifi.bio.splicing.genome.Transcript;

/**
 * Created by uhligc on 12.02.14.
 */
public class DBUpdate implements DatabaseUpdate {

    @Override
    public void insertGene(Gene gene) {
    	DB_Backend db = new DB_Backend();
    	String insert = "insert into Gene values(" + gene.getGeneId() +","+ gene.getChromosome()+"," + gene.getStrand() +")";
    	try {
			db.executeUpdate(insert);
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }

    @Override
    public void insertTranscript(Transcript transcript, String geneId) {
    	DB_Backend db = new DB_Backend();
    	String insert = "insert into Transcript values(" + transcript.getTranscriptId()+"," + transcript.getProteinId() +"," + geneId +")";
    	try {
			db.executeUpdate(insert);
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }


    @Override
    public void insertEvent(Event event) {// acc, sec  ??
    	DB_Backend db = new DB_Backend();
    	String insert = "insert into Exon(start,stop,frame,transcriptId,proteinId) values(" + exon.getStart() +"," + exon.getStop() +"," + exon.getFrame() +"," +transcript.getTranscriptId()+"," +transcript.getProteinId()+")";
    	try {
			db.executeUpdate(insert);
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }

	@Override
	public void insertExon(Exon exon, Transcript transcript) {
		DB_Backend db = new DB_Backend();
    	String insert = "insert into Exon(start,stop,frame,transcriptId,proteinId) values(" + exon.getStart() +"," + exon.getStop() +"," + exon.getFrame() +"," +transcript.getTranscriptId()+"," +transcript.getProteinId()+")";
    	try {
			db.executeUpdate(insert);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}