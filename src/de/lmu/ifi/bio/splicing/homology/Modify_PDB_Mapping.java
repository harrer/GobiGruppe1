package de.lmu.ifi.bio.splicing.homology;

import de.lmu.ifi.bio.splicing.genome.Transcript;
import de.lmu.ifi.bio.splicing.io.GenomeSequenceExtractor;
import de.lmu.ifi.bio.splicing.jsqlDatabase.DBQuery;
import de.lmu.ifi.bio.splicing.jsqlDatabase.DB_Backend;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author harrert
 */
public class Modify_PDB_Mapping {
    
    private HashMap<String, ArrayList<String>> ENSP_PDBmap;
    
    public Modify_PDB_Mapping(String file) throws IOException{
        ENSP_PDBmap = new HashMap<>();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while ((line = br.readLine()) != null) {
            String[] split = line.split("\t");
            if(split[split.length-2].startsWith("ENSG") && split[split.length-1].startsWith("ENSP")){
                ArrayList<String> list = (ENSP_PDBmap.containsKey(split[split.length-1]))? ENSP_PDBmap.get(split[split.length-1]) : new ArrayList<String>();
                if(!list.contains(split[split.length-3])){
                    list.add(split[split.length-3]);
                }
                ENSP_PDBmap.put(split[split.length-1], list);
            }
        }
        br.close();
    }

    public HashMap<String, ArrayList<String>> getENSP_PDBmap() {
        return ENSP_PDBmap;
    }
    
    public void createENSP_seqlib(String out) throws IOException{
        DBQuery dbq = new DBQuery();
        PrintWriter pw = new PrintWriter(out);
        int c = 0, empty=0;
        for (Map.Entry<String, ArrayList<String>> entry : ENSP_PDBmap.entrySet()) {
            c++;
            if(c%100 == 0){System.out.println(c);}
            String string = entry.getKey();
            Transcript t = dbq.getTranscriptForProteinId(string);
            String seq = GenomeSequenceExtractor.getProteinSequence(t);
            if(!seq.isEmpty()){pw.printf("%s:%s\n", string, seq);}
            else{empty++;}
        }
        pw.close();
        System.out.println(empty+" empty sequences");
    }
    
    public void createPairFile(String out) throws IOException{
        DB_Backend db = new DB_Backend();
        PrintWriter pw = new PrintWriter(out);
        Object[][] result = null;
        try {
            result = db.select("select transcriptId, pdbId from transcript_has_pdbs", 2);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        for (Object[] objects : result) {
            pw.printf("%s %s\n", objects[0], objects[1]);
        }
        pw.close();
    }
    
    public static void main(String[] args) throws IOException {
        Modify_PDB_Mapping map = new Modify_PDB_Mapping("/home/proj/biocluster/praktikum/genprakt-ws13/abgaben/assignment2/harrer/2_e_enriched");
        map.createENSP_seqlib("/tmp/ENST_seqlibBBBBBBBBBB.txt");
        //map.createPairFile("/tmp/ENSP_PDP_pairFile.txt");
    }
}
