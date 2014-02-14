package de.lmu.ifi.bio.splicing.structures.mapping;

import de.lmu.ifi.bio.splicing.genome.Event;
import de.lmu.ifi.bio.splicing.structures.PDBData;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by schmidtju on 13.02.14.
 */
public class Map {
//    private Set<Event> events;
    private PDBData pdb;

    public Map(PDBData pdb) {
        this.pdb = pdb;
    }

    public String createColoringJmolScript(Set<Event> events, HashMap<Integer, Integer> aligned){
        StringBuilder jmolScript = new StringBuilder();
        for (Event event : events) {
            if(event.getType() == 'D'){
                jmolScript.append("select " + aligned.get(event.getStart()) + "-" + aligned.get(event.getStop()) + "; color red; ");
            } else if(event.getType() == 'R'){
                jmolScript.append("select " + aligned.get(event.getStop()) + "-" + aligned.get(event.getStop()) + "; color green; ");
            }
        }
        return jmolScript.toString();
    }


}
