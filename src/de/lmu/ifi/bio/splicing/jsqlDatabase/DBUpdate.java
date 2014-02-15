package de.lmu.ifi.bio.splicing.jsqlDatabase;

import de.lmu.ifi.bio.splicing.genome.*;
import de.lmu.ifi.bio.splicing.interfaces.DatabaseUpdate;

import java.sql.SQLException;

import de.lmu.ifi.bio.splicing.zkoss.entity.PatternEvent;

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
        String insert = "insert into Event(start,stop,isoform1,isoform2,type) values(" + event.getStart() + "," + event.getStop() + ",'" + event.getI1() + "','" + event.getI2() + "','" + event.getType() + "')";
        try {
            db.executeUpdate(insert);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insertPattern(Pattern pattern) {
        String insert = null;

        //for insert
        boolean specialquote = false;
        if (pattern.hasDescription()) {
            if (pattern.getDescription().contains("'"))
                specialquote = true;
        }

        if (!pattern.isProfile()) { //wenn kein
            if (pattern.hasLink()) //contructor only link having when description too -> description must be included
                insert = String.format("insert into Pattern values('%s','%s','%s','%s','%s', 1)", pattern.getId(), pattern.getPattern(), pattern.getDescription(), pattern.getLink(), pattern.getName());
            else if (pattern.hasDescription())
                if (!specialquote)
                    insert = String.format("insert into Pattern (id,pattern,name,description, type) values('%s','%s','%s','%s',1)", pattern.getId(), pattern.getPattern(), pattern.getName(), pattern.getDescription());
                else
                    insert = String.format("insert into Pattern (id,pattern,name,description, type) values('%s','%s','%s',\"%s\",1)", pattern.getId(), pattern.getPattern(), pattern.getName(), pattern.getDescription());
            else {
                insert = String.format("insert into Pattern (id,pattern,name,type) values('%s','%s','%s',1)", pattern.getId(), pattern.getPattern(), pattern.getName());
            }
        } else {
            if (pattern.hasDescription()) {
                if (!specialquote)
                    insert = String.format("insert into Pattern (id,name,description,type) values('%s','%s','%s',0)", pattern.getId(), pattern.getName(), pattern.getDescription());
                else
                    insert = String.format("insert into Pattern (id,name,description,type) values('%s','%s',\"%s\",0)", pattern.getId(), pattern.getName(), pattern.getDescription());
            } else {
                insert = String.format("insert into Pattern (id,name,type) values('%s','%s',0)", pattern.getId(), pattern.getName());
            }
        }

        try {
            db.executeUpdate(insert);
        } catch (SQLException e) {
            System.err.printf("[DBUpdate]: insertPattern: %s%n", insert);
        }
    }

    @Override
    public void insertGene(Gene gene) {
        String insert = "insert into Gene values('" + gene.getGeneId() + "','" + gene.getChromosome() + "'," + gene.getStrand() + ")";
        try {
            db.executeUpdate(insert);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        insertTranscript(gene);
    }

    @Override
    public void insertTranscript(Gene gene) {
        HashMap<String, List<Exon>> exons_with_transIds = new HashMap<>();
        StringBuilder insert = new StringBuilder("insert into Transcript values");
        boolean setComma = false;
        for (Map.Entry<String, Transcript> en : gene.getHashmap_transcriptid().entrySet()) {
            if (setComma) {
                insert.append(',');
            } else {
                setComma = true;
            }
            Transcript t = en.getValue();
            exons_with_transIds.put(t.getTranscriptId(), t.getCds());
            insert.append("('").append(t.getTranscriptId()).append("','").append(t.getProteinId()).append("','").append(gene.getGeneId()).append("')");
        }
        try {
            db.executeUpdate(insert.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        insertExon(exons_with_transIds);
    }

    @Override
    public void insertExon(HashMap<String, List<Exon>> exons) {
        StringBuilder insert = new StringBuilder("insert into Exon(start,stop,frame,transcriptId) values");
        boolean setComma = false;
        for (Map.Entry<String, List<Exon>> entry : exons.entrySet()) {
            String string = entry.getKey();
            List<Exon> list = entry.getValue();
            for (Exon exon : list) {
                if (setComma) {
                    insert.append(',');
                } else {
                    setComma = true;
                }
                insert.append("('").append(exon.getStart()).append("','").append(exon.getStop()).append("','").append(exon.getFrame()).append("','").append(string).append("')");
            }
        }
        try {
            db.executeUpdate(insert.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Transcript t1 = new Transcript("ENST1", "ENSP1");
        t1.addExon(new Exon(12, 32, 1));
        t1.addExon(new Exon(412, 532, 3));
        t1.addExon(new Exon(6122, 7232, 2));
        Transcript t2 = new Transcript("ENST2", "ENSP2");
        t2.addExon(new Exon(112, 232, 4));
        t2.addExon(new Exon(516, 562, 2));
        t2.addExon(new Exon(882, 932, 3));
        Gene gene = new Gene("ENSG012", "12", true);
        gene.addTranscript(t1);
        gene.addTranscript(t2);
        DBUpdate db = new DBUpdate();
        db.insertGene(gene);
    }
}
