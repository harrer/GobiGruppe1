package de.lmu.ifi.bio.splicing.structures.mapping;

import de.lmu.ifi.bio.splicing.config.Setting;
import de.lmu.ifi.bio.splicing.genome.Event;
import de.lmu.ifi.bio.splicing.io.DSSPParser;

import java.util.*;

/**
 * Created by schmidtju on 14.02.14.
 */
public class DSSP {
    public static void updateEventWithAccAndSS(Model model, Event event, DSSPData dssp){
        mapAccessibility(dssp, model, event);

        mapBordersAcc(dssp, model, event);
        mapBordersSS(dssp, model, event);

        Setting.dbu.fullUpdateEvent(event);
    }

    public static List<Double> calcAccessiblity(DSSPData dssp) {
        List<Double> accessible = new ArrayList<>();
        for (int i = 0; i < dssp.getAccesibility().length; i++) {
            double fraction = calcAccessibility(dssp.getSequence().charAt(i), dssp.getAccesibility()[i]);
            accessible.add(fraction);
        }
        return accessible;
    }

    public static double mapAccessibility(DSSPData dssp, Model model, Event event) {
        double meanAccess = 0;
        List<Integer> affected = model.getAffectedPositions(event);
        List<Double> accessible = calcAccessiblity(dssp);
        for (Integer aff : affected) {
            meanAccess += accessible.get(aff);
        }
        event.setAccessibility(meanAccess / affected.size());
        return meanAccess / affected.size();
    }
/*    H	Alpha helix
    B	Beta bridge
    E	Strand
    G	Helix-3
    I	Helix-5
    T	Turn
    S   Bend*/

    public static int[] mapSS(DSSPData dssp, Model model, Event event) {
        double meanAccess = 0;
        int[] sec = new int[8]; // 0 = default, 1 = 'H', 2 = 'B', 3 = 'E', 4 = G, 5 = I, 6 = T, 7 = S
        List<Integer> affected = model.getAffectedPositions(event);
        for (Integer aff : affected) {
            switch(dssp.getSecondarySructure()[aff]){
                case 'H':
                    sec[1]++;
                    break;
                case 'B':
                    sec[2]++;
                    break;
                case 'E':
                    sec[3]++;
                    break;
                case 'G':
                    sec[4]++;
                    break;
                case 'I':
                    sec[5]++;
                    break;
                case 'T':
                    sec[6]++;
                    break;
                case 'S':
                    sec[7]++;
                    break;
                default:
                    sec[0]++;
                    break;
            }
        }
        event.setAccessibility(meanAccess / affected.size());
        return null;
    }

    public static char[] mapBordersSS(DSSPData dssp, Model model, Event event) {
        char[] bordersSS = new char[2];
        int[] borders = model.getBoundaries(event);
        for (int i = 0; i < borders.length; i++) {
            if (borders[i] == -1 || dssp.getSecondarySructure()[borders[i]] != ' ') {
                bordersSS[i] = 'N';
            } else {
                bordersSS[i] = dssp.getSecondarySructure()[borders[i]];
            }
        }
        event.setBordersSS(bordersSS[0], bordersSS[1]);
        return bordersSS;
    }

    public static char[] mapBordersAcc(DSSPData dssp, Model model, Event event) {
        char[] bordersAcc = new char[2];
        int[] borders = model.getBoundaries(event);
        for (int i = 0; i < borders.length; i++) {
            if (borders[i] == -1) {
                bordersAcc[i] = 'N';
            } else {
                double acc = calcAccessibility(dssp.getSequence().charAt(borders[i]), dssp.getAccesibility()[borders[i]]);
                if (acc < 0.069) {
                    bordersAcc[i] = 'B';
                } else if (acc <= 0.36) {
                    bordersAcc[i] = 'P';
                } else {
                    bordersAcc[i] = 'E';
                }
            }
        }
        event.setBordersAcc(bordersAcc[0], bordersAcc[1]);
        return bordersAcc;
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

    public static int[] calcSecondaryStructureDistribution(Collection<DSSPData> dsspData){
        int[] ssCount = new int[3]; //ssCount[0]: Helix, ssCount[1]: Extended, ssCount[2]: Coil
        for (DSSPData data : dsspData) {
            for (char ss : data.getSecondarySructure()) {
                switch(ss) {
                    case 'H':
                    case 'G':
                    case 'I':
                        ssCount[0]++;
                        break;
                    case 'B':
                    case 'E':
                        ssCount[1]++;
                        break;
                    case 'T':
                    case 'S':
                    case ' ':
                        ssCount[2]++;
                        break;
                }
            }
        }
        return ssCount;
    }
}
