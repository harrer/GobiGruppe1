package de.lmu.ifi.bio.splicing.structures.mapping;

import java.util.*;

/**
 * Created by schmidtju on 14.02.14.
 */
public class DSSP {
    public static List<Boolean> calcAccessiblity(String protein,
                                                 String pdbDirectory, double cutoff) {
        // Values taken from: C. Chotia, The Nature of the Accessible and Buried
        // Surfaces in Proteins (ASA calculated in G-X-G tripeptide)
        java.util.Map<Character, Integer> surface = new HashMap<Character, Integer>();
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
        String[] split = null; //Utilities.getDsspAcc(protein, pdbDirectory).split("\n");
        boolean acids = false;
        List<Boolean> accessible = new ArrayList<Boolean>();
        for (String line : split) {
            if (acids) {
                if (line.length() > 1) {
                    int access = Integer.parseInt(line.substring(35, 38)
                            .replaceAll(" ", ""));
                    char aa = line.charAt(13);
                    double rsa = (double) access / (double) surface.get(aa);
                    accessible.add(rsa >= cutoff);
                } else
                    break;
            } else if (line.startsWith("  #")) {
                acids = true;
            }
        }
        return accessible;
    }
}
