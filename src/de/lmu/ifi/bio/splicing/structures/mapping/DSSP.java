package de.lmu.ifi.bio.splicing.structures.mapping;

import de.lmu.ifi.bio.splicing.genome.Event;

import java.util.*;

/**
 * Created by schmidtju on 14.02.14.
 */
public class DSSP {
    public static List<Double> calcAccessiblity(DSSPData dssp) {
        List<Double> accessible = new ArrayList<>();
        for (int i = 0; i < dssp.getAccesibility().length; i++) {
            double fraction = calcAccessibility(dssp.getSequence().charAt(i), dssp.getAccesibility()[i]);
            accessible.add(fraction);
        }
        return accessible;
    }

    public static double mapAccessibility(DSSPData dssp, Map map, Event event){
        double meanAccess = 0;
        Set<Integer> affected = map.getAffectedPositions(event);
        List<Double> accessible = calcAccessiblity(dssp);
        for (Integer aff : affected) {
            meanAccess += accessible.get(aff);
        }
        return meanAccess/affected.size();
    }

    private static double calcAccessibility(char aa, int accessible) {
        // Values taken from: C. Chotia, The Nature of the Accessible and Buried
        // Surfaces in Proteins (ASA calculated in G-X-G tripeptide)
        HashMap<Character, Integer> surface = new HashMap<>();
        surface.put('A', 115);
        surface.put('R', 225);
        surface.put('D', 150);
        surface.put('N', 160);
        surface.put('C', 135);
        surface.put('E', 190);
        surface.put('Q', 180);
        surface.put('G', 75);
        surface.put('H', 195);
        surface.put('I', 175);
        surface.put('L', 170);
        surface.put('K', 200);
        surface.put('M', 185);
        surface.put('F', 210);
        surface.put('P', 145);
        surface.put('S', 115);
        surface.put('T', 140);
        surface.put('W', 255);
        surface.put('Y', 230);
        surface.put('V', 155);
        return (double) accessible / (double) surface.get(aa);
    }
}
