package de.lmu.ifi.bio.splicing.structures.mapping;

/**
 * Created by schmidtju on 14.02.14.
 */
public class DSSPData {
    Integer[] accesibility;
    Character[] secondarySructure;

    public DSSPData(Integer[] accesibility, Character[] secondarySructure) {
        this.accesibility = accesibility;
        this.secondarySructure = secondarySructure;
    }

    public Integer[] getAccesibility() {
        return accesibility;
    }

    public Character[] getSecondarySructure() {
        return secondarySructure;
    }
}
