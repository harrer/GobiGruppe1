package de.lmu.ifi.bio.splicing.jsqlDatabase;

import de.lmu.ifi.bio.splicing.interfaces.DatabaseQuery;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import de.lmu.ifi.bio.splicing.genome.*;

public class DBQuery implements DatabaseQuery {

    private DB_Backend db;

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

        String query;
        Object[] result;
        List<String> liste = new LinkedList<>();

        query = "SELECT geneid FROM gobi1.Gene WHERE geneid LIKE '%" + keyword + "%';";
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
        query = "SELECT transcriptid FROM gobi1.Transcript WHERE transcriptid LIKE '%" + keyword + "%';";
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

        query = "SELECT proteinid FROM gobi1.Transcript WHERE proteinid LIKE '%" + keyword + "%';";
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
    public Event getEvent(String isoform1, String isoform2) {
        String query = "Select start, stop, type from Event where isoform1 = '" + isoform1 + "' and isoform2 = '" + isoform2 + "'";
        Object[][] result = null;
        try {
            result = db.select(query, 3);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        if (result.length > 0)
            return new Event(isoform1, isoform2, (long) result[0][0], (long) result[0][1], ((String) result[0][2]).charAt(0));
        else
            return null;
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
    public Transcript getTranscript(String transcriptID) {
        String query = "Select start, stop, frame from Exon where transcriptId = '" + transcriptID + "'";
        Object[][] result = null;
        try {
            result = db.select(query, 3);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        String protId_query = "select proteinid from Transcript where transcriptid = '" + transcriptID + "'";
        Object[] prot_result = null;
        try {
            prot_result = db.select_oneColumn(protId_query);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        Transcript transcript = new Transcript(transcriptID, (String) prot_result[0]);
        for (int i = 0; i < result.length; i++) {
            Exon ex = new Exon((long) result[i][0], (long) result[i][1], (int) result[i][2]);
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
