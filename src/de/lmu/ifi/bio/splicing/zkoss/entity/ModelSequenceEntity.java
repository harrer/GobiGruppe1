/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.lmu.ifi.bio.splicing.zkoss.entity;

import java.util.List;

/**
 *
 * @author harrert
 */
public class ModelSequenceEntity {
    private String model1;
    private String model2;

    public String getModel1() {
        return model1;
    }

    public void setModel1(String model1) {
        this.model1 = model1;
    }

    public String getModel2() {
        return model2;
    }

    public void setModel2(String flowers) {
        this.model2 = flowers;
    }

    public ModelSequenceEntity(String flower, String flowers) {
        this.model1 = flower;
        this.model2 = flowers;
    }
    
}
