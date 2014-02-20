package de.lmu.ifi.bio.splicing.homology;

/**
 *
 * @author harrert
 */
public class Overlap {
    
    private final String pdb1, pdb2;
    private final int pdb1Start, pdb1End, pdb2Start, pdb2End;

    public Overlap(String pdb1, String pdb2, int pdb1Start, int pdb1End, int pdb2Start, int pdb2End) {
        this.pdb1 = pdb1;
        this.pdb2 = pdb2;
        this.pdb1Start = pdb1Start;
        this.pdb1End = pdb1End;
        this.pdb2Start = pdb2Start;
        this.pdb2End = pdb2End;
    }
    
}
