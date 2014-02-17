package de.lmu.ifi.bio.splicing.structures.mapping;

import de.lmu.ifi.bio.splicing.genome.Event;
import de.lmu.ifi.bio.splicing.structures.PDBData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by schmidtju on 13.02.14.
 */
public class Map {
    //    private Set<Event> events;
    private PDBData pdb;
    private HashMap<Integer, Integer> aligned;


    public Map(PDBData pdb, HashMap<Integer, Integer> aligned) {
        this.pdb = pdb;
        this.aligned = aligned;
    }

    public String createColoringJmolScript(Set<Event> events) {
        StringBuilder jmolScript = new StringBuilder();
        for (Event event : events) {
            if (event.getType() == 'D') {
                jmolScript.append("select " + aligned.get(event.getStart()) + "-" + aligned.get(event.getStop()) + "; color red; ");
            } else if (event.getType() == 'R') {
                jmolScript.append("select " + aligned.get(event.getStop()) + "-" + aligned.get(event.getStop()) + "; color green; ");
            }
        }
        return jmolScript.toString();
    }

    public Set<Integer> getAffectedPositions(Event event) {
        Set<Integer> affected = new HashSet<>();
        for (long i = event.getStart(); i <= event.getStop(); i++) {
            if (aligned.containsKey(i)) {
                affected.add(aligned.get(i));
            }
        }
        return affected;
    }

    public int[] getBoundaries(Event event) {
        int[] borders = new int[2];

        if (aligned.containsKey(event.getStart())) {
            borders[0] = aligned.get(event.getStart());
        } else {
            borders[0] = -1;
        }

        if (aligned.containsKey(event.getStop())) {
            borders[1] = aligned.get(event.getStop());
        } else {
            borders[1] = -1;
        }
        return borders;
    }
}
