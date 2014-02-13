package de.lmu.ifi.bio.splicing.structures.mapping;

import de.lmu.ifi.bio.splicing.genome.Event;
import de.lmu.ifi.bio.splicing.structures.PDBData;

import java.util.Set;

/**
 * Created by schmidtju on 13.02.14.
 */
public class map {
//    private Set<Event> events;
    private PDBData pdb;

    public map(PDBData pdb) {
        this.pdb = pdb;
    }

    public String createColoringJmolScript(Set<Event> events, int[][] aligned){
        StringBuilder jmolScript = new StringBuilder();
        for (Event event : events) {
            if(event.isReplace()){
//                jmolScript.append("select " + event.getStart() + "-" + event.getStop() + "\ncolor green");
            } else  {
//                jmolScript.append("select " + event.getStart() + "-" + event.getStop() + "\ncolor red");
            }
        }
        return jmolScript.toString();
    }


}
