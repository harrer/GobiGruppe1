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

    //returns an array of PDBids that are modelable on the given ENSP protein
    private String[] getModelSequences(String ENSP_id) throws SQLException{
        String query = "select pdbId from transcript_has_pdbs where transcriptId = '"+ ENSP_id +"'";
        Object[] result = dbq.db.select_oneColumn(query);
        String[] s = new String[result.length];
        for (int i = 0; i < s.length; i++) {
            s[i] = (String) result[i];
        }
        return s;
    }
    
    private ArrayList<String[]> alignPDBs_onENSP(String ENSP_id, String[] seq) throws IOException{
        Transcript t = dbq.getTranscriptForProteinId(ENSP_id);
        SingleGotoh gotoh = new SingleGotoh(GenomeSequenceExtractor.getProteinSequence(t), "");
        ArrayList<String[]> alignments = new ArrayList<>();
        for (String PDBid : seq) {
            gotoh.setSeq2(this.pdbSequences.get(PDBid));
            String[] ali = gotoh.backtrackingLocal(gotoh.fillMatrixLocal());
            alignments.add(new String[]{ali[0], ali[1], PDBid});
        }
        return alignments;
    }
    
    private ArrayList<String[]> modelAlignmentsOnProtein(ArrayList<String[]> alignments, String ENSP_id){
        String proteinSeq = GenomeSequenceExtractor.getProteinSequence(dbq.getTranscriptForProteinId(ENSP_id));
        ArrayList<Object[]> model = new ArrayList<>();//Object[3]: String, Integer, Boolean
        int pdbPos = -1, enspPos = -1;//indicates the pos. of the PDB/ENSP sequence in the alignment ENSP -> PDB
            for (int i = 0; i < alignments.get(0)[0].length(); i++) {//iterate over the first alignment
                if(alignments.get(0)[0].charAt(i) != '-'){//if upper ENSP != '-'
                    enspPos++;
                    if(alignments.get(0)[1].charAt(i) != '-'){//if lower PDB != '-' ==> two aligned AAs
                        pdbPos++;
                        String exactMatch = (alignments.get(0)[0].charAt(i) == alignments.get(0)[1].charAt(i))? "true" : "false";
                        model.add(new Object[]{alignments.get(0)[2], pdbPos, exactMatch});
                    }
                    else{
                        model.add(new Object[]{});
                    }
                }
            }
        for (int i=1; i<alignments.size(); i++) {//iterate over all other alignements ENSP -> PDB
            String[] ali = alignments.get(i);
            pdbPos = -1; enspPos = -1;
            for(int i=0; i<ali[0].length(); i++){
                if(ali[0].charAt(i) != '-'){//if upper ENSP != '-'
                    enspPos++;
                    if(ali[1].charAt(i) != '-'){//if lower PDB != '-' ==> two aligned AAs
                        pdbPos++;
                        String exactMatch = (ali[0].charAt(i) == ali[1].charAt(i))? "true" : "false";
                        
                        model.add(new Object[]{ali[2], pdbPos, exactMatch});
                    }
                }
            }
        }
        
    }
    
}
