package de.lmu.ifi.bio.splicing.structures;

import java.util.List;

/**
 * Created by schmidtju on 13.02.14.
 */
public class PDBData {
    private String pdbId, sequence;
    private double[][] coordinates;
    private List<String> atomType;
    private List<Character> chain;

    public PDBData(String pdbId, String sequence, double[][] coordinates, List<String> atomType, List<Character> chain) {
        this.pdbId = pdbId;
        this.sequence = sequence;
        this.coordinates = coordinates;
        this.atomType = atomType;
        this.chain = chain;
    }

    public String getPdbId() {
        return pdbId;
    }

    public String getSequence() {
        return sequence;
    }

    public double[][] getCoordinates() {
        return coordinates;
    }

    public List<String> getAtomType() {
        return atomType;
    }

    public List<Character> getChain() {
        return chain;
    }

    public double[][] getCACoordinates(){
        double[][] CACoordinates = new double[sequence.length()][3];
        int i = 0, j = 0;
        for (String s : atomType) {
            if(s.equalsIgnoreCase("CA")){
                CACoordinates[j] = coordinates[i];
                j++;
            }
            i++;
        }
        return CACoordinates;
    }
}
