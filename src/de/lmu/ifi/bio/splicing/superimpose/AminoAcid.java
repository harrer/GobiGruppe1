package de.lmu.ifi.bio.splicing.superimpose;

import cern.colt.matrix.DoubleMatrix2D;
import java.util.ArrayList;

/**
 *
 * @author harrert
 */
public class AminoAcid {
    
    private final int number;
    private final int startPos;
    private int endPos;
    private int cAlpha;
    private int C;
    private int N;
    private final String name;
    private DoubleMatrix2D coordinates;
    private ArrayList<String> atomNames;
    private final String domain;
    
    public AminoAcid(int number, int startPos, String name, String domain){
        this.number = number;
        this.startPos = startPos;
        this.name = name;
        this.domain = domain;
    }

    public void setEndPos(int endPos) {
        this.endPos = endPos;
    }

    public void setCoordinates(DoubleMatrix2D coordinates) {
        this.coordinates = coordinates;
    }

    public void setAtomNames(ArrayList<String> atomNames) {
        this.atomNames = atomNames;
    }

    public void setcAlpha(int cAlpha) {
        this.cAlpha = cAlpha;
    }

    public void setC(int C) {
        this.C = C;
    }

    public void setN(int N) {
        this.N = N;
    }
    
    public ArrayList<String> getAtomNames() {
        return atomNames;
    }

    public DoubleMatrix2D getCoordinates() {
        return coordinates;
    }

    public String getDomain() {
        return domain;
    }

    public int getEndPos() {
        return endPos;
    }

    public String getName() {
        return name;
    }

    public int getNumber() {
        return number;
    }

    public int getStartPos() {
        return startPos;
    }
    
    
}
