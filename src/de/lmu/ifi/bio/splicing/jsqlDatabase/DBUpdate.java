package de.lmu.ifi.bio.splicing.jsqlDatabase;

import de.lmu.ifi.bio.splicing.genome.*;
import de.lmu.ifi.bio.splicing.homology.Modify_PDB_Mapping;
import de.lmu.ifi.bio.splicing.interfaces.DatabaseUpdate;
import de.lmu.ifi.bio.splicing.zkoss.entity.PatternEvent;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

import de.lmu.ifi.bio.splicing.zkoss.entity.PatternEvent;

import java.io.IOException;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        String insert;

        //for insert
        boolean specialquote = false;
        if (pattern.hasDescription()) {
            if (pattern.getDescription().contains("'")) {
                specialquote = true;
            }
        }

        if (!pattern.isProfile()) { //wenn kein profile
            if (pattern.hasLink()) //contructor only link having when description too -> description must be included
            {
                insert = String.format("insert into Pattern values('%s','%s','%s','%s','%s', 1)", pattern.getId(), pattern.getPattern(), pattern.getDescription(), pattern.getLink(), pattern.getName());
            } else if (pattern.hasDescription()) {
                if (!specialquote) {
                    insert = String.format("insert into Pattern (id,pattern,name,description, type) values('%s','%s','%s','%s',1)", pattern.getId(), pattern.getPattern(), pattern.getName(), pattern.getDescription());
                } else {
                    insert = String.format("insert into Pattern (id,pattern,name,description, type) values('%s','%s','%s',\"%s\",1)", pattern.getId(), pattern.getPattern(), pattern.getName(), pattern.getDescription());
                }
            } else {
                insert = String.format("insert into Pattern (id,pattern,name,type) values('%s','%s','%s',1)", pattern.getId(), pattern.getPattern(), pattern.getName());
            }
        } else {
            if (pattern.hasDescription()) {
                if (!specialquote) {
                    insert = String.format("insert into Pattern (id,name,description,type) values('%s','%s','%s',0)", pattern.getId(), pattern.getName(), pattern.getDescription());
                } else {
                    insert = String.format("insert into Pattern (id,name,description,type) values('%s','%s',\"%s\",0)", pattern.getId(), pattern.getName(), pattern.getDescription());
                }
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
    public void insertPatternEvent(PatternEvent patternEvent) {
        String insert = String.format("insert into PatternEvent (fk_pattern_id,transcriptid,start,stop) values('%s','%s',%s,%s)", patternEvent.getId(), patternEvent.getTranscriptid(), patternEvent.getStart(), patternEvent.getStop());
        try {
            db.executeUpdate(insert);
        } catch (SQLException e) {
            System.err.printf("[DBUpdate]: insertPatternEvent: %s%n", insert);
        }
    }

    @Override
    public void insertEventSet(Set<Event> eventSet) {
        if (eventSet.size() == 0)
            return;
        StringBuilder sb = new StringBuilder("insert into Event(start,stop,isoform1,isoform2,type) values");
        for (Event event : eventSet) {
            sb.append("(" + event.getStart() + "," + event.getStop() + ",'" + event.getI1() + "','" + event.getI2() + "','" + event.getType() + "'),\n");
        }
        sb.replace(sb.length() - 2, sb.length(), "");
        try {
            db.executeUpdate(sb.toString());
        } catch (SQLException e) {
            System.err.printf("[DBUpdate]: %s%n", sb.toString());
            e.printStackTrace();
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

    @Override
    public void insertPDB_Transcript(HashMap<String, ArrayList<String>> map) {
        StringBuilder insert = new StringBuilder("insert into transcript_has_pdbs(transcriptId, pdbId) values");
        boolean setComma = false;
        HashMap<String, Boolean> hm = new HashMap<>();
        for (Map.Entry<String, ArrayList<String>> entry : map.entrySet()) {
            ArrayList<String> arrayList = entry.getValue();
            for (String string1 : arrayList) {
                if (setComma) {
                    insert.append(',');
                } else {
                    setComma = true;
                }
                hm.put(string1, Boolean.TRUE);
                insert.append("('").append(string1).append("','").append(string1).append("')");
            }
        }
        try {
            db.executeUpdate(insert.toString());
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        insert = new StringBuilder("insert into PDB(pdbId) values");
        setComma = false;
        for (Map.Entry<String, Boolean> entry : hm.entrySet()) {
            String string = entry.getKey();
            if (setComma) {
                insert.append(',');
            } else {
                setComma = true;
            }
            insert.append("('").append(string).append("')");
        }
        try {
            db.executeUpdate(insert.toString());
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void updateEvent(String isoform1, String isoform2, long start, long stop, String[] column, String[] values) {
        StringBuilder update = new StringBuilder("update Event set ");
        int amount = Math.min(column.length, values.length);
        for (int i = 0; i < amount; i++) {
            if (i < amount - 1)
                update.append(column[i]).append(" = '").append(values[i]).append("', ");
            else
                update.append(column[i]).append(" = '").append(values[i]).append("' ");
        }
        update.append("where isoform1 = '").append(isoform1).append("' and isoform2 = '").append(isoform2)
                .append("' and start = ").append(start).append(" and stop = ").append(stop);
        try {
            db.executeUpdate(update.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void fullUpdateEvent(Event event) {
        updateEvent(event.getI1(), event.getI2(), event.getStart(), event.getStop(), new String[]{"acc", "sec", "startAcc", "stopAcc", "startSS", "stopSS"},
                new String[]{String.valueOf(event.getAccessibility()), String.valueOf(event.getSecondaryStructure()), String.valueOf(event.getStartAcc()),
                String.valueOf(event.getStopAcc()), String.valueOf(event.getStartSS()), String.valueOf(event.getStopSS())});
    }


    public static void main(String[] args) throws IOException {
        DBUpdate db = new DBUpdate();
//        ShortenPDB_Mapping mapping = new ShortenPDB_Mapping("/home/proj/biocluster/praktikum/genprakt-ws13/abgaben/assignment2/harrer/2_e_enriched");
//        db.insertPDB_Transcript(mapping.getENSP_PDBmap());
    }
}
