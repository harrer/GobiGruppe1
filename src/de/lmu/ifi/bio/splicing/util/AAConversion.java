package de.lmu.ifi.bio.splicing.util;

import java.util.HashMap;
/**
 * Created by schmidtju on 13.02.14.
 */
public class AAConversion {
    private final static HashMap<String, Character> lts = new HashMap<String, Character>();
    static {
        lts.put("ALA", 'A');
        lts.put("ARG", 'R');
        lts.put("ASN", 'N');
        lts.put("ASP", 'D');
        lts.put("CYS", 'C');
        lts.put("GLN", 'Q');
        lts.put("GLU", 'E');
        lts.put("GLY", 'G');
        lts.put("HIS", 'H');
        lts.put("ILE", 'I');
        lts.put("LEU", 'L');
        lts.put("LYS", 'K');
        lts.put("MET", 'M');
        lts.put("PHE", 'F');
        lts.put("PRO", 'P');
        lts.put("SER", 'S');
        lts.put("THR", 'T');
        lts.put("TRP", 'W');
        lts.put("TYR", 'Y');
        lts.put("VAL", 'V');
    }
    private final static HashMap<Character, String> stl = new HashMap<Character, String>();
    static {
        stl.put('A', "ALA");
        stl.put('R', "ARG");
        stl.put('N', "ASN");
        stl.put('D', "ASP");
        stl.put('C', "CYS");
        stl.put('Q', "GLN");
        stl.put('E', "GLU");
        stl.put('G', "GLY");
        stl.put('H', "HIS");
        stl.put('I', "ILE");
        stl.put('L', "LEU");
        stl.put('K', "LYS");
        stl.put('M', "MET");
        stl.put('F', "PHE");
        stl.put('P', "PRO");
        stl.put('S', "SER");
        stl.put('T', "THR");
        stl.put('W', "TRP");
        stl.put('Y', "TYR");
        stl.put('V', "VAL");
    }

    public static char getOneLetter(String threeLetter){
        return lts.get(threeLetter);
    }

    public static String getThreeLetter(String oneLetter){
        return stl.get(oneLetter);
    }
}
