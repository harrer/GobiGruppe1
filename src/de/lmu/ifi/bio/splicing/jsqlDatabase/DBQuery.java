package de.lmu.ifi.bio.splicing.jsqlDatabase;

import de.lmu.ifi.bio.splicing.interfaces.DatabaseQuery;

import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import de.lmu.ifi.bio.splicing.genome.*;
import de.lmu.ifi.bio.splicing.zkoss.entity.EventDisplay;
import de.lmu.ifi.bio.splicing.zkoss.entity.PatternEvent;

public class DBQuery implements DatabaseQuery {
    public DB_Backend db = new DB_Backend();

    public DBQuery() {
        db = new DB_Backend();
    }

    @Override
    public Event getEvent() {
        //TODO getEvent() -> ohne Parameter
        return null;
    }

    @Override
    public List<String> search(String keyword) {
        if (keyword == null)
            return findAllGenes();
        if (keyword.isEmpty())
            return findAllGenes();

        //case-insensitivity
        keyword = keyword.toLowerCase();

        String limit = "";

        if (keyword.length() < 3)
            limit = " LIMIT 0, 100";

        String query;
        Object[] result;
        List<String> liste = new LinkedList<>();

        query = "SELECT geneid FROM Gene WHERE lower(geneid) REGEXP '" + keyword + "'" + limit + ";";
        result = null;
        try {
            result = db.select_oneColumn(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (result != null && result.length > 0) {
            for (Object object : result) {
                liste.add((String) object);
            }
        }

        db = new DB_Backend();
        query = "SELECT transcriptid FROM Transcript WHERE lower(transcriptid) REGEXP '" + keyword + "'" + limit + ";";
        result = null;
        try {
            result = db.select_oneColumn(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (result != null && result.length > 0) {
            for (Object object : result) {
                liste.add((String) object);
            }
        }

        query = "SELECT proteinid FROM Transcript WHERE lower(proteinid) REGEXP '" + keyword + "'" + limit + ";";
        result = null;
        try {
            result = db.select_oneColumn(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (result != null && result.length > 0) {
            for (Object object : result) {
                liste.add((String) object);
            }
        }

        return liste;
    }

    @Override
    public List<String> findAllGenes() {
        String query = "SELECT geneid FROM gobi1.Gene";
        Object[] result = null;
        try {
            result = db.select_oneColumn(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        List<String> liste = new LinkedList<>();

        if (result != null && result.length > 0) {
            for (int i = 0; i < result.length; i++) {
                liste.add((String) result[i]);
            }
        }
        return liste;
    }

    @Override
    public List<String> findAllTranscripts() {
        String query = "SELECT transcriptid FROM gobi1.Transcript";
        Object[] result = null;
        try {
            result = db.select_oneColumn(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        List<String> liste = new LinkedList<>();

        for (Object object : result) {
            liste.add((String) object);
        }
        return liste;
    }

    @Override
    public String getChrForTranscriptID(String transcriptid) {
        String query = "select chromosome from Gene\n" +
                "  natural join Transcript\n" +
                "  where transcriptid = '" + transcriptid + "'";
        try {
            return (String) db.select_oneColumn(query)[0];
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean getStrandForTranscriptID(String transcriptid) {
        String query = "select strand from Gene\n" +
                "  natural join Transcript\n" +
                "  where transcriptid = '" + transcriptid + "'";
        try {
            return (boolean) db.select_oneColumn(query)[0];
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("transcriptid " + transcriptid + " nicht gefunden.");
            return true;
        }
    }

    @Override
    public List<Transcript> findTranscriptsForKeyword(String keyword) {
        String query = "SELECT transcriptid FROM gobi1.Transcript";
        Object[] result = null;
        try {
            result = db.select_oneColumn(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        List<Transcript> liste = new LinkedList<>();

        for (int i = 0; i < result.length; i++) {
            liste.add(getTranscript((String) result[i]));
        }

        return liste;
    }

    @Override
    public List<String> findTranscriptIDsForKeyword(String keyword) {
        String query = "SELECT transcriptid FROM Transcript WHERE transcriptid REGEXP '" + keyword + "'";
        Object[] results;
        try {
            results = db.select_oneColumn(query);
        } catch (SQLException e) {
            return new LinkedList<>();
        }

        if (results.length == 0)
            return new LinkedList<>();

        List<String> liste = new LinkedList<>();

        for (int i = 0; i < results.length; i++) {
            liste.add((String) results[i]);
        }

        return liste;
    }

    @Override
    public List<String> findAllProteins() {
        String query = "SELECT proteinid FROM gobi1.Transcript";
        Object[] result = null;
        try {
            result = db.select_oneColumn(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        List<String> liste = new LinkedList<>();

        for (Object object : result) {
            liste.add((String) object);
        }
        return liste;
    }

    @Override
    public List<Event> getEvent(String isoform1, String isoform2) {
        String query = String.format("Select start, stop, type from Event where isoform1 = '%s' and isoform2 = '%s' order by start", isoform1, isoform2);
        Object[][] result = null;
        try {
            result = db.select(query, 3);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        List<Event> events = new LinkedList<>();
        if (result.length > 0) {
            for (int i = 0; i < result.length; i++) {
                events.add(new Event(isoform1, isoform2, (int) result[i][0], (int) result[i][1], ((String) result[i][2]).charAt(0)));
            }
        }
        return events;
    }

    public List<Event> getEvents(String geneid) {
        List<Event> events = new LinkedList<>();
        String query = "Select isoform1, isoform2, start, stop, type from Event, Transcript where isoform1 = transcriptid and geneid = '" + geneid + "'";
        Object[][] result = null;
        try {
            result = db.select(query, 5);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        for (Object[] object : result) {
            events.add(new Event((String) object[0], (String) object[1], (int) object[2], (int) object[3], ((String) object[4]).charAt(0)));
        }
        return events;
    }

    @Override
    public List<EventDisplay> getEventDisplay(String isoform1, String isoform2) {
        String query = "SELECT\n" +
                "  se.start,\n" +
                "  se.stop,\n" +
                "  se.type,\n" +
                "  access,\n" +
                "  startSS,\n" +
                "  stopSS,\n" +
                "  startAcc,\n" +
                "  stopAcc,\n" +
                "  fk_pattern_id,\n" +
                "  pe.start,\n" +
                "  pe.stop\n" +
                "FROM Event se LEFT OUTER JOIN PatternEvent pe ON isoform1 = transcriptid\n" +
                "  LEFT OUTER JOIN Pattern p ON fk_pattern_id = p.id\n" +
                "WHERE isoform1 = '" + isoform1 + "' AND isoform2 = '" + isoform2 + "' AND (pe.fk_pattern_id IS NULL OR\n" +
                "       (se.start < pe.start AND pe.start < se.stop) OR (se.start < pe.stop AND pe.stop < se.stop))";
        Object[][] result = null;
        try {
            result = db.select(query, 11);
        } catch (SQLException e) {
            e.printStackTrace();
            return new LinkedList<>();
        }

        if (result == null || result.length == 0)
            return new LinkedList<>();

        List<EventDisplay> eventList = new LinkedList<>();
        List<PatternEvent> pattern = null;
        EventDisplay cur = null;
        for (int i = 0; i < result.length; i++) {
            if (cur == null || cur.getStart() != (int) result[i][0]) {
                if (cur != null)
                    cur.setPattern(pattern);
                cur = new EventDisplay(isoform1, isoform2, (int) result[i][0],
                        (int) result[i][1],
                        ((String) result[i][2]).charAt(0),
                        result[i][3] != null ? ((String) result[i][3]).charAt(0) : 'N',
                        result[i][4] != null ? ((String) result[i][4]).charAt(0) : 'N',
                        result[i][5] != null ? ((String) result[i][5]).charAt(0) : 'N',
                        result[i][6] != null ? ((String) result[i][6]).charAt(0) : 'N',
                        result[i][7] != null ? ((String) result[i][7]).charAt(0) : 'N');
                eventList.add(cur);
                pattern = new LinkedList<>();
                if (result[i][8] != null) {
                    pattern.add(new PatternEvent((String) result[i][8], isoform1, (int) (long) result[i][9], (int) (long) result[i][10]));
                }
            } else {
                if (result[i][8] != null) {
                    pattern.add(new PatternEvent((String) result[i][8], isoform1, (int) (long) result[i][9], (int) (long) result[i][10]));
                }
            }
        }
        cur.setPattern(pattern);
        return eventList;
    }

    @Override
    public Gene getGene(String geneID) {
        String query = "select chromosome, strand from Gene where geneId = '" + geneID + "'";
        Object[][] result = null;
        try {
            result = db.select(query, 2);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        Gene gene = new Gene(geneID, (String) result[0][0], (boolean) result[0][1]);
        query = "Select transcriptId, proteinId from Transcript where geneId = '" + geneID + "'";
        try {
            result = db.select(query, 2);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        for (int i = 0; i < result.length; i++) {
            gene.addTranscript(getTranscript((String) result[i][0]));
        }

        return gene;
    }

    @Override
    public Transcript getTranscript(String transcriptid) {
        String query = "Select start, stop, frame from Exon where transcriptId = '" + transcriptid + "' order by start";
        Object[][] result = null;
        try {
            result = db.select(query, 3);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        String protId_query = "select proteinid from Transcript where transcriptid = '" + transcriptid + "'";
        Object[] prot_result = null;
        try {
            prot_result = db.select_oneColumn(protId_query);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        Transcript transcript = new Transcript(transcriptid, (String) prot_result[0]);
        for (int i = 0; i < result.length; i++) {
            Exon ex = new Exon((long) result[i][0], (long) result[i][1], (int) result[i][2]);
            transcript.addExon(ex);
        }

        return transcript;
    }

    @Override
    public Transcript getTranscriptForProteinId(String proteinId) {
        String query = "select transcriptid from Transcript where proteinid = '" + proteinId + "'";
        Object[] result = null;
        try {
            result = db.select_oneColumn(query);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        if (result.length != 1) {
            System.err.println(result.length + " transcripts found for " + proteinId);
            return null;
        } else {
            return getTranscript((String) result[0]);
        }
    }

    @Override
    public List<PatternEvent> getPatternEventForTranscriptID(String transcriptid) {
        String query = String.format("SELECT start, stop, fk_pattern_id FROM PatternEvent NATURAL JOIN Transcript WHERE transcriptid = '%s'", transcriptid);
        Object[][] res = new Object[0][];
        try {
            res = db.select(query, 3);
        } catch (SQLException e) {
            System.err.printf("[DBQuery]: Query: %s%n", query);
            return null;
        }

        List<PatternEvent> pe = new LinkedList<>();
        if (res.length == 0)
            return pe;

        for (Object[] re : res) {
            pe.add(new PatternEvent(((String) re[2]), transcriptid, (int) (long) (re[0]), (int) (long) (re[1])));
        }

        //sortiert nach start positionen --> comparable für patternevent
        Collections.sort(pe);
        return pe;
    }

    @Override
    public Gene getGeneForTranscriptID(String transcriptid) {
        String query = String.format("select geneid from Gene natural join Transcript where transcriptid = '%s'", transcriptid);
        Object[] bla = new Object[0];
        try {
            bla = db.select_oneColumn(query);
        } catch (SQLException e) {
            System.err.printf("[DBQuery]: %s%n", query);
        }

        String geneid;
        if (bla.length > 0) geneid = (String) bla[0];
        else return null;

        return getGene(geneid);
    }

    @Override
    public String getGeneIDForTranscriptID(String transcriptid) {
        String query = "select geneid from Gene natural join Transcript where transcriptid = '" + transcriptid + "'";

        Object[] result = new Object[0];

        try {
            result = db.select_oneColumn(query);
        } catch (SQLException e) {
            System.err.println("[DBQuery]: " + query);
        }

        if (result.length > 0) return (String) result[0];
        return null;
    }

    public static void main(String[] args) {
        DatabaseQuery dbq = new DBQuery();
        Gene g = dbq.getGene("ENSG1");
        System.out.println(g);
    }
}
