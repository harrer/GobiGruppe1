package de.lmu.ifi.bio.splicing.structures.mapping;

import de.lmu.ifi.bio.splicing.config.Setting;
import de.lmu.ifi.bio.splicing.genome.Event;

import java.util.*;

/**
 * Created by schmidtju on 14.02.14.
 */
public class DSSP {

    // Values taken from: C. Chotia, The Nature of the Accessible and Buried
    // Surfaces in Proteins (ASA calculated in G-X-G tripeptide)
    static HashMap<Character, Integer> surface = new HashMap<>();

    static {
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
    }

    public static void updateEventWithAccAndSS(Model model, Event event, DSSPData dssp) {
        event.setAccessibility(mapAccessibility(dssp, model, event));
        event.setSecondaryStructure(mapSS(dssp, model, event));
        char[] bordersAcc = mapBordersAcc(dssp, model, event);
        event.setBordersAcc(bordersAcc[0], bordersAcc[1]);
        char[] bordersSS = mapBordersSS(dssp, model, event);
        event.setBordersSS(bordersSS[0], bordersSS[1]);
        event.setAccess(mapAccessibility2(dssp, model, event));
        event.setModelPdbId(model.getPdbId());
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
        int access = 0, expAccess = 0;
        List<Integer> affected = model.getAffectedPositions(event);
        for (Integer aff : affected) {
            if (aff < dssp.getSecondarySructure().length) {
                char c = dssp.getSequence().charAt(aff);
                if (surface.containsKey(c)) {
                    access += dssp.getAccesibility()[aff];
                    expAccess += surface.get(c);
                }
            }
        }
        if (expAccess > 0)
            return Math.round(10000.0 * (double) access / (double) expAccess) / 10000.0;
        else
            return 0;
    }

    public static char mapAccessibility2(DSSPData dssp, Model model, Event event) {
        List<Integer> affected = model.getAffectedPositions(event);
        List<Double> accessible = calcAccessiblity(dssp);
        int buried = 0;
        boolean interior = false;
        for (Integer aff : affected) {
            if (aff < dssp.getAccesibility().length) {
                if (accessible.get(aff) < 0.069) {
                    buried++;
                }
                if (buried > 3) {
                    interior = true;
                    break;
                }
            }
        }
        return interior ? 'M' : 'S';
    }

    /*    H	Alpha helix
        B	Beta bridge
        E	Strand
        G	Helix-3
        I	Helix-5
        T	Turn
        S   Bend*/
    public static char mapSS(DSSPData dssp, Model model, Event event) {
        int[] sec = new int[8]; // 0 = H (H, G, I), 1 = E (E, B), 2 = C (T, S, ' ')
        List<Integer> affected = model.getAffectedPositions(event);
        for (Integer aff : affected) {
            if (aff < dssp.getSecondarySructure().length) {
                switch (dssp.getSecondarySructure()[aff]) {
                    case 'H':
                    case 'G':
                    case 'I':
                        sec[0]++;
                        break;
                    case 'B':
                    case 'E':
                        sec[1]++;
                        break;
                    case 'T':
                    case 'S':
                    case ' ':
                        sec[2]++;
                        break;
                }
            }
            if ((double) sec[0] / affected.size() > 0.8) {
                return 'H';
            } else if ((double) sec[1] / affected.size() > 0.8) {
                return 'E';
            } else if ((double) sec[0] / affected.size() > 0.8) {
                return 'C';
            }
        }
        return 'M';
    }


    public static char[] mapBordersSS(DSSPData dssp, Model model, Event event) {
        char[] bordersSS = new char[2];
        int[] borders = model.getBoundaries(event);
        for (int i = 0; i < borders.length; i++) {
            if (borders[i] == -1 || borders[i] >= dssp.getSecondarySructure().length) {
                bordersSS[i] = 'N';
            } else {
                switch (dssp.getSecondarySructure()[borders[i]]) {
                    case 'H':
                    case 'G':
                    case 'I':
                        bordersSS[i] = 'H';
                        break;
                    case 'B':
                    case 'E':
                        bordersSS[i] = 'E';
                        break;
                    case 'T':
                    case 'S':
                    case ' ':
                        bordersSS[i] = 'C';
                        break;
                }
            }
        }
        return bordersSS;
    }

    public static char[] mapBordersAcc(DSSPData dssp, Model model, Event event) {
        char[] bordersAcc = new char[2];
        int[] borders = model.getBoundaries(event);
        for (int i = 0; i < borders.length; i++) {
            if (borders[i] == -1 || borders[i] >= dssp.getAccesibility().length) {
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
        return bordersAcc;
    }


    private static double calcAccessibility(char aa, int accessible) {
        if (surface.containsKey(aa))
            return (double) accessible / (double) surface.get(aa);
        else
            return 0;
    }

    public static int[] calcSecondaryStructureDistribution(Collection<DSSPData> dsspData) {
        int[] ssCount = new int[3]; //ssCount[0]: Helix, ssCount[1]: Extended, ssCount[2]: Coil
        for (DSSPData data : dsspData) {
            if (data != null) {
                for (char ss : data.getSecondarySructure()) {
                    switch (ss) {
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
        }
        return ssCount;
    }

    public static double[] calcSecondaryStructureDistribution(DSSPData data, int startPDB, int stopPDB,
                                                              int eventLength) {
        int[] ssCount = new int[3]; //ssCount[0]: Helix, ssCount[1]: Extended, ssCount[2]: Coil
        for (int i = startPDB; i < Math.min(stopPDB, data.getAccesibility().length); i++) {
            switch (data.getSecondarySructure()[i]) {
                case 'H':
                case 'G':
                case 'I':
                    if (i - startPDB < eventLength || stopPDB - i < eventLength)
                        ssCount[0]++;
                    else
                        ssCount[0] += 2;
                    break;
                case 'B':
                case 'E':
                    if (i - startPDB < eventLength || stopPDB - i < eventLength)
                        ssCount[1]++;
                    else
                        ssCount[1] += 2;
                    break;
                case 'T':
                case 'S':
                case ' ':
                    if (i - startPDB < eventLength || stopPDB - i < eventLength)
                        ssCount[2]++;
                    else
                        ssCount[2] += 2;
                    break;
            }
        }
        int sum = ssCount[0] + ssCount[1] + ssCount[2];
        return new double[]{2 * ssCount[0] / (double) sum, 2 * ssCount[1] / (double) sum, 2 * ssCount[2] / (double) sum};
    }

    public static double[] calcAccessibilityDistribution(DSSPData data, int startPDB, int stopPDB,
                                                         int eventLength) {
        int[] accCount = new int[3];
        for (int i = startPDB; i < Math.min(stopPDB, data.getAccesibility().length); i++) {
            double acc = calcAccessibility(data.getSequence().charAt(i), data.getAccesibility()[i]);
            if (acc < 0.069) {
                if (i - startPDB < eventLength || stopPDB - i < eventLength)
                    accCount[0]++;
                else
                    accCount[0] += 2;
            } else if (acc <= 0.36) {
                if (i - startPDB < eventLength || stopPDB - i < eventLength)
                    accCount[1]++;
                else
                    accCount[1] += 2;
            } else {
                if (i - startPDB < eventLength || stopPDB - i < eventLength)
                    accCount[2]++;
                else
                    accCount[2] += 2;
            }
        }
        int sum = accCount[0] + accCount[1] + accCount[2];
        return new double[]{2 * accCount[0] / (double) sum, 2 * accCount[1] / (double) sum, 2 * accCount[2] / (double) sum};
    }
}
