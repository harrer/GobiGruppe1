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

/**
 *
 * @author harrert
 */
public class ModelPDB_onENSP {

    private DBQuery dbq;
    private HashMap<String, String> pdbSequences;

    public ModelPDB_onENSP() {
        dbq = new DBQuery();
        try {
            pdbSequences = readPDBseqlib("/home/proj/biosoft/PROTEINS/PDB_REP_CHAINS/pdb.seqlib");
        } catch (IOException ex) {
        }
    }

    private HashMap<String, String> readPDBseqlib(String file) throws IOException {
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

    //returns an array of PDBids that are modelable on the given ENSP protein
    private String[] getModelSequences(String ENSP_id) throws SQLException {
        String query = "select pdbId from transcript_has_pdbs where transcriptId = '" + ENSP_id + "'";
        Object[] result = dbq.db.select_oneColumn(query);
        String[] s = new String[result.length];
        for (int i = 0; i < s.length; i++) {
            s[i] = (String) result[i];
        }
        return s;
    }

    private ArrayList<String[]> alignPDBs_onENSP(String ENSP_id, String[] seq, double coverage, int longerThan, double seqIdentity) throws IOException {
        Transcript t = dbq.getTranscriptForProteinId(ENSP_id);
        SingleGotoh gotoh = new SingleGotoh(GenomeSequenceExtractor.getProteinSequence(t), "");
        ArrayList<String[]> alignments = new ArrayList<>();
        for (String PDBid : seq) {
            gotoh.setSeq2(this.pdbSequences.get(PDBid));
            String[] ali = gotoh.backtrackingLocal(gotoh.fillMatrixLocal());
            if (gotoh.sequenceIdentity(ali) >= seqIdentity && gotoh.coverage(ali, longerThan, coverage)) {//if alignment is significant given the 3 paramsz
                alignments.add(new String[]{ali[0], ali[1], PDBid});
            }
        }
        return alignments;
    }

    private ArrayList<Object[]> modelAlignmentsOnProtein(ArrayList<String[]> alignments, String ENSP_id) {
        String proteinSeq = GenomeSequenceExtractor.getProteinSequence(dbq.getTranscriptForProteinId(ENSP_id));
        ArrayList<Object[]> models = new ArrayList<>();//Object[6]: int ENSPstart, int ENSPend, String pdbId, int PDBstart, int PDBend, double seqIdentity
        for (String[] ali : alignments) {//iterate over all alignements ENSP -> PDB
            int[] ali_StartEnd = SingleGotoh.getAli_StartEnd(ali);
            int enspStart = -1, pdbStart = -1;
            for (int i = 0; i <= ali_StartEnd[0]; i++) {
                if(ali[0].charAt(i) != '-'){enspStart++;}
                if(ali[1].charAt(i) != '-'){pdbStart++;}
            }
            int enspEnd = proteinSeq.length(), pdbEnd = this.pdbSequences.get(ali[2]).length();
            for (int i = ali[0].length()-1; i >= ali_StartEnd[1]; i--) {
                if(ali[0].charAt(i) != '-'){enspEnd--;}
                if(ali[1].charAt(i) != '-'){pdbEnd--;}
            }
            models.add(new Object[]{enspStart, enspEnd, ali[2], pdbStart, pdbEnd, SingleGotoh.sequenceIdentity(ali)});
        }
        return models;
    }
    
    public String displayModels(ArrayList<Object[]> models, String ENSP_id){
        String proteinSeq = GenomeSequenceExtractor.getProteinSequence(dbq.getTranscriptForProteinId(ENSP_id));
        StringBuilder sb = new StringBuilder("        "+proteinSeq+'\n');
        for(Object[] model : models){
            sb.append(model[2]).append(": ");
            for (int i = 0; i < proteinSeq.length(); i++) {
                String append = (i >= (int)model[0] && i<= (int)model[1])? "+" : "|";
                sb.append(append);
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    public static void main(String[] args) throws SQLException, IOException {
        ModelPDB_onENSP model = new ModelPDB_onENSP();
        String enspSeq = "ENSP00000215939";
        String[] pdbs = model.getModelSequences(enspSeq);
        ArrayList<String[]> alignments = model.alignPDBs_onENSP(enspSeq, pdbs, 0.6, 60, 0.4);
        ArrayList<Object[]> models = model.modelAlignmentsOnProtein(alignments, enspSeq);
        System.out.println(model.displayModels(models, enspSeq));
    }

}
