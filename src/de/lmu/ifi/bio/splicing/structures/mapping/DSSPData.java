package de.lmu.ifi.bio.splicing.structures.mapping;

/**
 * Created by schmidtju on 14.02.14.
 */
public class DSSPData {
    private Integer[] accesibility;
    private Character[] secondarySructure;
    private String sequence, proteinId;

    public DSSPData(Integer[] accesibility, Character[] secondarySructure, String sequence, String proteinId) {
        this.accesibility = accesibility;
        this.secondarySructure = secondarySructure;
        this.sequence = sequence;
        this.proteinId = proteinId;
    }

    public Integer[] getAccesibility() {
        return accesibility;
    }

    public Character[] getSecondarySructure() {
        return secondarySructure;
    }

    public String getSequence() {
        return sequence;
    }
}
