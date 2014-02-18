package de.lmu.ifi.bio.splicing.homology;

import de.lmu.ifi.bio.splicing.genome.Transcript;
import de.lmu.ifi.bio.splicing.io.GenomeSequenceExtractor;
import de.lmu.ifi.bio.splicing.jsqlDatabase.DBQuery;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author harrert
 */
public class ModelPDB_onENSP {
    
    private DBQuery dbq;
    private HashMap<String,String> pdbSequences;
    
    public ModelPDB_onENSP(){
        dbq = new DBQuery();
        try {
            pdbSequences = readPDBseqlib("/home/proj/biosoft/PROTEINS/PDB_REP_CHAINS/pdb.seqlib");
        } catch (IOException ex) {}
    }
    
    private HashMap<String, String> readPDBseqlib(String file) throws IOException{
        HashMap<String, String> map = new HashMap<>();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while ((line = br.readLine()) != null) {
            String[] split = line.split(":");
            map.put(split[0], split[1]);
        }
        br.close();
        return map;
    }
    
    private String[] getModelSequences(String ENSP_id) throws SQLException{
        String query = "select pdbId from transcript_has_pdbs where transcriptId = '"+ ENSP_id +"'";
        Object[] result = dbq.db.select_oneColumn(query);
        String[] s = new String[result.length];
        for (int i = 0; i < s.length; i++) {
            s[i] = this.pdbSequences.get((String) result[i]);
        }
        return s;
    }
    
    private ArrayList<String[]> alignPDBs_onENSP(String ENSP_id, String[] seq) throws IOException{
        Transcript t = dbq.getTranscriptForProteinId(ENSP_id);
        SingleGotoh gotoh = new SingleGotoh(GenomeSequenceExtractor.getProteinSequence(t), "");
        ArrayList<String[]> alignments = new ArrayList<>();
        for (String sequence : seq) {
            gotoh.setSeq2(sequence);
            alignments.add(gotoh.backtrackingLocal(gotoh.fillMatrixLocal()));
        }
        return alignments;
    }
    
    private void modelAlignmentsOnProtein(ArrayList<String[]> alignments, String ENSP_id){
        String proteinSeq = GenomeSequenceExtractor.getProteinSequence(dbq.getTranscriptForProteinId(ENSP_id));
    }
    
}
