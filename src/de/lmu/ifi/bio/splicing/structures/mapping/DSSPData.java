package de.lmu.ifi.bio.splicing.structures.mapping;

/**
 * Created by schmidtju on 14.02.14.
 */
public class DSSPData {
    private Integer[] accesibility;
    private Character[] secondarySructure;
    private String sequence, proteinId;
    private double[][] ca_coordinates;

    public DSSPData(Integer[] accesibility, Character[] secondarySructure, String sequence, String proteinId) {
        this.accesibility = accesibility;
        this.secondarySructure = secondarySructure;
        this.sequence = sequence;
        this.proteinId = proteinId;
    }

    public void setCa_coordinates(double[][] ca_coordinates) {
        this.ca_coordinates = ca_coordinates;
    }

    public double[][] getCa_coordinates() {
        return ca_coordinates;
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
