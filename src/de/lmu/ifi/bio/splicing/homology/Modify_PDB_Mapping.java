package de.lmu.ifi.bio.splicing.homology;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

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
    
    public static void main(String[] args) throws IOException {
        Modify_PDB_Mapping map = new Modify_PDB_Mapping("/home/proj/biocluster/praktikum/genprakt-ws13/abgaben/assignment2/harrer/2_e_enriched");
        System.out.println("");
    }
}
