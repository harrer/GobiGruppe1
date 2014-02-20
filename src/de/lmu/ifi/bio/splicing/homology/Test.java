package de.lmu.ifi.bio.splicing.homology;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 *
 * @author harrert
 */
public class Test {
    
    public void readPdb() throws IOException{
        BufferedReader reader = new BufferedReader(new FileReader("1uli.pdb"));
        String line;
        HashMap<Character, Integer> map = new HashMap<>();
        while((line=reader.readLine()) != null){
            String[] split = line.split("\\s+");
            char atom = split[split.length-1].charAt(0);
            if(!map.containsKey(atom)){
                map.put(atom, 1);
            } else{
                map.put(atom, map.get(atom)+1);
            }
        }
        reader.close();
        String out = "";
        for (Map.Entry<Character, Integer> entry : map.entrySet()) {
            Character character = entry.getKey();
            Integer integer = entry.getValue();
            out += character+"_{"+integer+"}";
        }
        System.out.println(out);
    }
    
    
    public static void main(String[] args) {
        float f = 1.1f;
        //DatabaseQuery db = new DBQuery();
        //Transcript t = db.getTranscriptForProteinId("ENSP00000261313");
        //System.out.println(GenomeSequenceExtractor.getProteinSequence(t));
    }
}
