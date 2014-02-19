package de.lmu.ifi.bio.splicing.structures.mapping;

import de.lmu.ifi.bio.splicing.genome.Event;
import de.lmu.ifi.bio.splicing.structures.PDBData;

import java.util.*;

/**
 * Created by schmidtju on 13.02.14.
 */
public class Model {
    //    private Set<Event> events;
    private PDBData pdb;
    private HashMap<Integer, Integer> aligned;
    private int start, stop;
    private double quality;


    public Model(PDBData pdb, HashMap<Integer, Integer> aligned, int start, int stop, double quality) {
        this.pdb = pdb;
        this.aligned = aligned;
        this.start = start;
        this.stop = stop;
        this.quality = quality;
    }

    public String createColoringJmolScript(Set<Event> events) {
        StringBuilder jmolScript = new StringBuilder();
        for (Event event : events) {
            if (event.getType() == 'D') {
                jmolScript.append("select ").append(aligned.get(event.getStart())).append("-").append(aligned.get(event.getStop())).append("; color red; ");
            } else if (event.getType() == 'R') {
                jmolScript.append("select ").append(aligned.get(event.getStop())).append("-").append(aligned.get(event.getStop())).append("; color green; ");
            }
        }
        return jmolScript.toString();
    }

    public List<Integer> getAffectedPositions(Event event) {
        List<Integer> affected = new LinkedList<>();
        for (int i = event.getStart(); i <= event.getStop(); i++) {
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
        } else if (aligned.containsKey(event.getStart() - 1)) {
            borders[0] = aligned.get(event.getStart() - 1);
        } else if (aligned.containsKey(event.getStart() + 1)) {
            borders[0] = aligned.get(event.getStart() + 1);
        } else {
            borders[0] = -1;
        }

        if (aligned.containsKey(event.getStop())) {
            borders[1] = aligned.get(event.getStop());
        } else if (aligned.containsKey(event.getStop() - 1)) {
            borders[1] = aligned.get(event.getStop() - 1);
        } else if (aligned.containsKey(event.getStop() + 1)) {
            borders[1] = aligned.get(event.getStop() + 1);
        } else {
            borders[1] = -1;
        }
        return borders;
    }

    public boolean contains(int estart, int estop){
        return estart >= start && estop <= stop;
    }

    public PDBData getPdb() {
        return pdb;
    }

    public double getQuality() {
        return quality;
    }
}
