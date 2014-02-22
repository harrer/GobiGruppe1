package de.lmu.ifi.bio.splicing.homology;

import de.lmu.ifi.bio.splicing.structures.mapping.Model;

/**
 *
 * @author harrert
 */
public class Overlap {
    
    private final Model model1, model2;
    private final OverlapType type;
    private final int pdb1Start, pdb1End, pdb2Start, pdb2End, absoluteLength;
    private final double relativeLength;

    public Overlap(Model m1, Model m2, OverlapType type, int pdb1Start, int pdb1End, int pdb2Start, int pdb2End, double relative, int absolute) {
        this.model1 = m1;
        this.model2 = m2;
        this.type = type;
        this.pdb1Start = pdb1Start;
        this.pdb1End = pdb1End;
        this.pdb2Start = pdb2Start;
        this.pdb2End = pdb2End;
        this.relativeLength = relative;
        this.absoluteLength = absolute;
    }

    public Model getModel1() {
        return model1;
    }

    public Model getModel2() {
        return model2;
    }

    public int getPdb1End() {
        return pdb1End;
    }

    public int getPdb1Start() {
        return pdb1Start;
    }

    public int getPdb2End() {
        return pdb2End;
    }

    public int getPdb2Start() {
        return pdb2Start;
    }

    public OverlapType getType() {
        return type;
    }

    public int getAbsoluteLength() {
        return absoluteLength;
    }

    public double getRelativeLength() {
        return relativeLength;
    }
    
}
