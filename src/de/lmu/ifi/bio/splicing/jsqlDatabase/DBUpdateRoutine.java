package de.lmu.ifi.bio.splicing.jsqlDatabase;

import de.lmu.ifi.bio.splicing.eventdetection.EventDetector;
import de.lmu.ifi.bio.splicing.genome.Event;
import de.lmu.ifi.bio.splicing.homology.ModelPDB_onENSP;
import de.lmu.ifi.bio.splicing.io.DSSPParser;
import de.lmu.ifi.bio.splicing.structures.mapping.DSSP;
import de.lmu.ifi.bio.splicing.structures.mapping.DSSPData;
import de.lmu.ifi.bio.splicing.structures.mapping.Model;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by uhligc on 14.02.14.
 */
public class DBUpdateRoutine {
    final static DBQuery dbq = new DBQuery();
    final static DBUpdate dbu = new DBUpdate();

    public static void insertEvents() {
        List<String> thebiglist = dbq.findAllGenes();
        for (String s : thebiglist) {
            Set<Event> eventSet = EventDetector.getEvents(dbq.getGene(s));
            for (Event event : eventSet) {
                dbu.insertEvent(event);
            }
        }
    }

    public static void updateEvents() {
        ModelPDB_onENSP modelling = new ModelPDB_onENSP();
        List<String> geneIds = dbq.findAllGenes();
        HashMap<String, DSSPData> dsspData = new HashMap<>();
        for (String geneId : geneIds) {
            List<Event> events = dbq.getEvents(geneId);
            HashMap<String, List<Model>> mapModels = new HashMap<>();
            for (Event event : events) {
                Model used = null;
                double quality = 0;
                List<Model> models;
                if (mapModels.containsKey(event.getI1())) {
                    models = mapModels.get(event.getI1());
                } else {
                    models = modelling.getModelsForENSP(event.getI1());
                    mapModels.put(event.getI1(), models);
                    if(models.size() == 0){
                        System.out.println("No models meeting constraints for " + event.getI1());
                    }
                }
                for (Model model : models) {
                    if (model.contains(event.getStart(), event.getStop()) && model.getQuality() > quality) {
                        used = model;
                        quality = model.getQuality();
                    }
                }
                if (used != null) {
                    DSSPData dssp;
                    if (dsspData.containsKey(used.getPdbId())) {
                        dssp = dsspData.get(used.getPdbId());
                    } else {
                        dssp = DSSPParser.getDSSPData(used.getPdbId());
                        dsspData.put(used.getPdbId(), dssp);
                    }
                    if(dssp != null) {
                        System.out.println("Model " + used.getPdbId() + " applied for " + event.getI1());
                        try{
                        DSSP.updateEventWithAccAndSS(used, event, dssp);
                        } catch (Exception e){
                            System.out.println("Exception while using Model " + used.getPdbId() + " for " + event.getI1());
                            e.printStackTrace();
                        }
                    }
                    else
                        System.out.println("No dssp attainable for " + used.getPdbId() + " (" + event.getI1() + ")");
                }
            }
        }
    }

    public static void insertEventSets() {
        List<String> thebiglist = dbq.findAllGenes();
        for (String s : thebiglist) {
            Set<Event> eventSet = EventDetector.getEvents(dbq.getGene(s));
            dbu.insertEventSet(eventSet);
        }
    }

    public static void insertEventsForKeyword(String keyword) {
        List<String> list = dbq.search(keyword);
        for (String s : list) {
            if (s.startsWith("ENSG")) {
                insertEventsForGene(s);
            }
        }
    }

    public static void insertEventsForGene(String geneid) {
        Set<Event> eventSet = EventDetector.getEvents(dbq.getGene(geneid));
        for (Event event : eventSet) {
            dbu.insertEvent(event);
        }
    }

    public static void main(String[] args) {
        insertEventSets();
    }
}
