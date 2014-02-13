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
        insertTranscript(gene);
    }

    @Override
    public void insertTranscript(Gene gene) {
        HashMap<String[], List<Exon>> exons_with_transIds = new HashMap<>();
        StringBuilder insert = new StringBuilder("insert into Transcript values");
        boolean setComma = false;
        for (Map.Entry<String, Transcript> en : gene.getHashmap_transcriptid().entrySet()) {
            if (setComma) {
                insert.append(',');
            } else {
                setComma = true;
            }
            Transcript t = en.getValue();
            exons_with_transIds.put(new String[]{t.getTranscriptId(), t.getProteinId()}, t.getCds());
            insert.append('(').append(t.getTranscriptId()).append(',').append(t.getProteinId()).append(',').append(gene.getGeneId()).append(")");
        }
        try {
            db.executeUpdate(insert.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        insertExon(exons_with_transIds);
    }

    @Override
    public void insertExon(HashMap<String[], List<Exon>> exons) {
        StringBuilder insert = new StringBuilder("insert into Exon(start,stop,frame,transcriptId,proteinId) values");
        boolean setComma = false;
        for (Map.Entry<String[], List<Exon>> entry : exons.entrySet()) {
            String[] string = entry.getKey();
            List<Exon> list = entry.getValue();
            for (Exon exon : list) {
                if (setComma) {
                    insert.append(',');
                } else {
                    setComma = true;
                }
                insert.append('(').append(exon.getStart()).append(',').append(exon.getStop()).append(',').append(exon.getFrame()).append(',').append(string[0]).append(',').append(string[1]).append(')');
            }
        }

        try {
            db.executeUpdate(insert.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
