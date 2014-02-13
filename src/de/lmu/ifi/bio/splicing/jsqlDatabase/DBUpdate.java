package de.lmu.ifi.bio.splicing.jsqlDatabase;

import java.sql.SQLException;

import de.lmu.ifi.bio.splicing.genome.Event;
import de.lmu.ifi.bio.splicing.genome.Exon;
import de.lmu.ifi.bio.splicing.genome.Gene;
import de.lmu.ifi.bio.splicing.genome.Transcript;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by uhligc on 12.02.14.
 */
public class DBUpdate implements DatabaseUpdate {

    private DB_Backend db;

    public DBUpdate() {
        db = new DB_Backend();
    }

    @Override
    public void insertEvent(Event event) {// acc, sec  ??
        String insert = "";// "insert into Event(start,stop,frame,transcriptId,proteinId) values(" + exon.getStart() +"," + exon.getStop() +"," + exon.getFrame() +"," +transcript.getTranscriptId()+"," +transcript.getProteinId()+")";
        try {
            db.executeUpdate(insert);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insertGene(Gene gene) {
        String insert = "insert into Gene values(" + gene.getGeneId() + "," + gene.getChromosome() + "," + gene.getStrand() + ")";
        try {
            db.executeUpdate(insert);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insertTranscript(Gene gene) {
        HashMap<String,List<Exon>> exons_with_transIds = new HashMap<>();
        StringBuilder insert =  new StringBuilder("insert into Transcript values");
        boolean setComma = false;
        for (Map.Entry<String, Transcript> en : gene.getHashmap_transcriptid().entrySet()) {
            Transcript t = en.getValue();
            exons_with_transIds.put(t.getTranscriptId(), t.getCds());
            insert.append('(').append(t.getTranscriptId()).append(',').append(t.getProteinId()).append(',').append(gene.getGeneId()).append("),");
        }
        
        try {
            db.executeUpdate(insert);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insertExon(HashMap<String,List<Exon>> exons) {
        String insert = "insert into Exon(start,stop,frame,transcriptId,proteinId) values(" + exon.getStart() + "," + exon.getStop() + "," + exon.getFrame() + "," + transcript.getTranscriptId() + "," + transcript.getProteinId() + ")";
        try {
            db.executeUpdate(insert);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
