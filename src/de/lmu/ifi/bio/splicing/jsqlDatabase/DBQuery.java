package de.lmu.ifi.bio.splicing.jsqlDatabase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import de.lmu.ifi.bio.splicing.interfaces.*;
import de.lmu.ifi.bio.splicing.genome.*;

public class DBQuery implements DatabaseQuery {

    @Override
    public Event getEvent() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> search(String keyword) {
        if (keyword.isEmpty())
            return findAllGenes();
        DB_Backend db = new DB_Backend();
        String query = "SELECT transcriptid FROM gobi1.Transcript WHERE transcriptid LIKE '%" + keyword + "%';";
        Object[] result = null;
        try {
            result = db.select_oneColumn(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        List<String> liste = new LinkedList<>();

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

        return liste;
    }

    @Override
    public List<String> findAllGenes() {
        DB_Backend db = new DB_Backend();
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
        DB_Backend db = new DB_Backend();
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
        DB_Backend db = new DB_Backend();
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
    public Gene getGene(String geneID) {
        DB_Backend db = new DB_Backend();
        String query = "select chromosome, strand from Gene where geneId = '" + geneID + "'";
        Object[][] result = null;
        try {
            result = db.select(query);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        Gene gene = new Gene(geneID, (String) result[0][0], (boolean) result[0][1]);
        query = "Select transcriptId, proteinId from Transcript where geneId = '" + geneID + "'";
        try {
            result = db.select(query);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        for (int i = 0; i < result[0].length; i++) {
            gene.addTranscript(getTranscript((String) result[i][0], (String) result[i][1]));
        }

        return gene;
    }

    @Override
    public Transcript getTranscript(String transcriptID, String proteinID) {
        DB_Backend db = new DB_Backend();
        String query = "Select start, stop, frame from Exon where transcriptId = '" + transcriptID + "'";
        Object[][] result = null;
        try {
            result = db.select(query);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        Transcript transcript = new Transcript(transcriptID, proteinID);
        for (int i = 0; i < result[0].length; i++) {
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
